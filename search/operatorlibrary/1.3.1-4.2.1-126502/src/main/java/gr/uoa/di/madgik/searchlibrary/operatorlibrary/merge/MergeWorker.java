package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.util.Calendar;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Thread class that populates the output {@link RecordWriter} in the background
 * 
 * @author UoA
 */
public class MergeWorker extends Thread {
	/**
	 * The Logger the class uses
	 */
	private Logger logger = LoggerFactory.getLogger(MergeWorker.class.getName());
	/**
	 * The readers to use
	 */
	private Vector<ReaderHolder> readers = null;
	/**
	 * The writer to use
	 */
	private RecordWriter<Record> writer = null;
	/**
	 * Used by writer to set the appropriate record definition for each record encountered
	 */
	private int[] recordDefinitionOffsets = null;
	/**
	 * Synchronization object for writer initialization
	 */
	private Object synchWriterInit = new Object();
	/**
	 * The operation mode that will be used
	 */
	private OperationMode operationMode;
	/**
	 * A unique identifier for this operation
	 */
	private String uid = null;
	
	/**
	 * The timeout that will be used by {@link RecordWriter} and all {@link IRecordReader}s involved in the merging operation
	 */
	private long timeout;
	/**
	 * The timeout unit that will be used by {@link RecordWriter} and all {@link IRecordReader}s involved in the merging operation
	 */
	private TimeUnit timeUnit;
	/**
	 * The number of records merged
	 */
	private int count=0;
	/**
	 * Used for timing
	 */
	private long firststop=0;
	/**
	 * statistics
	 */
	private StatsContainer stats;
	
	/**
	 * Creates a new <code>MergeWorker</code>
	 * 
	 * @param readers Vector holding info on the input ResultSet
	 * @param stats Statistics
	 * @param operationMode The operation mode. One of {@link OperationMode#FIFO}, {@link OperationMode#FirstAvailable} and {@link OperationMode#Sort}
	 * @param timeout The timeout that will be used by the {@link RecordWriter} and all {@link ForwardReader}s
	 * @param timeUnit The timeout unit that will be used by the {@link RecordWriter} and all {@link ForwardReader}s
	 * @param uid A unique identifier for this operation
	 */
	public MergeWorker(Vector<ReaderHolder> readers,StatsContainer stats, OperationMode operationMode, long timeout, TimeUnit timeUnit, String uid) {
		this.readers = readers;
		this.stats = stats;
		this.operationMode = operationMode;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.uid = uid;
	}

	/**
	 * @param writer The output {@link RecordWriter} to place the records to
	 */
	public void setWriter(RecordWriter<Record> writer) {
		this.writer = writer;
	}
	
	public void setRecordDefinitionOffsets(int[] recordDefinitionOffsets) {
		this.recordDefinitionOffsets = recordDefinitionOffsets;
	}
	
	public Object getWriterInitSyncObject() {
		return synchWriterInit;
	}
	
//	private int estimateQueueCapacity(Vector<ReaderHolder> readers) throws GRS2ReaderException {
//		int capacity = readers.get(0).getReader().getCapacity();
//		for(ReaderHolder entry : readers) {
//			if(entry.getReader().getCapacity() < capacity)
//				capacity = entry.getReader().getCapacity();
//		}
//		return capacity-1 > 0 ? capacity-1 : capacity;
//	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		long start=Calendar.getInstance().getTimeInMillis();
		this.setName("Merge Worker");
		BlockingQueue<RecordBufferEntry> recs = null;
		Queue<EventEntry> events = null;
		
		try{
			//int capacity = estimateQueueCapacity(readers);
			int capacity = 100;
			logger.info("Queue capacity for " + this.uid + " is " + capacity);
			recs = new ArrayBlockingQueue<RecordBufferEntry>(capacity);
			events = new ConcurrentLinkedQueue<EventEntry>();
			ReaderScan []scan=new ReaderScan[readers.size()];
			
			for(int i = 0; i < readers.size(); i++)
				scan[i] = new ReaderScan(readers, i, recs, events, timeout, timeUnit, uid, operationMode);
			
			ScanDispatcher dispatcher;
			if(this.operationMode == OperationMode.FIFO)
				dispatcher = new FIFOScanDispatcher(scan);
			else if(this.operationMode == OperationMode.FirstAvailable)
				dispatcher = new FirstAvailableScanDispatcher(scan);
			else
			{
				dispatcher = new SortScanDispatcher(scan);
				Sorter sorter = new Sorter(scan, recs);
				sorter.setPriority(MIN_PRIORITY);
				sorter.start();
			}
			dispatcher.start();
			//Wait for writer to become available. This will happen once all readers are initialized and, consequently, all record definitions are retrieved
			synchronized(synchWriterInit) {
				while(this.writer == null || this.recordDefinitionOffsets == null)
					synchWriterInit.wait();
			}
			
			EventHandler<Record> eventHandler = new EventHandler<Record>(writer, events, readers.size(), 100);
			logger.info("After all readers have initialized, the intermediate buffer of " + this.uid + " contains " + recs.size() + " records");
			
			boolean finished = false;
		
			while(!finished){

				if(this.count==0) {
					firststop=Calendar.getInstance().getTimeInMillis();
					stats.timeToFirstInput(firststop-start);
				}
				RecordBufferEntry rbEntry = recs.poll(500, TimeUnit.MILLISECONDS); //avoid continual checking of reader status when queue becomes empty in exchange for a 500ms delay on completion
				eventHandler.propagateEvents();
				
				if(rbEntry != null) {
					Record rec = rbEntry.record;
					if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
						logger.info("Consumer side of " + this.uid + " stopped consumption. Notifying all " + readers.size() + " readers to stop.");
						synchronized(readers) {
							for(int i = 0; i < readers.size(); i++) {
								readers.get(i).setFinished(true);
								try {
									readers.get(i).getReader().close();
								}catch(GRS2ReaderException re) {
									logger.warn("Could not close reader #" + i);
								}
							}
						}
						recs.clear();
						break;
					}
					
				//	System.out.println("index = " +(this.recordDefinitionOffsets[rbEntry.id] + (rec.getDefinitionIndex() == -1 ? 0 : rec.getDefinitionIndex())));
					
					int newDefinitionIndex = this.recordDefinitionOffsets[rbEntry.id] + (rec.getDefinitionIndex() == -1 ? 0 : rec.getDefinitionIndex());
					if(!writer.importRecord(rec, newDefinitionIndex, timeout, timeUnit)) {
						if(writer.getStatus() == IBuffer.Status.Open)
							logger.warn("Writer of " + this.uid + " has timed out");
						break;
					}
					this.count++;
					eventHandler.increaseProducedRecordCount();
				}else {
					int count = 0;
					synchronized(readers){
						for(int i=0;i<scan.length;i++){
							if(!readers.get(i).hasFinished())
								count++;
						}
					}

					if(count==0)
						finished = true;
				}

			}
			
			dispatcher.join();
			long closestop=Calendar.getInstance().getTimeInMillis();
			eventHandler.sendPendingFinalEvents(this.count);
			try { writer.close(); } catch(GRS2WriterException e) { logger.warn("Could not close writer"); }
			for(int i = 0; i < readers.size(); i++) {
				try { readers.get(i).getReader().close(); } catch(GRS2ReaderException re) { logger.warn("Could not close reader #" + i); }
			}
			stats.timeToComplete(closestop-start);
			stats.timeToFirst(firststop-start);
			stats.productionRate((((float)this.count/(float)(closestop-start))*1000));
			stats.producedResults(this.count);
			logger.info("MERGE OPERATOR " + this.uid + ":\nProduced first result in "+(firststop-start)+" milliseconds\n" +
					"Produced last result in "+(closestop-start)+" milliseconds\n" +
					"Produced " + count + " records\n" + 
					"Production rate was "+(((float)this.count/(float)(closestop-start))*1000)+" records per second");
		}catch(Exception e){
			logger.error("Could not complete background merging for " + this.uid + ". Closing", e);
			synchronized(readers) {
				for(int i = 0; i < readers.size(); i++) {
					readers.get(i).setFinished(true);
					try { 
						if(readers.get(i).getReader().getStatus() != Status.Dispose) readers.get(i).getReader().close(); 
					} catch(Exception re) { logger.warn("Could not close reader #" + i); }
				}
			}
			recs.clear();
			try{
				if(writer.getStatus() != Status.Dispose) writer.close();
			}catch(Exception ee){
				logger.error("Could not close writer of " + this.uid, ee);
			}
		}
	}
}
