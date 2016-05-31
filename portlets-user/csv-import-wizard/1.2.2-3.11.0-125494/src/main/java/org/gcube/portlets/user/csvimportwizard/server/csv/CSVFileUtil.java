/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.portlets.user.csvimportwizard.client.data.CSVRowError;
import org.mozilla.universalchardet.UniversalDetector;

import net.sf.csv4j.CSVReader;
import net.sf.csv4j.ParseException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVFileUtil {
	
	protected static Logger logger = LoggerFactory.getLogger(CSVFileUtil.class);
	
	public static ArrayList<String> getHeader(File csvFile, CSVParserConfiguration config) throws ParseException, IOException
	{
		List<String> candidateHeader = getHeader(csvFile, config.getCharset(), config.getDelimiter(), config.getComment());
		
		if (config.isHasHeader()) return new ArrayList<String>(candidateHeader);
		else {		
			ArrayList<String> fakeHeaders = new ArrayList<String>();
			for (int i = 0; i<candidateHeader.size(); i++) fakeHeaders.add("Field "+i);
			return fakeHeaders;
		}
	}

	public static List<String> getHeader(File csvFile, Charset charset, char delimiter, char comment) throws ParseException, IOException
	{
		logger.trace("getHeader charset: "+charset+" delimiter: "+delimiter+" comment: "+comment);
		Reader fileReader = new InputStreamReader(new FileInputStream(csvFile), charset);
		CSVReader csvReader = new CSVReader(fileReader, delimiter, comment);

		List<String> header = csvReader.readLine();
		return header==null?Collections.<String>emptyList():header;
	}

	public static ArrayList<CSVRowError> checkCSV(File csvFile, CSVParserConfiguration config, long errorsLimit) throws ParseException, IOException
	{
		return checkCSV(csvFile, config.getCharset(), config.getDelimiter(), config.getComment(), errorsLimit);
	}

	public static ArrayList<CSVRowError> checkCSV(File csvFile, Charset charset, char delimiter, char comment, long errorsLimit) throws IOException
	{
		logger.trace("checkCSV charset: "+charset+" delimiter: "+delimiter+" comment: "+comment);
		ArrayList<CSVRowError> errors = new ArrayList<CSVRowError>();

		Reader fileReader = new InputStreamReader(new FileInputStream(csvFile), charset);
		CSVReader csvReader = new CSVReader(fileReader, delimiter, comment);
		long count = -1;
		long fields = -1;

		do{
			try{
				count = csvReader.countFields();
			}catch(ParseException exception)
			{
				StringBuilder errorMessage = new StringBuilder();
				errorMessage.append("Error parsing the file ");
				errorMessage.append(exception.getMessage());
				
				CSVRowError error = new CSVRowError(csvReader.getLineNumber(), csvReader.getCurrentLine(), errorMessage.toString());
				errors.add(error);
				logger.trace("CSVRowError: ",error);
				continue;
			}

			if (count>=0){
				if (fields<0 ) fields = count;
				else if (fields != count) {
					StringBuilder errorMessage = new StringBuilder();
					errorMessage.append("Expected ");
					errorMessage.append(fields);
					errorMessage.append(" fields, found ");
					errorMessage.append(count);
					errorMessage.append(" fields. Check the format of your input file.");
					CSVRowError error = new CSVRowError(csvReader.getLineNumber(), csvReader.getCurrentLine(), errorMessage.toString());
					errors.add(error);
					logger.trace("CSVRowError: ",error);
				}
			}

		}while(count >=0 && errors.size()<errorsLimit);

		return errors;
	}

	public static void toJson(File csvFile, Charset inputCharset, File outputFile, Charset outputCharset, boolean hasHeader, char delimiter, char comment, long limit) throws ParseException, IOException
	{
		toJson(new FileInputStream(csvFile), inputCharset, new FileOutputStream(outputFile), outputCharset, hasHeader, delimiter, comment, limit);					
	}

	public static void toJson(InputStream csv, OutputStream output, Charset outputCharset, CSVParserConfiguration config, long limit) throws ParseException, IOException
	{
		toJson(csv, config.getCharset(), output, outputCharset, config.isHasHeader(), config.getDelimiter(), config.getComment(), limit);
	}

	public static void toJson(InputStream csv, Charset inputCharset, OutputStream output, Charset outputCharset, boolean hasHeader, char delimiter, char comment, long limit) throws ParseException, IOException
	{
		logger.trace("toJson charset: "+inputCharset+" delimiter: "+delimiter+" comment: "+comment);
		Writer writer = new BufferedWriter(new OutputStreamWriter(output, outputCharset));
		Reader reader = new InputStreamReader(csv, inputCharset);
		CSVReader csvReader = new CSVReader(reader, delimiter, comment);
		String jsonLine;
		long count = 0;

		if (hasHeader) csvReader.readJSonLine();

		writer.write("{\"records\":[");
		while((jsonLine = csvReader.readJSonLine())!=null){

			if (count>0) writer.write(",");
			writer.write(jsonLine);
			count++;

			if (count>limit) break;
		}

		writer.write("]}");

		csvReader.close();
		writer.close();

	}
	
	public static String guessEncoding(File file) throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		UniversalDetector detector = new UniversalDetector(null);
		byte[] buf = new byte[4096];

		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}

		detector.dataEnd();
		String encoding = detector.getDetectedCharset();
		detector.reset();
		fis.close();
		
		return encoding;
	}

}
