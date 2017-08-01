/**
 * 
 */
package org.gcube.documentstore.persistence;

import org.gcube.documentstore.records.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 */
public class PersistenceNoInsert extends PersistenceBackend {


	private static final Logger logger = LoggerFactory
			.getLogger(PersistenceNoInsert.class);

		/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception {
		logger.trace("PersistenceNoInsert prepareConnection" );
	}

	@Override
	protected void openConnection() throws Exception {
		logger.trace("PersistenceNoInsert openConnection" );

	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reallyAccount(Record record) throws Exception {		
		logger.trace("PersistenceNoInsert reallyAccount" );
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		logger.trace("PersistenceNoInsert close" );
		
	}
	@Override
	protected void closeAndClean() throws Exception {
		logger.trace("PersistenceNoInsert closeAndClean" );
	}

	@Override
	protected void closeConnection() throws Exception {
		logger.trace("PersistenceNoInsert closeConnection" );
	};

	



}
