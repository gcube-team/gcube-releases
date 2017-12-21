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

	private static final Logger logger = LoggerFactory.getLogger(PersistenceNoInsert.class);

	@Override
	protected void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception {
		logger.trace("prepareConnection()");
	}

	@Override
	protected void openConnection() throws Exception {
		logger.trace("openConnection()");
	}

	@Override
	protected void reallyAccount(Record record) throws Exception {
		logger.trace("reallyAccount()");
	}
	
	@Override
	protected void accountWithFallback(Record... records) throws Exception {
		logger.trace("accountWithFallback()");
	}

	@Override
	public void close() throws Exception {
		logger.trace("close()");
	}

	@Override
	protected void clean() throws Exception {
		logger.trace("closeAndClean()");
	}

	@Override
	protected void closeConnection() throws Exception {
		logger.trace("closeConnection()");
	}

	@Override
	public boolean isConnectionActive() throws Exception {
		return true;
	};

}
