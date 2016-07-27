package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorObjectFactory.BatchPoolType;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EnvironmentalLogicManager {

	
	
	final static Logger logger= LoggerFactory.getLogger(EnvironmentalLogicManager.class);
	private static GenericObjectPool batchPool=new GenericObjectPool(new BatchGeneratorObjectFactory(BatchPoolType.LOCAL));
	private static GenericObjectPool remotePool=new GenericObjectPool(new BatchGeneratorObjectFactory(BatchPoolType.REMOTE));
	
	
	
	
	
	static{
		try{
		batchPool.setLifo(false);
		batchPool.setMaxActive(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.LOCAL_BATCH_POOL_SIZE));
		batchPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
			for(int i =0;i<batchPool.getMaxActive();i++){
				batchPool.addObject();
			}
			logger.debug("Added "+batchPool.getMaxActive()+" objects to local generator pool");
		}catch(Exception e){
			logger.error("Unable to init batch pool",e);
		}
		try{
			remotePool.setLifo(false);
			remotePool.setMaxActive(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.REMOTE_BATCH_POOL_SIZE));
			remotePool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
				for(int i =0;i<remotePool.getMaxActive();i++){
					remotePool.addObject();
				}
				logger.debug("Added "+remotePool.getMaxActive()+" objects to remote generator pool");
			}catch(Exception e){
				logger.error("Unable to init remote pool",e);
			}
	}
	public static BatchGeneratorI getBatch(String submissionBackend) throws Exception{
		if(submissionBackend.equalsIgnoreCase(ServiceContext.getContext().getName()))		
			return (BatchGeneratorI) batchPool.borrowObject();
		else return (BatchGeneratorI) remotePool.borrowObject();
	}
	public static void leaveBatch(BatchGeneratorI theBatch)throws Exception{
		switch (theBatch.getType()){
		case LOCAL : batchPool.returnObject(theBatch);
					break;
		default : remotePool.returnObject(theBatch);
		}
	}
	
}
