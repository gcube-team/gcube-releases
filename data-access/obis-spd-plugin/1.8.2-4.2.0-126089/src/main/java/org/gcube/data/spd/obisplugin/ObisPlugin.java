package org.gcube.data.spd.obisplugin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.obisplugin.data.SearchFilters;
import org.gcube.data.spd.obisplugin.pool.DatabaseCredential;
import org.gcube.data.spd.obisplugin.pool.PluginSessionPool;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ObisPlugin extends AbstractPlugin {

	protected static final String LOGO_URL = "http://iobis.org/sites/all/themes/corolla/logo.png";
	protected static final String HOME_URL = "http://iobis.org";
	protected static final String DESCRIPTION = "The Ocean Biogeographic information System (OBIS) seeks to absorb, integrate, and assess isolated datasets into a larger, more comprehensive pictures of life in our oceans. " +
			"The system hopes to stimulate research about our oceans to generate new hypotheses concerning evolutionary processes, species distributions, and roles of organisms in marine systems on a global scale. " +
			"Created by the Census of Marine Life, OBIS is now part of the Intergovernmental Oceanographic Commission (IOC) of UNESCO, under its International Oceanographic Data and Information Exchange (IODE) programme.";
	
	
	protected static final RepositoryInfo REPOSITORY_INFO = new RepositoryInfo(LOGO_URL, HOME_URL, DESCRIPTION);
	protected static final String ENTRY_POINT_NAME = "jdbc";
	protected GCUBELog logger = new GCUBELog(ObisPlugin.class); 
	protected PluginSessionPool sessionPool;
	protected ObisNameMapping nameMapping;
	protected ObisOccurrencesInterface occurrencesInterface;
	protected ObisClassification obisClassification;
	protected static final SimpleDateFormat sdf = new SimpleDateFormat();

	/**
	 * @return the sessionPool
	 */
	public PluginSessionPool getSessionPool() {
		return sessionPool;
	}

	@SuppressWarnings("serial")
	@Override
	public Set<Capabilities> getSupportedCapabilities() {
		return new HashSet<Capabilities>(){{
			add(Capabilities.NamesMapping);
			add(Capabilities.Occurrence);
			add(Capabilities.Classification);
		}};
	}

	@Override
	public String getRepositoryName() {
		return "OBIS";
	}

	@Override
	public String getDescription() {
		return "A plugin for OBIS interaction";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(ServiceEndpoint resource) throws Exception {
		
		setUseCache(true);
		
		DatabaseCredential databaseCredential = getDatabaseCredentials(resource);
		sessionPool = new PluginSessionPool(databaseCredential);
		nameMapping = new ObisNameMapping(sessionPool);
		occurrencesInterface = new ObisOccurrencesInterface(sessionPool);
		obisClassification = new ObisClassification(sessionPool);
	}

	public void initialize(DatabaseCredential databaseCredential) throws Exception {
		
		setUseCache(true);
		sessionPool = new PluginSessionPool(databaseCredential);
		nameMapping = new ObisNameMapping(sessionPool);
		occurrencesInterface = new ObisOccurrencesInterface(sessionPool);
		obisClassification = new ObisClassification(sessionPool);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() throws Exception {
		sessionPool.shutdown(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("serial")
	@Override
	public Set<Conditions> getSupportedProperties() {
		return new HashSet<Conditions>(){{
			add(Conditions.DATE);
			add(Conditions.COORDINATE);
		}};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(ServiceEndpoint resource) throws Exception {
		DatabaseCredential databaseCredential = getDatabaseCredentials(resource);
		sessionPool.setDatabaseCredential(databaseCredential);
	}
	
	protected DatabaseCredential getDatabaseCredentials(ServiceEndpoint resource) throws Exception
	{
		AccessPoint jdbcAccessPoint = null;
		for (AccessPoint accessPoint: resource.profile().accessPoints())
		{
			if (ENTRY_POINT_NAME.equalsIgnoreCase(accessPoint.name())) {
				jdbcAccessPoint = accessPoint;
				break;
			}
		}

		if (jdbcAccessPoint==null) {
			logger.error("AccessPoint with entry name "+ENTRY_POINT_NAME+" not found in the plugin RuntimeResource");
			throw new Exception("AccessPoint with entry name "+ENTRY_POINT_NAME+" not found in the plugin RuntimeResource");
		}
		String password = StringEncrypter.getEncrypter().decrypt(jdbcAccessPoint.password());
		return new DatabaseCredential(jdbcAccessPoint.address(), jdbcAccessPoint.username(), password);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void searchByScientificName(String searchTerm, final ObjectWriter<ResultItem> writer, Condition... properties) {
		logger.debug("starting the search for obisPlugin word: "+searchTerm);
		
		PluginSession session = sessionPool.checkOut();
		try {
			final String credits = getObisCredits();
			SearchFilters filters = new SearchFilters(properties);
			logger.trace("filters: "+filters);
			ObisClient.searchByScientificName(session, searchTerm, filters, new Writer<ResultItem>() {

				@Override
				public boolean write(ResultItem item) {
					item.setCredits(credits);
					writer.write(item);
					return writer.isAlive();
				}
			});

		} catch (Exception e) {
			logger.debug("searchByScientificName failed",e);
		} finally {
			sessionPool.checkIn(session);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MappingCapability getMappingInterface() {
		return nameMapping;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OccurrencesCapability getOccurrencesInterface() {
		return occurrencesInterface;
	}

	protected String getObisCredits()
	{
		StringBuilder credits = new StringBuilder("Intergovernmental Oceanographic Commission (IOC) of UNESCO. The Ocean Biogeographic Information System. Web. http://www.iobis.org. (Consulted on ");
		credits.append(sdf.format(Calendar.getInstance().getTime()));
		credits.append(")");
		return credits.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClassificationCapability getClassificationInterface() {
		return obisClassification;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RepositoryInfo getRepositoryInfo() {
		return REPOSITORY_INFO;
	}

}
