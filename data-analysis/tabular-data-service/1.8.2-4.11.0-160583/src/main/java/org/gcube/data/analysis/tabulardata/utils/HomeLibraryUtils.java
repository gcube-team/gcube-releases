package org.gcube.data.analysis.tabulardata.utils;

/*
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQuery;
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQueryBuilder;*/

public class HomeLibraryUtils {

/*	private static final Logger logger = LoggerFactory.getLogger(HomeLibraryUtils.class);
	
	private static final String SERVICE_NAME =  "TabularData"; 
	private static final String ID_PROPERTY_KEY =  "identifier"; 
	private static final String TABULAR_RESOURCE_ITEM_TYPE =  "TabularResourceItem"; 
	private static final String FLOW_ITEM_TYPE =  "TabularResourceItem"; 
	
	public static void createTabularResource(TabularResource tabularResource){
		try {
			Workspace ws = HomeLibrary.getHomeManagerFactory()
					.getHomeManager()
					.getHome(AuthorizationProvider.instance.get().getUser())
					.getWorkspace();
			
			if (!ws.exists(SERVICE_NAME, ws.getApplicationArea().getId()))
				ws.createFolder(SERVICE_NAME, "tabular data application folder", ws.getApplicationArea().getId());
			
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ID_PROPERTY_KEY, String.valueOf(tabularResource.getId()));			
			List<String> scopes = new ArrayList<String>();
			scopes.add(ScopeProvider.instance.get());
			ws.createGcubeItem(tabularResource.getName(), "empty description", scopes, AuthorizationProvider.instance.get().getUser(), 
					tabularResource.getTabularResourceType()==TabularResourceType.STANDARD?TABULAR_RESOURCE_ITEM_TYPE:FLOW_ITEM_TYPE, properties, ws.getApplicationArea().getId());
		} catch (Exception e) {
			logger.warn("Problem storing the tabularResource on Workspace",e);
			throw new RuntimeException("error creating item on Workspace",e);
		}

	}
	
	public static void updateTabularResource(TabularResource tabularResource){
		try {
			Workspace ws = HomeLibrary.getHomeManagerFactory()
					.getHomeManager()
					.getHome(AuthorizationProvider.instance.get().getUser())
					.getWorkspace();
			
			if (!ws.exists(SERVICE_NAME, ws.getApplicationArea().getId()))
				ws.createFolder(SERVICE_NAME, "tabular data application folder", ws.getApplicationArea().getId());
						
			SearchQuery query = new SearchQueryBuilder().ofType(TABULAR_RESOURCE_ITEM_TYPE).contains(ID_PROPERTY_KEY, String.valueOf(tabularResource.getId())).build();
			List<GCubeItem> items = ws.searchGCubeItems(query);
			if (items.size()>1) throw  new Exception("found more items with the same id");
			if (items.size()==0) throw  new Exception("item not found");
						
			if (!items.get(0).getName().equals(tabularResource.getName()))
				items.get(0).rename(tabularResource.getName());
						
		} catch (Exception e) {
			logger.warn("Problem storing the tabularResource on Workspace",e);
			throw new RuntimeException("error creating item on Workspace",e);
		}

	}
	
	public static void removeTabularResource(TabularResource tabularResource){
		try {
			Workspace ws = HomeLibrary.getHomeManagerFactory()
					.getHomeManager()
					.getHome(AuthorizationProvider.instance.get().getUser())
					.getWorkspace();

			SearchQuery query = new SearchQueryBuilder().ofType(TABULAR_RESOURCE_ITEM_TYPE).contains(ID_PROPERTY_KEY, String.valueOf(tabularResource.getId())).build();
			List<GCubeItem> items = ws.searchGCubeItems(query);
			if (items.size()>1) throw  new Exception("found more items with the same id");
			if (items.size()==0) throw  new Exception("item not found");
			
			ws.removeItem(items.get(0).getId());
						
		} catch (Exception e) {
			logger.warn("Problem storing the tabularResource on Workspace",e);
			throw new RuntimeException("error creating item on Workspace",e);
		}

	}*/
}
