package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.samplegenerator;

import java.net.URI;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Operator class used to perform a keep top operation on an input ResultSet
 * and produce a new output ResultSet with the top
 * records
 * 
 * @author UoA
 */
public class SampleGeneratorOp {
	/**
	 * Logger used by this class
	 */
	private Logger logger = LoggerFactory.getLogger(SampleGeneratorOp.class.getName());

	/**
	 * stats
	 */
	//private StatsContainer stats=null;
	
	/**
	 * Creates a new {@link SampleGeneratorOp}
	 * 
	 * @param locator The input ResultSet
	 */
	public SampleGeneratorOp(/*,StatsContainer stats*/){
		//this.stats=stats;
	}
	
	/**
	 * Performs the Keep Top operation
	 * 
	 * @param count The number of records to keep

	 * @return The produced ResultSet
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI compute(int tableID/*,String ssid*/) throws Exception{
		try{
			long start=Calendar.getInstance().getTimeInMillis();
			
			Object synchWriter=new Object();
			long readerstart=Calendar.getInstance().getTimeInMillis();
			//SortWorker worker=new SortWorker(inFileName, outFileName, key, order, stats, synchWriter, ComparisonMethod.PROVIDED_MODE, Mode.COMPARE_STRINGS);
			//SortWorker worker=new SortWorker(inFileName, outFileName, key, order, stats, synchWriter, ComparisonMethod.DETECT_MODE, null);
			//SortWorker worker=new SortWorker(inFileName, outFileName, key, order, stats, synchWriter, ComparisonMethod.FULL_COMPARISON, null);
			SampleGeneratorWorker worker = new SampleGeneratorWorker(tableID, synchWriter);
			worker.start();
			URI outLocator = null;
			synchronized (synchWriter) {
				while((outLocator = worker.getLocator()) == null) 
					synchWriter.wait();
			}
			//long readerstop = Calendar.getInstance().getTimeInMillis();
			//stats.info("Time to initialize: " + (readerstop-readerstart));
			//	stats.timeToComplete(Calendar.getInstance().getTimeInMillis()-start);
			//	stats.producedResults(0);
			//	stats.timeToFirst(0);
			//	stats.productionRate(0);
			return outLocator;
		}catch(Exception e){
			logger.error("Could not start background process of keep top operator. Throwing Exception", e);
			throw new Exception("Could not start background process of keep top operator");
		}
	}
}
