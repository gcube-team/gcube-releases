package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.randomgenerator;

import gr.uoa.di.madgik.grs.proxy.IProxy.ProxyType;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.Generator;

import java.io.File;
import java.net.URI;
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
public class RandomGeneratorOp {
	/**
	 * Logger used by this class
	 */
	private Logger logger = LoggerFactory.getLogger(RandomGeneratorOp.class.getName());

	/**
	 * stats
	 */
	//private StatsContainer stats=null;
	
	/**
	 * Creates a new {@link RandomGeneratorOp}
	 * 
	 * @param locator The input ResultSet
	 */
	public RandomGeneratorOp(/*,StatsContainer stats*/){
		//this.stats=stats;
	}
	
	/**
	 * Performs the Keep Top operation
	 * 
	 * @param count The number of records to keep

	 * @return The produced ResultSet
	 * @throws Exception An unrecoverable for the operation error ocurred
	 */
	public URI compute(int count, boolean RS, ProxyType proxyType, String[] fieldNames, Generator<? extends Object>[] fieldGenerators, boolean singleField, boolean onlyFinalEvent,
			int id, long timeout, TimeUnit timeUnit, int bufferCapacity, Float threshold, File outFile/*,String ssid*/) throws Exception{
		try{	
			Object synchWriter=new Object();
			//SortWorker worker=new SortWorker(inFileName, outFileName, key, order, stats, synchWriter, ComparisonMethod.PROVIDED_MODE, Mode.COMPARE_STRINGS);
			//SortWorker worker=new SortWorker(inFileName, outFileName, key, order, stats, synchWriter, ComparisonMethod.DETECT_MODE, null);
			//SortWorker worker=new SortWorker(inFileName, outFileName, key, order, stats, synchWriter, ComparisonMethod.FULL_COMPARISON, null);
			RandomGeneratorWorker worker = new RandomGeneratorWorker(count, RS, proxyType, fieldNames, fieldGenerators, singleField, onlyFinalEvent, id, timeout, timeUnit, threshold, bufferCapacity, outFile, synchWriter);
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
			logger.error("Could not start background process of random data generation operator. Throwing Exception", e);
			throw new Exception("Could not start background process of random data generation operator");
		}
	}
}
