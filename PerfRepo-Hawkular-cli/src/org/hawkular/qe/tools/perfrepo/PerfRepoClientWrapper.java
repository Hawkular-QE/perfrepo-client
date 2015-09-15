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

import org.hawkular.qe.tools.perfrepo.CSVFile;
import org.hawkular.qe.tools.perfrepo.Settings;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * @author Vojtěch Průša vprusa@redhat.com
 * @version 1.0
 */
public class PerfRepoClientWrapper {

	public CSVFile csv;
	public Settings settings;
	public static String help = "Pushes data from *.csv to PerfRepo with *.yml setting file\n"
			+ "Necessary:\n"
			+ "\t-Dhost=\"IP:PORT\" IP & Port of PerfRepo host"
			+ "\t-Durl=\"URL\" URL of PerfRepo (empty = root of PerfRepo host)"
			+ "\t-DbasicHash=\"base64(username:password)\" PerfRepo username & password encoded by base64"
			+ "\t-DtestUId=\"perfrepoClientTestUId\" Test UId"
			+ "\t-DsettingsFile=\"./resources/example.yml\" resource YAML file";

	public static void main(String[] args) {
		// help printing
		if (Arrays.asList(args).contains("help")
				|| Arrays.asList(args).contains("--help")
				|| Arrays.asList(args).contains("-h"))
			System.out.println(PerfRepoClientWrapper.help);

		try {
			PerfRepoClientWrapper ptt = new PerfRepoClientWrapper();
			ptt.loadSettings();
			ptt.loadCSV(ptt.settings.getDelimiter(),
					ptt.settings.getCsvFilePath());
			ptt.pushData(ptt.csv, ptt.settings);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Finished");
		}
	}

	public void loadCSV(String delimeter, String filePath) {
		csv = new CSVFile(filePath, delimeter);
		csv.printCSV();
	}

	public void pushData(CSVFile csv, Settings data) throws Exception {
		String host = Settings.getHost();
		String url = Settings.getUrl();
		String basicHash = Settings.getBasicHash();
		String testUId = settings.getTestUId();
		String testExecutionName = settings.getTestExecutionName();
		System.out.println("TestUId: " + testUId);
		PerfRepoClient client = new PerfRepoClient(host, url, basicHash);

		TestExecutionBuilder testExecutionBuilder = TestExecution.builder();

		testExecutionBuilder.testUid(testUId);
		testExecutionBuilder.name(testExecutionName);
		Date date = new Date();
		testExecutionBuilder.started(date);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("Started at: " + formatter.format(date));

		Test t = client.getTestByUid(testUId);
		if (t == null) {
			System.out.println("Ending test because of wrong TestUId");
			return;
		}

		// SET PARAMETERS
		if (data.parameters != null) {
			System.out.println("Loading parameters...");
			Map<String, String> hm = data.parameters;
			for (int i = 0; i < data.parameters.size(); i++) {
				for (String name : hm.keySet()) {
					String key = name.toString();
					String value = hm.get(name).toString();
					System.out.println("\t" + key + ": " + value);
					testExecutionBuilder.parameter(key, value);
				}
			}
		}

		// SET TAGS
		if (data.tags != null) {
			System.out.println("Loading tags...");
			for (String tag : data.tags) {
				System.out.println("\t" + tag);
				testExecutionBuilder.tag(tag);
			}
		}

		// SET VALUES
		if (csv.body != null) {
			System.out.println("Loading values...");
			for (int i = 0; i < csv.body.length; i++) {
				String csvMetricName = csv.head[i];
				System.out.println("\t" + csvMetricName + ":");
				for (MetricGlue mg : settings.metrics) {
					if (mg.CSVColumnName.matches(csvMetricName)) {
						for (String sv : csv.body[i]) {
							testExecutionBuilder.value(mg.remoteName,
									Double.parseDouble(sv));
							System.out.println("\t\t" + sv);
						}
					}
				}
			}
		}

		// BUILD TEST EXECUTION
		System.out.println("Building test execution...");
		TestExecution testExecution = testExecutionBuilder.build();
		double testExecutionId = client.createTestExecution(testExecution);
		System.out.println("\tTestExecutionId: " + testExecutionId);

		// SET ATTACHMENTS - text/plain
		System.out.println("Loading attachments...");
		for (String name : data.attachments.keySet()) {
			ArrayList<String> value = (ArrayList<String>) data.attachments
					.get(name);
			client.uploadAttachment((long) testExecutionId,
					new File(value.get(1)), value.get(0), name);
		}

	}

	public void loadSettings() throws FileNotFoundException, YamlException {
		YamlReader reader = new YamlReader(new FileReader(
				Settings.getSettingsFile()));
		settings = (Settings) reader.read();
		settings.printSettings();
		System.out.println("\n");
	}

}
