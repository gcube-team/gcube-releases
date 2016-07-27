/**
 * 
 */
package org.gcube.data.spd.obisplugin;

import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.obisplugin.pool.PluginSessionPool;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ObisNameMapping implements MappingCapability {

	protected Logger logger = Logger.getLogger(ObisNameMapping.class);
	protected PluginSessionPool sessionPool;

	/**
	 * @param sessionPool
	 */
	public ObisNameMapping(PluginSessionPool sessionPool) {
		this.sessionPool = sessionPool;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getRelatedScientificNames(final ObjectWriter<String> writer, String commonName) {
		logger.debug("retrieving mapping for "+commonName);
		PluginSession session = sessionPool.checkOut();
		try {
			ObisClient.getScientificNames(session, commonName, new Writer<String>() {
				
				@Override
				public boolean write(String item) {
					writer.write(item);
					return writer.isAlive();
				}
			});
		} catch (SQLException e) {
			logger.error("An error occurred retrieving the mapping for common name "+commonName, e);
			writer.write(new StreamBlockingException("OBIS",commonName));
		} finally {
			sessionPool.checkIn(session);
		}
		
	}

}
