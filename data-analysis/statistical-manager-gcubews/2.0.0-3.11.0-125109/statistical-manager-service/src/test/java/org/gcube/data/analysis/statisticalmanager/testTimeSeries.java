package org.gcube.data.analysis.statisticalmanager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.gcube.contentmanagement.graphtools.utils.DateGuesser;

public class testTimeSeries {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

//		System.out
//				.println(formatRowWithText(
//						"1,-43.46,48.44,\"2/19/2011 1:17:00 AM\",27,3,3.00",
//						","));
//		SimpleDateFormat sdf = new SimpleDateFormat(
//				"yyyy-MM-dd hh:mm:ss");
//		String timeValue = sdf.format();
//		String s="\"22/19/2011 1:17:00 AM\"";
//		String cleanedValue = (s.contains("\"")) ? s.replaceAll("\"", "") : s;
		String cleanedValue = "1";

		Calendar calendar = DateGuesser.convertDate(cleanedValue);
		if(calendar==null)
			System.out.print("null");

		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String timeValue = sdf.format(calendar.getTime());
//
//
			System.out.print(timeValue);
//		System.out.print(timeValue);
		// File f= new File ("/home/angela/Desktop/ts.csv");
		// File file= new File ("/home/angela/Desktop/newts.csv");
		// FileWriter fstream = new FileWriter(file, true);
		// BufferedWriter out = new BufferedWriter(fstream);
		// FileReader read = new FileReader(f);
		// BufferedReader bufferReader = new BufferedReader(read);
		// bufferReader.readLine();
		// String row = bufferReader.readLine();
		// while (row != null) {
		//
		// String[] fields = row.split(";");
		// for (int i = 0; i < fields.length; i++) {
		// String fieldValue = fields[i];
		// System.out.println("fieldValue"+fieldValue);
		//
		// if (i == 3) {
		// Calendar calendar = DateGuesser.convertDate(fieldValue);
		// SimpleDateFormat sdf = new SimpleDateFormat(
		// "yyyy-MM-dd hh:mm:ss");
		// String timeValue = sdf.format(calendar.getTime());
		//
		// timeValue = (i == fields.length - 1) ? timeValue
		// : timeValue + ",";
		// System.out.println(timeValue);
		// out.write(timeValue);
		//
		// } else {
		// String cleanedValue = (fieldValue.contains(",")) ? "\""
		// + fieldValue + "\"" : fieldValue;
		// cleanedValue = (i == fields.length - 1) ? cleanedValue
		// : cleanedValue + ",";
		//
		// System.out.println(cleanedValue);
		// out.write(cleanedValue);
		// }
		//
		// }
		// out.write("\n");
		// row = bufferReader.readLine();
		//
		// }
		//
		// out.write("\n");
		// out.close();
	}

	public static String formatRowWithText(String row, String separator) {
		int indexFrom = 0;
		int indexChar = 0;
		int pair = 0;
		String result = new String();
		int i = 0;
		int lastPositiveIndex = 0;
		System.out.println("inside while");
		while (indexChar >= 0) {
			if (i == 0) {
				if (row.toCharArray()[0] == '\"') 
					{System.out.println("inside here");
						indexChar = 0;
					}else
						indexChar = row.indexOf("\"", indexFrom + 1);
				
			
			}
			else	
				indexChar = row.indexOf("\"", indexFrom + 1);
					
			
			System.out.println("char index :" + indexChar);

			if (indexChar >= 0) {
				pair++;
				System.out.println("pair:" + pair);

				if (pair % 2 == 0) {
					System.out.println("pair is pair");

					String sub = row.substring(indexFrom, indexChar);
					System.out.println("substring is " + sub);

					if (sub.contains(separator)) {
						System.out.println("sub contain separato");

						if (separator == ",") {
							System.out.println("replace ,");
							System.out.println("before  "+sub);

							sub = sub.replace(";", " ");
							System.out.println("after1  "+sub);


							sub = sub.replace(separator, ";");
							System.out.println("after2  "+sub);

						} else {
							System.out.println("before  "+sub);

							sub = sub.replace(",", " ");
							System.out.println("after1  "+sub);


							sub = sub.replaceAll(separator, ",");
							System.out.println("after2  "+sub);

						}

						System.out.println("sub senza separato " + sub);

					}
					result = result + sub;
					System.out.println("string concat " + result);

				} else {
					System.out.println("sub non pair");

					result = result + row.substring(indexFrom, indexChar);
					System.out.println("string concat " + result);

				}
				lastPositiveIndex = indexChar;

			} else {
				if (i == 0) {
					System.out.println("non c è apici ");

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
