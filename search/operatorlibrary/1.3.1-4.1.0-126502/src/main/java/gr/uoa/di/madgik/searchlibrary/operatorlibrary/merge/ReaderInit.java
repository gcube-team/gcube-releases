package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.reader.decorators.keepalive.KeepAliveReader;
import gr.uoa.di.madgik.grs.record.Record;

import java.net.URI;
import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper thread class that initializes an {@link ForwardReader} 
 * 
 * @author UoA
 */
public class ReaderInit extends Thread{
	/**
	 * The Logger this class uses
	 */
	private Logger logger = LoggerFactory.getLogger(ReaderInit.class.getName());
	/**
	 * The readers to use
	 */
	private Vector<ReaderHolder> readers=null;
	/**
	 * The index of the reader
	 */
	private int index=0;
	/**
	 * The locator to use
	 */
	private URI locator = null;
	/**
	 * The desired buffer capacity for the reader
	 */
	private int bufferCapacity = -1;
	/**
	 * A unique identifier for this operation
	 */
	private String uid = null;
	
	/**
	 * The multiplex policy used by the {@link MergeOp}. If this is equal to {@link RecordGenerationPolicy.FIFO}, all {@link ForwardReader}s
	 * will be made into {@link KeepAliveReader}s. Otherwise, simple {@link ForwardReader}s will be used.
	 */
	private OperationMode multiplexPolicy;
	
	/**
	 * Creates a new {@link ForwardReader}
	 * 
	 * @param readers The {@link ReaderHolder} vector to update
	 * @param index The index of the holder this thread should update
	 * @param locator The locator to the ResultSet
	 * @param bufferCapacity The desired buffer capacity for the reader
	 * @param uid A unique identifier for this operation
	 */
	public ReaderInit(Vector<ReaderHolder> readers,int index,URI locator, OperationMode multiplexPolicy, int bufferCapacity, String uid){
		this.readers=readers;
		this.index=index;
		this.locator=locator;
		this.multiplexPolicy = multiplexPolicy;
		this.bufferCapacity = bufferCapacity;
		this.uid = uid;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try{
			this.setName("Reader Init #" + this.index);
			long initstart=Calendar.getInstance().getTimeInMillis();
			logger.trace(this.uid + ": Initializing reader #" + index + " with locator " + locator);
			IRecordReader<Record> reader = new ForwardReader<Record>(this.locator, this.bufferCapacity);
			if(this.multiplexPolicy == OperationMode.FIFO)
				reader = new KeepAliveReader<Record>(reader, 30, TimeUnit.SECONDS);
			readers.get(index).setReader(reader);
			long initstop=Calendar.getInstance().getTimeInMillis();
			logger.info("MERGE OPERATOR " + this.uid + " THREAD INIT TOOK "+(initstop-initstart));
		}catch(Exception e){
			logger.error("Could not initialize reader #" + index + " of operation " + this.uid + ". setting null",e);
			readers.get(index).setReader(null);
		}finally{
			synchronized(readers) {
				readers.get(index).setWaitingForInit(false);
				Object synchReader = readers.get(index).getSynchReader();
				synchronized(synchReader) {
					synchReader.notify();
				}
			}
		}
	}
}
