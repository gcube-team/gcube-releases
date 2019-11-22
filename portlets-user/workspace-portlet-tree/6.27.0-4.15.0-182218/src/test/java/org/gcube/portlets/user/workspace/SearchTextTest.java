//TODO IT MUST BE MOVED TO SHUB
///**
// * 
// */
//package org.gcube.portlets.user.workspace;
//
//import java.util.List;
//
//import org.apache.log4j.Logger;
//import org.gcube.common.homelibrary.home.HomeLibrary;
//import org.gcube.common.homelibrary.home.workspace.Workspace;
//import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.common.scope.impl.ScopeBean;
//import org.gcube.portlets.user.workspace.client.model.FileGridModel;
//
///**
// * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
// * Jul 3, 2013
// *
// */
//
//
///*
// * 
// * 
// * IT MUST BE MOVED TO SHUB
// * 
// * 
// * 
//public class SearchTextTest {
//	
//	
//	protected static Logger logger = Logger.getLogger(SearchTextTest.class);
//	private static String text = "1_Networking Activities";
//	public static String DEFAULT_SCOPE = "/gcube/devsec/devVRE"; //PRODUCTION
//	
//	public static String USER = "francesco.mangiacrapa"; //PRODUCTION
//	
//	static GWTWorkspaceBuilder builder = new GWTWorkspaceBuilder();
//	static Workspace workspace;
//	
//	public static void main(String[] args) {
//
//		try {
////			DEFAULT_SCOPE = "/gcube/devsec";
//			
//			
//			ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
//			ScopeProvider.instance.set(scope.toString());
//			
//			System.out.println("start get workspace\n");
//			
//			workspace = HomeLibrary
//					.getHomeManagerFactory()
//					.getHomeManager()
//					.getHome(USER)
//					.getWorkspace();
//			//
//			
////			List<SearchItem> listSearchItems = ws.searchByName(text);
//			
//			logger.info("Calling search HL..");
//			List<SearchItem> listSearchItems = workspace.searchByName(text, workspace.getRoot().getId());
//			logger.info("HL search returning "+listSearchItems.size()+" items");
//			
//			logger.info("Converting "+listSearchItems.size()+" items");
//			
//			List<FileGridModel> listFileGridModels = builder.buildGXTListFileGridModelItemForSearch(listSearchItems);
//			logger.info("Search objects converted, returning");
//			
//			for (FileGridModel fileGridModel : listFileGridModels) {
//				logger.info(fileGridModel);
//			}
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//
//}
//
//*/
