package gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to perform an external search to a jdbc database creating a {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
 * with the produced results
 * 
 * @author UoA
 */
public class JdbcOp {
	/**
	 * The Logger the class uses
	 */
	private static Logger logger = LoggerFactory.getLogger(JdbcOp.class.getName());
	/**
	 * Statistics
	 */
	private StatsContainer stats;
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
	 * The default timeout used by the {@link IRecordWriter} and the {@link IRecordReader}. Currently set to 60.
	 */
	private static final long TimeoutDef = 60;
	/**
	 * The default timeout unit used by the {@link IRecordWriter} and the {@link IRecordReader}. The current default unit is seconds.
	 */
	private static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	
	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;
	
	/**
	 * Creates a new {@link JdbcOp} ith the default timeout and the default field names for object id and payload
	 * 
	 * @param stats statistics
	 */
	public JdbcOp(StatsContainer stats){
		this.stats=stats;
	}
	
	/**
	 * Creates a new {@link JdbcOp} with the default field names for object id and payload
	 * 
	 * @ param timeout The timeout which will be used by the writer
	 * @param timeUnit The time unit of the timeout
	 * @param stats Statistics
	 */
	public JdbcOp(long timeout, TimeUnit timeUnit, StatsContainer stats){
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.stats = stats;
	}
	
	/**
	 * Creates a new {@link JdbcOp} with configurable timeout and field names for object id and payload
	 * 
	 * @param objectIdFieldName The name of the {@link Field} which will hold the object id
	 * @param payloadFieldName The name of the {@link Field} which will hold the payload
	 * @param timeout The timeout which will be used by the writer
	 * @param timeUnit The time unit of the timeout
	 * @param stats Statistics
	 */
	public JdbcOp(String objectIdFieldName, String payloadFieldName, long timeout, TimeUnit timeUnit, StatsContainer stats) {
		this.objectIdFieldName = objectIdFieldName;
		this.payloadFieldName = payloadFieldName;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.stats = stats;
	}

	/**
	 * Perform the external search
	 * 
	 * @param query The query string
	 * @return The locator of the output
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI compute(String query) throws Exception{
		try{
			QueryParser parser=new QueryParser(query);
			QueryBridge qb = new QueryBridge(parser.getDriverName(), parser.getConnectionString(),20);
			ResultSet rs = qb.executeQuery(parser.getQuery());
			String[] columnNames = qb.getColumnNames();
			FieldDefinition[] fieldDefs = new FieldDefinition[]{new StringFieldDefinition(objectIdFieldName), new StringFieldDefinition(payloadFieldName)};
			IRecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[]{new GenericRecordDefinition(fieldDefs)}, 100,
					RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
			long writestart=Calendar.getInstance().getTimeInMillis();
			String UniqueDocId = parser.getQuery()+parser.getConnectionString()+parser.getDriverName();
			QueryJdbcWorker worker=new QueryJdbcWorker(rs, columnNames, UniqueDocId, writer, timeout, timeUnit, stats);
			worker.start();
			long writestop=Calendar.getInstance().getTimeInMillis();
			stats.timeToInitialize(writestop-writestart);
			//return worker.getWriter().getRSLocator(new RSResourceWSRFType());
			return writer.getLocator();
		}catch(Exception e){
			logger.error("Could not initialize jdbc operation. Throwing Exception",e);
			throw new Exception("Could not initialize jdbc operation");
		}
	}
}
