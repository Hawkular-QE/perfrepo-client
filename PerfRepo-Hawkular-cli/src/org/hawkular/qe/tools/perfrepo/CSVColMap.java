package org.hawkular.qe.tools.perfrepo;

import java.util.ArrayList;

/**
 * Describe relation between *.csv file, relation between columns and metrics
 * and multimetric X-axis relation
 * 
 * @author vprusa
 *
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