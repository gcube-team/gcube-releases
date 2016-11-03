package gr.uoa.di.madgik.searchlibrary.operatorlibrary.sort;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.comparator.CompareTokens;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.comparator.ComparisonMode;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.ComparisonMethod;

import java.net.URI;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class performs a sorting based on some key on its input {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
 * and produces a output {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet} sorted in the specified order.
 * 
 * @author UoA
 */
public class SortOp {
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(SortOp.class.getName());
	
	/**
	 * The locator of the input
	 */
	private URI inLocator = null;
	
	/**
	 * Statistics
	 */
	private StatsContainer stats=null;
	
	/**
	 * The default timeout used by the {@link IRecordWriter} and the {@link RandomReader}. Currently set to 60.
	 */
	private static final long TimeoutDef = 60;
	/**
	 * The default timeout unit used by the {@link RecordWriter} and the {@link RandomReader}. The current default unit is seconds.
	 */
	private static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	
	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;
	
	/**
	 * Creates a new {@link SortOp} operating on the identified input with default timeout
	 * 
	 * @param inLocator The locator identifying the input
	 * @param stats statistics
	 */
	public SortOp(URI inLocator, StatsContainer stats) {
		this.inLocator = inLocator;
		this.stats = stats;
	}
	
	/**
	 * 
	 * @param inLocator The locator identifying the input with configurable timeout
	 * @param stats statistics
	 * @param timeout The timeout to be used both by the reader and the writer
	 * @param timeUnit The unit of the timeout to be used
	 */
	public SortOp(URI inLocator, StatsContainer stats, long timeout, TimeUnit timeUnit) {
		this(inLocator, stats);
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	/**
	 * Performs the sorting operation using default method
	 * 
	 * @param key The key to base the sorting on 
	 * @param order The order of the sorting. This can be one of {@link CompareTokens#ASCENDING_ORDER} and {@link CompareTokens#DESCENDING_ORDER}
	 * @return The locator of the produced ResultSet
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI compute(String key, short order) throws Exception {
		return compute(key, order, ComparisonMethod.FULL_COMPARISON, null, SortAlgorithm.OFFLINE);
	}
	
	/**
	 * Performs the sorting operation
	 * 
	 * @param keyFieldName The name of the {@link Field} which will be used as a sort key
	 * @param order The order of the sorting. This can be one of {@link CompareTokens#ASCENDING_ORDER} and {@link CompareTokens#DESCENDING_ORDER}
	 * @param comparisonMethod The method of comparison. This can be one of {@link ComparisonMethod.PROVIDED_MODE}, {@link ComparisonMethod#DETECT_MODE} and {@link ComparisonMethod#FULL_COMPARISON}
	 * @param comparisonMode The mode of comparison, provided only if comparisonMethod={@link ComparisonMethod#PROVIDED_MODE}. 
	 * This can be one of {@link ComparisonMode#COMPARE_LONGINTS}, {@link ComparisonMode#COMPARE_DOUBLES}, {@link ComparisonMode#COMPARE_DATES} and {@link ComparisonMode#COMPARE_STRINGS}
	 * @return The locator of the produced ResultSet
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI compute(String keyFieldName, short order, ComparisonMethod comparisonMethod, ComparisonMode comparisonMode, SortAlgorithm algorithm) throws Exception{
	
		try{
			long readerstart=Calendar.getInstance().getTimeInMillis();
			RandomReader<Record> reader = new RandomReader<Record>(inLocator);
			IRecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), reader);
		//	ValueExtractor ve = new GCubeValueExtractor();
		//	ve.setExtractionExpression(key);
			SortWorker worker = null;
			if(algorithm == SortAlgorithm.OFFLINE)
				worker = new OfflineSortWorker<Record>(reader, writer, keyFieldName, order, comparisonMethod, comparisonMode, timeout, timeUnit, stats);
			else
				worker = new OnlineSortWorker<Record>(reader, writer, keyFieldName, order, comparisonMethod, comparisonMode, timeout, timeUnit, stats);
			new Thread(worker).start();
			long readerstop = Calendar.getInstance().getTimeInMillis();
			logger.info("Time to initialize: " + (readerstop-readerstart));
			stats.timeToInitialize(readerstop-readerstart);
			return writer.getLocator();
		}catch(Exception e){
			logger.error("Could not start background process of sort. Throwing Exception", e);
			throw new Exception("Could not start background process of sort");
		}
	}
	
	public static void main(String[] args) throws Exception {

	//	Logger stats = LoggerFactory.getLogger(SortOp.class.getName());
	//	SortOp s = new SortOp(stats);
		//s.compute("/home/gerasimos/workspace/Ops/Input/newInput1k.xml", "/home/gerasimos/workspace/Ops/Input/Output.xml",  "/book/intField", CompareTokens.ASCENDING_ORDER,
		//		ComparisonMethod.PROVIDED_MODE, ComparisonMode.COMPARE_LONGINTS);
		//s.compute("/home/gerasimos/workspace/Ops/Input/newInput1k.xml", "/home/gerasimos/workspace/Ops/Input/Output.xml",  "/book/intField", CompareTokens.ASCENDING_ORDER,
		//		ComparisonMethod.DETECT_MODE, null);
		//s.compute("/home/gerasimos/workspace/Ops/Input/input100k.xml", "/home/gerasimos/workspace/Ops/Input/Output.xml",  "/book/price", CompareTokens.ASCENDING_ORDER,
		//		ComparisonMethod.FULL_COMPARISON, null);
	}
}
