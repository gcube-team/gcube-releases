package gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink.predefined;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.FieldNaming.LocalFieldName;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink.DataSink;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.FileUtils;

import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DataSink} created for local file system. Contents are retrieved
 * through a gRS2 are stored in given local path.
 * 
 * @author john.gerbesiotis - DI NKUA
 * @param <T>
 *            extends {@link Record}
 * 
 */
public class PathDataSink<T extends Record> extends DataSink {
	private static Logger log = LoggerFactory.getLogger(PathDataSink.class.getName());

	private String sinkPath;

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
	public PathDataSink(URI inLocator, String output, Map<String, String> outputParameters, StatsContainer statsCont) throws Exception {
		super(inLocator, output, outputParameters, statsCont);

		sinkPath = this.output;
		if (!sinkPath.endsWith(File.separator))
			sinkPath += File.separator;

		reader = new ForwardReader<T>(inLocator);
		RecordDefinition defs = reader.getRecordDefinitions()[0];
		if (defs.getDefinition(LocalFieldName.id.name()) < 0 || defs.getDefinition(LocalFieldName.bytestream.name()) < 0 || defs.getDefinition(LocalFieldName.mimeType.name()) < 0) {
			log.error("LocalDataSink could not be initialized, cause corresponding fields are missing from resultSet");
			throw new Exception("LocalDataSink could not be initialized, cause corresponding fields are missing from resultSet");
		}

		if (!new File(sinkPath).exists()) {
			throw new Exception("path does not exist: " + sinkPath);
		}
		
		log.info("Ininializing local data sink at: " + sinkPath);

	}

	public void run() {
		Thread.currentThread().setName(PathDataSink.class.getName());
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
						if (reader.getStatus() == Status.Open)
							log.warn("Producer has timed out");
						else
							break;
					}

					if (rc == 0)
						firstInputStop = Calendar.getInstance().getTimeInMillis();

					String recordId = null;
					File payload = null;
					String contentType = "";
					try {
						Field recIdField = rec.getField(LocalFieldName.id.name());
						if (recIdField instanceof StringField)
							recordId = ((StringField) recIdField).getPayload();
						Field payloadField = rec.getField(LocalFieldName.bytestream.name());
						if (payloadField instanceof FileField)
							payload = ((FileField) payloadField).getPayload();
						Field contentTypeField = rec.getField(LocalFieldName.mimeType.name());
						if (contentTypeField instanceof StringField)
							contentType = ((StringField) contentTypeField).getPayload();
					} catch (Exception e) {
						log.warn("Could not extract payload from record #" + rc + ". Continuing");
						continue;
					}

					String filename = recordId.hashCode() + contentType.replaceAll("/", ".");
					log.info("Trying to persist file " + sinkPath + filename);
					FileUtils.copyFile(payload, new File(sinkPath + filename));

					rc++;
					if (rc == 1)
						firstOutputStop = Calendar.getInstance().getTimeInMillis();
				} catch (GRS2ReaderException e) {
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
		return sinkPath;
	}
}
