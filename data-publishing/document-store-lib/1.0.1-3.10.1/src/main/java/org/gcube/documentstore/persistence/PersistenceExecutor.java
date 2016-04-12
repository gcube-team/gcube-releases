/**
 * 
 */
package org.gcube.documentstore.persistence;

import org.gcube.documentstore.records.Record;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface PersistenceExecutor {

	public void persist(Record... records)throws Exception;
	
}
