/**
 * 
 */
package org.gcube.data.spd.obisplugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.obisplugin.data.SearchFilters;
import org.gcube.data.spd.obisplugin.pool.PluginSessionPool;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ObisOccurrencesInterface extends OccurrencesCapability {

	protected static final Set<Conditions> SUPPORTED_PROPERTIES = new HashSet<Conditions>(Arrays.asList(Conditions.values()));
	protected Logger logger = Logger.getLogger(ObisOccurrencesInterface.class);
	
	protected PluginSessionPool sessionPool;

	/**
	 * @param sessionPool
	 */
	public ObisOccurrencesInterface(PluginSessionPool sessionPool) {
		this.sessionPool = sessionPool;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Conditions> getSupportedProperties() {
		return SUPPORTED_PROPERTIES;
	}

	@Override
	public void searchByScientificName(String word, final ObjectWriter<OccurrencePoint> writer, Condition... properties) {
		final PluginSession session = sessionPool.checkOut();
		try {
			SearchFilters filters = new SearchFilters(properties);
			logger.trace("filters: "+filters);
			ObisClient.searchByScientificName(session, word, filters, new Writer<ResultItem>() {

				@Override
				public boolean write(ResultItem item) {
					for (Product product:item.getProducts()){
						if (product.getType()==ProductType.Occurrence) {
							String key = product.getKey();
							getOccurrencePoints(session, key, writer);
						}
					}
					return writer.isAlive();
				}
			});
		} catch (Exception e) {
			logger.debug("searchByScientificName failed",e);
		} finally {
			sessionPool.checkIn(session);
		}

	}


	@Override
	public void getOccurrencesByIds(ClosableWriter<OccurrencePoint> writer, Iterator<String> reader) {		
		final PluginSession session = sessionPool.checkOut();
		try {
			while(reader.hasNext() && writer.isAlive()){
				String id = reader.next();
				try {
					OccurrencePoint occurrencePoint = ObisClient.getOccurrenceById(session, id);
					if (occurrencePoint!=null) writer.write(occurrencePoint);
				} catch (Exception e) {
					logger.debug("searchByScientificName failed",e);
				}
			}

			writer.close();
		} finally {
			sessionPool.checkIn(session);
		}

	}


	@Override
	public void getOccurrencesByProductKeys(ClosableWriter<OccurrencePoint> writer, Iterator<String> reader) {
		PluginSession session = sessionPool.checkOut();

		try {
			while(reader.hasNext() && writer.isAlive()){
				String key = reader.next();
				getOccurrencePoints(session, key, writer);
			}
			writer.close();
		} finally {
			sessionPool.checkIn(session);
		}
	}

	protected void getOccurrencePoints(PluginSession session, String key, final ObjectWriter<OccurrencePoint> writer)
	{
		try {
			ObisClient.getOccurrences(session, key, new Writer<OccurrencePoint>() {

				@Override
				public boolean write(OccurrencePoint item) {
					writer.write(item);
					return writer.isAlive();
				}
			});
		} catch (Exception e) {
			logger.error("Error getting occurrence points for key "+key, e);
		}
	}



}
