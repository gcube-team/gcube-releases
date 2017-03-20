package gr.uoa.di.madgik.searchlibrary.operatorlibrary.booleancompare;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author UoA
 */
public class ComputeSize implements Compute {
	/**
	 * Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(ComputeSize.class.getName());

	/**
	 * RS reader
	 */
	private IRecordReader<Record> reader;
	/**
	 * The timeout used by the {@link IRecordWriter} and the {@link IRecordReader}.
	 */
	private long timeout;
	/**
	 * The timeout unit used by the {@link IRecordWriter} and the {@link IRecordReader}.
	 */
	private TimeUnit timeUnit;
	
	/**
	 * Constructor
	 * @param xpath refers to the field being processed, withing the {@link Field}
	 * @param fieldName The name of the {@field Field} that contains the data to be processed
	 * @param reader The reader used to read input
	 * @param timeout The timeout of the reader
	 * @param timeUnit The time unit of the timeout
	 */
	public ComputeSize(String xpath, String fieldName, IRecordReader<Record> reader, long timeout, TimeUnit timeUnit)
	{
		this.reader = reader;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	/**
	 * Implemented method
	 * @see Compute#compute()
	 * @return result of computation
	 * @throws Exception in case of error
	 */
	public Object compute() throws Exception
	{
		int SIZE = 0;
		try{
			while(true) {
				
				Record rec = reader.get(timeout, timeUnit);
				if(rec == null) {
					if(reader.getStatus() == Status.Open) 
						logger.warn("Producer has timed out");
					break;
				}
			
				SIZE++; 
				// System.out.println("#recs up to now: " + new Integer(SIZE).toString());
			}

			// System.out.println("TotalSize: " + new Integer(SIZE).toString());
			return new Integer(SIZE).toString(); 
			
		}catch(Exception e){
			logger.error("could not complete compute", e);
			return null;
		}finally {
			try { reader.close(); } catch(Exception e) { }
		}
	}
	
}
