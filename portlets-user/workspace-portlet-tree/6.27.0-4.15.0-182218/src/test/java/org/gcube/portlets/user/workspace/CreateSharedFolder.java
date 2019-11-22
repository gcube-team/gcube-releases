//TODO IT MUST BE MOVED TO SHUB

///**
// * 
// */
//package org.gcube.portlets.user.workspace;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//import org.gcube.common.homelibrary.home.HomeLibrary;
//import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
//import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
//import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
//import org.gcube.common.homelibrary.home.workspace.Workspace;
//import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
//import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
//import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
//import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
//import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
//import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.common.scope.impl.ScopeBean;
//
///**
// * 
// * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it Jul 14, 2015
// */
//public class CreateSharedFolder {
//	
//	protected static Logger logger = Logger.getLogger(CreateSharedFolder.class);
////	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling"; //PRODUCTION
//	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
//	
//	public static String USER = "francesco.mangiacrapa"; //PRODUCTION
//
//	public static void main(String[] args) {
//
//		try {
//			ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
//			ScopeProvider.instance.set(scope.toString());
//			Workspace workspace = HomeLibrary.getHomeManagerFactory()
//					.getHomeManager().getHome(USER)
//					.getWorkspace();
//
//			String folderName = "Test Folder";
//			String desc = "descr";
//			List<String> listLogin = new ArrayList<String>();
//
//			listLogin.add("valentina.marioli");
//			listLogin.add("massimiliano.assante");
//
//			WorkspaceSharedFolder sharedFolder = workspace.createSharedFolder(
//					folderName, desc, listLogin, workspace.getRoot().getId());
//
//			if (sharedFolder == null)
//				System.out.println("shared folder is null");
//
//			System.out
//					.println("Shared folder created: " + sharedFolder.getId());
//		} catch (InsufficientPrivilegesException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ItemAlreadyExistException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (WrongDestinationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ItemNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (WorkspaceFolderNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InternalErrorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (HomeNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UserNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
