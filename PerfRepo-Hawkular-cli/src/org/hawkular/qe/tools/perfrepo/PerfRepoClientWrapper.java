package org.hawkular.qe.tools.perfrepo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

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

	// public ArrayList<CSVFile> csvs;
	public Settings settings;
	public ArrayList<CSVColMap> values;
	public static String help = "Pushes data from *.csv to PerfRepo with *.yml setting file\n"
			+ "Necessary:\n"
			+ "\t-Dhost=\"IP:PORT\" IP & Port of PerfRepo host"
			+ "\t-Durl=\"URL\" URL of PerfRepo (empty = root of PerfRepo host)"
			+ "\t-DbasicHash=\"base64(username:password)\" PerfRepo username & password encoded by base64"
			+ "\t-DtestUId=\"perfrepoClientTestUId\" Test UId"
			+ "\t-DsettingsFile=\"./resources/example.yml\" resource YAML file";

	final static Logger logger = Logger.getLogger(PerfRepoClientWrapper.class);

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
			ptt.loadCSVs(ptt.settings.getDelimiter());
			ptt.pushData(ptt.values, ptt.settings);
		} catch (Exception e) {
			logger.error("Error occured, see below");
			e.printStackTrace();
		} finally {
			logger.info("Finished");
		}
	}

	public void loadSettings() throws FileNotFoundException, YamlException {
		String file = Settings.getSettingsFile();
		logger.info("Loading settings file: " + file);
		YamlReader reader = new YamlReader(new FileReader(file));
		settings = (Settings) reader.read();
		// equivalent but easier access
		values = settings.metrics;
		settings.printSettings();
	}

	public void loadCSVs(String delimeter) {
		// csvs = new ArrayList<CSVFile>();
		logger.info("CSVs file expected delimiter: " + delimeter);
		for (CSVColMap colMap : settings.metrics) {
			CSVFile csv = this.loadCSV(delimeter, colMap.sourceCSVFilePath);
			for (MetricColRelation mcr : colMap.metricColRelation) {
				mcr.values = csv.getColumnValues(mcr.CSVColumnName);
			}
		}

	}

	public CSVFile loadCSV(String delimeter, String filePath) {
		logger.info("Loading CSV file: " + filePath);
		CSVFile csv = new CSVFile(filePath, delimeter);
		csv.printCSV();
		return csv;
	}

	public void pushData(ArrayList<CSVColMap> csvs, Settings data)
			throws Exception {
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
		if (data.parameters != null) {
			logger.info("Loading parameters...");
			String out = "";
			Map<String, String> hm = data.parameters;
			for (int i = 0; i < data.parameters.size(); i++) {
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
		if (data.tags != null) {
			String out = "";
			logger.info("Loading tags...");
			for (String tag : data.tags) {
				out += "\n\t" + tag;
				testExecutionBuilder.tag(tag);
			}
			logger.info(out);
		}

		// SET VALUES
		String out = "";
		for (CSVColMap cm : values) {
			logger.info("Loading values from file: " + cm.sourceCSVFilePath);
			for (MetricColRelation mcr : cm.metricColRelation) {
				out += "\n\t" + mcr.remoteMetricName + ":";
				for (String val : mcr.values) {
					testExecutionBuilder.value(mcr.remoteMetricName,
							Double.parseDouble(val));
					out += "\n\t\t" + val;
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
		for (String name : data.attachments.keySet()) {
			ArrayList<String> value = (ArrayList<String>) data.attachments
					.get(name);
			client.uploadAttachment((long) testExecutionId,
					new File(value.get(1)), value.get(0), name);
		}

	}

}
