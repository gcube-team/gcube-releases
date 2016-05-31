package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper thread class used to iterate over a ResultSet
 * to retrieve the local payload parts and notify the merging thread when they are available
 * 
 * @author UoA
 */
public class ReaderScan extends Thread{
	/**
	 * The Logger the class uses
	 */
	private Logger logger = LoggerFactory.getLogger(ReaderScan.class.getName());
	
	/**
	 * The reader to use
	 */
	private Vector<ReaderHolder> readers=null;
	/**
	 * The buffer to place records in
	 */
	private BlockingQueue<RecordBufferEntry> buf=null;
	/**
	 * A queue to place events in
	 */
	private Queue<EventEntry> events = null;
	
	private BlockingQueue<Pair<RecordBufferEntry,Integer>> privateBuf= null;
	/**
	 * The readers' timeout
	 */
	private long timeout = 0;
	/**
	 * The readers' timeout unit
	 */
	private TimeUnit timeUnit;
	/**
	 * The index of the user
	 */
	private int index=0;
	/**
	 * A unique identifier for this operation
	 */
	private String uid = null;

	/**
	 * The operation mode of the scan
	 */
	private OperationMode operationMode;
		
	/**
	 * Creates a new {@link ReaderScan} working on some input {@link ForwardReader}
	 * 
	 * @param readers The {@link ReaderHolder} vector with the available input sources
	 * @param index The index of the {@link ReaderHolder} this thread should operate on
	 * @param buf The buffer to place the records read along with the id of the reader, which is equal to {@link #index}
	 * @param events A queue to place the events read along with the id of the reader, which is equal to {@link #index}
	 * @param timeout The timeout to use
	 * @param timeUnit The timeout unit to use
	 * @param uid A unique identifier for this operation
	 * @param operationMode The operation mode to use
	 */
	public ReaderScan(Vector<ReaderHolder> readers, int index, BlockingQueue<RecordBufferEntry> buf, Queue<EventEntry> events, long timeout, TimeUnit timeUnit, String uid, OperationMode operationMode){
		this.readers=readers;
		this.index=index;
		this.buf=buf;
		this.events=events;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.uid = uid;
		this.operationMode = operationMode;
		if(this.operationMode==OperationMode.Sort)
			privateBuf = new ArrayBlockingQueue<Pair<RecordBufferEntry,Integer>>(1);
	}
	
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		int rc = 0;
		try{
			this.setName("Reader Scan #" + this.index);
			//synchronized(readers){
			//	readers.get(index).setReady(true);
			//}
			IRecordReader<? extends Record> reader = null;
			Object synchReader = readers.get(index).getSynchReader();
			synchronized (synchReader) {
				while((readers.get(index).getWaitingForInit()) == true)
					synchReader.wait();
				reader = readers.get(index).getReader();
			}

			if(reader == null) {
				logger.trace("Reader " + index + " of" + this.uid + " is null, returning");
				synchronized(readers){
					readers.get(index).setFinished(true);
				}
				return;
			}
			while(true){
				
				synchronized(readers) {
					if(readers.get(index).hasFinished()) {
						logger.trace("Reader " + index + " of " + this.uid + " stopping after being notified");
						break;
					}
				}
				if(reader.getStatus()==Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0)) 
				{
					logger.trace("Reader " + index + " of " + this.uid + " stopping, status=" + reader.getStatus());
					break;
				}
				
				Record rec = reader.get(timeout, timeUnit);
				BufferEvent ev = reader.receive();
				if(ev!=null) events.add(new EventEntry(ev, index));
				
				if(rec == null) {
					if(reader.getStatus() == Status.Open)
						logger.warn("Reader " + index + " of " + this.uid + " has timed out");
					else
						logger.trace("Reader " + index + " of " + this.uid + " failed to read record and stopped, status=" + reader.getStatus());
					break;
				}

				rc++;
				//	rec.makeAvailable();
			//	rec.unbind();
				int rank = -1;
				if(this.operationMode==OperationMode.Sort)
					rank = Integer.parseInt(((StringField)rec.getField(MergeOp.RankFieldNameDef)).getPayload());
				rec.hide();
				RecordBufferEntry rbe = new RecordBufferEntry(rec, index);
				// in case of OperationMode.Sort use the privateBuf
				if(this.operationMode==OperationMode.Sort)
				{
					privateBuf.put(new Pair<RecordBufferEntry, Integer>(rbe,rank));
				}
				else
					buf.put(rbe);
			}
			//readers.get(index).getReader().close();
			
		}catch(Exception e){
			logger.error("Could not scan entire reader. exiting", e);
			//readers.get(index).setReader(null);
		}
		finally {
			logger.trace("Reader " + index +  " of " + this.uid + " produced " + rc + " records");
			synchronized(readers){
				readers.get(index).setFinished(true);
			}
		}
	}

	public Pair<RecordBufferEntry, Integer> poll()
	{
		synchronized (readers) {
			if(!readers.get(index).hasFinished())
			{
				return privateBuf.poll();
			}
		}
		// code reached only when index reader has finished, -1 used to inform the Sorter
		return new Pair<RecordBufferEntry, Integer>(new RecordBufferEntry(null, -1),-1);
	}
	
	public Pair<RecordBufferEntry, Integer> peek()
	{
		synchronized (readers) {
			if(!readers.get(index).hasFinished())
			{
				return privateBuf.peek();
			}
		}
		// code reached only when index reader has finished, -1 used to inform the Sorter
		return new Pair<RecordBufferEntry, Integer>(new RecordBufferEntry(null, -1),-1);
	}
	
}
