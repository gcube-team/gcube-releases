package gr.uoa.di.madgik.rr.element.query;

import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;
import gr.uoa.di.madgik.rr.element.data.DataCollection;
import gr.uoa.di.madgik.rr.element.data.DataLanguage;
import gr.uoa.di.madgik.rr.element.search.Field;
import gr.uoa.di.madgik.rr.element.search.Presentable;
import gr.uoa.di.madgik.rr.element.search.Searchable;
import gr.uoa.di.madgik.rr.element.search.index.DataSource;
import gr.uoa.di.madgik.rr.element.search.index.DataSourceDao;
import gr.uoa.di.madgik.rr.element.search.index.DataSourceService;
import gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainer.FieldType;
import gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainerDao;
import gr.uoa.di.madgik.rr.utils.DatastoreHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryHelper
{
	private static final Logger logger = LoggerFactory
			.getLogger(QueryHelper.class);
	
	public static Map<String, String> getAllCollections(String scope) throws ResourceRegistryException{
		Map<String, String> collections = new HashMap<String, String>();
		
		List<DataCollection> cols = DataCollection.getAllCollections(true);
		
		for(DataCollection col : cols)
		{
			logger.debug("    col : " + col.getID() + " , " + col.getName() + " with scopes : " + col.getScopes());
			if(scope!=null && !col.getScopes().contains(scope)) continue;
			
			collections.put(col.getID(), col.getName());
		}
		
		logger.info("all collections in Resource Registry are : " + collections);
		return collections;
	}
	
	public static Map<String, String> getAllCollectionsTypes(String scope) throws ResourceRegistryException{
		Map<String, String> collections = new HashMap<String, String>();
		
		List<DataCollection> cols = DataCollection.getAllCollections(true);
		
		for(DataCollection col : cols)
		{
			logger.debug("    col : " + col.getID() + " , " + col.getName() + " with scopes : " + col.getScopes() + " type : " + col.getCollectionType());
			if(scope!=null && !col.getScopes().contains(scope)) continue;
			
			String type = col.getCollectionType();
			if (type == null || type.trim().length() == 0)
				type = "non opensearch";
			
			collections.put(col.getID(), type);
		}
		
		logger.info("all collections in Resource Registry are : " + collections);
		return collections;
	}
	
	public static Map<String, String> getAllSearchableCollections(String scope) throws ResourceRegistryException{
		Map<String, String> allAvailableCollections = getAllCollections(scope);
		
		logger.info("all available collections for scope : " + scope + " in Resource Registry are : " + allAvailableCollections);
		
		Set<String> allSearchableCollectionsIDs = getAllSearchableFieldsPerCollection(scope).keySet();
		
		logger.info("all searchable collection ids for scope : " + scope + " in Resource Registry are : " + allSearchableCollectionsIDs);
		
		Map<String, String> allSearchableCollections = new HashMap<String, String>();
		
		for (String collID : allSearchableCollectionsIDs){
			String collName = allAvailableCollections.get(collID);
			allSearchableCollections.put(collID, collName);
		}
		
		logger.info("all collections in Resource Registry are : " + allSearchableCollections);
		return allSearchableCollections;
	}
	
	public static Map<String, Set<String>> getAllSearchableFieldsPerCollection(String scope) throws ResourceRegistryException{
		
		Map<String, Set<String>> fieldsPerCollection = new HashMap<String, Set<String>>();
		
		List<Field> fields = Field.getAll(true);
		for (Field field : fields){
			
			for (Searchable searchable : field.getSearchables()){
				String collection = searchable.getCollection();
				
				logger.debug(" field : " + field.getName() + " searchable : " + searchable.getID() + "   col : " + collection + " with scopes : " + searchable.getDatasourceScopes() + " in ? : " + searchable.getDatasourceScopes().contains(scope));
				
				if(scope!=null && !searchable.getDatasourceScopes().contains(scope)) continue;
				
				
				
				if (!fieldsPerCollection.containsKey(collection)){
					fieldsPerCollection.put(collection, new HashSet<String>());
				}
				
				fieldsPerCollection.get(collection).add(field.getName());
			}
		}
		
		logger.info("all (searchable) fields for scope : " + scope + " in Resource Registry are : " + fieldsPerCollection);
		
		return fieldsPerCollection;
	}
	
	public static Map<String, Set<String>> getAllPresentableFieldsPerCollection(String scope) throws ResourceRegistryException{
		
		Map<String, Set<String>> fieldsPerCollection = new HashMap<String, Set<String>>();
		
		List<Field> fields = Field.getAll(true);
		for (Field field : fields){
			
			for (Presentable presentable : field.getPresentables()){
				String collection = presentable.getCollection();
				
				logger.debug(" field : " + field.getName() + " presentable : " + presentable.getID() + "   col : " + collection + " with scopes : " + presentable.getDatasourceScopes());
				
				if(scope!=null && !presentable.getDatasourceScopes().contains(scope)) continue;
				
				
				
				if (!fieldsPerCollection.containsKey(collection)){
					fieldsPerCollection.put(collection, new HashSet<String>());
				}
				
				fieldsPerCollection.get(collection).add(field.getName());
			}
		}
		
		logger.info("all (presentable) fields in Resource Registry are : " + fieldsPerCollection);
		
		return fieldsPerCollection;
	}
	
	public static Map<String, Set<String>> getCollectionLangsByFieldRelation(Map<String, List<String>> fieldRelationMap, List<String> projections, String scope) throws ResourceRegistryException
	{
		logger.info("getting collection languages by field relation. fieldRelationMap : " + fieldRelationMap + " projections : " + projections + " scope : " + scope);
		Map<String, Set<String>> lngs=new HashMap<String, Set<String>>();
		List<DataCollection> cols = DataCollection.getAllCollections(true);
		logger.info("all collections : ");
		for(DataCollection col : cols)
		{
			logger.debug("    col : " + col.getID() + " , " + col.getName() + " with scopes : " + col.getScopes());
			if(scope!=null && !col.getScopes().contains(scope)) continue;
			
			Set<String> langByFieldRelationCol  = getLanguageByFieldRelationCol(fieldRelationMap, col.getID(), projections, scope);
			logger.debug("     langByFieldRelationCol : " + langByFieldRelationCol);
			
			lngs.put(col.getID(), langByFieldRelationCol);
		}
		
		logger.info("getting collection languages by field relation. fieldRelationMap : " + fieldRelationMap + " projections : " + projections + " scope : " + scope + " Found : " + lngs);
		
		return lngs;
	}
	
	public static Set<String> getLanguageByFieldRelationCol(Map<String, List<String>> fieldRelationMap, String collection, List<String> projections, String scope) throws ResourceRegistryException
	{
		logger.info("getting languages by field relation collection(1). fieldRelationMap : " + fieldRelationMap + " collection : " + collection + " projections : " + projections + " scope : " + scope);
		Set<String> lngs=null;
		for(Map.Entry<String,List<String>> m : fieldRelationMap.entrySet())
		{
			logger.debug("  ~>examining field relation : " + m);
			
			Set<String> l=getLanguageByFieldRelationCol(m.getKey(), m.getValue(), collection, projections, scope);
			
			logger.debug("  ~>languages by field relation col : " + l);
			
			if(lngs==null) lngs=new HashSet<String>(l);
			else lngs.retainAll(l);
		}
		
		logger.info("getting languages by field relation collection(1). fieldRelationMap : " + fieldRelationMap + " collection : " + collection + " projections : " + projections + " scope : " + scope + " Found : " + lngs);
		if(lngs==null) lngs=new HashSet<String>();
		return lngs;
	}
	
	public static Set<String> getCollectionByFieldRelationLang(Map<String, List<String>> fieldRelationMap, String language, List<String> projections, String scope) throws ResourceRegistryException
	{
		Set<String> cols=null;
		for(Map.Entry<String,List<String>> m : fieldRelationMap.entrySet())
		{
			Set<String> c=getCollectionByFieldRelationLang(m.getKey(), m.getValue(), language, projections, scope);
			if(cols==null) cols=new HashSet<String>(c);
			else cols.retainAll(c);
		}
		if(cols==null) cols=new HashSet<String>();
		return cols;
	}
	
	public static Set<String> getLanguageByFieldRelationCol(String field, List<String> relations, String collection, List<String> projections, String scope) throws ResourceRegistryException
	{
		
		logger.debug("getting languages by field realtion collection(2). relations : " + relations + " collection : " + collection + " projections : " + projections + " scope : " + scope);
		
		List<DataSource> indexes = DataSource.queryByFieldIDAndTypeAndCollection(true, field, collection, FieldType.Searchable);
		logger.debug("indexes found for field : " + field + " collection : " + collection + " type : " + FieldType.Searchable + ". number of indexes found : " + indexes.size());

		HashMap<String, List<DataSource>> x=new HashMap<String, List<DataSource>>();
		
		for(DataSource ind : indexes)
		{
			
			logger.debug("    index : " + ind.getID() + " , type : " + ind.getType() + " scopes : " + ind.getScopes());
			if(scope!=null && !ind.getScopes().contains(scope)) { 
				logger.debug("    index : " + ind.getID() + " is skipped because scope : " + scope + " is not contained in  : " + ind.getScopes());
				continue;
			}
			Set<String> languages = ind.getLanguagesForCollectionAndSearchableField(collection, field);
			logger.debug("    languages for collection and searchable field. collection : " + collection + " , field : " + field + " =>  languages : " + languages);
			
			for(String lang : languages)
			{
				if(!x.containsKey(lang)) x.put(lang, new ArrayList<DataSource>());
				x.get(lang).add(ind);
			}
		}
		
		logger.debug("datasources per language");
		for (Map.Entry<String, List<DataSource>> e : x.entrySet()){
			List<String> dsList = new ArrayList<String>();
			for (DataSource ds : e.getValue())
				dsList.add(ds.getID() + " " + ds.getCapabilities());
			
			
			logger.debug("     language : " + e.getKey() + " -> datasources (id, capabilities): " + dsList);
		}
		

		Set<String> lngs=new HashSet<String>();
		for(String lang : x.keySet())
		{
			boolean include=true;
			for(String relation : relations)
			{
				boolean found=false;
				for(DataSource index : x.get(lang))
				{
					if(index.getCapabilities().contains(relation))
					{
						found=true;
						break;
					}
				}
				if(!found)
				{
					include=false;
					break;
				}
			}
			if(include) lngs.add(lang);
		}
		
		logger.debug("getting languages by field realtion collection(2). relations : " + relations + " collection : " + collection + " projections : " + projections + " scope : " + scope + ". Found : " + relations);
		
		return lngs;
	}	
	
	public static Set<String> getCollectionByFieldRelationLang(String field, List<String> relations, String language, List<String> projections, String scope) throws ResourceRegistryException
	{
		logger.info("getting collection by field realtion lang. " + "field : " + field + "relations : " + relations + " language : " + language + " projections : " + projections + " scope : " + scope);
		
		List<DataSource> indexes = DataSource.queryByFieldIDAndTypeAndLanguage(true, field, language, FieldType.Searchable);
		logger.info("indexes found for field : " + field + " language : " + language + " type : " + FieldType.Searchable);
		
		HashMap<String, List<DataSource>> x=new HashMap<String, List<DataSource>>();
		
		for(DataSource ind : indexes)
		{
			logger.debug("    index : " + ind.getID() + " , type : " + ind.getType() + " scopes : " + ind.getScopes());
			
			if(scope!=null && !ind.getScopes().contains(scope)) continue;
			Set<String> collections = ind.getLanguagesForCollectionAndSearchableField(language, field);
			logger.debug("    collections for languages and searchable field. collection : " + language + " , field : " + field + " =>  languages : " + collections);
			
			for(String col : collections)
			{
				if(!x.containsKey(col)) x.put(col, new ArrayList<DataSource>());
				x.get(col).add(ind);
			}
		}
		
		logger.info("datasources per language");
		for (Map.Entry<String, List<DataSource>> e : x.entrySet()){
			List<String> dsList = new ArrayList<String>();
			for (DataSource ds : e.getValue())
				dsList.add(ds.getID() + " " + ds.getCapabilities());
			
			
			logger.info("     language : " + e.getKey() + " -> datasources (id, capabilities): " + dsList);
		}

		Set<String> cols=new HashSet<String>();
		for(String col: x.keySet())
		{
			boolean include=true;
			for(String relation : relations)
			{
				boolean found=false;
				for(DataSource index : x.get(x))
				{
					if(index.getCapabilities().contains(relation))
					{
						found=true;
						break;
					}
				}
				if(!found)
				{
					include=false;
					break;
				}
			}
			if(include) cols.add(col);
		}
		
		logger.info("getting collection by field realtion lang. " + "field : " + field + "relations : " + relations + " language : " + language + " projections : " + projections + " scope : " + scope + ". Found : " + cols);
		
		return cols;
	}	
	
	public static Map<String, Set<Field>> getCollectionFieldsByPresentationInfo(Set<String> info) throws ResourceRegistryException 
	{
		Map<String, Set<Field>> res = new HashMap<String, Set<Field>>();
		List<Field> allFields = Field.getAll(true);
		for(Field f : allFields)
		{
			for(Presentable p : f.getPresentables())
			{
				boolean found = true;
				for(String pi : info)
				{
					if(!p.getPresentationInfo().contains(pi)) 
					{
						found = false;
						break;
					}
				}
				if(found == true)
				{
					if(!res.containsKey(p.getCollection())) res.put(p.getCollection(), new HashSet<Field>());
					res.get(p.getCollection()).add(f);
				}
			}
		}
		return res;
	}
	
	public static Set<Field> getFieldsByPresentationInfo(Set<String> info) throws ResourceRegistryException
	{
		Set<Field> res = new HashSet<Field>();
		List<Field> allFields = Field.getAll(true);
		for(Field f : allFields)
		{
			for(Presentable p : f.getPresentables())
			{
				for(String pi : info)
				{
					if(p.getPresentationInfo().contains(pi)) 
					{
						res.add(f);
						break;
					}
				}
			}
		}
		return res;
	}
	
	public static Set<String> getPresentationInfoOfField(Field f) throws ResourceRegistryException
	{
		Set<String> info = new HashSet<String>();
		if(f.getPresentables().isEmpty()) return info;
		info.addAll(f.getPresentables().iterator().next().getPresentationInfo());
		for(Presentable p : f.getPresentables())
		{
			Set<String> toDelInfo = new HashSet<String>();
			for(String i : info)
			{
				if(!p.getPresentationInfo().contains(i)) toDelInfo.add(i);
			}
			info.removeAll(toDelInfo);
			if(info.isEmpty()) return info;
		}
		return info;
	}
	
	public static Set<DataCollection> getExternalCollectionsOfScope(String scope) throws ResourceRegistryException {
		Set<String> colIds = new HashSet<String>();
		Set<DataCollection> cols = new HashSet<DataCollection>();
		List<Field> allFields = Field.getAll(true);
		List<DataSource> allDs = DataSource.getAll(true);
		List<DataSource> dsToRemove = new ArrayList<DataSource>();
		for(DataSource ds : allDs)
			if(!ds.getScopes().contains(scope)) dsToRemove.add(ds);
		allDs.removeAll(dsToRemove);
		
		for(DataSource ds : allDs) 
		{
			if(!ds.getScopes().contains(scope)) continue;
			
			for(Field f : allFields) 
			{
				for(Searchable s : f.getSearchables()) 
				{
					if(ds.getDataSourceServices() == null) continue;
					boolean foundService = false;
					for(DataSourceService service : ds.getDataSourceServices())
					{
						if(service.getID().equals(s.getLocator()))
						{
							foundService = true;
							break;
						}
					}
					if(foundService == false) continue;
					if(ds.getType().isExternal()) colIds.add(s.getCollection());
				}
				for(Presentable p : f.getPresentables()) 
				{
					boolean foundService = false;
					for(DataSourceService service : ds.getDataSourceServices())
					{
						if(service.getID().equals(p.getLocator()))
						{
							foundService = true;
							break;
						}
					}
					if(foundService == false) continue;
					if(!ds.getScopes().contains(scope)) continue;
					if(ds.getType().isExternal()) colIds.add(p.getCollection());
				}
			}
		}
		for(String colId : colIds)
		{
			DataCollection col = new DataCollection();
			col.setID(colId);
			if(!col.load(true)) logger.info("Could not load collection with id " + colId);
			else cols.add(col);
		}
		return cols;
	}
	
	public static Set<DataCollection> getExternalCollections() throws ResourceRegistryException {
		Set<String> colIds = new HashSet<String>();
		Set<DataCollection> cols = new HashSet<DataCollection>();
		List<Field> allFields = Field.getAll(true);
		List<DataSource> allDs = DataSource.getAll(true);
		
		for(DataSource ds : allDs) {
			for(Field f : allFields) 
			{
				for(Searchable s : f.getSearchables()) 
				{
					if(ds.getDataSourceServices() == null) continue;
					boolean foundService = false;
					for(DataSourceService service : ds.getDataSourceServices())
					{
						if(service.getID().equals(s.getLocator()))
						{
							foundService = true;
							break;
						}
					}
					if(foundService == false) continue;
					if(ds.getType().isExternal()) colIds.add(s.getCollection());
				}
				for(Presentable p : f.getPresentables()) 
				{
					if(ds.getDataSourceServices() == null) continue;
					boolean foundService = false;
					for(DataSourceService service : ds.getDataSourceServices())
					{
						if(service.getID().equals(p.getLocator()))
						{
							foundService = true;
							break;
						}
					}
					if(foundService == false) continue;
					if(ds.getType().isExternal()) colIds.add(p.getCollection());
				}
			}
		}
		for(String colId : colIds)
		{
			DataCollection col = new DataCollection();
			col.setID(colId);
			if(!col.load(true)) logger.info("Could not load collection with id " + colId);
			else cols.add(col);
		}
		return cols;
	}
	
	public static Set<String> getCapabilitiesByFieldCollection(Field f, String collection) throws ResourceRegistryException 
	{
		Set<String> capabilities = new HashSet<String>();
		for(Searchable s : f.getSearchables()) 
		{
			if(s.getCapabilities().isEmpty()) s.load(true);
			if(s.getCollection().equals(collection)) capabilities.addAll(s.getCapabilities());
		}
		return capabilities;
	}
	
	public static List<String> getSourceIdsForFieldRelationCollectionLanguage(String field, String relation, String collection, String language, String scope) throws ResourceRegistryException
	{
		logger.info("getting source ids for field : " + field + ", relation : " + relation + ", collection : " + collection + ", language : " + language + ", scope : " + scope);
		List<String> sources=new ArrayList<String>();
		long fieldRetrieveStart = Calendar.getInstance().getTimeInMillis();
		Field f=new Field();
		f.setID(field);
		if(!f.exists()) throw new ResourceRegistryException("field with id "+field+" does not exist");
		f = Field.getById(true, field);
		long fieldRetrieveEnd = Calendar.getInstance().getTimeInMillis();
		long processSearchablesStart = Calendar.getInstance().getTimeInMillis();
		for(Searchable s : f.getSearchables())
		{
			logger.info("    checking searchable : " + s.getID());
			
			logger.info("    scope : " + scope);
			logger.info("    datasource scopes : " + s.getDatasourceScopes());
			
			logger.info("    relation : " + relation);
			logger.info("    capabilities : " + s.getCapabilities());
			
			logger.info("    collection : " + collection);
			logger.info("    collections : " + s.getCollection());
			
			if(scope!=null && !s.getDatasourceScopes().contains(scope)) continue;
			if(!s.getCapabilities().contains(relation)) continue;
			if(!s.getCollection().equals(collection)) continue;
			Set<String> lngs = DataLanguage.getLanguages(collection, field);
			logger.info("    language : " + language);
			logger.info("    languages : " + lngs);
			
			if(lngs==null) continue;
			if(!lngs.contains(language)) continue;

			logger.debug("searchable : " + s.getLocator() + " added!");
			sources.add(s.getLocator());
		}
		long processSearchablesEnd = Calendar.getInstance().getTimeInMillis();
		
		logger.info("getting source ids for field : " + field + ", relation : " + relation + ", collection : " + collection + ", language : " + language + " returned : " + sources);
		
		return sources;
	}
	
	public static HashMap<String, HashSet<String>> getProjectionsPerSource(Set<String> sources, Set<String> projectionsNeeded, HashMap<String, HashSet<String>> colLangs, String scope) throws ResourceRegistryException
	{
		HashMap<String, HashSet<String>> ret=new HashMap<String, HashSet<String>>();
	//	List<DataSource> all = DataSource.getAll(true);
		Set<IDaoElement> allDatasources = null;
		Set<IDaoElement> allFieldInfo = null;
		try 
		{
			allDatasources = DatastoreHelper.getItems(DatastoreType.LOCAL, DataSourceDao.class);
			allFieldInfo = DatastoreHelper.getItems(DatastoreType.LOCAL, FieldIndexContainerDao.class);
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Could not load data source information", e);
		}
		for(IDaoElement indEl : allDatasources)
		{
			DataSourceDao ind = (DataSourceDao)indEl;
			if(scope!=null && !ind.getScopes().contains(scope)) continue;
			boolean foundService = false;
			String foundServiceId = null;
			if(ind.getBoundDataSourceServices() == null) continue;
			for(String service : ind.getBoundDataSourceServices())
			{
				if(sources.contains(service)) //assumption: references are service ids
				{
					foundService = true;
					foundServiceId = service;
					break;
				}
			}
			if(foundService == false) continue;
			if(!ret.containsKey(foundServiceId)) ret.put(foundServiceId, new HashSet<String>());
			for(String projection : projectionsNeeded)
			{
				if(QueryHelper.IndexCompliesForPresentableForAllCollLanguages(ind, allFieldInfo, projection, colLangs)) ret.get(ind.getID()).add(projection);
			}
		}
		return ret;
	}
	
	private static boolean IndexCompliesForPresentableForAllCollLanguages(DataSourceDao index, Set<IDaoElement> allFieldInfo, String field, HashMap<String, HashSet<String>> colLangs)
	{
		HashMap<String, HashSet<String>> indColLangs=new HashMap<String, HashSet<String>>();
		for(String containerId : index.getFields())
		{
			FieldIndexContainerDao container = null;
			for(IDaoElement fic : allFieldInfo)
			{
				if(((FieldIndexContainerDao)fic).getID().equals(containerId))
				{
					container = (FieldIndexContainerDao)fic;
					break;
				}
			}
			if(!container.getFieldType().equalsIgnoreCase("p")) continue;
			if(!indColLangs.containsKey(container.getCollection())) indColLangs.put(container.getCollection(), new HashSet<String>());
			indColLangs.get(container.getCollection()).add(container.getLanguage());
		}
		if(colLangs.size()>indColLangs.size()) return false;
		for(Map.Entry<String, HashSet<String>> entry : colLangs.entrySet())
		{
			if(!indColLangs.containsKey(entry.getKey())) return false;
			for(String l : entry.getValue())
			{
				if(!indColLangs.get(entry.getKey()).contains(l)) return false;
			}
		}
		return true;
	}
	
	public static DataSource GetSourceById(String id) throws ResourceRegistryException 
	{
		return DataSource.getById(true, id);
	}
	
	public static DataSourceService GetSourceServiceById(String id) throws ResourceRegistryException
	{
		return DataSourceService.getById(true, id);
	}

	public static String GetFieldNameById(String id) throws ResourceRegistryException 
	{
		return Field.getFieldNameById(id);
	}
	
	public static List<DataSource> GetSources(String type) throws ResourceRegistryException 
	{
		if(type.equals(DataSource.class.getName()))
			return new ArrayList<DataSource>(DataSource.getAll(true));
		return null;
	}
}
