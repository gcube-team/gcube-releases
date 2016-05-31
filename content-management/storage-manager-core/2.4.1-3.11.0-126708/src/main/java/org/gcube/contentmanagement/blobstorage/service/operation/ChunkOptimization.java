package org.gcube.contentmanagement.blobstorage.service.operation;

import org.gcube.contentmanagement.blobstorage.service.operation.OperationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a simple algorithm for calculating the size of the chunk
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */

public class ChunkOptimization {
	/**
	 * Logger for this class
	 */
	final Logger logger=LoggerFactory.getLogger(ChunkOptimization.class);
	private long fileSize;
	
	public ChunkOptimization(long dimensioneFile){
		if (logger.isDebugEnabled()) {
			logger.debug("ChunkOptimization(long) - Dimensione del file: "
					+ dimensioneFile);
		}
		this.fileSize=dimensioneFile;
	}
	
	public int chunkCalculation(){
		long chunkSize=0;
		if(fileSize<= OperationManager.sogliaDimensioneMinima){
			chunkSize=fileSize;
		}else{
			// numero chunk
			for(int i=OperationManager.sogliaNumeroMinimo; i<OperationManager.sogliaNumeroMassimo; i++){
				chunkSize=(fileSize/i);
				if((chunkSize < OperationManager.sogliaDimensioneMassima) && (chunkSize> OperationManager.sogliaDimensioneMinima)){
					break;
				}else if(chunkSize<OperationManager.sogliaDimensioneMinima){
					chunkSize=OperationManager.sogliaDimensioneMinima;
					break;
				}
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("The chunk size is "+chunkSize);	
		}
		return (int)chunkSize;
	}
}
