package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.OperatorLibraryConstants;

public class EventHandler<T extends Record> {
	private static Logger logger = LoggerFactory.getLogger(EventHandler.class.getName());
	
	private RecordWriter<T> writer = null;
	private Queue<EventEntry> eventQueue = null;
	private boolean finalReceivedFromAll = false;
	private boolean[] finalReceived = null;
	private int finalReceivedCount = 0;
	private boolean finalSent = false;
	private int[] currentResultCount = null;
	private int currentResultCountSum = 0;
	private int previousResultCountSum = 0;
	private int writerResultCount = 0;
	private int previousEmissionCheckpoint = 0;
	private int emissionStep = 0;
	private int readerCount;
	
	public EventHandler(RecordWriter<T> writer, Queue<EventEntry> eventQueue, int readerCount, int emissionStep) {
		this.writer = writer;
		this.eventQueue = eventQueue;
		this.readerCount = readerCount;
		this.emissionStep = emissionStep;
		this.finalReceived = new boolean[readerCount];
		this.currentResultCount = new int[readerCount];
		this.finalReceivedFromAll = false;
		for(int i = 0; i < readerCount; i++) {
			currentResultCount[i] = 0;
			finalReceived[i] = false;
		}
	}
	
	private void setFinalReceived(int id) {
		if(finalReceived[id] == false) {
			finalReceived[id] = true;
			if(++finalReceivedCount == readerCount)
				finalReceivedFromAll = true;
		}
	}
	
	private void setAndUpdateCurrentCount(int id, int count) {
		if(finalReceived[id] == false) {
			int prev = currentResultCount[id];
			currentResultCount[id] = count;
			currentResultCountSum = currentResultCountSum - prev + count;
		}
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
		if(finalReceivedFromAll) {
			//Propagate events which are not related to result count
			EventEntry ev = null;
			while((ev = (EventEntry)eventQueue.poll()) != null)
				propagateNonResultEvent(ev.event);
			return;
		}
		
		EventEntry ev = null;
		KeyValueEvent resultEvent = null;
		previousResultCountSum = currentResultCountSum;
		boolean received = false;
		while((ev = (EventEntry)eventQueue.poll()) != null) {
			if(!(ev.event instanceof KeyValueEvent))
				emitEvent(ev.event);
			else {
				resultEvent = (KeyValueEvent)ev.event;
				if(resultEvent.getKey().equals(OperatorLibraryConstants.RESULTSNOFINAL_EVENT)) {
					setAndUpdateCurrentCount(ev.id, Integer.parseInt(resultEvent.getValue()));
					setFinalReceived(ev.id);
					received = true;
				}
				else if(resultEvent.getKey().equals(OperatorLibraryConstants.RESULTSNO_EVENT)) {
					setAndUpdateCurrentCount(ev.id, Integer.parseInt(resultEvent.getValue()));
					received = true;
				}
				else
					emitEvent(ev.event);
			}
		}
		
		if(received == true) {
			if(finalReceivedFromAll) {
				emitEvent(new KeyValueEvent(OperatorLibraryConstants.RESULTSNOFINAL_EVENT, ""+currentResultCountSum));
				finalSent = true;
			}
			else {
				int val = Math.max(writerResultCount, currentResultCountSum);
				if(val != currentResultCountSum) {
					if(currentResultCountSum != previousResultCountSum)
						emitEvent(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, ""+val));
				}else if(writerResultCount - previousEmissionCheckpoint >= emissionStep) {
					previousEmissionCheckpoint = writerResultCount;
					emitEvent(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, ""+val));
				}
			}
		}else {
			if((writerResultCount > currentResultCountSum) && (writerResultCount - previousEmissionCheckpoint >= emissionStep)) {
				previousEmissionCheckpoint = writerResultCount;
				emitEvent(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, ""+writerResultCount));
			}
		}
	}
	
	public void increaseProducedRecordCount() {
		writerResultCount++;
	}
	
	public void sendPendingFinalEvents(int count) {
		if(!finalSent)
			emitEvent(new KeyValueEvent(OperatorLibraryConstants.RESULTSNOFINAL_EVENT, ""+count));
	}
}
