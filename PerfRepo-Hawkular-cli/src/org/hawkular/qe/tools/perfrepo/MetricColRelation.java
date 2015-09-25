package org.hawkular.qe.tools.perfrepo;

import java.util.ArrayList;

public class MetricColRelation {
	// *.csv file column name
	public String CSVColumnName;
	// metric name in PerfRepo
	public String remoteMetricName;

	private ArrayList<String> values;

	public ArrayList<String> getValues() {
		return values;
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

}