package org.hawkular.qe.tools.perfrepo;

import java.util.ArrayList;

public class CSVColMap {
	public String sourceCSVFilePath;
	public ArrayList<MetricColRelation> metricColRelation;
	public String multimetricParamCSVColumnName;
	public String multimetricParamRemoteName;

	private ArrayList<String> multimetricsParamValues;

	public ArrayList<String> getParamValues() {
		return multimetricsParamValues;
	}

	public void setParamValues(ArrayList<String> multimetricsParamValues) {
		this.multimetricsParamValues = multimetricsParamValues;
	}

}