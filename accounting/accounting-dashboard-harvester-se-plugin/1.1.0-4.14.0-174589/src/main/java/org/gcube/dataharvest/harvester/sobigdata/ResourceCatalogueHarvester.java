package org.gcube.dataharvest.harvester.sobigdata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.gcube.dataharvest.datamodel.HarvestedDataKey;
import org.gcube.dataharvest.utils.DateUtils;
import org.gcube.dataharvest.utils.Utils;
import org.gcube.portlets.user.urlshortener.UrlEncoderUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ResourceCatalogueHarvester.
 *
 * @author Eric Perrone (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 * @author Francesco Mangiacrapa(ISTI - CNR)
 */
public class ResourceCatalogueHarvester extends SoBigDataHarvester {
	
	private static final String AND = " AND ";
	
	public static int ROWS = 500;
	
	private static Logger logger = LoggerFactory.getLogger(ResourceCatalogueHarvester.class);
	
	protected String solrBaseUrl;
	
	/**
	 * Instantiates a new resource catalogue harvester.
	 *
	 * @param start the start
	 * @param end the end
	 * @param catalogueContext the catalogue context
	 * @param contexts the contexts. They are the VREs
	 * @throws Exception the exception
	 */
	public ResourceCatalogueHarvester(Date start, Date end, SortedSet<String> contexts) throws Exception {
		super(start, end, contexts);
	}
	
	/**
	 * Gets the solr base url.
	 *
	 * @return the solr base url
	 */
	
	//TODO @LUCA FROSINI
	protected String getSolrBaseUrl() {
		return "https://ckan-solr-d4s.d4science.org/solr/sobigdata";
	}
	
	@Override
	public List<AccountingRecord> getAccountingRecords() throws Exception {
		
		ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();
		
		//FOR EACH SYSTEM_TYPE
		for(String systemType : mapSystemTypeToDBEntry.keySet()) {
			
			List<String> solrParameters = new ArrayList<String>(1);
			solrParameters.add("extras_systemtype:\"" + systemType + "\"");
			//EXECUTING THE QUERY IN THE PERIOD
			String queryResult = executeQueryFor(solrParameters, start, end, "groups");
			HarvestedDataKey insertDBKey = HarvestedDataKey.valueOf(mapSystemTypeToDBEntry.get(systemType));
			logger.debug("Creating statistics for type {} using db key {}", systemType, insertDBKey);
			
			accountingRecords.addAll(buildListOfHarvestedData(queryResult, insertDBKey));
		}
		
		return accountingRecords;
	}
	
	/**
	 * Builds the list of harvested data.
	 *
	 * @param json the json
	 * @param harvestKey the harvest key
	 * @return the list
	 * @throws Exception the exception
	 */
	private List<AccountingRecord> buildListOfHarvestedData(String json, HarvestedDataKey harvestKey) throws Exception {
		
		JSONObject jsonObject = new JSONObject(json);
		JSONObject responseHeader = jsonObject.getJSONObject("responseHeader");
		int status = responseHeader.getInt("status");
		if(status != 0) {
			throw new Exception("Query Deliverable in error: status " + status);
		}
		
		JSONObject response = jsonObject.getJSONObject("response");
		int numFound = response.getInt("numFound");
		Map<String,Integer> counter = new HashMap<String,Integer>(mapCatalogueGroupToVRE.size() + 1);
		
		for(String groupName : mapCatalogueGroupToVRE.keySet()) {
			counter.put(groupName, 0);
		}
		
		String catalogueContext = Utils.getCurrentContext();
		
		//Counter for default context of accounting
		int catalogueContextCount = 0;
		logger.debug("For {}  has found {} doc/s", harvestKey, numFound);
		if(numFound > 0) {
			
			JSONArray docs = response.getJSONArray("docs");
			for(Object item : docs) {
				JSONObject doc = (JSONObject) item;
				try {
					JSONArray groups = doc.getJSONArray("groups");
					Iterator<Object> git = groups.iterator();
					while(git.hasNext()) {
						String catalogueGroupName = (String) git.next();
						logger.debug("GroupName found {}", catalogueGroupName);
						//counterByGroup(groupItem);
						Integer currentCount = counter.get(catalogueGroupName);
						if(currentCount != null)
							counter.put(catalogueGroupName, currentCount + 1);
						else {
							logger.warn(
									"No mapping found for Catalogue-Group Name {} from VREs. Accounting it in the catalogue context {}",
									catalogueGroupName, catalogueContext);
							catalogueContextCount++;
						}
						
						break; //Accounting the item only in the first group found
					}
				} catch(JSONException x) {
					logger.debug("Document without groups, accounting it in the catalogue context");
					catalogueContextCount++;
				} catch(Exception e) {
					logger.warn("Skipping parsing error", e);
				}
			}
			
		}
		
		ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();
		
		logger.trace("The context {} has count ", catalogueContext, catalogueContextCount);
		
		ScopeDescriptor catalogueScopeDescriptor = AccountingDataHarvesterPlugin.getScopeDescriptor(catalogueContext);
		Dimension dimension = getDimension(harvestKey);
		
		AccountingRecord ar = new AccountingRecord(catalogueScopeDescriptor, instant, dimension, (long) catalogueContextCount);
		accountingRecords.add(ar);
		
		for(String key : counter.keySet()) {
			logger.trace("The group {} has count {}", key, counter.get(key));
			ScopeDescriptor sd = AccountingDataHarvesterPlugin.getScopeDescriptor(mapCatalogueGroupToVRE.get(key));
			AccountingRecord accountingRecord = new AccountingRecord(sd, instant, dimension, (long) counter.get(key));
			accountingRecords.add(accountingRecord);
		}
		
		logger.debug("For {} in the period [from {} to {}] returning accouting data :", harvestKey,
				DateUtils.format(start), DateUtils.format(end), accountingRecords);
		
		return accountingRecords;
	}
	
	/**
	 * Execute query.
	 *
	 * @param solrParameters the solr parameters
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param flValue the fl value
	 * @return the string
	 * @throws Exception the exception
	 */
	//TODO THIS METHOD MUST BE OPTIMIZED USING HttpSolrClient
	//We are not considering the rows (the number of documents returned from Solr by default)
	public String executeQueryFor(List<String> solrParameters, Date startDate, Date endDate, String flValue)
			throws Exception {
		
		String query = getSolrBaseUrl().endsWith("/") ? getSolrBaseUrl() : getSolrBaseUrl() + "/";
		query += "select?";
		
		String q = "";
		//ADDING START AND END DATE IF ARE VALIDS
		if(startDate != null && endDate != null) {
			q += "metadata_created:[" + DateUtils.dateToStringWithTZ(startDate) + " TO "
					+ DateUtils.dateToStringWithTZ(endDate) + "]";
		}
		
		//ADDING PARAMETERS
		if(solrParameters != null && solrParameters.size() > 0) {
			q += q.isEmpty() ? "" : AND;
			
			for(int i = 0; i < solrParameters.size() - 1; i++) {
				q += solrParameters.get(i) + AND;
			}
			
			q += solrParameters.get(solrParameters.size() - 1);
		}
		
		query += "q=" + UrlEncoderUtil.encodeQuery(q) + "&wt=json&indent=true&rows=" + ROWS;
		query += flValue != null && !flValue.isEmpty() ? "&fl=" + UrlEncoderUtil.encodeQuery(flValue) : "";
		logger.debug("\nPerforming query {}", query);
		String jsonResult = Utils.getJson(query);
		logger.trace("Response is {}", jsonResult);
		
		return jsonResult;
	}
	
}
