package org.gcube.portlets.docxgenerator.transformer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of the CSVLineProcessor interface for reading TimeSeries CSV
 * files.
 * 
 * @author Luca Santocono
 * 
 */
public class CSVLineProcessorImpl implements CSVLineProcessor {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(CSVLineProcessorImpl.class);

	protected boolean finished;
	protected int start;
	protected int end;
	protected List<Integer> columns;

	protected List<List<String>> table;
	protected List<String> header;

	/**
	 * Constructor for the CSVLineProcessorImpl class.
	 * 
	 * @param start
	 *            Line, on which the parser starts parsing.
	 * @param end
	 *            Line, on which the parser stops parsing.
	 * @param columns
	 *            Indicates which columns number should be read.
	 */
	public CSVLineProcessorImpl(final int start, final int end,
			final List<Integer> columns) {
		this.start = start;
		this.end = end;
		this.columns = columns;
		table = new LinkedList<List<String>>();
	}

	/**
	 * 
	 * @return Boolean value indicating weather the parsing process should stop.
	 * @see net.sf.csv4j.CSVLineProcessor#continueProcessing()
	 */
	@Override
	public boolean continueProcessing() {
		return !finished;
	}

	/**
	 * @param numberline
	 *            Current row number.
	 * @param fields
	 *            Contains the content of the relevant columns.
	 * 
	 * @see net.sf.csv4j.CSVLineProcessor#processDataLine(int, java.util.List)
	 */
	@Override
	public void processDataLine(int numberline, List<String> fields) {
		if (numberline < start)
			return;
		if (numberline > end) {
			finished = true;
			return;
		}
		List<String> row = new ArrayList<String>();
		if (columns != null) {
			for (int column : columns) {
				String field = fields.get(column);
				row.add(field);
			}

		} else {
			for (String field : fields) {
				row.add(field);
			}

		}
		table.add(row);
	}

	/**
	 * @param numberline
	 *            Current row number.
	 * @param fields
	 *            Contains the content of the relevant columns.
	 * 
	 * @see net.sf.csv4j.CSVLineProcessor#processHeaderLine(int, java.util.List)
	 */
	@Override
	public void processHeaderLine(int numberline, List<String> fields) {
		header = new ArrayList<String>();
		if (columns != null) {
			for (int column : columns) {
				String field = fields.get(column);
				header.add(field);
			}
		} else {
			for (String field : fields) {
				header.add(field);
			}
		}
	}

	/**
	 * Getter for the table field. Gets the table resulting from the CSV file
	 * parsing.
	 * 
	 * @return A table containing the contents of the CSV file.
	 */
	public List<List<String>> getTable() {
		return table;
	}

	/**
	 * Setter for the table field.
	 * 
	 * @param table
	 *            The table, in which relevant content should be inserted.
	 */
	public void setTable(List<List<String>> table) {
		this.table = table;
	}

	/**
	 * Getter for the header field.
	 * 
	 * @return The header row.
	 */
	public List<String> getHeader() {
		return header;
	}

	/**
	 * Setter for the header field.
	 * 
	 * @param header
	 *            Header to assign.
	 */
	public void setHeader(List<String> header) {
		this.header = header;
	}

}
