package gr.uoa.di.madgik.searchlibrary.operatorlibrary.transform;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptWorker<T extends Record> extends Thread {

	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(ScriptWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private IRecordWriter<Record> writer;
	/**
	 * The reader to use
	 */
	private IRecordReader<T> reader = null;

	/**
	 * The script to be used for transformation
	 */
	private String script = null;

	private String schema = null;

	private final CommandExecutor transformer;
	
	/**
	 * Statistics
	 */
	private StatsContainer stats = null;

	/**
	 * The timeout that will be used by {@link IRecordWriter} and the
	 * {@link IRecordReader} involved in the transform operation
	 */
	private long timeout;
	/**
	 * The timeout unit that will be used by {@link IRecordWriter} and the
	 * {@link IRecordReader} involved in the transform operation
	 */
	private TimeUnit timeUnit;

	private CountDownLatch latch = new CountDownLatch(1);
			
	/**
	 * 
	 * @param reader
	 *            The {@link IRecordReader} to consume records from
	 * @param writer
	 *            The {@link IRecordWriter} which will be used to write the
	 *            output
	 * @param payloadFieldName
	 *            The name of the {@link Field} containing the payload on which
	 *            the transformation will be applied
	 * @param script
	 *            The script to apply on each record field
	 * @param schema schema that describes new result set
	 * @param stats
	 *            Statistics
	 * @param timeout
	 *            The timeout which will be used both by the reader and the
	 *            writer
	 * @param timeUnit
	 *            The unit of the timeout which will be used
	 * @throws Exception if script is not initialised right
	 */
	public ScriptWorker(IRecordReader<T> reader, String script, String schema, StatsContainer stats, long timeout, TimeUnit timeUnit) throws Exception {
		this.reader = reader;
		this.script = script;
		this.schema = schema;
		this.stats = stats;
		this.timeout = timeout;
		this.timeUnit = timeUnit;

		if (!schema.isEmpty()){
			ArrayList<StringFieldDefinition> fieldDefs = new ArrayList<StringFieldDefinition>();
			for (int i = 0; i < schema.substring(1, schema.length() - 1).split(",").length; i++) {
				String fieldName = schema.substring(1, schema.length() - 1).split(",")[i].trim();
				
				fieldDefs.add(new StringFieldDefinition(fieldName));
			}
			
			FieldDefinition[] fieldDefsArray = fieldDefs.toArray(new FieldDefinition[fieldDefs.size()]);
			writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[] { new GenericRecordDefinition(fieldDefsArray) },
					RecordWriter.DefaultBufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor,
					timeout, timeUnit);
			latch.countDown();
		}
		
		transformer = new CommandExecutor(script);
		transformer.execute();
		
	}

	/**
	 * Performs the transform operation
	 */
	public void run() {
		Thread.currentThread().setName(ScriptWorker.class.getName());

		int rc = 0;
		long firstInputStop = 0, firstOutputStop = Calendar.getInstance().getTimeInMillis();
		long start = Calendar.getInstance().getTimeInMillis();

		// Reader part
		new Thread() {
			public void run() {
				Thread.currentThread().setName("Script worker transformer");

				int inrc = 0;
				while (true) {
					try {
						if (reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))
							break;

						T rec = reader.get(timeout, timeUnit);
						if (rec == null) {
							if (reader.getStatus() == Status.Open)
								logger.warn("Producer has timed out");
							break;
						}

						String payload;
						StringBuilder sbuilder = new StringBuilder();
						try {
							for (Field key : rec.getFields())
								if (key instanceof StringField)
									sbuilder.append(((StringField) key).getPayload() + "\t");

							if (sbuilder.length() > 0)
								sbuilder.deleteCharAt(sbuilder.length() - 1);
							
							payload = sbuilder.toString();
						} catch (Exception e) {
							logger.warn("Could not extract payload from record #" + inrc + ". Continuing");
							continue;
						}

						if (payload != null) {
							try {
								transformer.transform(payload);
							}catch (IOException e) {
								try {
									logger.error("Could not make transformation. returned code: " + transformer.exitValue() + " with payload: " + payload, e);
								} catch (Exception e2) {
									logger.error("Could not transform payload: " + payload);
								}
								try {
									reader.close();
									writer.close();
								} catch (Exception e1) {}
							}
						}
						inrc++;
						if (inrc % 100000 == 0)
							logger.trace("transformed " + inrc + " records. Continuing...");
					} catch (Exception e) {
						logger.warn("Could not extract payload from record #" + inrc + ". Continuing", e);
					}
				}
				try {
					transformer.finishedWriting();
				} catch (IOException e) {
				}
			}
		}.start();
		
		// Writer part
		try {
			BufferedReader br = transformer.getBufferedReader();

			while (true) {
				try {
					if (rc == 0)
						firstInputStop = Calendar.getInstance().getTimeInMillis();

					if (writer != null && (writer.getStatus() == Status.Close || writer.getStatus() == Status.Dispose)) {
						logger.info("Consumer side stopped consumption. Stopping.");
						break;
					}

					String line;
					GenericRecord rec = new GenericRecord();
					List<Field> fieldList = new ArrayList<Field>();
					if ((line = br.readLine()) != null) {
						for (String tok : line.split("\t"))
							fieldList.add(new StringField(tok));

						rec.setFields(fieldList.toArray(new Field[fieldList.size()]));
					} else
						break;

					if (writer == null) {
						ArrayList<StringFieldDefinition> fieldDefs = new ArrayList<StringFieldDefinition>();
						for (int i = 0; i < rec.getFields().length; i++) {
							String fieldName = null;
							try {
								fieldName = "_col" + i;
							} catch(Exception e) {
								fieldName = Integer.toString(i);
							}
							
							fieldDefs.add(new StringFieldDefinition(fieldName));
						}
						
						FieldDefinition[] fieldDefsArray = fieldDefs.toArray(new FieldDefinition[fieldDefs.size()]);
						writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[] { new GenericRecordDefinition(fieldDefsArray) },
								RecordWriter.DefaultBufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor,
								timeout, timeUnit);
						latch.countDown();
					}
					if (!writer.importRecord(rec, timeout, timeUnit)) {
						if (writer.getStatus() == Status.Open)
							logger.warn("Consumer has timed out");
						break;
					}

					rc++;
					if (rc == 1)
						firstOutputStop = Calendar.getInstance().getTimeInMillis();
				} catch (Exception e) {
					logger.error("Could not process record. Continuing", e);
				}
			}
			
			logger.info("Finished with code: " + transformer.waitFor());
		} catch (Exception e) {
			logger.error("Error during background transformation. Closing", e);
		} finally {
			try {
				writer.close();
				reader.close();
				transformer.finishedReading();
			} catch (Exception ee) {
			}
		}
		long closestop = Calendar.getInstance().getTimeInMillis();
		stats.timeToComplete(closestop - start);
		stats.timeToFirstInput(firstInputStop - start);
		stats.timeToFirst(firstOutputStop - start);
		stats.producedResults(rc);
		stats.productionRate(((float) rc / (float) (closestop - start)) * 1000);
		logger.info("SCRIPT OPERATOR:" + "Produced first result in " + (firstOutputStop - start) + " milliseconds\n" + "Produced last result in "
				+ (closestop - start) + " milliseconds\n" + "Produced " + rc + " results\n" + "Production rate was "
				+ (((float) rc / (float) (closestop - start)) * 1000) + " records per second");
	}
	
	protected URI getLocator() throws InterruptedException, GRS2WriterException {
		if (writer == null)
			latch.await(60, TimeUnit.SECONDS);
		return writer.getLocator();
	}
}
