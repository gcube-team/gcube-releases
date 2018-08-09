package org.gcube.data.analysis.statisticalmanager.dataspace.importer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.DataTypeRecognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CSVLineProcessorGeneric extends CSVAbstractLineProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(CSVLineProcessorGeneric.class);
	
	private boolean stop = true;
	private int count = 0;
//	private Map<String, List<String>> map;
	int maxIteration = 100;
	ArrayList<String> types;
	public ArrayList<ArrayList<String>> firstColumns = new ArrayList<ArrayList<String>>();

	public CSVLineProcessorGeneric(String separator) {
		super(separator);
	}

	@Override
	public boolean continueProcessing() {
		// if (firstColumns.size() != 0)
		// maxIteration = firstColumns.get(0).size() - 1;
		return stop;
	}


//	private ArrayList<String> getColumn(int index,
//			ArrayList<ArrayList<String>> values) {
//		logger.debug("Column " + index);
//		ArrayList<String> column = new ArrayList<String>();
//		for (ArrayList<String> ar : values)
//			if (ar != null && index < ar.size()) {
//				// logger.debug(ar.get(i));
//				column.add(ar.get(index));
//			}
//		printArray(column);
//		return column;
//	}

	@Override
	public void processDataLine(int arg0, List<String> fields) {

		super.processDataLine(arg0, fields);
		// printArray(fields);
		if (count >= maxIteration) {
			stop = false;
		} else {

			if (fields != null) {
				ArrayList<String> ar = new ArrayList<String>(fields);
//				printArray(ar);
				String fieldValue = ar.get(0);
				List<String> elements = null;

				
//					try {
//						elements = Transformations.parseCVSString(fieldValue,
//								separator);
//					} catch (Exception e) {
//						elements = new ArrayList<String>();
//					}
				if (separator.equals(",")) 
					elements= fields;
				else elements = Arrays.asList(fieldValue.split(separator));
				
//				printArray(elements);
				int ncolumns = elements.size();
				// int ncolumns = fields.size();
//				logger.debug("number of columns are" + ncolumns);
				for (int i = 0; i < ncolumns; i++) {
//					logger.debug("i: " + i);
					String element = elements.get(i);
					// String element = fields.get(i);
//					logger.debug("element :" + element);
//					logger.debug("count " + count);

					if (i >= firstColumns.size()) {
						ArrayList<String> newer = new ArrayList<String>();
						newer.add(element);
						firstColumns.add(newer);

					} else {
						firstColumns.get(i).add(element);
						// firstColumns.get(i).set(count, element );
					}
//					logger.debug("in the column " + i + "put element" + element);
				}

			}

			count++;
		}

	}




	public static String getSqlType(ArrayList<String> values) {

		String className = DataTypeRecognizer.guessType(values);
		String sqlType = null;
		if (String.class.getName().equals(className)) {
			sqlType = "varchar";
		}
		if (Boolean.class.getName().equals(className)) {
			sqlType = "boolean";
		}

		if (BigDecimal.class.getName().equals(className)) {
			sqlType = "double precision";
		}
		return sqlType;
	}

	public static String getSqlType(String value) {
		ArrayList<String> values = new ArrayList<String>();
		values.add(value);

		String className = DataTypeRecognizer.guessType(values);
//		logger.debug("class name for : " + value + " is " + className);
		String sqlType = "varchar";
		if (String.class.getName().equals(className)) {
			sqlType = "varchar";
		}
		if (Boolean.class.getName().equals(className)) {
			sqlType = "boolean";
		}

		if (BigDecimal.class.getName().equals(className)) {
			sqlType = "double precision";
		}
		return sqlType;
	}

	

};
