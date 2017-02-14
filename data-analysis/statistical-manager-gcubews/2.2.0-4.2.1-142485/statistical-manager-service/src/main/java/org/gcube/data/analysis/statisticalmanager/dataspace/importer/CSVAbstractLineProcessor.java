package org.gcube.data.analysis.statisticalmanager.dataspace.importer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CSVAbstractLineProcessor implements CSVLineProcessor {
	private static Logger logger = LoggerFactory.getLogger(CSVAbstractLineProcessor.class);

	private static final String FIELD = "field";

	private boolean stopFieldName = false;
	protected List<String> colNames = new ArrayList<String>();
	protected List<String> sqlTypes = new ArrayList<String>();
	protected String separator;
	private boolean quote = false;

	public CSVAbstractLineProcessor(String separator) {

		this.separator = separator;

	}

	protected String formattedString(String refactorStrings) {
		ArrayList<String> ar = new ArrayList<String>(
				Arrays.asList(refactorStrings.split("\"")));
		String result = new String();
		for (int i = 0; i < ar.size(); i++) {
			if ((i % 2) == 0) {
				result.concat(ar.get(i));

			} else {
				String tmp = ar.get(i).replace(separator, " ");
				result.concat(tmp);
			}
		}
		return result;
	}

	@Override
	public void processDataLine(int arg0, List<String> fieldValues) {

		if (separator.equals("|")) {
			separator = "\\|";
		}
		if (separator.equals(".")) {
			separator = "\\.";
		}
//		printArray(fieldValues);

		if( !separator.equals(","))
		{
			String support= new String();
			for(String s:fieldValues)
			{
				support+=s;
			}
//			logger.debug("Support"+support);
			fieldValues.clear();
			fieldValues.add(support);
//			printArray(fieldValues);
		}
		if (!colNames.isEmpty() || stopFieldName)
			return;

		for (int i = 0; i < fieldValues.size(); i++) {
			String fieldValue = fieldValues.get(i);
//			logger.debug("fieldValue:" + fieldValue);
			String[] values;
			
			values = fieldValue.split(separator);

			int j = 0;
			if (!separator.equals(",")) {
				for (String value : values) {
					if (!quote) {
//						logger.debug("Value:" + value);
						colNames.add(FIELD + (j + i));
						j++;
					}
					if (value.startsWith("\"") || value.startsWith("'")) {
						quote = !(quote);
					}

				}
			} else {
				colNames.add(FIELD + i);
			}
		}
		stopFieldName = true;
	}

	@Override
	public void processHeaderLine(int arg0, List<String> fieldNames) {
		for (String fieldName : fieldNames) {
			String[] values = fieldName.split(separator);
			for (String value : values) {
				colNames.add(value);
			}
		}
	}

	public List<String> getColsName() {
		return colNames;
	}

	public List<String> getSqlType() {
		return sqlTypes;
	}

	private   void printArray(List<String> a) {
		for (String ar : a) {
			logger.debug(ar);
			logger.debug("****");
		}
	}
	
	
	public void addSqlType(ArrayList<String> values) {
		if (values != null)
			sqlTypes.addAll(values);
	}
}
