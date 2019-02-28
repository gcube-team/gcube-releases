package org.gcube.dataharvest.harvester.sobigdata;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.Validate;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueImpl;
import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.gcube.dataharvest.datamodel.HarvestedDataKey;
import org.gcube.dataharvest.harvester.BasicHarvester;
import org.gcube.dataharvest.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanGroup;

/**
 * The Class SoBigDataHarvester.
 *
 * @author Luca Frosini (ISTI-CNR)
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 24, 2018
 */
public abstract class SoBigDataHarvester extends BasicHarvester {
	
	private static Logger logger = LoggerFactory.getLogger(SoBigDataHarvester.class);
	
	//Added by Francesco
	private static final String GROUP_LABEL = "group";
	
	//Added by Francesco
	protected HashMap<String,String> mapSystemTypeToDBEntry;
	
	//Added by Francesco
	protected HashMap<String,String> mapCatalogueGroupToVRE;
	
	//Added by Francesco
	protected HashMap<String,String> mapWsFolderNameToVRE;
	
	//Added by Francesco
	private DataCatalogueFactory catalogueFactory;
	
	protected SortedSet<String> contexts;
	
	/**
	 * Instantiates a new so big data harvester.
	 *
	 * @param start the start
	 * @param end the end
	 * @param catalogueContext the catalogue context
	 * @param vreScopes the contexts
	 * @throws ParseException the parse exception
	 */
	public SoBigDataHarvester(Date start, Date end, SortedSet<String> contexts) throws Exception {
		super(start, end);
		
		this.catalogueFactory = DataCatalogueFactory.getFactory();
		
		String currentContext = Utils.getCurrentContext();
		
		// Truncating the context to the last / (the last / is retained for filtering issues) 
		String baseContext = currentContext.substring(0, currentContext.lastIndexOf("/")+1);
		
		this.contexts = getValidContexts(contexts, baseContext);
		logger.trace("Valid contexts are {}", this.contexts);
		
		initMappingMaps();
		
		
	}
	
	/**
	 * Inits the mapping maps.
	 * @throws Exception 
	 * @throws ObjectNotFound 
	 */
	protected void initMappingMaps() throws ObjectNotFound, Exception {
		Properties properties = AccountingDataHarvesterPlugin.getProperties().get();
		Set<String> keys = properties.stringPropertyNames();
		
		mapSystemTypeToDBEntry = new HashMap<String,String>();
		for(String key : keys) {
			try {
				HarvestedDataKey valueEnum = HarvestedDataKey.valueOf(key);
				mapSystemTypeToDBEntry.put(properties.getProperty(key), valueEnum.name());
			} catch(Exception e) {
				//silent
			}
		}
		
		logger.info("Built from properties the mapping 'SystemType' to 'DB entry' {}", mapSystemTypeToDBEntry);
		
		String currentContext = Utils.getCurrentContext();
		
		//GET CATALOGUE'S GROUPS
		List<String> groups = loadGroupsFromCKAN(currentContext);
		//NORMALIZING THE GROUP NAME TO MATCH WITH VRE NAME
		Map<String,String> mapNormalizedGroups = normalizeGroups(groups);
		logger.debug("Map of Normalized Groups is {} ", mapNormalizedGroups);
		
		//CREATING MAPPING BETWEEN (CATALOGUE GROUP NAME TO VRE NAME)
		mapCatalogueGroupToVRE = new HashMap<String,String>();
		//CREATING MAPPING BETWEEN (WS FOLDER NAME TO VRE NAME)
		mapWsFolderNameToVRE = new HashMap<String,String>();
		Set<String> normalizedGroups = mapNormalizedGroups.keySet();
		for(String context : contexts) {
			String loweredVREName = context.substring(context.lastIndexOf("/") + 1, context.length()).toLowerCase();
			try {
				if(normalizedGroups.contains(loweredVREName)) {
					logger.debug("Normalized Groups matching the lowered VRE name {}", loweredVREName);
					// Creating the map with couple (catalogue group name, scope)
					mapCatalogueGroupToVRE.put(mapNormalizedGroups.get(loweredVREName), context);
				}
				
				mapWsFolderNameToVRE.put(loweredVREName, context);
			} catch(Exception e) {
				// silent
			}
		}
		
		logger.info("Map of Catalogue Groups To VRE is {} ", mapCatalogueGroupToVRE);
		logger.info("Map of (lowered) Ws Folder Name To VRE is {}", mapWsFolderNameToVRE);
		
	}
	
	/**
	 * Normalize groups.
	 *
	 * @author Francesco Mangiacrapa
	 * @param groups the groups
	 * @return the map with couples (normalized group name, group name)
	 */
	private Map<String,String> normalizeGroups(List<String> groups) {
		Map<String,String> listNGroups = new HashMap<String,String>(groups.size());
		for(String group : groups) {
			String normalizedGroup = group;
			if(normalizedGroup.endsWith(GROUP_LABEL)) {
				normalizedGroup = normalizedGroup.substring(0, normalizedGroup.length() - GROUP_LABEL.length());
			}
			normalizedGroup = normalizedGroup.replaceAll("-", "");
			listNGroups.put(normalizedGroup.toLowerCase(), group);
		}
		return listNGroups;
	}
	
	/**
	 * Load groups from ckan.
	 *
	 * @param scope the scope
	 * @return the list
	 */
	private List<String> loadGroupsFromCKAN(String scope) {
		List<String> groups = new ArrayList<String>();
		String ckanURL = "";
		try {
			DataCatalogueImpl utils = catalogueFactory.getUtilsPerScope(scope);
			ckanURL = utils.getCatalogueUrl();
			List<CkanGroup> theGroups = utils.getGroups();
			Validate.notNull(theGroups, "The list of Groups is null");
			for(CkanGroup ckanGroup : theGroups) {
				groups.add(ckanGroup.getName());
			}
		} catch(Exception e) {
			logger.error("Error occurred on getting CKAN groups for scope {} and CKAN URL {}", scope, ckanURL, e);
		}
		
		return groups;
	}
	
	/**
	 * Gets the map catalogue group to vre.
	 *
	 * @return the map catalogue group to vre
	 */
	public HashMap<String,String> getMapCatalogueGroupToVRE() {
		return mapCatalogueGroupToVRE;
	}
	
	/**
	 * @return the mapSystemTypeToDBEntry
	 */
	public HashMap<String,String> getMapSystemTypeToDBEntry() {
		return mapSystemTypeToDBEntry;
	}
	
	/**
	 * @return the mapWsFolderNameToVRE
	 */
	public HashMap<String,String> getMapWsFolderNameToVRE() {
		return mapWsFolderNameToVRE;
	}
	
	/**
	 * Gets the so big data contexts.
	 *
	 * @param contexts the contexts
	 * @param base the base
	 * @return the so big data contexts
	 */
	public SortedSet<String> getValidContexts(Set<String> contexts, String base) {
		SortedSet<String> filteredContext = new TreeSet<>();
		for(String context : contexts) {
			if(context.startsWith(base)) {
				filteredContext.add(context);
			}
		}
		return filteredContext;
	}
	
}
