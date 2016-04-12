package org.gcube.portlets.widgets.file_dw_import_wizard.server.file;

/**
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.csv4j.ParseException;

import org.fao.fi.comet.domain.species.tools.converters.dwca.cli.DWCAConverter;
import org.mozilla.universalchardet.UniversalDetector;

public class FileUtil {

	protected static Logger logger = Logger.getLogger("");

	@SuppressWarnings("finally")
	public static ArrayList<String> checkDWCA(File file, String user)
			throws Exception {
		final ArrayList<String> generatedFile = new ArrayList<String>();

		logger.log(Level.SEVERE, "checking DWCA...");
		logger.log(Level.SEVERE,
				"checking file located in " + file.getAbsolutePath());
		if (user == null)
			user = "DWCAimportWizard";
//		final String[] arguments = { "-inFile", "\"" + file.getPath() + "\"",
//				"-outDir", "\"./DWCAmain/\"", "-providerId", "\"" + user + "\"" };
//		for (int i = 0; i < arguments.length; i++)
//			System.out.print(arguments[i] + " ");
		logger.log(Level.SEVERE, "DWCA converter call...");
	generatedFile.add(System.getProperty("java.io.tmpdir")+"/DWCAmain/" + user + "_taxa.taf.gz");
		generatedFile.add(System.getProperty("java.io.tmpdir")+"/DWCAmain/" + user + "_vernacular.taf.gz");

		DWCAelaboration converter= new DWCAelaboration(file.getPath(), System.getProperty("java.io.tmpdir")+"/DWCAmain/", user);
		if(!converter.elaborations())
			throw new  Exception ("Failure in DWCA elaborations");
//		Thread thread = new Thread() {
//			public void run() {
//
//				try {
//					DWCAConverter.main(arguments);
//				} catch (Throwable e) {
//					logger.log(Level.SEVERE, "Inside catch...");
//					logger.log(Level.SEVERE, e.toString());
//					e.printStackTrace();
//				}
//
//			}
//
//		};
//		thread.start();
//		 try {
//			 thread.join();
//	        } catch (InterruptedException e) {
//	            e.printStackTrace();
//	        }
//		

		logger.log(Level.SEVERE, "Inside catch...");
		// logger.log(Level.SEVERE, e.toString());
		// e.printStackTrace();

		logger.log(Level.SEVERE, "joined...");

		logger.log(Level.SEVERE, "DWCA converter call done");

		return generatedFile;

	}

	// public static List<String> getFirstLine(CSVReader csvReader, boolean
	// includeComment) throws ParseException, IOException
	// {
	// logger.trace("getFirstLine includeComment: "+includeComment);
	//
	// List<String> header = csvReader.readLine(includeComment);
	// return header==null?Collections.<String>emptyList():header;
	// }
	//

	// public static void toJson(File csvFile, Charset inputCharset, File
	// outputFile, Charset outputCharset, HeaderPresence headerPresence, char
	// delimiter, char comment, long limit) throws ParseException, IOException
	// {
	// toJson(new FileInputStream(csvFile), inputCharset, new
	// FileOutputStream(outputFile), outputCharset, headerPresence, delimiter,
	// comment, limit);
	// }

	public static void toJson(InputStream is, OutputStream output,
			Charset outputCharset, long limit) throws ParseException,
			IOException {
		toJson(is, output, outputCharset, limit);
	}

	// public static void toJson(InputStream csv, Charset inputCharset,
	// OutputStream output, Charset outputCharset, HeaderPresence
	// headerPresence, char delimiter, char comment, long limit) throws
	// ParseException, IOException
	// {
	// logger.trace("toJson charset: "+inputCharset+" delimiter: "+delimiter+" comment: "+comment);
	// Writer writer = new BufferedWriter(new OutputStreamWriter(output,
	// outputCharset));
	// Reader reader = new InputStreamReader(csv, inputCharset);
	// CSVReader csvReader = new CSVReader(reader, delimiter, comment);
	// String jsonLine;
	// long count = 0;
	//
	// if (headerPresence!=HeaderPresence.NONE) {
	// csvReader.readLine(headerPresence==HeaderPresence.FIRST_LINE_COMMENTED_INCLUDED);
	// }
	//
	// writer.write("{\"records\":[");
	// while((jsonLine = csvReader.readJSonLine())!=null){
	//
	// if (count>0) writer.write(",");
	// writer.write(jsonLine);
	// count++;
	//
	// if (count>limit) break;
	// }
	//
	// writer.write("]}");
	//
	// csvReader.close();
	// writer.close();
	//
	// }

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
