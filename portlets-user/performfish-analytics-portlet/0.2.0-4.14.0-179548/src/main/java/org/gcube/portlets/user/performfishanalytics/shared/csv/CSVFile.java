/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.shared.csv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The Class CSVFile.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 29, 2019
 */
public class CSVFile implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 6408321963787244600L;
	private CSVRow headerRow;
	private List<CSVRow> valueRows;
	private String fileName;

	/**
	 * Instantiates a new CSV file.
	 */
	public CSVFile(){

	}


	/**
	 * Instantiates a new csv file.
	 *
	 * @param headerRow the header row
	 * @param valueRows the value rows
	 */
	public CSVFile(String fileName, CSVRow headerRow, List<CSVRow> valueRows) {
		this.fileName = fileName;
		this.headerRow = headerRow;
		this.valueRows = valueRows;
	}


	/**
	 * Gets the header row.
	 *
	 * @return the headerRow
	 */
	public CSVRow getHeaderRow() {

		return headerRow;
	}


	/**
	 * Gets the value rows.
	 *
	 * @return the valueRows
	 */
	public List<CSVRow> getValueRows() {

		return valueRows;
	}


	/**
	 * Sets the header row.
	 *
	 * @param headerRow the headerRow to set
	 */
	public void setHeaderRow(CSVRow headerRow) {

		this.headerRow = headerRow;
	}



	/**
	 * Adds the row.
	 *
	 * @param csvRow the csv row
	 */
	public void addRow(CSVRow csvRow) {

		if(this.valueRows==null)
			this.valueRows = new ArrayList<CSVRow>();

		this.valueRows.add(csvRow);
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {

		return fileName;
	}



	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {

		this.fileName = fileName;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("CSVFile [headerRow=");
		builder.append(headerRow);
		builder.append(", valueRows=");
		builder.append(valueRows);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append("]");
		return builder.toString();
	}



}
