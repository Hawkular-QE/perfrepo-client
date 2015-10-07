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
 * Describe relation between *.csv file, relation between columns and metrics
 * and multimetric X-axis relation
 * @author vprusa
 */
public class CSVColMap {

    // *.yml file data structure

    public String sourceCSVFilePath;
    public ArrayList<MetricColRelation> metricColRelation;

    // multimetric X-axis column name and remote metric name
    public String multimetricParamCSVColumnName;
    public String multimetricParamRemoteName;

    // code only

    private ArrayList<String> multimetricsParamValues;

    public ArrayList<String> getParamValues() {
        return multimetricsParamValues;
    }

    public void setParamValues(ArrayList<String> multimetricsParamValues) {
        this.multimetricsParamValues = multimetricsParamValues;
    }

}