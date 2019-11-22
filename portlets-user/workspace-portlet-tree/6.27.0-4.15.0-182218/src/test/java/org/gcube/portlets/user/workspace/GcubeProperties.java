////TODO IT MUST BE MOVED TO SHUB
///**
// * 
// */
//package org.gcube.portlets.user.workspace;
//
//import org.apache.log4j.Logger;
//import org.gcube.common.homelibrary.home.HomeLibrary;
//import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
//import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
//import org.gcube.common.homelibrary.home.workspace.Workspace;
//import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
//import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.common.scope.impl.ScopeBean;
//
///**
// * 
// * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
// * Jun 12, 2015
// */
//public class GcubeProperties {
//	public static Logger logger = Logger.getLogger(GcubeProperties.class);
//	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
//	public static String TEST_USER = "francesco.mangiacrapa";
//	
//	public static void main(String[] args)  {
//		
//		try{
//			ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
//			ScopeProvider.instance.set(scope.toString());
//			Workspace ws = getWorkspace();
//			GCubeItem item = (GCubeItem) ws.getItem("7d5fd78a-6543-4d20-b6e7-9ae490fa1ad8");
//	//		Properties props = item.getProperties();			
////			item.getProperties().addProperty("key06", "value0006");
////			item.getProperties().addProperty("key05", "value0005");
////			item.getProperties().update();
////			
//
//			System.out.println(item.getProperties().getProperties());
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static Workspace getWorkspace() throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException{
//		logger.trace("Get Workspace scope: "+DEFAULT_SCOPE + " username: "+TEST_USER);
//		ScopeProvider.instance.set(DEFAULT_SCOPE);
//		logger.trace("Scope provider instancied");
//		Workspace workspace = HomeLibrary.getUserWorkspace(TEST_USER);
//		return workspace;
//	}
//}
