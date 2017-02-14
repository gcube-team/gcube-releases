package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.seqgenerator;

import gr.uoa.di.madgik.grs.proxy.IProxy.ProxyType;

import java.net.URI;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Operator class used to perform a keep top operation on an input ResultSet
 * and produce a new output ResultSet with the top
 * records
 * 
 * @author UoA
 */
public class SeqGeneratorOp {
	/**
	 * Logger used by this class
	 */
	//private static GCUBELog log = new GCUBELog(KeepTopOp.class);
	private Logger logger = LoggerFactory.getLogger(SeqGeneratorOp.class.getName());

	/**
	 * stats
	 */
	//private StatsContainer stats=null;
	
	/**
	 * Creates a new {@link SeqGeneratorOp}
	 * 
	 * @param locator The input ResultSet
	 */
	public SeqGeneratorOp(/*,StatsContainer stats*/){
		//this.stats=stats;
	}
	
	/**
	 * Performs the Keep Top operation
	 * 
	 * @param count The number of records to keep

	 * @return The produced ResultSet
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI compute(int count, boolean RS, ProxyType proxyType, boolean singleField, boolean noise, int noiseField, String[] fieldNames, int id, Integer seed, long timeout, TimeUnit timeUnit) throws Exception{
		try{
			long start=Calendar.getInstance().getTimeInMillis();
			
			Object synchWriter=new Object();
			//SortWorker worker=new SortWorker(inFileName, outFileName, key, order, stats, synchWriter, ComparisonMethod.PROVIDED_MODE, Mode.COMPARE_STRINGS);
			//SortWorker worker=new SortWorker(inFileName, outFileName, key, order, stats, synchWriter, ComparisonMethod.DETECT_MODE, null);
			//SortWorker worker=new SortWorker(inFileName, outFileName, key, order, stats, synchWriter, ComparisonMethod.FULL_COMPARISON, null);
			SeqGeneratorWorker worker = new SeqGeneratorWorker(count, RS, proxyType, singleField, noise, noiseField, fieldNames, id, seed, timeout, timeUnit, synchWriter);
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
