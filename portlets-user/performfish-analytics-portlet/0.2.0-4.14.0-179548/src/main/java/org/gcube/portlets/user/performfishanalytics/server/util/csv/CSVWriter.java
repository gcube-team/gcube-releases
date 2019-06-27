/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * The Class CSVWriter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 30, 2019
 */
public class CSVWriter {

	private PrintWriter reportPrintWriter;

	/**
	 * Instantiates a new CSV writer.
	 *
	 * @param tempFile the temp file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public CSVWriter(File tempFile) throws IOException {

		FileWriter reportWriter = new FileWriter(tempFile, true);
		BufferedWriter reportBW = new BufferedWriter(reportWriter);
		reportPrintWriter = new PrintWriter(reportBW);
	}

	/**
	 * Write csv line.
	 *
	 * @param newline the newline
	 */
	public void writeCSVLine(String newline){

		reportPrintWriter.println(newline);
	}

	/**
	 * Close writer.
	 */
	public void closeWriter(){

		if(reportPrintWriter!=null)
			reportPrintWriter.close();
	}

}
