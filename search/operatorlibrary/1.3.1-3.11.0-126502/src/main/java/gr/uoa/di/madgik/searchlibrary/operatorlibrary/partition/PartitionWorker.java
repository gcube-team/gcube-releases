package gr.uoa.di.madgik.searchlibrary.operatorlibrary.partition;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartitionWorker<T extends Record> extends Thread {
	/**
	 * Logger used by the class
	 */

	private static Logger logger = LoggerFactory.getLogger(PartitionWorker.class.getName());

	/**
	 * The Writer to use
	 */
	private IRecordWriter<Record> writer = null;

	/**
	 * The reader to use
	 */
	private IRecordReader<T> reader = null;

	/**
	 * if partition will be applied by specified column or by a predifined number of partitions
	 */
	private boolean byColumn;
	
	/**
	 * The name of the field that partition will be applied
	 */
	private int partitionField;

	/**
	 * Statistics
	 */
	private StatsContainer stats = null;

	/**
	 * The timeout that will be used by {@link IRecordWriter} and the
	 * {@link IRecordReader} involved in the filter operation
	 */
	private long timeout;

	/**
	 * The timeout unit that will be used by {@link IRecordWriter} and the
	 * {@link IRecordReader} involved in the filter operation
	 */
	private TimeUnit timeUnit;

	/**
	 * Creates a new {@link PartitionWorker} which will perform the background
	 * filter operation
	 * 
	 * @param reader
	 *            The reader to consume record from
	 * @param writer
	 *            The writer which will be used for authoring
	 * @param byColumn
	 *            If partition will be applied by column or for a predefined
	 *            number of partitions
	 * @param partitionField
	 *            The name of the {@link Field} containing the payload on which
	 *            the partitioning will be applied
	 * @param stats
	 *            Statistics
	 * @param timeout
	 *            The timeout to be used both by the reader and the writer
	 * @param timeUnit
	 *            The unit of the timeout to be used
	 */
	public PartitionWorker(IRecordReader<T> reader, IRecordWriter<Record> writer, boolean byColumn, int partitionField, StatsContainer stats, long timeout,
			TimeUnit timeUnit) {
		this.reader = reader;
		this.writer = writer;
		this.byColumn = byColumn;
		this.partitionField = partitionField;
		this.stats = stats;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	/**
	 * Performs the filter operation
	 */
	public void run() {
		Thread.currentThread().setName(PartitionWorker.class.getName());

		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = 0, firstOutputStop = start;
		int rc = 0; int ln = 0;
		Partitioner partitioner = null;
		
		try {
			partitioner = new Partitioner(writer, reader.getRecordDefinitions(), timeout, timeUnit);
			int ro = 0;
			
			while (true) {
				try {
					if (reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))
						break;

					T rec = reader.get(timeout, timeUnit);
					if (rec == null) {
						if (reader.getStatus() == Status.Open)
							logger.warn("Producer has timed out");

						if (writer.getStatus() == Status.Close || writer.getStatus() == Status.Dispose) {
							logger.info("Producer has been closed.");
							break;
						}
						continue;
					}

					if (rc == 0)
						firstInputStop = Calendar.getInstance().getTimeInMillis();

					String fieldValue;
					if (byColumn) {
						try{
							Field key = rec.getField(partitionField);
							if(key instanceof StringField)
								fieldValue = ((StringField)key).getPayload();
							else
								throw new Exception("Refence: " + key.getFieldDefinition().getName() + " is not a String field");
						}catch(Exception e){
							logger.warn("Could not extract payload from record #" + ln++ + ". Continuing");
							continue;
						}
					} else
						fieldValue = String.valueOf(ro++ % partitionField);

					IRecordWriter<Record> rami = partitioner.getWriter(fieldValue);
					
					
					if (!rami.importRecord(rec, timeout, timeUnit)) {
						if (rami.getStatus() == Status.Open)
							logger.warn("Consumer has timed out");
						break;
					}

					rc++;
					if (rc == 1)
						firstOutputStop = Calendar.getInstance().getTimeInMillis();
				} catch (Exception e) {
					logger.error("Could not retrieve the record. Continuing", e);
				}
			}
		} catch (Exception e) {
			logger.error("Error during partitioning process. Closing", e);
		} finally {
			try {
				partitioner.closeAll();
				writer.close();
				reader.close();
			} catch (Exception ee) {
			}
		}

		long closeStop = Calendar.getInstance().getTimeInMillis();

		stats.timeToComplete(closeStop - start);
		stats.timeToFirstInput(firstInputStop - start);
		stats.timeToFirst(firstOutputStop - start);
		stats.producedResults(rc);
		stats.productionRate(((float) rc / (float) (closeStop - start)) * 1000);
		logger.info("PARTITION OPERATOR:Produced first result in " + (firstOutputStop - start) + " milliseconds\n" + "Produced last result in "
				+ (closeStop - start) + " milliseconds\n" + "Produced " + rc + " results\n" + "Production rate was "
				+ (((float) rc / (float) (closeStop - start)) * 1000) + " records per second");
	}
}
