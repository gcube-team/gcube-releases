package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyzerManager {
	final static Logger logger= LoggerFactory.getLogger(AnalyzerManager.class);
	private static GenericObjectPool analyzerPool=new GenericObjectPool(new AnalyzerFactory());
	
	
	
	
	
	
	static{
		try{
		analyzerPool.setLifo(false);
		analyzerPool.setMaxActive(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.ANALYZER_BATCH_POOL_SIZE));
		analyzerPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
			for(int i =0;i<analyzerPool.getMaxActive();i++){
				analyzerPool.addObject();
			}
			logger.debug("Added "+analyzerPool.getMaxActive()+" objects to source generator pool");
		}catch(Exception e){
			logger.error("Unable to init batch pool",e);
		}
		
	}
	public static Analyzer getBatch() throws Exception{
		return (Analyzer) analyzerPool.borrowObject();
	}
	public static void leaveBatch(Analyzer theBatch)throws Exception{
		analyzerPool.returnObject(theBatch);
	}
	
}
