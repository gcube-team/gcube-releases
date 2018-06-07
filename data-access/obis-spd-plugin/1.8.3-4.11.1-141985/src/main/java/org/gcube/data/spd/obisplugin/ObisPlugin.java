package org.gcube.data.spd.obisplugin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.obisplugin.capabilities.OccurrencesCapabilityImpl;
import org.gcube.data.spd.obisplugin.search.ResultItemSearch;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	protected static Logger logger = LoggerFactory.getLogger(ObisPlugin.class); 
	//protected ObisNameMapping nameMapping;
	protected OccurrencesCapabilityImpl occurrencesInterface;
	//protected ObisClassification obisClassification;
	protected static final SimpleDateFormat sdf = new SimpleDateFormat();

	private String baseUrl = "http://api.iobis.org/";

	@SuppressWarnings("serial")
	@Override
	public Set<Capabilities> getSupportedCapabilities() {
		return new HashSet<Capabilities>(){{
			//add(Capabilities.NamesMapping);
			add(Capabilities.Occurrence);
			//add(Capabilities.Classification);
		}};
	}

	@Override
	public String getRepositoryName() {
		return Constants.REPOSITORY_NAME;
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
		
		//nameMapping = new ObisNameMapping();
		occurrencesInterface = new OccurrencesCapabilityImpl(baseUrl);
		//obisClassification = new ObisClassification();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() throws Exception {
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
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void searchByScientificName(String searchTerm, final ObjectWriter<ResultItem> writer, Condition... properties) {
		logger.debug("starting the search for obisPlugin word: "+searchTerm);
		
		try {
			new ResultItemSearch(baseUrl, searchTerm, properties).search(writer,Constants.QUERY_LIMIT);
		} catch (Exception e) {
			logger.debug("searchByScientificName failed",e);
		} 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MappingCapability getMappingInterface() {
		return null;
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
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RepositoryInfo getRepositoryInfo() {
		return REPOSITORY_INFO;
	}

}
