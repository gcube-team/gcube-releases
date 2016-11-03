package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.duplicategenerator;

import gr.uoa.di.madgik.grs.proxy.IProxy.ProxyType;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.proxy.http.HTTPWriterProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.Generator;

import java.io.File;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author UoA
 */
public class DuplicateGeneratorOp {
	/**
	 * Logger used by this class
	 */
	private Logger logger = LoggerFactory.getLogger(DuplicateGeneratorOp.class.getName());

	/**
	 * stats
	 */
	//private StatsContainer stats=null;
	
	/**
	 * Creates a new {@link DuplicateGeneratorOp}
	 * 
	 * @param locator The input ResultSet
	 */
	public DuplicateGeneratorOp(/*StatsContainer stats*/){
		//this.stats=stats;
	}
	
	/**
	 * Performs the Keep Top operation
	 * 
	 * @param count The number of records to keep

	 * @return The produced ResultSet
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI compute(int count, String[] fieldNames, String objIdFieldName, String objRankFieldName, Generator<? extends Object>[] fieldGenerators, boolean singleField, boolean onlyFinalEvent, double duplicateProbability, Integer seed, ProxyType proxyType, File outFile) throws Exception{
		try{
			RecordDefinition[] defs=null;
			if(singleField == false) {
		    	FieldDefinition[] fieldDefs = new StringFieldDefinition[fieldNames.length];
		    	for(int i = 0; i < fieldNames.length; i++)
		    	fieldDefs[i] = new StringFieldDefinition(fieldNames[i]);
		    //	stringFieldDef.setCompress(true);
		    	defs = new RecordDefinition[]{new GenericRecordDefinition(fieldDefs)};
		    }else {
		    	defs = new RecordDefinition[]{new GenericRecordDefinition(new FieldDefinition[]{new StringFieldDefinition()})};
		    }
			
			IWriterProxy producerProxy = null;
			switch(proxyType)
			{
			case Local:
				producerProxy = new LocalWriterProxy();
				break;
			case TCP:
				producerProxy = new TCPWriterProxy();
				break;
			case HTTP:
				producerProxy = new HTTPWriterProxy();
				break;
			}
			IRecordWriter<Record> writer = new RecordWriter<Record>(producerProxy, defs, 100,
					RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
			DuplicateGeneratorWorker worker = new DuplicateGeneratorWorker(writer, count, fieldNames, objIdFieldName, objRankFieldName, fieldGenerators, singleField, onlyFinalEvent, duplicateProbability, seed, outFile);
			worker.start();
			//long readerstop = Calendar.getInstance().getTimeInMillis();
			//stats.info("Time to initialize: " + (readerstop-readerstart));
			//	stats.timeToComplete(Calendar.getInstance().getTimeInMillis()-start);
			//	stats.producedResults(0);
			//	stats.timeToFirst(0);
			//	stats.productionRate(0);
			return writer.getLocator();
		}catch(Exception e){
			logger.error("Could not start background process of keep top operator. Throwing Exception", e);
			throw new Exception("Could not start background process of keep top operator");
		}
	}
}
