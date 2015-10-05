package org.hawkular.qe.tools.perfrepo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.perfrepo.client.PerfRepoClient;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.builder.TestExecutionBuilder;
import org.apache.log4j.Logger;
import org.hawkular.qe.tools.perfrepo.CSVFile;
import org.hawkular.qe.tools.perfrepo.Settings;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * @author Vojtěch Průša vprusa@redhat.com
 * @version 1.0
 */
public class PerfRepoClientWrapper {

    public Settings settings;
    public ArrayList<CSVColMap> values;
    public static String help = "Pushes data from *.csv to PerfRepo with *.yml setting file\n"
            + "Necessary:"
            + "\n\t-Dhost=\"IP:PORT\" IP & Port of PerfRepo host"
            + "\n\t-Durl=\"URL\" URL of PerfRepo (empty = root of PerfRepo host)"
            + "\n\t-DbasicHash=\"base64(username:password)\" PerfRepo username & password encoded by base64"
            + "\n\t-DtestUId=\"perfrepoClientTestUId\" Test UId"
            + "\n\t-DsettingsFile=\"./resources/example.yml\" resource YAML file"
            + "\n\t-testUID=\"Test_example_1\" PerfRepo Test UID"
            + "\n\t-testExecturionName=\"Test_example_1_exec_2\" PerfRepo Test execution name"
            + "\n\t-csvFileDelimiter=\"./resources/example.yml\" resource YAML file"
            + "";

    final static Logger logger = Logger.getLogger(PerfRepoClientWrapper.class);

    /**
     * Send data from *.csv files and *.yml configuration file to PerfRepo
     * 
     * @param args
     */
    public static void main(String[] args) {
        logger.info("Starting PerfRepoClient wrapper\nRead more: https://github.com/Hawkular-QE/perfrepo-client");
        // help printing
        if (Arrays.asList(args).contains("help")
                || Arrays.asList(args).contains("--help")
                || Arrays.asList(args).contains("-h"))
            logger.info(PerfRepoClientWrapper.help);

        try {
            PerfRepoClientWrapper ptt = new PerfRepoClientWrapper();
            ptt.loadSettings();
            ptt.loadCSVs();
            ptt.pushData();
        } catch (Exception e) {
            logger.error("Error occured, see below");
            e.printStackTrace();
        } finally {
            logger.info("Finished");
        }
    }

    /**
     * Loads *.yml file and its settings to
     * org.hawkular.qe.tools.perfrepo.Settings class
     * 
     * @throws FileNotFoundException
     * @throws YamlException
     */
    public void loadSettings() throws FileNotFoundException, YamlException {
        String file = Settings.getSettingsFile();
        logger.info("Loading settings file: " + file);
        YamlReader reader = new YamlReader(new FileReader(file));
        settings = (Settings) reader.read();
        // equivalent but easier access
        values = (ArrayList<CSVColMap>) settings.metrics.clone();
        settings.printSettings();
    }

    /**
     * Loads data from *.csv files
     */
    public void loadCSVs() {
        logger.info("CSVs file expected delimiter: " + settings.getDelimiter());
        for (CSVColMap colMap : values) {
            CSVFile csv = this.loadCSV(settings.getDelimiter(),
                    colMap.sourceCSVFilePath);
            /*
             * values multimetricParamCSVColumnName and
             * multimetricParamRemoteName are not necessary in *.yml config file
             * and takes care of multimetrics.
             */
            if (colMap.multimetricParamCSVColumnName != null
                    && colMap.multimetricParamRemoteName != null) {
                /*
                 * Parse time with format HH:mm:ss to [ms]
                 */
                if (colMap.multimetricParamCSVColumnName.matches("Time")) {
                    ArrayList<String> paramValues = csv
                            .getColumnValues(colMap.multimetricParamCSVColumnName);
                    try {
                        ArrayList<String> paramValuesTime = new ArrayList<String>();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        for (String paramVal : paramValues) {
                            Date date = sdf.parse(paramVal);
                            paramValuesTime.add(String.valueOf(date.getTime()));
                        }
                        colMap.setParamValues(paramValuesTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    // set metric values
                    colMap.setParamValues(csv
                            .getColumnValues(colMap.multimetricParamCSVColumnName));
                }
            }
            for (MetricColRelation mcr : colMap.metricColRelation) {
                mcr.setValues(csv.getColumnValues(mcr.CSVColumnName));
            }
        }
    }

    /**
     * Load *.csv file with delimiter that separate columns
     * 
     * @param delimeter
     * @param filePath
     * @return CSVFile
     */
    public CSVFile loadCSV(String delimeter, String filePath) {
        logger.info("Loading CSV file: " + filePath);
        CSVFile csv = new CSVFile(filePath, delimeter);
        csv.printCSV();
        return csv;
    }

    /**
     * Push loaded data to PerfRepo
     * Pushed data: 
     * 	*.csv files values
     * 	tags
     * 	parameters
     * 	attachments
     * @throws Exception
     */
    public void pushData() throws Exception {
        String host = Settings.getHost();
        String url = Settings.getUrl();
        String basicHash = Settings.getBasicHash();
        String testUId = settings.getTestUId();
        String testExecutionName = settings.getTestExecutionName();
        logger.info("TestUId: " + testUId);
        PerfRepoClient client = new PerfRepoClient(host, url, basicHash);

        TestExecutionBuilder testExecutionBuilder = TestExecution.builder();

        testExecutionBuilder.testUid(testUId);
        testExecutionBuilder.name(testExecutionName);
        Date date = new Date();
        testExecutionBuilder.started(date);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("Started at: " + formatter.format(date));

        Test t = client.getTestByUid(testUId);
        if (t == null) {
            logger.error("Ending test because of wrong TestUId");
            return;
        }

        // SET PARAMETERS
        if (settings.parameters != null) {
            logger.info("Loading parameters...");
            String out = "";
            Map<String, String> hm = settings.parameters;
            for (int i = 0; i < settings.parameters.size(); i++) {
                for (String name : hm.keySet()) {
                    String key = name.toString();
                    String value = hm.get(name).toString();
                    out += "\n\t" + key + ": " + value;
                    testExecutionBuilder.parameter(key, value);
                }
            }
            logger.info(out);
        }

        // SET TAGS
        testExecutionBuilder.tag(""); // set 1 empty tag otherwise
                                      // NullPointerException is thrown
        if (settings.tags != null) {
            String out = "";
            logger.info("Loading tags...");
            for (String tag : settings.tags) {
                out += "\n\t" + tag;
                testExecutionBuilder.tag(tag);
            }
            logger.info(out);
        }

        // SET METRICS VALUES
        String out = "";
        for (CSVColMap colMap : values) {
            logger.info("Loading values from file: " + colMap.sourceCSVFilePath);

            for (MetricColRelation mcr : colMap.metricColRelation) {
                out += "\n\t" + mcr.remoteMetricName + ":";
                logger.info(mcr.getValues());

                /*
                 * values multimetricParamCSVColumnName and
                 * multimetricParamRemoteName are not necessary in *.yml config
                 * file and takes care of multimetrics.
                 */
                if (colMap.multimetricParamCSVColumnName != null
                        && colMap.multimetricParamRemoteName != null) {
                    ArrayList<String> mcrValues = mcr.getValues();
                    ArrayList<String> paramValues = colMap.getParamValues();
                    for (int i = 0; i < mcrValues.size(); i++) {
                        testExecutionBuilder.value(mcr.remoteMetricName,
                                Double.parseDouble(mcrValues.get(i)),
                                colMap.multimetricParamRemoteName,
                                paramValues.get(i));
                        out += " " + mcrValues.get(i);
                    }
                } else {
                    for (String val : mcr.getValues()) {
                        testExecutionBuilder.value(mcr.remoteMetricName,
                                Double.parseDouble(val));
                        out += " " + val;
                    }

                }

            }
        }
        logger.info(out);

        // BUILD TEST EXECUTION
        logger.info("Building test execution...");
        TestExecution testExecution = testExecutionBuilder.build();

        double testExecutionId = client.createTestExecution(testExecution);
        logger.info("\tTestExecutionId: " + testExecutionId);

        // SET ATTACHMENTS - text/plain
        logger.info("Loading attachments...");
        if (settings.attachments != null && !settings.attachments.isEmpty()) {
            for (String name : settings.attachments.keySet()) {
                ArrayList<String> value = (ArrayList<String>) settings.attachments
                        .get(name);
                client.uploadAttachment((long) testExecutionId,
                        new File(value.get(1)), value.get(0), name);
            }
        }
    }
}
