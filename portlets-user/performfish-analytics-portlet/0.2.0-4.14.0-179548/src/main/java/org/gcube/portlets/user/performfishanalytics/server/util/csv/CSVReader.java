/**
 *
 */

package org.gcube.portlets.user.performfishanalytics.server.util.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVRow;

/**
 * The Class CSVReader.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 29, 2019
 */
public class CSVReader {

	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';
	private File file;
	private CSVFile csvFile;

	/**
	 * Instantiates a new CSV reader.
	 *
	 * @param file the file
	 * @throws FileNotFoundException the file not found exception
	 */
	public CSVReader(File file) throws FileNotFoundException {

		this.file = file;
		this.csvFile = new CSVFile();
		readCSV(file);
	}

	/**
	 * Read csv.
	 *
	 * @param file the file
	 * @throws FileNotFoundException the file not found exception
	 */
	private void readCSV(File file) throws FileNotFoundException {

		Scanner scanner = new Scanner(file);
		int i = 0;
		while (scanner.hasNext()) {
			CSVRow csvRow = new CSVRow();
			List<String> line = parseLine(scanner.nextLine());
			csvRow.setListValues(line);

			if(i==0){
				csvFile.setHeaderRow(csvRow);
			}else{
				csvFile.addRow(csvRow);
			}
			i++;

		}
		scanner.close();
	}

	/**
	 * Parses the line.
	 *
	 * @param cvsLine the cvs line
	 * @return the list
	 */
	public static List<String> parseLine(String cvsLine) {

		return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
	}

	/**
	 * Parses the line.
	 *
	 * @param cvsLine the cvs line
	 * @param separators the separators
	 * @return the list
	 */
	public static List<String> parseLine(String cvsLine, char separators) {

		return parseLine(cvsLine, separators, DEFAULT_QUOTE);
	}

	/**
	 * Parses the line.
	 *
	 * @param cvsLine the cvs line
	 * @param separators the separators
	 * @param customQuote the custom quote
	 * @return the list
	 */
	private static List<String> parseLine(String cvsLine, char separators, char customQuote) {

		List<String> result = new ArrayList<>();
		// if empty, return!
		if (cvsLine == null || cvsLine.isEmpty()) {
			return result;
		}
		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}
		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}
		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;
		char[] chars = cvsLine.toCharArray();
		for (char ch : chars) {
			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				}
				else {
					// Fixed : allow "" in custom quote enclosed
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					}
					else {
						curVal.append(ch);
					}
				}
			}
			else {
				if (ch == customQuote) {
					inQuotes = true;
					// Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && customQuote == '\"') {
						curVal.append('"');
					}
					// double quotes in column will hit this!
					if (startCollectChar) {
						curVal.append('"');
					}
				}
				else if (ch == separators) {
					result.add(curVal.toString());
					curVal = new StringBuffer();
					startCollectChar = false;
				}
				else if (ch == '\r') {
					// ignore LF characters
					continue;
				}
				else if (ch == '\n') {
					// the end, break!
					break;
				}
				else {
					curVal.append(ch);
				}
			}
		}
		result.add(curVal.toString());
		return result;
	}


	/**
	 * Gets the csv file.
	 *
	 * @return the csvFile
	 */
	public CSVFile getCsvFile() {

		return csvFile;
	}


	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {

		return file;
	}
}
