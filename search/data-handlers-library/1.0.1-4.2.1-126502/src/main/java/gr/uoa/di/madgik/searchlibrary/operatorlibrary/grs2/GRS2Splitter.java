package gr.uoa.di.madgik.searchlibrary.operatorlibrary.grs2;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.FieldNaming.LocalFieldName;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.Unary;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A GRS2 created from gRS2 that contains FileField. Given a gRS2,
 * records contained in each file field are returned through a gRS2.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class GRS2Splitter extends Unary implements Runnable {
	private static Logger log = LoggerFactory.getLogger(GRS2Splitter.class.getName());

	private IRecordReader<Record> reader;
	private File sourceFile;

	/**
	 * Field delimiter of records stored in file. Default value is ^A but can be
	 * overridden through inputParameters.
	 */
	private String delimiter = "\\" + Character.toString((char) 1);

	private String filterMask;

	private RecordWriter<Record> writer;

	private FieldDefinition[] fieldDefs;

	public GRS2Splitter(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats) throws Exception {
		super(inLocator, operatorParameters, stats);
		init();
	}

	public GRS2Splitter(URI inLocator, Map<String, String> inputParameters, StatsContainer stats, long timeout, TimeUnit timeUnit) throws Exception {
		super(inLocator, inputParameters, stats, timeout, timeUnit);
		init();
	}

	private void init() throws Exception{
		if (operatorParameters != null) {
			filterMask = operatorParameters.get("filterMask");
			if (operatorParameters.get("delimiter") != null)
				delimiter = operatorParameters.get("delimiter");
		}
		
		log.info("Initialized with filtermask: " + filterMask + " and delimiter: '" + delimiter + "'");
	}
	
	public URI compute() throws Exception{
		reader = new ForwardReader<Record>(inLocator);

		// if filterMask is null, use all fields
		if (filterMask == null) {
			sourceFile = retrieveNextFileFromRS(reader);
			if (sourceFile != null)
				filterMask = initializeMask(sourceFile.getAbsolutePath(), delimiter);
			else
				filterMask = "";
		}
		fieldDefs = initializeSchema(filterMask);

		writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[] { new GenericRecordDefinition(fieldDefs) },
				RecordWriter.DefaultBufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);

		new Thread(this).start();;
		return writer.getLocator();
	}
	
	public void run() {
		Thread.currentThread().setName(GRS2Splitter.class.getName());

		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = start, firstOutputStop = start;
		int rc = 0;
		try {
			if (sourceFile == null)
				sourceFile = retrieveNextFileFromRS(reader);

			outerloop: while (sourceFile != null) {
				log.debug("retrieving records from file: " + sourceFile);
				if (rc == 0)
					firstInputStop = Calendar.getInstance().getTimeInMillis();

				GenericRecord rec = null;
				String line;
				int ln = 1;
				try {
					BufferedReader br = new BufferedReader(new FileReader(sourceFile));
					while ((line = br.readLine()) != null) {
						List<Field> fieldList = new ArrayList<Field>();

						String[] toks = line.endsWith(delimiter) ? (line + " ").split(delimiter) : line.split(delimiter);

						if (line.endsWith(delimiter))
							toks[toks.length - 1] = "";

						boolean empty = true;
						for (FieldDefinition fd : fieldDefs) {
							Matcher m = Pattern.compile("\\d+$").matcher(fd.getName());

							int fn = -1;
							if (m.find())
								fn = Integer.parseInt(m.group());

							try {
								fieldList.add(new StringField(toks[fn]));
								if (!toks[fn].isEmpty())
									empty = false;

							} catch (Exception e) {
								log.warn("Field: " + fd.getName() + " can not be retrieved from file: " + sourceFile + " line: " + line);
								sourceFile = retrieveNextFileFromRS(reader);
								continue outerloop;
							}
						}

						if (empty) {
							log.debug("skipping empty record");
							continue;
						}

						rec = new GenericRecord();
						rec.setFields(fieldList.toArray(new Field[fieldList.size()]));

						ln++;
//						log.trace("Returning next row: " + sourceFile + "#" + ln++);
						if (!writer.importRecord(rec, timeout, timeUnit)) {
							if (writer.getStatus() == Status.Open)
								log.warn("Consumer has timed out");
							else
								break outerloop;
						}
						rc++;

						if (rc == 1)
							firstOutputStop = Calendar.getInstance().getTimeInMillis();
					}
					br.close();
				} catch (IOException e) {
					log.warn("could not read source file: " + sourceFile);
				}
				sourceFile = retrieveNextFileFromRS(reader);
			}
		} catch (Exception e) {
			log.error("Error during source retrieval. Closing", e);
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (Exception ee) {
			}
		}

		long closeStop = Calendar.getInstance().getTimeInMillis();

		stats.timeToComplete(closeStop - start);
		stats.timeToFirstInput(firstInputStop - start);
		stats.timeToFirst(firstOutputStop - start);
		stats.producedResults(rc);
		stats.productionRate(((float) rc / (float) (closeStop - start)) * 1000);
		log.info("GRS2SPLITTER OPERATOR:Produced first result in " + (firstOutputStop - start) + " milliseconds\n" + "Produced last result in "
				+ (closeStop - start) + " milliseconds\n" + "Produced " + rc + " results\n" + "Production rate was "
				+ (((float) rc / (float) (closeStop - start)) * 1000) + " records per second");
	}

	private static FieldDefinition[] initializeSchema(String filterMask) {
		List<FieldDefinition> fieldDefsList = new ArrayList<FieldDefinition>();

		// Filter mask consisted of names e.g [recordId, payload, contentType]
		for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
			if (ref.equals("bytestream")) {
				log.warn("No file field is allowed");
				// fieldDefsList.add(new FileFieldDefinition(ref));
			} else {
				fieldDefsList.add(new StringFieldDefinition(ref));
			}
		}

		log.info("ResultSet schema that will be used: " + fieldDefsList);
		return fieldDefsList.toArray(new FieldDefinition[fieldDefsList.size()]);
	}

	private static String initializeMask(String fName, String delimiter) {

		String line = null, filterMask = null;

		File f = new File(fName);

		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				break;
			}
			br.close();
		} catch (IOException e) {
			log.warn("could not read source");
		}

		int stringFieldsNum;
		try {
			stringFieldsNum = line.split(delimiter).length;
		} catch (Exception e) {
			log.error("Wrong delimiter", e);
			return null;
		}

		filterMask = "[";
		for (int i = 0; i < stringFieldsNum; i++)
			filterMask += "field" + i + ", ";
		filterMask = filterMask.substring(0, filterMask.length() - 2);
		filterMask += "]";

		return filterMask;
	}

	private static File retrieveNextFileFromRS(IRecordReader<Record> reader) throws Exception {
		File sourceFile = null;

		Record rec = null;
		while (rec == null) {
			if (reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0)) {
				break;
			}

			rec = reader.get(TimeoutDef, TimeUnitDef);
			if (rec == null) {
				if (reader.getStatus() == Status.Open) {
					log.warn("Reader has timeout. Continue to wait");
					continue;
				} else
					break;
			}
		}
		if (rec == null)
			return null;

		Field payloadField = rec.getField(LocalFieldName.bytestream.name());
		if (payloadField == null || !(payloadField instanceof FileField))
			throw new Exception("Record does not contain any file field");

		sourceFile = ((FileField) payloadField).getPayload();

		return sourceFile;
	}

	/**
	 * @param args
	 */
	// public static void main(String[] args) {
	// String line = ".b..";
	// String delimiter = "\\.";
	// String[] toks = line.endsWith(delimiter.substring(1)) ? (line +
	// " ").split(delimiter) : line.split(delimiter);
	//
	// if (line.endsWith(delimiter.substring(1)))
	// toks[toks.length - 1] = "";
	//
	// for (String tok : toks)
	// System.out.println(":" + tok);
	// }

}
