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

/**
 * Describes relation between PerfRepo metric name and *.csv file column which
 * values will be pushed to PerfRepo
 * @author vprusa
 */
public class MetricColRelation {

    // *.yml file data structure

    // *.csv file column name
    public String CSVColumnName;
    // metric name in PerfRepo
    public String remoteMetricName;

    // code only
    // contains final values loaded from csv files
    private ArrayList<String> values;

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

}