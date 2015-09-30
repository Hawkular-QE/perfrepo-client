package org.hawkular.qe.tools.perfrepo;

import java.util.ArrayList;

/**
 * Describes relation between PerfRepo metric name and *.csv file column which
 * values will be pushed to PerfRepo
 * 
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