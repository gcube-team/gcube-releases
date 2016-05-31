package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.OperatorLibraryConstants;

public class EventHandler<T extends Record> {
	private static Logger logger = LoggerFactory.getLogger(EventHandler.class.getName());
	
	/**
	 * A default value for the number of results which is considered safe for a reliable join
	 * ratio computation.
	 */
	public static final int SafeNumberOfResultsDef = 100;
	/**
	 * A default value for the step of the join ratio recomputation
	 */
	public static final int JoinRatioComputationStepDef = 100;
	
	private IRecordWriter<T> writer = null;
	private Queue<EventEntry> eventQueue = null;
	private boolean finalReceivedFromAll = false;
	private boolean[] finalReceived = null;
	private int finalReceivedCount = 0;
	private int finalResultCountValue = 0;

	private int currentResultCountEstimation = 0;
	
	private int[] currentResultCountFromEvents = null;
	private int currentResultCountMaxFromEvents = 0;
	private int previousResultCountMaxFromEvents = 0;
	
	private int[] currentResultCountRead = null;
	private int currentResultCountMaxRead = 0;
	
	private int writerResultCount = 0;
	private Float joinRatio = null;
	private int previousEmissionCheckpoint = 0;
	private int emissionStep = 0;
	private int readerCount;
	boolean emitStoredFinal = false;
	
	private int safeNumberOfResults = SafeNumberOfResultsDef;
	private int joinRatioComputationStep = JoinRatioComputationStepDef;
	private int previousRatioComputationCheckpoint = 0;
	private boolean postFinalEstimationUpdate = false;
	
	public EventHandler(IRecordWriter<T> writer, Queue<EventEntry> eventQueue, int readerCount, int emissionStep) {
		this.writer = writer;
		this.eventQueue = eventQueue;
		this.readerCount = readerCount;
		this.emissionStep = emissionStep;
		this.finalReceived = new boolean[readerCount];
		this.currentResultCountFromEvents = new int[readerCount];
		this.currentResultCountRead = new int[readerCount];
		this.finalReceivedFromAll = false;
		for(int i = 0; i < readerCount; i++) {
			currentResultCountFromEvents[i] = 0;
			currentResultCountRead[i] = 0;
			finalReceived[i] = false;
		}
	}
	
	public EventHandler(IRecordWriter<T> writer, Queue<EventEntry> eventQueue, int readerCount, int emissionStep, int safeNumberOfResults, int joinRatioComputationStep) {
		this(writer, eventQueue, readerCount, emissionStep);
		this.safeNumberOfResults = safeNumberOfResults;
		this.joinRatioComputationStep = joinRatioComputationStep;
	}
	
	private void setFinalReceived(int id) {
		if(finalReceived[id] == false) {
			finalReceived[id] = true;
			if(++finalReceivedCount == readerCount)
				finalReceivedFromAll = true;
		}
	}
	
	private void setAndUpdateCurrentCountEstimation(int id, int count) {
		if(finalReceived[id] == false) {
			currentResultCountFromEvents[id] = count;
			if(currentResultCountMaxFromEvents < count)
				currentResultCountMaxFromEvents = count;
		}
	}
	
	private void setAndUpdateCurrentCount(int id) {
		currentResultCountRead[id]++;
		if(currentResultCountMaxRead < currentResultCountRead[id])
			currentResultCountMaxRead = currentResultCountRead[id];
	}
	
	private void emitEvent(BufferEvent ev) {
		try { writer.emit(ev); }
		catch(Exception e) { logger.info("Could not emit event", e); }
	}
	
	private void propagateNonResultEvent(BufferEvent ev) {
		if(ev instanceof KeyValueEvent) {
			if(((KeyValueEvent)ev).getKey().equals(OperatorLibraryConstants.RESULTSNO_EVENT) || 
					((KeyValueEvent)ev).getKey().equals(OperatorLibraryConstants.RESULTSNOFINAL_EVENT))
				return;
			emitEvent(ev);
		}else
			emitEvent(ev);
	}
	
	public void propagateEvents() {
		
		if(currentResultCountMaxRead > safeNumberOfResults && writerResultCount > 0) {
			if(currentResultCountMaxRead - previousRatioComputationCheckpoint > joinRatioComputationStep) {
				previousRatioComputationCheckpoint = currentResultCountMaxRead;
				joinRatio = writerResultCount/(float)currentResultCountMaxRead;
				if(finalReceivedFromAll && !postFinalEstimationUpdate) {
					currentResultCountEstimation = (int)Math.floor(joinRatio*(float)currentResultCountMaxFromEvents);
					emitStoredFinal = true;
					postFinalEstimationUpdate = true;
				}
			}
		}
		
		EventEntry ev = null;
		KeyValueEvent resultEvent = null;
		previousResultCountMaxFromEvents = currentResultCountMaxFromEvents;
		boolean received = false;
		while((ev = (EventEntry)eventQueue.poll()) != null) {
			if(!(ev.event instanceof KeyValueEvent))
				emitEvent(ev.event);
			else {
				resultEvent = (KeyValueEvent)ev.event;
				if(resultEvent.getKey().equals(OperatorLibraryConstants.RESULTSNOFINAL_EVENT)) {
					finalResultCountValue = Integer.parseInt(resultEvent.getValue());
					setAndUpdateCurrentCountEstimation(ev.id, finalResultCountValue);
					setFinalReceived(ev.id);
					if(joinRatio != null)
						currentResultCountEstimation = (int)Math.floor(joinRatio*(float)currentResultCountMaxFromEvents);
					received = true;
				}
				else if(resultEvent.getKey().equals(OperatorLibraryConstants.RESULTSNO_EVENT)) {
					int val = Integer.parseInt(((KeyValueEvent)ev.event).getValue());
					setAndUpdateCurrentCountEstimation(ev.id, val);
					if(joinRatio != null)
						currentResultCountEstimation = (int)Math.floor(joinRatio*(float)currentResultCountMaxFromEvents);
					received = true;
				}
				else
					emitEvent(ev.event);
			}
		}
		
		if(received == true) {
			int val = Math.max(writerResultCount, currentResultCountEstimation);
			if(val != writerResultCount || emitStoredFinal) {
				if(currentResultCountMaxFromEvents != previousResultCountMaxFromEvents)
					emitEvent(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, ""+val));
			}else if(writerResultCount - previousEmissionCheckpoint >= emissionStep) {
				previousEmissionCheckpoint = writerResultCount;
				emitEvent(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, ""+val));
			}
		}else {
			if(emitStoredFinal) {
				emitEvent(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, "" + currentResultCountEstimation));
				emitStoredFinal = false;
			}
			else if((writerResultCount > currentResultCountEstimation) && (writerResultCount - previousEmissionCheckpoint >= emissionStep)) {
				previousEmissionCheckpoint = writerResultCount;
				emitEvent(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, ""+writerResultCount));
			}
		}
	}
	
	public void increaseProducedRecordCount() {
		writerResultCount++;
	}
	
	public void increaseReadRecordCount(int id) {
		setAndUpdateCurrentCount(id);
	}
	
	public void sendPendingFinalEvents(int count) {
		emitEvent(new KeyValueEvent(OperatorLibraryConstants.RESULTSNOFINAL_EVENT, ""+count));
	}
}
