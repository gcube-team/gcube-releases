package gr.uoa.di.madgik.searchlibrary.operatorlibrary.indexfuse;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to perform a fuse operation on its input {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}, 
 * that represent results produced from full text index lookup operations on a set of collections, and produces an output 
 * {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet} holding the joined results. For each collection the input
 * comes from metadata and content collections that correspond to that collection. The output contains 0 or 1 result 
 * for each OID of the input collections.
 * 
 * @author UoA
 */
public class IndexFuseOp {
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(IndexFuseOp.class.getName());

	/**
	 * The locators of the results for the content collections
	 */
	private URI[] contentLocators=null;
	/**
	 * The locators of the results for the metadata collections
	 */
	private URI[][] metaLocators=null;
	/**
	 * Container of statistics
	 */
	private StatsContainer stats;
	/**
	 * weight for content - default 0.5
	 */
	private double contentWeight = 0.5;
	/**
	 * weight for metadata - default 0.5
	 */
	private double metaWeight = 0.5;
	/**
	 * the metadata ColIDs for tagging the results
	 */
	private String[] colIDs = null;	
	/**
	 * The default name of the {@link Field} the object id will be stored to
	 */
	public static String ObjectIdFieldNameDef = "objId";
	/**
	 * The default name of the {@link Field} the collection will be stored to
	 */
	public static String CollectionFieldNameDef = "collId";
	/**
	 * The default name of the {@link Field} the rank will be stored to
	 */
	public static String RankFieldNameDef = "rank";
	/**
	 * The name of the {@link Field} the object id will be stored to
	 */
	private String objectIdFieldName = ObjectIdFieldNameDef;
	/**
	 * The name of the {@link Field} the collection will be stored to
	 */
	private String collectionFieldName = CollectionFieldNameDef;
	/**
	 * The name of the {@link Field} the rank will be stored to
	 */
	private String rankFieldName = RankFieldNameDef;
	/**
	 * The default timeout
	 */
	private static long TimeoutDef = 60;
	/**
	 * The default time unit
	 */
	private static TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	/**
	 * The timeout that will be used both by the two {@link RandomReader}s and the {@link IRecordWriter} involved in the join operation
	 */
	private long timeout = TimeoutDef;
	/**
	 * The time unit of the timeout that will be used
	 */
	private TimeUnit timeUnit = TimeUnitDef;
	/**
	 * Creates a new {@link IndexFuseOp} with the default field names and the default timeout
	 * 
	 * @param contentLocators The content collection inputs {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @param metaLocators The metadata collection inputs {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @param colIDs
	 * @param stats statistics
	 */
	public IndexFuseOp(URI[] contentLocators,URI[][] metaLocators,String[] colIDs,StatsContainer stats) throws Exception{
		if(contentLocators.length != metaLocators.length)
			throw new Exception("RS locators for content and metadata don't have the same length. This length defines the number of collections");
		if(contentLocators.length != colIDs.length)
			throw new Exception("RS locators array for content and colIDs array don't have the same length. This length defines the number of collections");
		this.colIDs = colIDs;
		this.contentLocators = contentLocators;
		this.metaLocators = metaLocators;
		this.stats=stats;
	}
	
	/**
	 * Creates a new {@link IndexFuseOp} with configurable field names and the default timeout
	 * 
	 * @param contentLocators The content collection inputs {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @param metaLocators The metadata collection inputs {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @param colIDs
	 * @param objectIdFieldName The name of the {@link Field} the object id will be stored to
	 * @param collectionFieldName The name of the {@link Field} the collection will be stored to
	 * @param rankFieldName The name of the {@link Field} the rank will be stored to
	 * @param stats statistics
	 */
	public IndexFuseOp(URI[] contentLocators,URI[][] metaLocators,String[] colIDs, String objectIdFieldName, String collectionFieldName, String rankFieldName, StatsContainer stats) throws Exception{
		if(contentLocators.length != metaLocators.length)
			throw new Exception("RS locators for content and metadata don't have the same length. This length defines the number of collections");
		if(contentLocators.length != colIDs.length)
			throw new Exception("RS locators array for content and colIDs array don't have the same length. This length defines the number of collections");
		this.colIDs = colIDs;
		this.contentLocators = contentLocators;
		this.metaLocators = metaLocators;
		this.objectIdFieldName = objectIdFieldName;
		this.collectionFieldName = collectionFieldName;
		this.rankFieldName = rankFieldName;
		this.stats=stats;
	}
	

	/**
	 * Creates a new {@link IndexFuseOp} with configurable field names and timeout
	 * 
	 * @param contentLocators The content collection inputs {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @param metaLocators The metadata collection inputs {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @param colIDs
	 * @param objectIdFieldName The name of the {@link Field} the object id will be stored to
	 * @param collectionFieldName The name of the {@link Field} the collection will be stored to
	 * @param rankFieldName The name of the {@link Field} the rank will be stored to
	 * @param timeout The timeout that will be used by all {@link IRecordReader}s and the {@link IRecordWriter}
	 * @param timeUnit The time unit of the timeout
	 * @param stats statistics
	 */
	public IndexFuseOp(URI[] contentLocators,URI[][] metaLocators,String[] colIDs, String objectIdFieldName, String collectionFieldName, String rankFieldName, long timeout, TimeUnit timeUnit, StatsContainer stats) throws Exception{
		if(contentLocators.length != metaLocators.length)
			throw new Exception("RS locators for content and metadata don't have the same length. This length defines the number of collections");
		if(contentLocators.length != colIDs.length)
			throw new Exception("RS locators array for content and colIDs array don't have the same length. This length defines the number of collections");
		this.colIDs = colIDs;
		this.contentLocators = contentLocators;
		this.metaLocators = metaLocators;
		this.objectIdFieldName = objectIdFieldName;
		this.collectionFieldName = collectionFieldName;
		this.rankFieldName = rankFieldName;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.stats=stats;
	}
	/**
	 * Creates a new {@link IndexFuseOp}
	 * 
	 * @param contentLocators The content collection inputs {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @param metaLocators The metadata collection inputs {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @param stats statistics
	 * @param factory the factory to use
	 */
//	public IndexFuseOp(IProxyLocator[] contentLocators,IProxyLocator[][] metaLocators,String[] colIDs,StatsContainer stats,RSWriterFactory factory) throws Exception{
//		if(contentLocators.length != metaLocators.length)
//			throw new Exception("RS locators for content and metadata don't have the same length. This length defines the number of collections");
//		if(contentLocators.length != colIDs.length)
//			throw new Exception("RS locators array for content and colIDs array don't have the same length. This length defines the number of collections");
//		this.colIDs = colIDs;
//		this.contentLocators = contentLocators;
//		this.metaLocators = metaLocators;
//		this.stats=stats;
//		this.factory=factory;
//	}
	
	/**
	 * Performs the fuse operation
	 * @return The locator of the results
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI compute() throws Exception{
		try{
			long startInit=Calendar.getInstance().getTimeInMillis();
			ForwardReader[] contentReaders = new ForwardReader[contentLocators.length];
			
			//RSXMLIterator[] contentIters = new RSXMLIterator[contentLocators.length];
			logger.info("Creating iterators for the locators of the ResultSets");
			//create iterators for each content locator
			for(int i = 0; i<contentLocators.length; i++)
			{
				if(contentLocators[i] == null){
					logger.info("There is no content collection given for the " + i + "-th collection");
					contentReaders[i] = null;
				}else{
					contentReaders[i] = new ForwardReader<Record>(contentLocators[i]);
				//	RSXMLReader reader = RSXMLReader.getRSXMLReader(contentLocators[i]).makeLocal(new RSResourceLocalType());
					//contentIters[i] = reader.getRSIterator();
				}
			}
			//RSXMLIterator[][] metaIters = new RSXMLIterator[metaLocators.length][];
			ForwardReader[][] metaReaders = new ForwardReader[metaLocators.length][];
			//create iterators for each metadata locator
			for(int i = 0; i<metaLocators.length; i++)
			{
				if(metaLocators[i] == null){
					logger.info("There are no metadata collections given for the " + i + "-th collection");
					metaReaders[i] = null;
				}else{
					//metaIters[i] = new RSXMLIterator[metaLocators[i].length];
					metaReaders[i] = new ForwardReader[metaLocators[i].length];
					for(int j = 0; j<metaLocators[i].length; j++)
					{
						if(metaLocators[i][j] == null)
							throw new Exception("The locator for the " + j + "-th metadata collection of the " + i + "-th collection is null.");
						else
							//metaIters[i][j] = RSXMLReader.getRSXMLReader(metaLocators[i][j]).makeLocal(new RSResourceLocalType()).getRSIterator();
							metaReaders[i][j] = new ForwardReader<Record>(metaLocators[i][j]);
					}
				}
			}		
			stats.timeToInitialize(Calendar.getInstance().getTimeInMillis()-startInit);

			RecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), 
					new RecordDefinition[]{new GenericRecordDefinition(
							new FieldDefinition[] {
					           new StringFieldDefinition(this.objectIdFieldName), new StringFieldDefinition(this.collectionFieldName), new StringFieldDefinition(this.rankFieldName)
							})},
					100, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
		
			
//TODO provide weights based on a generic resource 
			IndexFuseWorker worker=new IndexFuseWorker(writer,contentReaders,metaReaders,colIDs,contentWeight,metaWeight,
					objectIdFieldName, collectionFieldName, rankFieldName, timeout, timeUnit, stats);
			logger.info("Starting worker to perform the index fuse operation");
			worker.start();
			return writer.getLocator();
		}catch(Exception e){
			logger.error("Could not initialize index fuse operation. Throwing Exception",e);
			throw new Exception("Could not initialize index fuse operation");
		}
	}
}
