package org.gcube.contentmanagement.blobstorage.service.operation;

import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
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
		if(fileSize<= Costants.sogliaDimensioneMinima){
			chunkSize=fileSize;
		}else{
			// numero chunk
			for(int i=Costants.sogliaNumeroMinimo; i<Costants.sogliaNumeroMassimo; i++){
				chunkSize=(fileSize/i);
				if((chunkSize < Costants.sogliaDimensioneMassima) && (chunkSize> Costants.sogliaDimensioneMinima)){
					break;
				}else if(chunkSize<Costants.sogliaDimensioneMinima){
					chunkSize=Costants.sogliaDimensioneMinima;
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
