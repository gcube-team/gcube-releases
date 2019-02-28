package org.gcube.portlets.docxgenerator.transformer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import net.sf.csv4j.CSVFileReaderProcessor;
import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

/**
 * Parser for reading CSV files.
 * 
 * @author Luca Santocono
 * 
 */
public class CSVTimeSeriesParser {

	private CSVFileReaderProcessor frp;
	private CSVLineProcessorImpl csvlineproc;

	/**
	 * 
	 * Processes a CSV file.
	 * 
	 * @param filePath
	 *            Path to the file.
	 * @param start
	 *            First line number to parse.
	 * @param end
	 *            Last line number to parse.
	 * @param columns
	 *            Column numbers, from which content should be extracted.
	 * @throws ParseException
	 *             If there are syntactic errors in the CSV files.
	 * @throws IOException
	 *             If there are problems in reading/writing on the filesystem.
	 * @throws ProcessingException
	 *             If there are problems with the CSV processor.
	 */
	public void processFile(final String filePath, final int start,
			final int end, final List<Integer> columns) throws ParseException,
			IOException, ProcessingException {
		frp = new CSVFileReaderProcessor();
		csvlineproc = new CSVLineProcessorImpl(start, end, columns);
		Charset charset = Charset.forName("UTF-8");
		frp.processFile(filePath, charset, csvlineproc);
	}

	/**
	 * Gets the contents to insert in the generated document.
	 * 
	 * @return A table containing the content.
	 */
	public List<List<String>> getTableToShow() {
		return csvlineproc.getTable();
	}

	/**
	 * Gets the table header.
	 * 
	 * @return The header of the table.
	 */
	public List<String> getHeader() {
		return csvlineproc.getHeader();
	}
}
