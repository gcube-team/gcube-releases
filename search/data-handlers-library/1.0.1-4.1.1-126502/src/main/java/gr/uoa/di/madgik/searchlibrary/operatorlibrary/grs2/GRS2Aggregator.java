package gr.uoa.di.madgik.searchlibrary.operatorlibrary.grs2;

import gr.uoa.di.madgik.grs.GRS2Exception;
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
import gr.uoa.di.madgik.grs.record.field.FileFieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.FieldNaming.LocalFieldName;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.Unary;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.contenttype.ContentTypeEvaluator;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GRS2 created for a result set. Contents are retrieved through a
 * gRS2 are stored in a file that is passed to a result set as a fileField.
 * 
 * @author john.gerbesiotis - DI NKUA
 * @param <T>
 *            extends {@link Record}
 * 
 */
public class GRS2Aggregator extends Unary implements Runnable {
	private static Logger log = LoggerFactory.getLogger(GRS2Aggregator.class.getName());

	private File sinkFile;

	/**
	 * The writer to use
	 */
	private IRecordWriter<Record> writer;
	FieldDefinition[] wfdefs;
	/**
	 * Field delimiter of records stored in file. Default value is ^A but can be
	 * overridden through outputParameters.
	 */
	private String delimiter = Character.toString((char) 1);

	/**
	 * The reader to use
	 */
	private IRecordReader<Record> reader = null;

	private String filterMask;
	
	private RecordDefinition[] fieldDefs;
	
	public GRS2Aggregator(URI inLocator, Map<String, String> parameters, StatsContainer statsCont, long timeout, TimeUnit timeUnit) throws Exception {
		super(inLocator, parameters, statsCont, timeout, timeUnit);
		init();
	}

	/**
	 * @param inLocator
	 *            input locator of the consuming result set
	 * @param output
	 *            output value of the operator
	 * @param parameters
	 *            output parameters of the operator
	 * @param statsCont
	 *            statistics container
	 * @throws Exception
	 *             If the initialization of the operator fails
	 */
	public GRS2Aggregator(URI inLocator, Map<String, String> parameters, StatsContainer statsCont) throws Exception {
		super(inLocator, parameters, statsCont);
		init();
	}

	private void init() {
		if (operatorParameters != null) {
			if (operatorParameters.get("delimiter") != null)
				delimiter = operatorParameters.get("delimiter");
		}
		
		log.info("Initialized with delimiter: '" + delimiter + "'");
	}
	
	public URI compute() throws Exception {
		reader = new ForwardReader<Record>(inLocator);
		fieldDefs = reader.getRecordDefinitions();

		wfdefs = initializeSchema();
		writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[] { new GenericRecordDefinition(wfdefs) },
				RecordWriter.DefaultBufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor, 600, TimeUnit.SECONDS);

		sinkFile = File.createTempFile("grs2DS", ".tmp");
		
		new Thread(this).start();
		
		return writer.getLocator();
	}
	
	public void run() {
		Thread.currentThread().setName(GRS2Aggregator.class.getName());

		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = start, firstOutputStop = start;
		int rc = 0;

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(sinkFile, true));
//			long tstamp = System.currentTimeMillis();
			while (true) {
				try {
					if (reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))
						break;

					Record rec = reader.get(timeout, timeUnit);
					if (rec == null) {
						if (reader.getStatus() == Status.Open)
							log.warn("Producer has timed out");
						break;
					}

					if (rc == 0)
						firstInputStop = Calendar.getInstance().getTimeInMillis();

					for (int i = 0; i < rec.getFields().length; i++) {
						Field field = rec.getField(i);
						if (field instanceof StringField) {
							bw.write(((StringField) field).getPayload());
							if (i != rec.getFields().length - 1)
								bw.write(delimiter);
						}
					}
					bw.newLine();
					
					rc++;
					if (rc % 10000 == 0) {
						log.trace("persisted " + rc + " records so far. Continuing...");
//						long interval = System.currentTimeMillis() - tstamp;
//						if (interval > 1000)
//							log.warn("Last 1000 record took too much time(msec): " + interval);
//						tstamp = System.currentTimeMillis();
					}
				} catch (Exception e) {
					log.error("Could not retrieve and store the record. Continuing", e);
				}
			}
		} catch (Exception e) {
			log.error("Error during source retrieval. Closing", e);
		} finally {
			try {
				bw.close();
				reader.close();
			} catch (Exception ee) {
			}
		}

		List<Field> fieldList = new ArrayList<Field>();
		for (FieldDefinition field : wfdefs) {
			switch (LocalFieldName.valueOf(field.getName())) {
			case id:
				fieldList.add(new StringField(sinkFile.getName()));
				break;
			case bytestream:
				fieldList.add(new FileField(sinkFile));
				break;
			case mimeType:
				fieldList.add(new StringField(ContentTypeEvaluator.getContentType(sinkFile)));
				break;
			default:
				log.warn("Unexpected field: " + field.getName());
				break;
			}
		}

		GenericRecord rec = new GenericRecord();
		rec.setFields(fieldList.toArray(new Field[fieldList.size()]));

		log.debug("Returning next row with id: " + sinkFile);
		while(true) {
			try {
				if (!writer.importRecord(rec, timeout, timeUnit)) {
					if (writer.getStatus() == Status.Open)
						log.warn("Consumer has timed out");
					else
						break;
				} else
					break;
			} catch (GRS2Exception e) {
				log.error("" + e);
				return;
			}finally {
				firstOutputStop = Calendar.getInstance().getTimeInMillis();

				try {
					writer.close();
				} catch (GRS2WriterException e) {
				}
			}
		}
		
		long closeStop = Calendar.getInstance().getTimeInMillis();

		stats.timeToComplete(closeStop - start);
		stats.timeToFirstInput(firstInputStop - start);
		stats.timeToFirst(firstOutputStop - start);
		stats.producedResults(rc);
		stats.productionRate(((float) rc / (float) (closeStop - start)) * 1000);
		log.info("GRS2Aggregator OPERATOR:Produced first result in " + (firstOutputStop - start) + " milliseconds\n" + "Produced last result in "
				+ (closeStop - start) + " milliseconds\n" + "Produced " + rc + " results\n" + "Production rate was "
				+ (((float) rc / (float) (closeStop - start)) * 1000) + " records per second");
	}

	private FieldDefinition[] initializeSchema() {
		List<FieldDefinition> fieldDefsList = new ArrayList<FieldDefinition>();

		// if filterMask is null, use all fields
		if (filterMask == null) {
			filterMask = "[";
			for (LocalFieldName value : LocalFieldName.values())
				filterMask += value.name() + ", ";
			filterMask = filterMask.substring(0, filterMask.length() - 2);
			filterMask += "]";
		}

		// Filter mask consisted of references e.g [1, 2, 3]
		if (filterMask.replaceAll("[\\[\\],\\s]", "").matches("\\d*")) {
			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
				int index = Integer.parseInt(ref);

				if (index >= LocalFieldName.values().length) {
					log.warn("Filter mask out of range");
					continue;
				}

				if (LocalFieldName.values()[index].equals(LocalFieldName.bytestream))
					fieldDefsList.add(new FileFieldDefinition(LocalFieldName.values()[index].name()));
				else
					fieldDefsList.add(new StringFieldDefinition(LocalFieldName.values()[index].name()));
			}
		}
		// Filter mask consisted of names e.g [recordId, payload, contentType]
		else {
			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
				try {
					switch (LocalFieldName.valueOf(ref)) {
					case bytestream:
						FileFieldDefinition ffd = new FileFieldDefinition(LocalFieldName.valueOf(ref).name());
						ffd.setDeleteOnDispose(true); // XXX to be reconsidered
						fieldDefsList.add(ffd);
						break;
					default:
						fieldDefsList.add(new StringFieldDefinition(LocalFieldName.valueOf(ref).name()));
						break;
					}
				} catch (IllegalArgumentException e) {
					log.warn("Filter mask out of range for value: " + ref);
				}
			}
		}

		log.info("ResultSet schema that will be used: " + fieldDefsList);
		return fieldDefsList.toArray(new FieldDefinition[fieldDefsList.size()]);
	}
}
