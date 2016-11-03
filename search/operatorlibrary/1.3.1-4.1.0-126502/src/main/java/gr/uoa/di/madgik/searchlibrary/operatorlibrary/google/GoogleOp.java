package gr.uoa.di.madgik.searchlibrary.operatorlibrary.google;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.GRS2RecordSerializationException;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.URI;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to perform external lookups to the Google engine and create a
 * {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet} with the results
 * 
 * @author UoA
 */
public class GoogleOp {
	/**
	 * The Logger this class uses
	 */
	private static Logger logger = LoggerFactory.getLogger(GoogleOp.class.getName());
	/**
	 * Statistics
	 */
	private static StatsContainer stats = null;
	
	/**
	 * The default name for the {@link Field} of the object id
	 */
	public static String ObjectIdFieldNameDef = "objectId";
	/**
	 * The default name for the {@link Field} of the payload
	 */
	public static String PayloadFieldNameDef = "payload";
	/**
	 * The name of the {@link Field} which will hold the object id
	 */
	private String objectIdFieldName = ObjectIdFieldNameDef;
	/**
	 * The name of the {@link Field} which will hold the payload
	 */
	private String payloadFieldName = PayloadFieldNameDef;
	/**
	 * The default timeout used by the {@link IRecordWriter}. Currently set to 60.
	 */
	public static final long TimeoutDef = 60;
	/**
	 * The default timeout unit used by the {@link RecordWriter}. The current default unit is seconds.
	 */
	public static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	
	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;
	
	
	/**
	 * Creates a new {@link GoogleOp} with the default timeout and the default field names for object id and payload
	 * 
	 * @param stats Statistics
	 */
	public GoogleOp(StatsContainer stats) {
		this.stats = stats;
	}
	
	/**
	 * Creates a new {@link GoogleOp} with the default field names for object id and payload
	 * 
	 * @ param timeout The timeout which will be used by the writer
	 * @param timeUnit The time unit of the timeout
	 * @param stats Statistics
	 */
	public GoogleOp(long timeout, TimeUnit timeUnit, StatsContainer stats){
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.stats = stats;
	}
	
	/**
	 * Creates a new {@link GoogleOp} with configurable timeout and field names for object id and payload
	 * 
	 * @param objectIdFieldName The name of the {@link Field} which will hold the object id
	 * @param payloadFieldName The name of the {@link Field} which will hold the payload
	 * @param timeout The timeout which will be used by the writer
	 * @param timeUnit The time unit of the timeout
	 * @param stats Statistics
	 */
	public GoogleOp(String objectIdFieldName, String payloadFieldName, long timeout, TimeUnit timeUnit, StatsContainer stats) {
		this.objectIdFieldName = objectIdFieldName;
		this.payloadFieldName = payloadFieldName;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.stats = stats;
	}
	
	/**
	 * Performs the external lookup
	 * 
	 * @param type The type of resource to create
	 * @param query Teh query string
	 * @param resNo number of results
	 * @return The output {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public URI compute(String query, int resNo) throws Exception{
		try{
			long startTime = Calendar.getInstance().getTimeInMillis();
			FieldDefinition[] fieldDefs = new FieldDefinition[]{new StringFieldDefinition(objectIdFieldName), new StringFieldDefinition(payloadFieldName)};
			IRecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[]{new GenericRecordDefinition(fieldDefs)}, 100,
					RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
			
			QueryGoogleWorker worker=new QueryGoogleWorker(query, resNo, writer, timeout, timeUnit, stats);
			stats.timeToInitialize(Calendar.getInstance().getTimeInMillis() - startTime);
			worker.start();
			return writer.getLocator();
		}catch(Exception e){
			logger.error("Could not initialize google operation. Throwing Exception",e);
			throw new Exception("Could not initialize google operation");
		}
	}
}
