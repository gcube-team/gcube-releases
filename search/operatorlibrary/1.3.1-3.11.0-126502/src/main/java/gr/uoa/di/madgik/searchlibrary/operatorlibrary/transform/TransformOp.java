package gr.uoa.di.madgik.searchlibrary.operatorlibrary.transform;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.Unary;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class performs an xslt transformation on the records that it receives as
 * input and produces a new set of records as output, each one of them
 * containing the transformed result
 * 
 * @author UoA
 */
public class TransformOp extends Unary {
	/**
	 * The Logger used by the class
	 */
	private Logger logger = LoggerFactory.getLogger(TransformOp.class.getName());

	/**
	 * The name of the payload field
	 */
	private String payloadFieldName = null;

	private String xslt;

	/**
	 * Creates a new {@link TransformOp} with the default timeout for the reader
	 * and the writer
	 * 
	 * @param inLocator
	 *            The locator of the input
	 * @param operatorParameters
	 *            operator parameters containing the name of the {@link Field}
	 *            which contains the payload to apply the transformation on
	 * @param stats
	 *            Statistics
	 * @throws Exception
	 */
	public TransformOp(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats) throws Exception {
		super(inLocator, operatorParameters, stats);

		payloadFieldName = operatorParameters.get("payloadFieldName");
		xslt = operatorParameters.get("xslt");
		if (payloadFieldName == null || xslt == null)
			throw new Exception("payload fields name not set in parameters");
	}

	/**
	 * Creates a new {@link TransformOp} with configurable timeout for the
	 * reader and the writer
	 * 
	 * @param inLocator
	 *            The locator of the input
	 * @param operatorParameters
	 *            operator parameters containing the name of the {@link Field}
	 *            which contains the payload to apply the transformation on
	 * @param stats
	 *            Statistics
	 * @param timeout
	 *            The timeout to be used both by the reader and the writer
	 * @param timeUnit
	 *            The time unit of the timeout used
	 * @throws Exception
	 */
	public TransformOp(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats, long timeout, TimeUnit timeUnit) throws Exception {
		super(inLocator, operatorParameters, stats, timeout, timeUnit);

		xslt = operatorParameters.get("xslt");
		payloadFieldName = operatorParameters.get("payloadFieldName");
		if (payloadFieldName == null || xslt == null)
			throw new Exception("payload fields name not set in parameters");

	}

	/**
	 * Initiates the transformation procedure
	 * 
	 * @param xslt
	 *            The xslt to be applied
	 * @return The {@link RSLocator} pointing to the produced
	 *         {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @throws Exception
	 *             An unrecoverable for the operation has occured
	 */
	public URI compute() throws Exception {
		try {
			long start = Calendar.getInstance().getTimeInMillis();
			IRecordReader<Record> reader = new ForwardReader<Record>(inLocator);
			IRecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), reader);

			TransformWorker<Record> worker = new TransformWorker<Record>(reader, writer, payloadFieldName, xslt, stats, timeout, timeUnit);
			worker.start();

			long readerstop = Calendar.getInstance().getTimeInMillis();
			stats.timeToInitialize(readerstop - start);
			return writer.getLocator();
		} catch (Exception e) {
			logger.error("Could not initialize transform operation. Throwing Exception", e);
			throw new Exception("Could not initialize transform operation");
		}
	}
}
