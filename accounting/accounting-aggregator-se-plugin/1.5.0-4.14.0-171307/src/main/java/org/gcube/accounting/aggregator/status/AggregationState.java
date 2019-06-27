package org.gcube.accounting.aggregator.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public enum AggregationState {
	/**
	 * The Aggregation has been started
	 */
	RESTARTED, 
	/**
	 * The Aggregation has been started
	 */
	STARTED, 
	/**
	 * Original Records were aggregated. 
	 * Original Records and Aggregated ones has been saved on a local files
	 */
	AGGREGATED,
	/**
	 * Original Records has been deleted from DB.
	 */
	DELETED,
	/**
	 * Aggregated Records has been saved on DB and the backup file has been deleted 
	 */
	ADDED,
	/**
	 * The backup file of Original Records has been saved on Workspace and the local file has been deleted
	 */
	COMPLETED;
	
	private static Logger logger = LoggerFactory.getLogger(AggregationState.class);
	
	public static boolean canContinue(AggregationState effective, AggregationState desired) throws Exception{
		if(effective == desired){
			return true;
		}else{
			if(effective.ordinal() > desired.ordinal()){
				logger.debug("{} is {}. The already reached value to continue is {}. The next step has been already done. It can be skipped.", 
						AggregationState.class.getSimpleName(), effective.name(), desired.name());
				return false;
			}else{
				String error = String.format("%s is %s which is lower than the required value to continue (%s). This is really strange and should not occur. Please contact the administrator.", 
						AggregationState.class.getSimpleName(), effective.name(), desired.name());
				throw new Exception(error);
			}
		}
	}
	
}
