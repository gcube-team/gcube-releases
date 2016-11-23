package gr.uoa.di.madgik.searchlibrary.operatorlibrary.duplicateeliminatoroperator;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.OperatorLibraryConstants;

import java.net.URI;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This thread performs the actual duplicate elimination
 * 
 * @author UoA
 * 
 */
public class DistinctOp extends Thread {

	private static Logger logger = LoggerFactory.getLogger(DistinctOp.class.getName());

	private IRecordReader<Record> reader = null;
	private IRecordWriter<Record> writer = null;
	private int currentResultCountEstimation = 0;
	private int previousResultCountEstimation = 0;
	private boolean finalEventReceived = false;
	private boolean postFinalEstimationUpdate = false;
	private int finalResultCountValue;
	private int previousRatioComputationCheckpoint = 0;
	private int previousEmissionCheckpoint = 0;
	private int emissionStep = 100;
	private Float eliminationRatio = null;
	String objectIdFieldName = null;
	String objectRankFieldName = null;
	private StatsContainer stats = null;
	private long startTime = 0;
	private long firstInputStop = 0;
	private long firstOutputStop = 0;
	private int rc = 0;
	private int rcOut = 0;
	private int uniqueResults = 0;
	
	/**
	 * The default timeout used by the {@link IRecordWriter} and all {@link IRecordReader}s. Currently set to 60.
	 */
	public static final long TimeoutDef = 180;
	/**
	 * The default timeout unit used by the {@link IRecordWriter} and all {@link IRecordReader}s. The current default unit is seconds.
	 */
	public static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	/**
	 * The default buffer capacity for the {@link IRecordWriter} and, for the {@link IRecordReader}, if applicable
	 */
	public static final int BufferCapacityDef = 100;
	/**
	 * The default rank processing policy
	 */
	public static final boolean KeepMaximumRankDef = false;
	
	/**
	 * A default value for the number of results which is considered safe for a reliable duplicate elimination
	 * ratio.
	 */
	public static final int SafeNumberOfResultsDef = 100;
	/**
	 * A default value for the step of the elimination ratio recomputation
	 */
	public static final int EliminationRatioComputationStepDef = 100;
	/**
	 * The timeout which will be used by the {@link IRecordWriter} and all {@link IRecordReader}s
	 */
	private long timeout;
	/**
	 * The timeout unit which will be used by the {@link IRecordWriter} and all {@link IRecordReader}s
	 */
	private TimeUnit timeUnit;	
	/**
	 * The buffer capacity for the {@link IRecordWriter} and for the {@link IRecordReader}, if applicable
	 */
	private int bufferCapacity;
	/**
	 * The rank processing policy. False instructs the operator to ignore the object ranks, while true results in the operator keeping the object
	 * with the maximumrank.
	 */
	private boolean keepMaximumRank = KeepMaximumRankDef;
	/**
	 * The number of results which is considered safe for a reliable duplicate elimination
	 * ratio. Used by the event handling method in order to determine when to send a final result count event
	 */
	private int safeNumberOfResults = SafeNumberOfResultsDef;
	/**
	 * The step of the elimination ratio recomputation
	 */
	private int eliminationRatioComputationStep = EliminationRatioComputationStepDef;
	/**
	 * The unique ID of this operator invocation
	 */
	private String uid = null;

	/**
	 * Static constructor
	 * 
	 * @param loc
	 *            Locator of the incoming RS
	 * @param stats
	 * @param keepMaximumRank
	 * @param timeout
	 * @param timeUnit
	 * @param bufferCapacity
	 * @return A gRS2 locator
	 * @throws Exception
	 *             in case of error
	 */
	public static synchronized URI dispatchNewWorker(URI loc, String objectIdFieldName, String objectRankFieldName, boolean keepMaximumRank, long timeout, TimeUnit timeUnit, int bufferCapacity, StatsContainer stats) throws Exception {

		try {
			String uid = UUID.randomUUID().toString();
			DistinctOp f = new DistinctOp();
			f.objectIdFieldName = objectIdFieldName;
			f.objectRankFieldName = objectRankFieldName;
			logger.trace(uid + ": Initializing reader with locator " + loc);
			if(keepMaximumRank)
				f.reader = new RandomReader<Record>(loc, bufferCapacity);
			else
				f.reader = new ForwardReader<Record>(loc, bufferCapacity);
			f.writer = new RecordWriter<Record>(new LocalWriterProxy(), f.reader);
			f.keepMaximumRank = keepMaximumRank;
			f.timeout = timeout;
			f.timeUnit = timeUnit;
			f.bufferCapacity = bufferCapacity;
			f.stats = stats;
			f.stats.timeToInitialize(Calendar.getInstance().getTimeInMillis());
			f.uid = uid;
			f.start();
			logger.trace(uid + ": Returning " + f.writer.getLocator());
			return f.writer.getLocator();
		} catch (Exception e) {
			logger.error("Error in method dispatchNewWorker:\n" + e.getMessage());
			throw new Exception(e);
		}
	}
	
	/**
	 * Static constructor
	 * 
	 * @param loc
	 *            Locator of the incoming RS
	 * @param stats
	 * @param keepMaximumRank
	 * @param timeout
	 * @param timeUnit
	 * @return A gRS2 locator
	 * @throws Exception
	 *             in case of error
	 */
	public static synchronized URI dispatchNewWorker(URI loc, String objectIdFieldName, String objectRankFieldName, boolean keepMaximumRank, long timeout, TimeUnit timeUnit, StatsContainer stats) throws Exception {
		return DistinctOp.dispatchNewWorker(loc, objectIdFieldName, objectRankFieldName, keepMaximumRank, timeout, timeUnit, BufferCapacityDef, stats);
	}
	
	/**
	 * 
	 * @param loc
	 * @param stats
	 * @param keepMaximumRank
	 * @return
	 * @throws Exception
	 */
	public static synchronized URI dispatchNewWorker(URI loc, String objectIdFieldName, StatsContainer stats) throws Exception {
		return DistinctOp.dispatchNewWorker(loc, objectIdFieldName, null, false, DistinctOp.TimeoutDef, DistinctOp.TimeUnitDef, stats);
	}
	
	/**
	 * 
	 * @param loc
	 * @param objectIdFieldName
	 * @param objectRankFieldName
	 * @param keepMaximumRank
	 * @param stats
	 * @return
	 * @throws Exception
	 */
	public static synchronized URI dispatchNewWorker(URI loc, String objectIdFieldName, String objectRankFieldName, boolean keepMaximumRank, StatsContainer stats) throws Exception {
		return DistinctOp.dispatchNewWorker(loc, objectIdFieldName, objectRankFieldName, keepMaximumRank, DistinctOp.TimeoutDef, DistinctOp.TimeUnitDef, stats);
	}
	
	/**
	 * Main working cycle:<br>
	 * <ol>
	 * <li>Get a hashtable of distinct DocIDs (plus their ranks)</li>
	 * <li>Re-iterate the RS keeping only the RSs that have a matching DocID and Rank with the stored hashtable</li>
	 * </ol>
	 */
	@Override
	public void run() {
		try {
			startTime = Calendar.getInstance().getTimeInMillis();
			Hashtable<String, Double> distincts = getDistincts();
			
			if(this.keepMaximumRank == true) {
				reader.seek(-reader.currentRecord());
	
				rc = 0;
				while (true) {
					if(reader.getStatus() == Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0))
						break;
					
					Record rec = reader.get(timeout, timeUnit);
					if(rec == null) {
						if(reader.getStatus() == Status.Open) 
							logger.warn("Producer of " + this.uid + " has timed out");
						break;
					}
					
					rc++;
					
					Double storedRank = null;
					ObjectRank or = extractObjectRank(rec);
					if(or.objID != null)
						storedRank = distincts.get(or.objID);
//					if(storedRank == null)
//						throw new Exception("In the second RS iteration I could not find object with id: " + or.objID);
					
					if(storedRank == null || (storedRank.doubleValue() == or.rank.doubleValue())) {
						if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
							logger.info("Consumer side of " + this.uid + " stopped consumption. Stopping.");
							break;
						}
						
						if(!writer.importRecord(rec, timeout, timeUnit) ) {
							if(writer.getStatus() == IBuffer.Status.Open)
								logger.warn("Consumer of " + this.uid + " has timed out");
							break;
						}
						rcOut++;
						if(rcOut == 1)
							firstOutputStop = Calendar.getInstance().getTimeInMillis();
						//if(rc%1000 == 0)
						//	System.out.println("Second scan: " + rc);
					}
				}
			}
			emitPendingFinalEvents(rcOut);
			
			long closeStop = Calendar.getInstance().getTimeInMillis();
			stats.timeToFirstInput(firstInputStop - startTime);
			stats.timeToFirst(firstOutputStop - startTime);
			stats.timeToComplete(closeStop - startTime);
			stats.producedResults(rc);
			stats.productionRate(((float)rc/(float)(closeStop - startTime))*1000);
			logger.info("DUPLICATE ELIMINATION OPERATOR " + this.uid + ":" +
					"Produced first result in "+(firstOutputStop - startTime)+" milliseconds\n" +
					"Produced last result in "+(closeStop - startTime)+" milliseconds\n" +
					"Produced " + rcOut + " results\n" + 
					"Read " + rc + " results (" + (rc-rcOut) + " duplicates)\n" + 
					"Production rate was "+(((float)rc/(float)(closeStop - startTime))*1000)+" records per second");

		} catch (Exception e) {
			logger.error("Error in method run for " + this.uid, e);
		}
		finally {
			try {
				writer.close();
				reader.close();
			}catch(Exception e) { }
		}
	}
	
	/**
	 * Private constructor to avoid the creation of instances of this class,
	 * apart from the dispatchNewWorker method.
	 */
	private DistinctOp() {
	}

	/**
	 * Get a hashtable of distinct DocIDs (plus their ranks)
	 * @return hashtable of distinct DocIDs (plus their ranks)
	 * @throws Exception in case of unexpected error
	 */
	private Hashtable<String, Double> getDistincts() throws Exception {
		
		Hashtable<String, Double> distincts = new Hashtable<String, Double>();
		rc = 0;
		rcOut = 0;
		try {
			while (true) {

				if(reader.getStatus() == Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0))
					break;
				
				Record rec = reader.get(timeout, timeUnit);
				handleEvents();
				if(rec == null) {
					if(reader.getStatus() == Status.Open) 
						logger.warn("Producer of " + this.uid + " has timed out");
					break;
				}
				
				rc++;
				if(rc == 1)
					firstInputStop = Calendar.getInstance().getTimeInMillis();
			//	if(rc % 1000 == 0)
			//		System.out.println("First scan: " + rc);
				ObjectRank or = extractObjectRank(rec);
				Double storedValue = null;
				if(or.objID != null)
					storedValue = distincts.get(or.objID);
				else
					or.objID = UUID.randomUUID().toString(); //null object ids are treated as unique records
				if(storedValue == null) {
					uniqueResults++;
					distincts.put(or.objID, or.rank);
					if(this.keepMaximumRank == false) {
						if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
							logger.info("Consumer side of " + this.uid + " stopped consumption. Stopping.");
							break;
						}
						
						if(!writer.importRecord(rec, timeout, timeUnit) ) {
							if(writer.getStatus() == IBuffer.Status.Open)
								logger.warn("Consumer of " + this.uid + " has timed out");
							break;
						}
						rcOut++;
						
						if(rc == 1)
							firstOutputStop = Calendar.getInstance().getTimeInMillis();
					}
				}
				else {
					if(this.keepMaximumRank == true && storedValue.doubleValue() < or.rank.doubleValue())
					distincts.put(or.objID, or.rank);
				}
			}
			
			return distincts;

		} catch (Exception e) {
			logger.error("Error in method getDistincts for " + this.uid + ":\n" + e);
			throw new Exception(e);
		}
	}
	
	/**
	 * Extract DocID and Rank
	 * If a field name for object id has been defined, the object id and optionally the object rank are read from the corresponding {@link Field}s
	 * If a field name for object id has not been defined, the record is expected to be an instance of {@link GCubeXMLRecord}, which contains
	 * object id and rank as attributes
	 * 
	 * @param rec The record to extract object id and rank from
	 * @return DocID and Rank
	 */
	private ObjectRank extractObjectRank(Record rec) throws Exception {
		ObjectRank or = new ObjectRank();
		if(objectIdFieldName == null) {
			//or.objID = ((GCubeXMLRecord)rec).getId();
			//or.rank = Double.valueOf(((GCubeXMLRecord)rec).getRank());
		}else {
			or.objID = ((StringField)rec.getField(this.objectIdFieldName)).getPayload();
			if(objectRankFieldName != null)
				or.rank = Double.valueOf(((StringField)rec.getField(this.objectRankFieldName)).getPayload());
			else {
				if(this.keepMaximumRank == true)
					throw new Exception("Keep maximum rank is enabled, however a rank field could not be found");
				or.rank = 1.0;
			}
		}
		return or;
	}
	
	
	private void handleEvents() throws Exception  {
		
		boolean emitStoredFinal = false;
		if(rc > safeNumberOfResults) {
			if(rc - previousRatioComputationCheckpoint > eliminationRatioComputationStep) {
				previousRatioComputationCheckpoint = rc;
				eliminationRatio = uniqueResults/(float)rc;
				if(finalEventReceived && !postFinalEstimationUpdate) {
					currentResultCountEstimation = (int)Math.floor(eliminationRatio*(float)finalResultCountValue);
					postFinalEstimationUpdate = true;
					emitStoredFinal = true;
				}
			}
		}
		
		previousResultCountEstimation = currentResultCountEstimation;
		boolean received = false;
		BufferEvent ev = null;
		while((ev = reader.receive()) != null) {
			if(!(ev instanceof KeyValueEvent))
				writer.emit(ev);
			else if(((KeyValueEvent)ev).getKey().equals(OperatorLibraryConstants.RESULTSNO_EVENT)) {
				received = true;
				if(finalEventReceived == false) {
					if(eliminationRatio != null)
						currentResultCountEstimation = (int)Math.floor(eliminationRatio*(float)Integer.parseInt(((KeyValueEvent)ev).getValue()));
				}
			}
			else if(((KeyValueEvent)ev).getKey().equals(OperatorLibraryConstants.RESULTSNOFINAL_EVENT)) {
				received = true;
				finalEventReceived = true;
				finalResultCountValue = Integer.parseInt(((KeyValueEvent)ev).getValue());
				if(eliminationRatio!=null)
					currentResultCountEstimation = (int)Math.floor(eliminationRatio*(float)finalResultCountValue);
			}
			else
				writer.emit(ev);		
		}

		if(received == true) {
			int val = Math.max(currentResultCountEstimation, rcOut);
			if(val != rcOut || emitStoredFinal) {
				if(currentResultCountEstimation != previousResultCountEstimation)
					writer.emit(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, "" + val));
			}else if(rcOut - previousEmissionCheckpoint >= emissionStep) {
				previousEmissionCheckpoint = rcOut;
				writer.emit(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, "" + rcOut));
			}
		}else {
			if(emitStoredFinal)
				writer.emit(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, "" + currentResultCountEstimation));
			else if((rcOut > currentResultCountEstimation) && (rcOut - previousEmissionCheckpoint >= emissionStep)) {
				previousEmissionCheckpoint = rcOut;
				writer.emit(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, "" + rcOut));
			}
		}
		
	}
	
	private void emitPendingFinalEvents(int count) throws Exception {
		writer.emit(new KeyValueEvent(OperatorLibraryConstants.RESULTSNOFINAL_EVENT, "" + count));
	}

}
