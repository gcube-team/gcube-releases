package org.gcube.data.spd.gbifplugin;

import java.util.Collections;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.data.spd.gbifplugin.capabilities.OccurrencesCapabilityImpl;
import org.gcube.data.spd.gbifplugin.search.ResultItemSearch;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;


public class GBIFPlugin extends AbstractPlugin{

	private GCUBELog logger = new GCUBELog(GBIFPlugin.class); 
	
	
	private OccurrencesCapability occurrencesCapability;
	private String baseURL;
	
		
	@Override
	public void initialize(ServiceEndpoint resource) throws Exception {
		baseURL = resource.profile().accessPoints().iterator().next().address();
		occurrencesCapability = new OccurrencesCapabilityImpl(baseURL);
		setUseCache(true);
		super.initialize(resource);
	}
	
	@Override
	public Set<Capabilities> getSupportedCapabilities() {
		return Collections.singleton(Capabilities.Occurrence);
	}


	

	@Override
	public OccurrencesCapability getOccurrencesInterface() {
		return occurrencesCapability;
	}

	@Override
	public String getRepositoryName() {
		return "GBIF";
	}

	@Override
	public String getDescription() {
		return "A plugin for GBIF interaction";
	}

	@Override
	public void searchByScientificName(String word, ObjectWriter<ResultItem> writer,
			Condition... properties) {
		logger.debug("starting the search for gbifPlugin with word "+word);
		try {
			new ResultItemSearch(baseURL, word, properties).search(writer,Constants.QUERY_LIMIT);
		} catch (Exception e) {
			logger.debug("searchByScientificName failed",e);
			writer.write(new StreamBlockingException(Constants.REPOSITORY_NAME, word));
		}
	}


	@Override
	public RepositoryInfo getRepositoryInfo() {
		return new RepositoryInfo(
				"http://www.gbif.org/fileadmin/templates/main/images/logo_leaf.gif", 
				"http://www.gbif.org/",
				"The Global Biodiversity Information Facility (GBIF) was established by governments in 2001 to encourage free and open access to biodiversity data, " +
				"via the Internet. Through a global network of countries and organizations, GBIF promotes and facilitates the mobilization, access, " +
				"discovery and use of information about the occurrence of organisms over time and across the planet");
	}
	
}
