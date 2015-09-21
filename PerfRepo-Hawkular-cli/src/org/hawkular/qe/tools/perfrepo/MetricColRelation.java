package org.hawkular.qe.tools.perfrepo;

import java.util.ArrayList;

public class MetricColRelation {
	// *.csv file column name
	public String CSVColumnName;
	// metric name in PerfRepo
	public String remoteMetricName;
	// values
	public ArrayList<String> values;
}