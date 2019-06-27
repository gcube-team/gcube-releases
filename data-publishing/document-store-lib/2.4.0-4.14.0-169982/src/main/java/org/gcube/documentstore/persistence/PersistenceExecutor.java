/**
 * 
 */
package org.gcube.documentstore.persistence;

import org.gcube.documentstore.records.Record;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public interface PersistenceExecutor {

	public void persist(Record... records)throws Exception;
	
}
