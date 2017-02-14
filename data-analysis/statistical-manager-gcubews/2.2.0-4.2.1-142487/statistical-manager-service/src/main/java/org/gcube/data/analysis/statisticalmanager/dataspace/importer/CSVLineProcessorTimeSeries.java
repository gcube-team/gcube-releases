package org.gcube.data.analysis.statisticalmanager.dataspace.importer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.DateGuesser;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.DataTypeRecognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVLineProcessorTimeSeries extends CSVAbstractLineProcessor {

	private int indexTime = -1;
	private boolean toContinue = true;
	public ArrayList<ArrayList<String>> firstColumns = new ArrayList<ArrayList<String>>();
	private boolean stop = true;
	private int count = 0;
	private File file;
	int maxIteration = 10;
	private static Logger logger = LoggerFactory.getLogger(CSVLineProcessorTimeSeries.class);
	
	
	
	ArrayList<String> types;

	public CSVLineProcessorTimeSeries(String separator) throws IOException {
		super(separator);

		file = File.createTempFile("TimeSeries", "csv");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean continueProcessing() {
		return toContinue;
	}

	private int getIndexTime() {

		if (indexTime != -1)
			return indexTime;

		int i = 0;
		for (String value : getColsName()) {
			logger.debug("field " + value);
			if (value.toLowerCase().equals("time"))
				indexTime = i;
			i++;
		}
		return indexTime;
	}

	@Override
	public void processDataLine(int arg0, List<String> fields) {

		super.processDataLine(arg0, fields);
		// printArray(fields);
		if (count >= maxIteration) {
			stop = false;
		} else {

			if (fields != null) {
				ArrayList<String> ar = new ArrayList<String>(fields);
				// printArray(ar);
				String fieldValue = ar.get(0);
				List<String> elements = null;

				// try {
				// elements = Transformations.parseCVSString(fieldValue,
				// separator);
				// } catch (Exception e) {
				// elements = new ArrayList<String>();
				// }
				if (separator.equals(","))
					elements = fields;
				else
					elements = Arrays.asList(fieldValue.split(separator));

				// printArray(elements);
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


//	private void printArray(List<String> a) {
//		for (String ar : a) {
//			logger.debug(ar);
//			logger.debug("****");
//		}
//	}

	public void getWellFormaFile(File f, int indexTime) throws IOException {
		FileWriter fstream = new FileWriter(file, true);
		BufferedWriter out = new BufferedWriter(fstream);
		FileReader read = new FileReader(f);
		BufferedReader bufferReader = new BufferedReader(read);
		//bufferReader.readLine();
		// elements=Transformations.parseCVSString(fieldValue,
		// separator);
		String row = bufferReader.readLine();
		String formatedRow = formatRowWithText(row, separator);
		while (formatedRow != null) {

			String[] fields = formatedRow.split(separator);
			for (int i = 0; i < fields.length; i++) {
				String fieldValue = fields[i];
				//System.out.println("fieldValue" + fieldValue);

				if (i == getIndexTime()) {
					String cleanedValue = (fieldValue.contains("\"")) ? fieldValue.replaceAll("\"", "") : fieldValue;

					Calendar calendar = DateGuesser.convertDate(cleanedValue);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss");
					String timeValue= new String();
					if(calendar!=null)
					{ timeValue = sdf.format(calendar.getTime());

					timeValue = (i == fields.length - 1) ? timeValue
							: timeValue + separator;
					}else
						timeValue=""+separator;
//					logger.debug("timevalue :"+timeValue);
					out.write(timeValue);

				} else {
					String cleanedValue = (fieldValue.contains(",")) ? "\""
							+ fieldValue + "\"" : fieldValue;

//					logger.debug("first clean: " + cleanedValue);
					cleanedValue = (i == fields.length - 1) ? cleanedValue
							: cleanedValue + separator;

//					logger.debug(cleanedValue);
					out.write(cleanedValue);
				}

			}
			out.write("\n");
			row = bufferReader.readLine();
			if(row!=null)
			{
				formatedRow = formatRowWithText(row, separator);
			}else
				formatedRow=null;

		}

		out.close();

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

//	private String getSQLType(String value) {
//
//		String sqlType = null;
//		ArrayList<String> values = new ArrayList<String>();
//		values.add(value);
//
//		String className = DataTypeRecognizer.guessType(values);
//
//		if (String.class.getName().equals(className)) {
//			sqlType = "varchar";
//		}
//		if (Boolean.class.getName().equals(className)) {
//			sqlType = "boolean";
//		}
//
//		if (BigDecimal.class.getName().equals(className)) {
//			sqlType = "double precision";
//		}
//
//		return sqlType;
//	}

	public boolean isTimeFound() {
		return indexTime != -1;
	}

	public File getFile() {
		return file;
	}

	public  String formatRowWithText(String row, String separator) {
		int indexFrom = 0;
		int indexChar = 0;
		int pair = 0;
		String result = new String();
		int i = 0;
		int lastPositiveIndex = 0;
//		System.out.println("inside while");
		while (indexChar >= 0) {
			if (i == 0) {
				if (row.toCharArray()[0] == '\"') 
					indexChar = 0;
				else
					indexChar = row.indexOf("\"", indexFrom + 1);
					

			} else
				indexChar = row.indexOf("\"", indexFrom + 1);
//			System.out.println("char index :" + indexChar);

			if (indexChar >= 0) {
				pair++;
//				System.out.println("pair:" + pair);

				if (pair % 2 == 0) {
//					System.out.println("pair is pair");

					String sub = row.substring(indexFrom, indexChar);
//					System.out.println("substring is " + sub);

					if (sub.contains(separator)) {
//						System.out.println("sub contain separato");

						if (separator == ",") {
//							System.out.println("replace ,");

							sub = sub.replace(";", " ");


							sub = sub.replace(separator, ";");

						} else {

							sub = sub.replace(",", " ");


							sub = sub.replaceAll(separator, ",");

						}

//						System.out.println("sub senza separato " + sub);

					}
					result = result + sub;
//					System.out.println("string concat " + result);

				} else {
//					System.out.println("sub non pair");

					result = result + row.substring(indexFrom, indexChar);
//					System.out.println("string concat " + result);

				}
				lastPositiveIndex = indexChar;

			} else {
				if (i == 0) {
//					System.out.println("non c è apici ");

					return row;
				}
			}
			indexFrom = indexChar;

			i++;
		}
		result = result + row.substring(lastPositiveIndex, row.length());
		return result;

	}


}
