package org.hawkular.qe.tools.perfrepo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {
	// Test Execution Tags
	public ArrayList tags;
	// Test Execution Parameters
	public Map<String, String> parameters;
	public ArrayList metrics;
	public Map<String, String> perfrepo;
	public Map<String, ArrayList> attachments;

	public void printSettings() {
		printList(tags, "tags");
		printList(metrics, "metrics");
		printHashMap(attachments, "attachments");
		printHashMap(parameters, "parameters");
		printHashMap(perfrepo, "perfrepo");
	}

	public void printList(ArrayList l, String label) {
		System.out.println(label + ":");
		for (int i = 0; i < l.size(); i++)
			System.out.println("\t" + l.get(i));
	}

	public void printHashMap(Map<String, ?> hm, String label) {
		System.out.println(label + ":");
		for (String name : hm.keySet()) {
			String key = name.toString();
			Object value = hm.get(name).toString();
			if (value.getClass() == String.class)
				System.out.println("\t" + key + ": " + value);
			if (value.getClass() == ArrayList.class) {
				System.out.println("\t" + key + ":");
				for(String s : (ArrayList<String>) value){
					System.out.println("\t\t"+ value);
				}
			}
		}
	}

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

}
