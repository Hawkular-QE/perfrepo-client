/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.qe.tools.perfrepo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Describes structure that contains *.yml configuration file and handles
 * settings of this program
 * @author vprusa
 */
public class Settings {

    // Test Execution Tags
    public ArrayList<String> tags;
    // Test Execution Parameters
    public Map<String, String> parameters;
    // Test Execution Metrics
    public ArrayList<CSVColMap> metrics;
    public Map<String, String> perfrepo;
    public Map<String, ArrayList<String>> attachments;

    public void printSettings() {
        printList(tags, "tags");
        printList(metrics, "metrics");
        printHashMap(attachments, "attachments");
        printHashMap(parameters, "parameters");
        printHashMap(perfrepo, "perfrepo");
    }

    /**
     * Prints lists used here
     * @param l
     * @param label
     */
    public void printList(ArrayList<?> l, String label) {
        PerfRepoClientWrapper.logger.info(label + ":");
        String out = "";
        if (l != null)
            for (Object value : l) {
                if (value.getClass() == String.class)
                    out += "\n\t" + value;
                if (value.getClass() == CSVColMap.class) {
                    CSVColMap cm = (CSVColMap) value;
                    out += "\n\tFile: " + cm.sourceCSVFilePath;
                    for (MetricColRelation mcr : cm.metricColRelation) {
                        out += "\n\t\tRemoteMetricName: "
                                + mcr.remoteMetricName + "\tCSVColumnName: "
                                + mcr.CSVColumnName;
                    }
                }
            }
        PerfRepoClientWrapper.logger.info(out);
    }

    /**
     * Prints Maps used here
     * @param hm
     * @param label
     */
    @SuppressWarnings("unchecked")
    // for line: for (String s : (ArrayList<String>) value) {
    public void printHashMap(Map<String, ?> hm, String label) {
        String out = "";
        PerfRepoClientWrapper.logger.info(label + ":");
        if (hm != null)
            for (String name : hm.keySet()) {
                String key = name.toString();
                Object value = hm.get(name).toString();
                if (value.getClass() == String.class)
                    out += "\n\t" + key + ": " + value + "";
                if (value.getClass() == ArrayList.class) {
                    out += "\n\t" + key + ":";
                    for (String s : (ArrayList<String>) value) {
                        out += "\n\t\t" + s;
                    }
                }
            }
        PerfRepoClientWrapper.logger.info(out);
    }

    // *.yml file data getters - -D... arguments have priority

    public String getTestUId() {
        return (System.getProperty("testUID") != null ? System
                .getProperty("testUID") : perfrepo.get("testUID"));
    }

    public String getTestExecutionName() {
        return (System.getProperty("testExecutionName") != null ? System
                .getProperty("testExecutionName") : perfrepo
                .get("testExecutionName"));
    }

    public String getDelimiter() {
        return (System.getProperty("csvFileDelimiter") != null ? System
                .getProperty("csvFileDelimiter") : perfrepo
                .get("csvFileDelimiter"));
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
