/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import net.sf.csv4j.CSVReader;
import net.sf.csv4j.CSVWriter;
import net.sf.csv4j.ParseException;

import org.gcube.portlets.user.td.gwtservice.shared.file.HeaderPresence;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class CSVFileUtil {

	private static final int MAXROWCHECK = 1000000;
	private static Logger logger = LoggerFactory.getLogger(CSVFileUtil.class);

	public static ArrayList<String> getHeader(File csvFile,
			CSVParserConfiguration parserConfiguration) throws ParseException,
			IOException {

		CSVReader csvReader = createCSVReader(csvFile, parserConfiguration);
		switch (parserConfiguration.getHeaderPresence()) {
		case FIRST_LINE_COMMENTED_INCLUDED: {
			List<String> firstLine = getFirstLine(csvReader, true);
			return new ArrayList<String>(firstLine);
		}
		case FIRST_LINE: {
			List<String> firstLine = getFirstLine(csvReader, false);
			return new ArrayList<String>(firstLine);
		}
		case NONE:
			break;
		}

		int fieldCount = (int) csvReader.countFields();
		ArrayList<String> fakeHeaders = new ArrayList<String>();
		for (int i = 0; i < fieldCount; i++)
			fakeHeaders.add("Field " + i);
		return fakeHeaders;

	}

	public static List<String> getFirstLine(CSVReader csvReader,
			boolean includeComment) throws ParseException, IOException {
		logger.trace("getFirstLine includeComment: " + includeComment);

		List<String> header = csvReader.readLine(includeComment);
		return header == null ? Collections.<String> emptyList() : header;
	}

	protected static CSVReader createCSVReader(File csvFile,
			CSVParserConfiguration parserConfiguration)
			throws FileNotFoundException {
		logger.trace("createCSVReader csvFile: " + csvFile
				+ " parserConfiguration: " + parserConfiguration);
		Reader fileReader = new InputStreamReader(new FileInputStream(csvFile),
				parserConfiguration.getCharset());
		CSVReader csvReader = new CSVReader(fileReader,
				parserConfiguration.getDelimiter(),
				parserConfiguration.getComment());
		return csvReader;
	}

	public static File skipError(File inCSVFile, CSVParserConfiguration config)
			throws ParseException, IOException {
		return skipError(inCSVFile, config.getCharset(), config.getDelimiter(),
				config.getComment());
	}

	public static File skipError(File inCSVFile, Charset charset,
			char delimiter, char comment) throws ParseException, IOException {
		File outCSVFile = File.createTempFile("import", "csv");

		outCSVFile.deleteOnExit();

		BufferedReader fileReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inCSVFile), charset));
		BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outCSVFile), charset));
		CSVWriter csvWriter = new CSVWriter(fileWriter, delimiter, comment);
		CSVReader csvReader = new CSVReader(fileReader, delimiter, comment);

		List<String> line = null;

		do {
			try {
				line = csvReader.readLine();

			} catch (ParseException exception) {
				logger.debug("Skip line:" + line.toString());
				continue;
			}
			csvWriter.writeLine(line);

		} while (line != null);

		csvReader.close();
		csvWriter.close();
		return outCSVFile;
	}

	public static CheckCSVSession checkCSV(File csvFile,
			CSVParserConfiguration config, long errorsLimit)
			throws ParseException, IOException {
		return checkCSV(csvFile, config.getCharset(), config.getDelimiter(),
				config.getComment(), errorsLimit);
	}

	public static CheckCSVSession checkCSV(File csvFile,
			Charset charset, char delimiter, char comment, long errorsLimit)
			throws IOException {
		logger.trace("checkCSV charset: " + charset + " delimiter: "
				+ delimiter + " comment: " + comment);
		CheckCSVSession checkCSVSesssion;
		ArrayList<CSVRowError> errors = new ArrayList<CSVRowError>();

		Reader fileReader = new InputStreamReader(new FileInputStream(csvFile),
				charset);
		CSVReader csvReader = new CSVReader(fileReader, delimiter, comment);

		long count = -1;
		long fields = -1;
		int maxRowCheck = 0;

		do {
			try {
				count = csvReader.countFields();
			} catch (ParseException exception) {
				StringBuilder errorMessage = new StringBuilder();
				errorMessage.append("Error parsing the file ");
				errorMessage.append(exception.getMessage());

				CSVRowError error = new CSVRowError(csvReader.getLineNumber(),
						csvReader.getCurrentLine(), errorMessage.toString());
				errors.add(error);
				logger.trace(error.getErrorDescription());
				continue;
			}

			if (count >= 0) {
				if (fields < 0)
					fields = count;
				else if (fields != count) {
					StringBuilder errorMessage = new StringBuilder();
					errorMessage.append("Expected ");
					errorMessage.append(fields);
					errorMessage.append(" fields, found ");
					errorMessage.append(count);
					errorMessage
							.append(" fields. Check the format of your input file.");
					CSVRowError error = new CSVRowError(
							csvReader.getLineNumber(),
							csvReader.getCurrentLine(), errorMessage.toString());
					errors.add(error);
					logger.trace(error.getErrorDescription());
				}
			}
			maxRowCheck++;

		} while (count >= 0 && errors.size() < errorsLimit
				&& maxRowCheck < MAXROWCHECK);
		
		if(maxRowCheck < MAXROWCHECK){
			checkCSVSesssion=new CheckCSVSession(errors, false);
		} else {
			checkCSVSesssion=new CheckCSVSession(errors, true);
		}
		
		return checkCSVSesssion;
	}

	public static void toJson(File csvFile, Charset inputCharset,
			File outputFile, Charset outputCharset,
			HeaderPresence headerPresence, char delimiter, char comment,
			long limit) throws ParseException, IOException {
		toJson(new FileInputStream(csvFile), inputCharset,
				new FileOutputStream(outputFile), outputCharset,
				headerPresence, delimiter, comment, limit);
	}

	public static void toJson(InputStream csv, OutputStream output,
			Charset outputCharset, CSVParserConfiguration config, long limit)
			throws ParseException, IOException {
		toJson(csv, config.getCharset(), output, outputCharset,
				config.getHeaderPresence(), config.getDelimiter(),
				config.getComment(), limit);
	}

	public static void toJson(InputStream csv, Charset inputCharset,
			OutputStream output, Charset outputCharset,
			HeaderPresence headerPresence, char delimiter, char comment,
			long limit) throws ParseException, IOException {
		logger.trace("toJson charset: " + inputCharset + " delimiter: "
				+ delimiter + " comment: " + comment);
		Writer writer = new BufferedWriter(new OutputStreamWriter(output,
				outputCharset));
		Reader reader = new InputStreamReader(csv, inputCharset);
		CSVReader csvReader = new CSVReader(reader, delimiter, comment);
		String jsonLine;
		long count = 0;

		if (headerPresence != HeaderPresence.NONE) {
			csvReader
					.readLine(headerPresence == HeaderPresence.FIRST_LINE_COMMENTED_INCLUDED);
		}

		writer.write("{\"records\":[");
		while ((jsonLine = csvReader.readJSonLine()) != null) {

			if (count > 0)
				writer.write(",");
			writer.write(jsonLine);
			count++;

			if (count > limit)
				break;
		}

		writer.write("]}");

		csvReader.close();
		writer.close();

	}

	public static String guessEncoding(File file) throws IOException {
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
