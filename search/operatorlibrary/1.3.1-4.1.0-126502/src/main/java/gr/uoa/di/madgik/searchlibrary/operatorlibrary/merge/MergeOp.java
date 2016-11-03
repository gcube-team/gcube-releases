package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

import org.gcube.search.datafusion.DataFusion;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to create a merged ResultSet
 * output from the contents of an array of input ResultSet
 * 
 * @author UoA
 */
public class MergeOp {

	/**
	 * The default operation mode. Currently set to {@link OperationMode#FIFO}
	 */
	public static final OperationMode OperationModeDef = OperationMode.FIFO;
	/**
	 * The default timeout used by the {@link IRecordWriter} and all {@link IRecordReader}s. Currently set to 60.
	 */
	public static final long TimeoutDef = 180;
	/**
	 * The default timeout unit used by the {@link RecordWriter} and all {@link IRecordReader}s. The current default unit is seconds.
	 */
	public static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	/**
	 * The default capacity of the {@link RecordWriter}s and, if applicable, of all {@link IRecordReader}s' buffers
	 */
	public static final int BufferCapacityDef = 100;
	/**
	 * The default name of the field which contains the record rank. Used if {@link MergeOp#operationMode} is set to {@link OperationMode#Sort}
	 */
	public static final String RankFieldNameDef = "rank";
	
	/**
	 * The Logger the class uses
	 */
	private Logger logger = LoggerFactory.getLogger(MergeOp.class.getName());

	/**
	 * The locators of the inputs
	 */
	private URI[] locators = null;
	
	/**
	 * The unique ID of this operator invocation
	 */
	private String uid = UUID.randomUUID().toString();
	
	/**
	 * statistics
	 */
	private StatsContainer stats=null;
	
	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;
	private String rankFieldName = RankFieldNameDef;
	
	private String query = null;
	
	private int bufferCapacity= BufferCapacityDef;
	
	private RecordDefinition[] recordDefinitions;
	private int[] recordDefinitionOffsets = null;
	
	private OperationMode operationMode = OperationModeDef;
	/**
	 * Creates a new {@link MergeOp} with the default operation mode and the default timeout used both for readers and the writer
	 * 
	 * @param locators The locators of the inputs that will be merged
	 * @param stats Statistics
	 */
	public MergeOp(URI[] locators, StatsContainer stats){
		this.locators = locators;
		this.stats = stats;
	}
	
	/**
	 * Creates a new {@link MergeOp} with configurable operation mode and the default timeout used both for readers and the writer
	 * 
	 * @param locators The locators of the inputs that will be merged
	 * @param operationMode The operation mode. One of {@link OperationMode#FIFO} and {@link OperationMode#FirstAvailable}
	 * @param stats Statistics
	 */
	public MergeOp(URI[] locators, OperationMode operationMode,  StatsContainer stats) {
		this.locators = locators;
		this.stats = stats;
		this.operationMode = operationMode;
	}
	
	/**
	 * Creates a new {@link MergeOp} with configurable operation mode and timeout
	 * 
	 * @param locators The locators of the inputs that will be merged
	 * @param operationMode The operation mode. One of {@link OperationMode#FIFO} and {@link OperationMode#FirstAvailable}
	 * @param timeout The timeout that will be used by the {@link RecordWriter} and all {@link ForwardReader}s
	 * @param timeUnit The timeout unit that will be used by the {@link RecordWriter} and all {@link ForwardReader}s
	 * @param stats Statistics
	 */
	public MergeOp(URI[] locators, OperationMode operationMode, long timeout, TimeUnit timeUnit, StatsContainer stats) {
		this(locators, operationMode, stats);
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	/**
	 * Creates a new {@link MergeOp} with configurable operation mode and timeout
	 * 
	 * @param locators The locators of the inputs that will be merged
	 * @param operationMode The operation mode. One of {@link OperationMode#FIFO} and {@link OperationMode#FirstAvailable}
	 * @param timeout The timeout that will be used by the {@link RecordWriter} and all {@link ForwardReader}s
	 * @param timeUnit The timeout unit that will be used by the {@link RecordWriter} and all {@link ForwardReader}s
	 * @param bufferCapacity The capacity of the buffer which will be used by the {@link RecordWriter} and all {@link ForwardReader}s (if applicable)
	 * @param stats Statistics
	 */
	public MergeOp(URI[] locators, OperationMode operationMode, long timeout, TimeUnit timeUnit, int bufferCapacity, StatsContainer stats) {
		this(locators, operationMode, stats);
		this.bufferCapacity = bufferCapacity;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	/**
	 * Sets the name of the field which contains the record rank
	 * Used only if {@link MergeOp#operationMode} is set to {@link OperationMode#Sort}
	 * 
	 * @param rankFieldName The name of the field which contains the record rank
	 */
	public void setRankFieldName(String rankFieldName) {
		this.rankFieldName = rankFieldName;
	}
	
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	/**
	 * Retrieves the record definitions from all {@link ForwardReader}s used
	 * 
	 * @param readers The readers from which the definitions will be retrieved
	 * @return An array of all retrieved {@link RecordDefinition}s that can be used to instantiate a {@link RecordWriter}
	 * @throws GRS2ReaderException An gRS2 error has occurred while retrieving the record definitions
	 */
	private void getDefinitions(Vector<ReaderHolder> readers) throws GRS2ReaderException {
		this.recordDefinitionOffsets = new int[readers.size()];
		ArrayList<RecordDefinition[]> definitionsList = new ArrayList<RecordDefinition[]>();
		ArrayList<RecordDefinition> definitions=new ArrayList<RecordDefinition>();
		this.recordDefinitionOffsets[0] = 0;
		for(int i = 0; i < readers.size(); i++)
		{
			if(readers.get(i).getReader() == null)
				continue;
			logger.trace(this.uid + ": Reading record definitions from reader #" + i);
			RecordDefinition[] defs = readers.get(i).getReader().getRecordDefinitions();
			definitions.addAll(Arrays.asList(defs));
			definitionsList.add(defs);
			if(i != 0)
				this.recordDefinitionOffsets[i] = this.recordDefinitionOffsets[i-1] + definitionsList.get(i-1).length;
		}
		this.recordDefinitions = definitions.toArray(new RecordDefinition[0]);
	}
	
	/**
	 * Performs the merging operation
	 * 
	 * @return The locator of the merged result set
	 * @throws Exception An unrecoverable for the operation error ocurred
	 */
	public URI compute() throws Exception{
		try{
			
			long mergestart=Calendar.getInstance().getTimeInMillis();
			
			logger.info("Operational mode set : " + operationMode);
			
			
			logger.info("Operational equal fusion ? " + operationMode.equals(OperationMode.Fusion));
			logger.info(operationMode + " = " + operationMode.equals(OperationMode.Fusion));
			
			
			if (operationMode.equals(OperationMode.Fusion)){
				
				logger.info("Executing data fusion!!!");
				
				
				DataFusion df = new DataFusion(locators, query);
				URI writerLocator = df.operate();
				
				long mergestop=Calendar.getInstance().getTimeInMillis();
				stats.timeToInitialize(mergestop-mergestart);
				logger.info("MERGE OPERATOR " + this.uid + " TOOK "+(mergestop-mergestart));
				logger.trace(this.uid + ": Returning " + writerLocator);
				return writerLocator;
			} else {
				
				logger.info("Executing merge!!!");
				
				Vector<ReaderHolder> readers=new Vector<ReaderHolder>();
				readers.setSize(locators.length);
				ReaderInit []initT=new ReaderInit[locators.length];
				long initstart=Calendar.getInstance().getTimeInMillis();
				for(int i=0;i<locators.length;i+=1){
					readers.set(i,new ReaderHolder());
					initT[i]=new ReaderInit(readers,i,locators[i], operationMode, bufferCapacity, uid);
					initT[i].start();
				}
				
				long initstop=Calendar.getInstance().getTimeInMillis();
				MergeWorker worker = new MergeWorker(readers, stats, operationMode, timeout, timeUnit, uid);
				worker.start();
				
				for(int i = 0; i < readers.size(); i++)
					initT[i].join();
	
				getDefinitions(readers);
				RecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), this.recordDefinitions, this.bufferCapacity, 
						RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
				
				worker.setWriter(writer);
				worker.setRecordDefinitionOffsets(this.recordDefinitionOffsets);
				Object synchWorker = worker.getWriterInitSyncObject();
				synchronized(synchWorker) {
					synchWorker.notify();
				}
				
				long mergestop=Calendar.getInstance().getTimeInMillis();
				stats.timeToInitialize(initstop-initstart);
				logger.info("MERGE OPERATOR " + this.uid + " INIT TOOK "+(initstop-initstart));
				logger.info("MERGE OPERATOR " + this.uid + " TOOK "+(mergestop-mergestart));
				logger.trace(this.uid + ": Returning " + writer.getLocator());
				return writer.getLocator();
			}
		}catch(Exception e){
			logger.error("Could not start background process of merging for operator " + this.uid + ". Throwing Exception", e);
			throw new Exception("Could not start background process of merging");
		}
	}
}
