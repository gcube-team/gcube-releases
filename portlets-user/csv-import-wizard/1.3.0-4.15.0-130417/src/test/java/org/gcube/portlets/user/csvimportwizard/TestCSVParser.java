/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import net.sf.csv4j.CSVReader;
import net.sf.csv4j.ParseException;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestCSVParser {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException, IOException {
		
		String file = "/home/fedy2/Desktop/TS test data/smallWithComment.csv";
		
		Reader fileReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
		CSVReader csvReader = new CSVReader(fileReader, ',', '#');

		//System.out.println(csvReader.readCSVLine());
		List<String> header = csvReader.readLine(true);
		System.out.println(header);
	}

}
