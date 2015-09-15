package org.hawkular.qe.tools.perfrepo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 */
public class CSVFile {

	// th - names of columnts
	public String[] head;
	// tbody = tr * th
	public ArrayList<String>[] body;
	public String csvFilePath;
	public String delimiter; // ;

	@SuppressWarnings("unchecked") // for line: body = new ArrayList[head.length];
	public CSVFile(String csvFilePath, String delimiter) {
		this.csvFilePath = csvFilePath;
		this.delimiter = delimiter;
		BufferedReader br = null;
		String line = "";

		try {
			br = new BufferedReader(new FileReader(csvFilePath));
			boolean first = true;
			while ((line = br.readLine()) != null) {
				if (first == true) {
					head = line.split(delimiter);
					body = new ArrayList[head.length];
					for (int i = 0; i < head.length; i++) {
						body[i] = new ArrayList<String>();
					}
					first = false;
					continue;
				}
				String[] bodyLine = line.split(delimiter);
				for (int i = 0; i < bodyLine.length; i++) {
					body[i].add(bodyLine[i]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void printCSV() {
		if (head == null || body == null)
			return;
		System.out.println("Head:");
		for (int i = 0; i < head.length; i++)
			System.out.print(head[i] + delimiter);
		System.out.println("");
		int lines = body[0].size();
		System.out.println("Body:");
		for (int ii = 0; ii < lines; ii++) {
			for (int i = 0; i < body.length; i++) {
				System.out.print(body[i].get(ii) + delimiter);
			}
			System.out.println("");
		}
		System.out.println("");
	}

}
