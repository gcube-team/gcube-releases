package org.gcube.application.framework.search.library.util;

import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.data.DataCollection;
import gr.uoa.di.madgik.rr.element.data.DataLanguage;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;
import gr.uoa.di.madgik.rr.element.search.Field;
import gr.uoa.di.madgik.rr.element.search.Presentable;
import gr.uoa.di.madgik.rr.element.search.Searchable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;









//import org.apache.xpath.XPathAPI;
import org.gcube.application.framework.core.cache.CachesManager;
import org.gcube.application.framework.core.cache.factories.GenericResourceCacheEntryFactory;
import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.application.framework.core.util.CacheEntryConstants;
import org.gcube.application.framework.contentmanagement.util.CacheEntryConstants;
import org.gcube.application.framework.core.util.QueryString;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.model.CollectionInfo;
import org.gcube.application.framework.search.library.model.PresentableFieldInfo;
import org.gcube.application.framework.search.library.model.SearchableFieldInfo;
import org.gcube.common.scope.impl.ScopeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 
 * @author rena - NKUA (Nikolas also)
 * 
 */

public class FindFieldsInfo {
	
	public static final String ALL = "ALL";
	public static final String NAME = "NAME";
	public static final String DESCRIPTION = "DESCRIPTION";
	
	public static final String LOCAL_EXCLUDED_FIELDS_PROP_FILEPATH = "/excluded_fields.properties";
	public static final String CATALINA_EXCLUDED_FIELDS_PROP_FILEPATH = System.getProperty("catalina.base")+"/conf"+LOCAL_EXCLUDED_FIELDS_PROP_FILEPATH;
	public static final String EXCLUDED_BROWSABLE_FIELD_NAMES_PROP = "excluded_browsable_field_names";
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(FindFieldsInfo.class);

	/**
	 * Document factory instance
	 */
	public static final DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

	public static List<String> excludedBrowsableFieldNames;

	
	static {
		excludedBrowsableFieldNames = new ArrayList<String>(); //empty initially
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(CATALINA_EXCLUDED_FIELDS_PROP_FILEPATH);
			props.load(in);
		} catch (Exception e) { 
			logger.debug("Couldn't access property file "+CATALINA_EXCLUDED_FIELDS_PROP_FILEPATH
					+", parsing local file "+LOCAL_EXCLUDED_FIELDS_PROP_FILEPATH);
			in = FindFieldsInfo.class.getResourceAsStream(LOCAL_EXCLUDED_FIELDS_PROP_FILEPATH);
			try {				
				props.load(in);
			} catch (Exception e1) {e1.printStackTrace();	}
		}
		if (props.getProperty(EXCLUDED_BROWSABLE_FIELD_NAMES_PROP)!=null)
			excludedBrowsableFieldNames = Arrays.asList(props.getProperty(EXCLUDED_BROWSABLE_FIELD_NAMES_PROP).split(","));
		//remove any whitespaces
		for(int i=0;i<excludedBrowsableFieldNames.size();i++)
			excludedBrowsableFieldNames.set(i, excludedBrowsableFieldNames.get(i).trim());
		logger.debug("Excluding "+excludedBrowsableFieldNames.size()+" browsable fields");
		try {
			in.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	public static HashMap<CollectionInfo, ArrayList<CollectionInfo>> joinDynamicAndStaticConfiguration(
			String scope, boolean refresh) throws InitialBridgingNotCompleteException, InternalErrorException {
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collectionHierarchy = new HashMap<CollectionInfo, ArrayList<CollectionInfo>>();

		// Get collection infos dynamically
		ArrayList<CollectionInfo> allCollections = retrieveCollectionsFieldsInfos(scope);

		if (allCollections == null || allCollections.size() == 0) {
			logger.debug("No available collections returned from Registry!");
			return collectionHierarchy;
		}
		
//		logger.debug("Returned "+allCollections.size()+" collections from registry");
//		for(CollectionInfo ci : allCollections)
//			logger.debug("Name: "+ci.getName()+"\tID: "+ci.getId());
		
		// Get all information for collections by joining static with dynamic configuration
		try {
			HashMap<CollectionInfo, ArrayList<CollectionInfo>> colHierarchy = retrieveCollectionsInformation(scope, allCollections, refresh);
			
			//Sort it as well, before returning it.
			colHierarchy = ArraysComparison.getSortedHashMap(colHierarchy);
			
			return colHierarchy;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}

		return null;
	}

	public static ArrayList<CollectionInfo> retrieveCollectionsFieldsInfos(String scope) throws InitialBridgingNotCompleteException, InternalErrorException {
		try {
			logger.debug("Bridging is about to start...");
			ResourceRegistry.startBridging();
		}
		catch (ResourceRegistryException e1) {
			logger.error("Exception:", e1);
			throw new InternalErrorException(e1.getCause());
		}

		List<DataCollection> flds = null;
		
		try {
			flds = DataCollection.getCollectionsOfScope(true, scope);
			logger.debug("Got "+flds.size()+" collections for scope "+scope+" from registy");
			//the above might contain collections which are not searchable, so intersect them with the ones which are surely searchable.
			String parentScope = new ScopeBean(scope).enclosingScope().toString();
			Map<String,String> allSearchable = QueryHelper.getAllSearchableCollections(parentScope);
			Set<String> allSearchableIDs = allSearchable.keySet(); 
			logger.debug("Got "+allSearchableIDs.size()+" surely searchable fields. Doing an intersection...");
			Iterator <DataCollection> iter = flds.iterator();
			while(iter.hasNext()){
				DataCollection dc = iter.next();
				if(!allSearchableIDs.contains(dc.getID()))
					iter.remove();
			}
		}
		catch (ResourceRegistryException e1) {
			try {
				logger.error("Exception:", e1);
				if(ResourceRegistry.isInitialBridgingComplete() == false)
					throw new InitialBridgingNotCompleteException(e1.getCause());
				else
					throw new InternalErrorException(e1.getCause());
			}
			catch(ResourceRegistryException rre) {
				logger.error("Exception:", rre);
				throw new InternalErrorException(rre.getCause());
			}
		}

		// Retrieve information about the fields of each collection
		ArrayList<CollectionInfo> colInfos = new ArrayList<CollectionInfo>();
		logger.debug("Finding collection infos - number of collections: " + flds.size());
		
		// Retrieve the fields for each collection
		//for (DataCollection item:flds) {
		for (int c = 0; c < flds.size(); c++) {
			DataCollection myCollection = flds.get(c);
			boolean fts = false;
			boolean geospatial = false;
			boolean searchable = true;
			boolean language = false;
			boolean sortable = false;
			String ftsId = "";
			Vector<String> languages = new Vector<String>();
			logger.debug("******************************************** START PROCEDURE *******************************" + c);
			logger.debug("Name of collection: " + myCollection.getName() + " id: " + myCollection.getID());
			try {
				// Get the fields that contain searchable fields in this collection
				//List<Field> fs = Field.getSearchableFieldsOfCollection(true, item.getID());

				Set<String> capabilities = new HashSet<String>();
				capabilities.add(SearchConstants.CommonFieldRelation);
				capabilities.add(SearchConstants.CommonOpenSearchFieldRelation);
				logger.debug("Trying to find searchable fields- id: " + myCollection.getID());
				List<Field> fs = Field.getSearchableFieldsOfCollectionByCapabilities(true, myCollection.getID(), capabilities);

				logger.debug("Finding Field Infos - number of searchable fields: " + fs.size());
				ArrayList<org.gcube.application.framework.search.library.model.Field> fields = new ArrayList<org.gcube.application.framework.search.library.model.Field>();
				for (int i = 0; i < fs.size(); i++) {

					sortable = false;
					logger.debug("Name of Field: " + fs.get(i).getName());
					// create internal field
					org.gcube.application.framework.search.library.model.Field inField = new org.gcube.application.framework.search.library.model.Field();
					inField.setDescription(fs.get(i).getDescription());
					inField.setId(fs.get(i).getID());
					inField.setLabel(fs.get(i).getName());
					inField.setName(fs.get(i).getName());

					// Check for Full Text Search index - check if the name of the field is "AllFields"
					if (fs.get(i).getName().equals(SearchConstants.FTS_Field)) {
						ftsId = fs.get(i).getID();
						logger.debug(": " + fs.get(i).getID());
						inField.setName("Any");
						fts = true;
					}


					// create searchable fields of the field
					Set<Searchable> searchables = fs.get(i).getSearchables();
					for (Searchable se:searchables) {
						SearchableFieldInfo sfi = new SearchableFieldInfo();
						sfi.setCollectionId(se.getCollection());
						// this is wrong - it returns the id of the field
						sfi.setFieldName(se.getField());
						sfi.setId(se.getID());
						sfi.setSortable(se.isOrder());
						Set<String> caps = se.getCapabilities();
						sfi.setIndexCapabilities(caps);
						inField.addSearchable(sfi);
						// check if geospatial search is available - if the capabilities of the field contain the "GEO" capability, then it is
						//						if (sfi.getCollectionId().equals(fs.get(i).getID()) && caps.contains("geo")) {
						//							logger.debug("Found geo capability!");
						//							geospatial = true;
						//							if (caps.size() == 1)
						//								searchable = false;
						//							else
						//								searchable = true;
						//						} else
						//							searchable = true;

						if (sfi.getCollectionId().equals(fs.get(i).getID()) && sfi.isSortable()) {
							sortable = true;
						}
					}

					// create presentable fields of the field
					Set<Presentable> presentables = fs.get(i).getPresentables();
					for (Presentable pr:presentables) {
						PresentableFieldInfo pfi = new PresentableFieldInfo();
						pfi.setCollectionId(pr.getCollection());
						pfi.setFieldName(pr.getField());
						pfi.setId(pr.getID());
						pfi.setSortable(pr.isOrder());
						//pfi.setPresentationInfo(pr.getPresentationInfo());
						Set<String> pres = pr.getPresentationInfo();
						pfi.setPresentationInfo(pres);
						inField.addPresentable(pfi);
					}
					inField.setSearchable(searchable);
					inField.setSortable(sortable);
					/* Inform the index capabilities of the field based on the index capabilities of each search field */
					ArrayList<String> commonCaps = new ArrayList<String>();
					if (inField.getSearchableFields() != null && inField.getSearchableFields().size() > 0) {
						commonCaps.addAll(inField.getSearchableFields().get(0).getIndexCapabilities());
					}
					ArrayList<String> duplicated = commonCaps;
					for (int s = 1; s < inField.getSearchableFields().size(); s++) {
						for (int cc = 0; cc < commonCaps.size(); cc++) {
							if (!inField.getSearchableFields().get(s).getIndexCapabilities().contains(commonCaps.get(cc))) {
								if (duplicated.contains(commonCaps.get(cc))) {
									int ind = commonCaps.indexOf(commonCaps.get(cc));
									duplicated.remove(ind);
								}
							}
						}
					}
					commonCaps = duplicated;

					inField.setIndexCapabilities(commonCaps);
					fields.add(inField);	



				}

				// GEO Field
				capabilities = new HashSet<String>();
				capabilities.add(SearchConstants.GEO_Relation);
				logger.debug("Trying to find Geo relation- id: " + myCollection.getID());
				List<Field> geoF = Field.getSearchableFieldsOfCollectionByCapabilities(true, myCollection.getID(), capabilities);
				org.gcube.application.framework.search.library.model.Field geoField = new org.gcube.application.framework.search.library.model.Field();
				if (geoF != null && geoF.size() != 0) {
					language = true;
					geospatial = true;
					geoField.setId(geoF.get(0).getID());
					geoField.setDescription(geoF.get(0).getDescription());
					geoField.setLabel(geoF.get(0).getName());
					geoField.setName(geoF.get(0).getName());
				} else {
					logger.debug("No geo!- id: " + myCollection.getID());
				}


				/* Languages */
				ArrayList<String> allLanguages = new ArrayList<String>();	// keep all the languages of the collection to this variable
				//if (language) {
				//if (fs.size() > 0) {
				logger.debug("getting languages - id: " + myCollection.getID());
				/* Get the languages per field for the collection and inform the field languages */
				logger.debug(myCollection.getID());
				DataLanguage dataLanguage = DataLanguage.getLanguages(myCollection.getID());
				if (dataLanguage != null) {
					logger.debug("dataLangs " + myCollection.getName());
					HashMap<String, Set<String>> fieldLanguages = dataLanguage.getFieldLanguages();
					logger.debug("Number of languages: " + fieldLanguages.size() + " " + myCollection.getName() + " " + fields.size());
					
					for (String fl:fieldLanguages.keySet()) {
						logger.debug("KeyIs: " + fl + " " + myCollection.getName());
					}
					for (int i = 0; i < fields.size(); i++) {
						Set<String> langs = fieldLanguages.get(fields.get(i).getId());
						if (langs != null) {
							logger.debug("field langs: " + langs.size() + " " + fields.get(i).getId() + myCollection.getName());
							for (String lang:langs) {
								fields.get(i).addLanguage(lang);

								if (!allLanguages.contains(lang))
									allLanguages.add(lang);
							}
						} else 
							logger.debug("field langs NO " + fields.get(i).getId() + myCollection.getName());
					}
					Set<String> langs = fieldLanguages.get(geoField.getId());
					if (langs != null) {
						for (String lang:langs) {
							geoField.addLanguage(lang);

							if (!allLanguages.contains(lang))
								allLanguages.add(lang);
						}
					}
					language = true;
				} else {
					logger.debug("DataLanguages map is null! " + myCollection.getID());
				}
				//}
				//}




				// Presentation Fields

				logger.debug("Trying to find Presentation Fields - id: " + myCollection.getID());
				List<Field> prFs = Field.getPresentableFieldsOfCollection(true, myCollection.getID());
				logger.debug("Finding Presentation Field infor - number of presentation fields: " + prFs.size());
				ArrayList<org.gcube.application.framework.search.library.model.Field> prFields = new ArrayList<org.gcube.application.framework.search.library.model.Field>();
				for (int i = 0; i < prFs.size(); i++) {
					logger.debug("Name of presentation field: " + prFs.get(i).getName());
					// Create internal field
					org.gcube.application.framework.search.library.model.Field inField = new org.gcube.application.framework.search.library.model.Field();
					inField.setDescription(prFs.get(i).getDescription());
					inField.setId(prFs.get(i).getID());
					inField.setLabel(prFs.get(i).getName());
					inField.setName(prFs.get(i).getName());
					// TODO: Is more information needed?

					prFields.add(inField);
				}


				// Browsable Fields
				logger.debug("Trying to find browsable fields - id: " + myCollection.getID());
				List<Field> brFs = Field.getBrowsableFieldsOfCollection(true, myCollection.getID());
				logger.debug("Number of browsable fields found: " + brFs.size());
				brFs = filterOutBrowsableFields(brFs,excludedBrowsableFieldNames);							
				logger.debug("Removed the not allowed browsable fields, new number of browsable fields: "+ brFs.size());
				ArrayList<org.gcube.application.framework.search.library.model.Field> brFields = new ArrayList<org.gcube.application.framework.search.library.model.Field>();
				for (int i = 0; i < brFs.size(); i++) {
					logger.debug("Name of browsable field: " + brFs.get(i).getName());
					// Create internal field
					org.gcube.application.framework.search.library.model.Field inField = new org.gcube.application.framework.search.library.model.Field();
					inField.setDescription(brFs.get(i).getDescription());
					inField.setId(brFs.get(i).getID());
					inField.setLabel(brFs.get(i).getName());
					inField.setName(brFs.get(i).getName());
					// TODO: Is more information needed?

					language = true;
					brFields.add(inField);
				}


				// Create the collection info
				if (language) {
					CollectionInfo colInfo = new CollectionInfo();
					colInfo.setCollectionGroup(false);
					colInfo.setDescription(myCollection.getDescription());
					colInfo.setName(myCollection.getName());
					logger.debug("Item name: " + myCollection.getName());
					colInfo.setId(myCollection.getID());
					logger.debug("Item id: " + myCollection.getID());
					colInfo.setIndices(fields);
					logger.debug("Supports fts: " + fts);
					colInfo.setFts(fts);
					colInfo.setCollectionType(myCollection.getCollectionType());
					colInfo.setGeospatial(geospatial);
					colInfo.setLanguages(allLanguages);
					colInfo.setPresentationFields(prFields);
					colInfo.setBrowsableFields(brFields);
					colInfo.setFtsId(ftsId);
					colInfo.setGeospatialField(geoField);
					logger.debug("Set fts id: " + ftsId);

					//				if (language)
					//					colInfo.setLanguage();

					logger.debug("Adding COLLECTION: " + colInfo.getName());
					colInfos.add(colInfo);
				}
			} catch (ResourceRegistryException e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
				continue;
			}

			logger.debug("******************************************** END PROCEDURE *******************************");
		}

		logger.debug("Number of colINFOS: " + colInfos.size());
		return colInfos;
	}
	
	/**
	 *  @author nikolas 
	 *  this function filters out the browsable fields which are not allowed (within the property file) 
	 *  
	 */
	protected static List<Field> filterOutBrowsableFields(List<Field> initialList, List<String> notAllowedFieldNames ){
		ArrayList<Field> filteredList = new ArrayList<Field>();
		for(int i=0;i<initialList.size();i++)
			if(!notAllowedFieldNames.contains(initialList.get(i).getName()))
				filteredList.add(initialList.get(i));
		return filteredList;
	}
	

	protected static HashMap<CollectionInfo, ArrayList<CollectionInfo>> retrieveCollectionsInformation(
			String vre, List<CollectionInfo> collections, boolean refresh) throws Exception {
		// Reading the static configuration file and initializing the parameters
		logger.debug("About to retrieve the static configuration");
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> colHierarchy;

		QueryString query = new QueryString();
		query.put(org.gcube.application.framework.core.util.CacheEntryConstants.vre, vre);
		query.put(org.gcube.application.framework.core.util.CacheEntryConstants.name, /*SessionConstants.ScenarioSchemaInfo*/ "ScenarioCollectionInfo");

		if (refresh) {
//			CachesManager
//			.getInstance()
//			.getEhcache(
//					org.gcube.application.framework.contentmanagement.util.CacheEntryConstants.genericResourceCache,
//					new GenericResourceCacheEntryFactory()).get(query).setTimeToLive(-1);
			if(CachesManager.getInstance().getGenericResourceCache()!=null)
				CachesManager.getInstance().getGenericResourceCache().remove(query);
		}
		// Retrieving the generic resource:
		List<ISGenericResource> scenarioSchemaInfo = (List<ISGenericResource>) CachesManager.getInstance().getEhcache(CacheEntryConstants.genericResourceCache, new GenericResourceCacheEntryFactory()).get(query).getValue();

		if (scenarioSchemaInfo == null || scenarioSchemaInfo.size() == 0) {
			logger.debug("The scenarioCollectionInfo is null");
			colHierarchy = new HashMap<CollectionInfo, ArrayList<CollectionInfo>>();
			return colHierarchy;
		}
		logger.debug("Printing the static configuration");
		logger.debug("scenarioSchemaInfo.size()"+scenarioSchemaInfo.size());
		for(ISGenericResource gr : scenarioSchemaInfo){
			logger.debug("gr.getBody():"+gr.getBody());
			logger.debug("gr.getDescription():"+gr.getDescription());
			logger.debug("gr.getId():"+gr.getId());
			logger.debug("gr.getName():"+gr.getName());
			logger.debug("gr.getSecondaryType():"+gr.getSecondaryType());
		}
		
		logger.debug(scenarioSchemaInfo.get(0).getBody());
		InputSource in = new InputSource(new StringReader(scenarioSchemaInfo.get(0).getBody()));
		Document doc = dfactory.newDocumentBuilder().parse(in);

		retrieveCollections(doc, collections);
		colHierarchy = retrieveCollectionHierarchy(vre, doc, collections);
		return colHierarchy;
	}

	public static String findCollectionName(String id, String scope) {
		try {
			List<DataCollection> collections = DataCollection.getCollectionsOfScope(true, scope);
			for (int i = 0; i < collections.size(); i++) {
				if (collections.get(i).getID().equals(id))
					return collections.get(i).getName();
			}
		} catch (ResourceRegistryException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
		return null;

	}

	/**
	 * Retrieves the collections by joining information from static and dynamic
	 * configuration.
	 * 
	 * @param doc
	 *            document which holds the collections
	 * @param collections
	 *            the list containing the collections
	 */
	protected static void retrieveCollections(Document doc,
			List<CollectionInfo> collections) {
		int i, n;
		List<CollectionInfo> availableCollections = new ArrayList<CollectionInfo>();
		// Reading the collections and storing them in a list only if they are
		// currently available.
		NodeList res = doc.getElementsByTagName("collection");

		n = res.getLength();
		logger.debug("Retrieving Static Configuration: The number of collections is: "
				+ n);
		for (i = 0; i < n; i++) {
			// String val =
			// res.item(i).getAttributes().getNamedItem("name").getNodeValue();
			String val = res.item(i).getAttributes().getNamedItem("id")
			.getNodeValue();
			logger.debug("The id of the collection read is: " + val);
			CollectionInfo colInfo = null;

			// colInfo = getCollectionInfo(val, collections);
			colInfo = getCollectionInfoById(val, collections);
			if (colInfo == null) {
				logger.debug("Not available collection - omitting it");
				continue;// This collection is not currently available.
			}
			
			logger.debug("Printing collection Information gathered: " );
			logger.debug(colInfo.getName());
			logger.debug(colInfo.getId());
//			if (colInfo.getIndices() != null)
//				logger.debug(colInfo.getIndices().size());
//			if (colInfo.getLanguages() != null)
//				logger.debug(colInfo.getLanguages().size());
			logger.debug("end");

			val = res.item(i).getAttributes().getNamedItem("description")
			.getNodeValue();
			colInfo.setDescription(val);
			val = res.item(i).getAttributes().getNamedItem("recno")
			.getNodeValue();
			colInfo.setRecno(val);
			val = res.item(i).getAttributes().getNamedItem("creationDate")
			.getNodeValue();
			colInfo.setCreationDate(val);
			availableCollections.add(colInfo);
		}
		// Copying available collections to collections
		collections.clear();
		for (CollectionInfo col : availableCollections) {
			collections.add(col);
		}

		// Adding the collection groups to the available collections
		res = doc.getElementsByTagName("collectionsGroup");
		logger.debug("CollectionsGroup size: " + n);
		n = res.getLength();
		for (i = 0; i < n; i++) {
			String val;
			CollectionInfo colInfo = new CollectionInfo();
			try {
				val = res.item(i).getAttributes().getNamedItem("id")
				.getNodeValue();
				colInfo.setId(val);
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
			try {

				val = res.item(i).getAttributes().getNamedItem("name")
				.getNodeValue();
				colInfo.setName(val);
			} catch (Exception e) {
			}
			try {
				val = res.item(i).getAttributes().getNamedItem("description")
				.getNodeValue();
				colInfo.setDescription(val);
			} catch (Exception e) {
			}
			colInfo.setCollectionGroup(true);
			collections.add(colInfo);
		}
		logger.debug("***Number of collections:" + collections.size());
	}

	/**
	 * Retrieves the hierarchical structure of the available collections. Joins
	 * the static and the dynamic search configuration and produces a
	 * hierarchical structure containing information about each collection.
	 * 
	 * @param VREname
	 *            the VRE name of the active VRE
	 * @param doc
	 *            document which holds the collections
	 * @param collections
	 *            the available collections
	 * @return A HashMap containing the Collection Groups as keys the list of
	 *         the collections belonging to each group as values
	 * @throws Exception
	 *             an Exception occurred during processing
	 */
	protected static HashMap<CollectionInfo, ArrayList<CollectionInfo>> retrieveCollectionHierarchy(
			String VREname, Document doc, List<CollectionInfo> collections)
			throws Exception {

		XPath xpath = XPathFactory.newInstance().newXPath();
//		XPathExpression expr = xpath.compile("//VRE");
//		NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		XPathExpression expr = xpath.compile("//collectionsGroup");
		NodeList res = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		
		
		// Retrieving the tree structure of the collection hierarchy
//		NodeList res = XPathAPI.selectNodeList(doc, "//VRE");
//		res = XPathAPI.selectNodeList(doc, "//collectionsGroup");
		int n = res.getLength();
		logger.debug("***** number of collection groups:" + n + "*******");
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> colHierarchy = new HashMap<CollectionInfo, ArrayList<CollectionInfo>>();
		HashMap<String, ArrayList<CollectionInfo>> groupIDsCollections = new HashMap<String, ArrayList<CollectionInfo>>();
		for (int i = 0; i < n; i++) {// for each collection group:
			String val = res.item(i).getAttributes().getNamedItem("id")
			.getNodeValue();
//			NodeList res2 = XPathAPI.selectNodeList(doc,"//collectionsGroup[@id=\"" + val + "\"]/collection");
			XPathExpression expr2 = xpath.compile("//collectionsGroup[@id=\"" + val + "\"]/collection");
			NodeList res2 = (NodeList) expr2.evaluate(doc, XPathConstants.NODESET);
			// colHierachy[i] = new ArrayList<CollectionInfo>();
			// CollectionInfo colInfo = getCollectionInfo(val, collections);
			CollectionInfo colInfoGroup = getCollectionInfoById(val, collections);
			if (colInfoGroup != null) {
				// colHierachy[i].add(colInfo); // Collection Group Name
				for (int j = 0; j < res2.getLength(); j++) {

					CollectionInfo colInfo = getCollectionInfoById(res2.item(j)
							.getAttributes().getNamedItem("id").getNodeValue(),
							collections);
					if (colInfo != null) {
						ArrayList<CollectionInfo> groupCollections = groupIDsCollections
						.get(colInfoGroup.getId());
						if (groupCollections == null)
							groupCollections = new ArrayList<CollectionInfo>();

						groupCollections.add(colInfo);
						groupIDsCollections.put(colInfoGroup.getId(),
								groupCollections);
					}
				}

				if (groupIDsCollections.get(colInfoGroup.getId()) != null)
					colHierarchy.put(colInfoGroup,groupIDsCollections.get(colInfoGroup.getId()));
			}
			if (groupIDsCollections != null && colInfoGroup != null) {
				if (groupIDsCollections.get(colInfoGroup.getId()) != null)
					logger.debug("***collection group " + i + " contains "+ groupIDsCollections.get(colInfoGroup.getId()).size()+ " collections***");
			} else {
				logger.debug("No Available Collections Returned from Registry.");
			}

		}
		return colHierarchy;
	}

	/**
	 * Finds the collection info object of the collection with the given id.
	 * 
	 * @param collectionId
	 *            the id of the collection
	 * @param collections
	 *            a list containing all the collections
	 * @return the CollectionInfo of the collection with the corresponding id
	 */
	protected static CollectionInfo getCollectionInfoById(String colId,
			List<CollectionInfo> collections) {
		CollectionInfo colInfo = null;
		int i, n = collections.size();
		for (i = 0; i < n; i++) {
			logger.debug("*******" + collections.get(i).getId() + " vs. "
					+ colId);
			if (collections.get(i).getId().equals(colId)) {
				colInfo = collections.get(i);
				logger.debug("***collection found****");
				break;
			}
		}
		return colInfo;
	}

	public static ArrayList<org.gcube.application.framework.search.library.model.Field> getCollectionPresentationFields(String colId, ASLSession session) throws InitialBridgingNotCompleteException, InternalErrorException {
		SearchHelper sh = new SearchHelper(session);
		CollectionInfo colI = sh.findCollectionInfo(colId);
		ArrayList<org.gcube.application.framework.search.library.model.Field> fields = colI.getPresentationFields();
		return fields;
	}

	/**
	 * @param colID
	 * @param collections
	 * @return
	 */
	public static CollectionInfo findCollectionInfo(String colID, HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections)
	{
		if (collections != null) {
			for(CollectionInfo colInfo:collections.keySet())
			{
				for(int c2=0; c2 < collections.get(colInfo).size(); c2++)
				{
					logger.info("compare: " + collections.get(colInfo).get(c2).getId() + " with: " + colID);
					if(collections.get(colInfo).get(c2).getId().equals(colID))
					{
						logger.info("equal!!");
						return collections.get(colInfo).get(c2);
					}
				}
			}
		}
		else {
			logger.info("No collections!!");
			return null;
		}
		logger.info("Null!!");
		return null;
	}


	/**
	 * @param term
	 * @param whereToSearch
	 * @param collections
	 * @return
	 */
	public static List<CollectionInfo> searchCollectionInfo(String term, String whereToSearch, HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections)
	{
		term = term.trim().toLowerCase();
		if(term.startsWith("*"))
		{
			term = term.substring(1);
		}
		else
		{
			term = "(\\s|\\p{Punct})" + term;
		}
		if(term.endsWith("*"))
		{
			term = term.substring(0,term.length()-1);
		}
		else
		{
			term = term + "(\\s|\\p{Punct})";
		}
		term = term.replaceAll("\\x2A", ".*");
		term = term.replaceAll("\\x3F", ".");
		Pattern pattern = Pattern.compile(term);

		boolean name = false, descr = false;
		if(whereToSearch.equals(FindFieldsInfo.ALL))
		{
			name = true;
			descr = true;
		}
		else if(whereToSearch.equals(FindFieldsInfo.NAME))
		{
			name = true;
		}
		else if(whereToSearch.equals(FindFieldsInfo.DESCRIPTION))
		{
			descr = true;
		}
		List<CollectionInfo> res = new ArrayList<CollectionInfo>();
		for(CollectionInfo colInfo:collections.keySet())
		{
			for(int c2=0; c2 < collections.get(colInfo).size(); c2++)
			{
				if(name)
				{
					if(pattern.matcher(" " + collections.get(colInfo).get(c2).getName().toLowerCase() + " ").find())
					{
						res.add(collections.get(colInfo).get(c2));
						continue;
					}
				}
				if(descr)
				{
					if(pattern.matcher(" " + collections.get(colInfo).get(c2).getDescription().toLowerCase() + " ").find())
					{
						res.add(collections.get(colInfo).get(c2));
						continue;
					}
				}
			}
		}
		return res;
	}

}
