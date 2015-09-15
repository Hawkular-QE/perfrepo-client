package org.hawkular.qe.tools.perfrepo;

import java.util.ArrayList;
import java.util.Map;

public class Settings {

	// Test Execution Tags
	public ArrayList<String> tags;
	// Test Execution Parameters
	public Map<String, String> parameters;
	public ArrayList<MetricGlue> metrics;
	public Map<String, String> perfrepo;
	public Map<String, ArrayList<String>> attachments;

	public void printSettings() {
		printList(tags, "tags");
		printList(metrics, "metrics");
		printHashMap(attachments, "attachments");
		printHashMap(parameters, "parameters");
		printHashMap(perfrepo, "perfrepo");
	}

	public void printList(ArrayList<?> l, String label) {
		System.out.println(label + ":");
		for (Object value : l) {
			if (value.getClass() == String.class)
				System.out.println("\t" + value);
			if (value.getClass() == MetricGlue.class) {
				MetricGlue mg = (MetricGlue) value;
				System.out.println("\tRemoteName: " + mg.remoteName
						+ "\tCSVColumnName: " + mg.CSVColumnName);
			}
		}
	}

	@SuppressWarnings("unchecked")
	// for line: for (String s : (ArrayList<String>) value) {
	public void printHashMap(Map<String, ?> hm, String label) {
		System.out.println(label + ":");
		for (String name : hm.keySet()) {
			String key = name.toString();
			Object value = hm.get(name).toString();
			if (value.getClass() == String.class)
				System.out.println("\t" + key + ": " + value);
			if (value.getClass() == ArrayList.class) {
				System.out.println("\t" + key + ":");
				for (String s : (ArrayList<String>) value) {
					System.out.println("\t\t" + s);
				}
			}
		}
	}

	// *.yml file data getters

	public String getTestUId() {
		return perfrepo.get("testUID");
	}

	public String getTestExecutionName() {
		return perfrepo.get("testExecturionName");
	}

	public String getDelimiter() {
		return perfrepo.get("csvFileDelimiter");
	}

	public String getCsvFilePath() {
		return perfrepo.get("csvFilePath");
	}

	// Static getters

	public static String getHost() {
		return System.getProperty("host");
	}

	public static String getUrl() {
		return (System.getProperty("url") == null) ? "" : System
				.getProperty("url");
	}

	public static String getBasicHash() {
		return System.getProperty("basicHash");
	}

	public static String getSettingsFile() {
		return System.getProperty("settingsFile");
	}

}
