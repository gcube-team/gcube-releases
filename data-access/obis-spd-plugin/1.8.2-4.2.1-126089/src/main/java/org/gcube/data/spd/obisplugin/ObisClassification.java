/**
 * 
 */
package org.gcube.data.spd.obisplugin;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.obisplugin.pool.PluginSessionPool;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ObisClassification extends ClassificationCapability {

	protected Logger logger = Logger.getLogger(ObisClassification.class);
	protected PluginSessionPool sessionPool;

	/**
	 * @param sessionPool
	 */
	public ObisClassification(PluginSessionPool sessionPool) {
		this.sessionPool = sessionPool;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Conditions> getSupportedProperties() {
		return Collections.emptySet();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void searchByScientificName(String word, final ObjectWriter<TaxonomyItem> writer, Condition... properties) {
		PluginSession session = sessionPool.checkOut();

		try {
			ObisClient.getTaxonByScientificNames(session, word, new Writer<TaxonomyItem>() {

				@Override
				public boolean write(TaxonomyItem item) {
					writer.write(item);
					return writer.isAlive();
				}
			});
		} catch (Exception e) {
			logger.error("Error retrieving taxon with word \""+word+"\"", e);
		} finally {
			sessionPool.checkIn(session);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TaxonomyItem> retrieveTaxonChildrenByTaxonId(String taxonId) throws IdNotValidException, ExternalRepositoryException {
		PluginSession session = sessionPool.checkOut();

		try {
			int id = converId(taxonId);
			return ObisClient.getChildrenTaxon(session, id);
		} catch (SQLException e) {
			logger.error("Error retrieving TaxonChildsByTaxonId", e);
			throw new ExternalRepositoryException(e);
		} finally {
			sessionPool.checkIn(session);
		}
	}

	/**
	 * {@inheritDoc}					writer.put(new StreamException());
	 */
	@Override
	public void retrieveTaxonByIds(Iterator<String> reader, ClosableWriter<TaxonomyItem> writer) {
		PluginSession session = sessionPool.checkOut();

		try {
			while(reader.hasNext() && writer.isAlive()) {
				try {
					String taxonId = reader.next();
					int id = converId(taxonId);
					TaxonomyItem item = ObisClient.getTaxonById(session, id);
					writer.write(item);
				} catch (Exception e) {
					logger.error("Error retrieving TaxonById", e);
				} 
			}
		} finally {
			sessionPool.checkIn(session);
		}

	}

	@Override
	public TaxonomyItem retrieveTaxonById(String taxonId) throws IdNotValidException {
		PluginSession session = sessionPool.checkOut();
		int id = converId(taxonId);
		try {
			TaxonomyItem item = ObisClient.getTaxonById(session, id);
			return item;
		} catch (IdNotValidException inve)
		{
			logger.error("Error retrieving TaxonById", inve);
			throw inve;
		} catch (Exception e) {
			logger.error("Error retrieving TaxonById", e);
			return null;
		} finally {
			sessionPool.checkIn(session);
		}
	}

	protected int converId(String taxonId) throws IdNotValidException
	{
		try {
			return Integer.parseInt(taxonId);
		} catch(NumberFormatException nfe)
		{
			logger.error("Invalid id "+taxonId, nfe);
			throw new IdNotValidException();
		}
	}

}
