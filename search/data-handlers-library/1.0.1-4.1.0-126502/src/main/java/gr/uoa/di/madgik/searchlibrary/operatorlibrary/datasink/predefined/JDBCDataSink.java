package gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink.predefined;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink.DataSink;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc.BatchQuery;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc.QueryParser;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DataSink} created for a database. Given a sql query, contents
 * retrieved through a gRS2 are stored in given db system.
 * 
 * @author john.gerbesiotis - DI NKUA
 * @param <T>
 *            extends {@link Record}
 * 
 */
public class JDBCDataSink<T extends Record> extends DataSink {
	private static Logger log = LoggerFactory.getLogger(JDBCDataSink.class.getName());

	private String query;
	private BatchQuery bq;
	
	/**
	 * The reader to use
	 */
	private IRecordReader<T> reader = null;

	/**
	 * @param inLocator
	 *            input locator of the consuming result set
	 * @param output
	 *            output value of the {@link DataSink}
	 * @param outputParameters
	 *            output parameters of the {@link DataSink}
	 * @param statsCont
	 *            statistics container
	 * @throws Exception
	 *             If the initialization of the {@link DataSink} fails
	 */
	public JDBCDataSink(URI inLocator, String output, Map<String, String> outputParameters, StatsContainer statsCont) throws Exception {
		super(inLocator, output, outputParameters, statsCont);

		query = this.output;
		QueryParser parser = new QueryParser(new URI(query));
		bq = new BatchQuery(parser.getDriverName(), parser.getConnectionString(), parser.getQuery(), 1000);
		
		reader = new ForwardReader<T>(inLocator);

		log.info("Ininializing local data sink at: " + parser.getConnectionString());

	}

	public void run() {
		Thread.currentThread().setName(JDBCDataSink.class.getName());

		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = start, firstOutputStop = start;
		int rc = 0;

		try {
			while (true) {
				try {
					if (reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))
						break;

					T rec = reader.get(timeout, timeUnit);
					if (rec == null) {
						if (reader.getStatus() == Status.Open) {
							log.warn("Producer has timed out");
							continue;
						} else
							break;
					}

					if (rc == 0)
						firstInputStop = Calendar.getInstance().getTimeInMillis();

					try {
						List<String> attrs = new ArrayList<String>();
						for (Field field : rec.getFields()) {
							if (field instanceof StringField)
								attrs.add(((StringField) field).getPayload());
						}
						bq.addBatch(attrs.toArray(new String[attrs.size()]));
					} catch (SQLException e) {
						log.warn("Could not extract payload from record #" + rc + ". Continuing");
						log.warn("nextException: ", e.getNextException());
						continue;
					}

					rc++;
					
					if (rc % 10000 == 0)
						log.debug("Persisted " + rc + " records");
					if (rc == 1)
						firstOutputStop = Calendar.getInstance().getTimeInMillis();
				} catch (Exception e) {
					log.error("Could not retrieve and store the record. Continuing", e);
				}
			}
		} catch (Exception e) {
			log.error("Error during datasink retrieval. Closing", e);
		} finally {
			try {
				reader.close();
			} catch (Exception ee) {
			}
		}

		try {
			log.info("Trying to execute final batch");
			bq.executeBatch();
		} catch (SQLException e) {
			log.error("Batch execution failed", e);
			log.error("nextException: ", e.getNextException());
		}

		long closeStop = Calendar.getInstance().getTimeInMillis();

		stats.timeToComplete(closeStop - start);
		stats.timeToFirstInput(firstInputStop - start);
		stats.timeToFirst(firstOutputStop - start);
		stats.producedResults(rc);
		stats.productionRate(((float) rc / (float) (closeStop - start)) * 1000);
		log.info("DATASINK OPERATOR:Produced first result in " + (firstOutputStop - start) + " milliseconds\n" + "Produced last result in "
				+ (closeStop - start) + " milliseconds\n" + "Produced " + rc + " results\n" + "Production rate was "
				+ (((float) rc / (float) (closeStop - start)) * 1000) + " records per second");
	}

	@Override
	public String getOutput() {
		return output;
	}
}
