package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.scope.api.ScopeProvider;

public class GetChildren {
	static Workspace ws = null;
	private static String WS_USER="leonardo.candela";
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		createLibrary();

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {
//		ScopeProvider.instance.set("/gcube");
				ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
				
				Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome(WS_USER).getWorkspace();
				

//				WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-AlieiaVRE");
//				WorkspaceFolder target = (WorkspaceFolder) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-FARM-AlieiaVRE");
//				
				
				WorkspaceSharedFolder sharefolder =(WorkspaceSharedFolder) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-FARM-AlieiaVRE");
System.out.println(sharefolder.getUsers().size());

List<String> users = sharefolder.getUsers();
for(String user: users){
	System.out.println(user);
}
				
				//				System.out.println(folder.getPath());
//				
//				List<WorkspaceItem> children = folder.getChildren();
//				for (WorkspaceItem child: children){
//					System.out.println(child.getPath());
////					System.out.println(child.getRemotePath());
//					ws.moveItem(child.getId(), target.getId());
//				}
//		ws = HomeLibrary
//				.getHomeManagerFactory()
//				.getHomeManager()
//				.getHome("valentina.marioli").getWorkspace();
//
//
//		WorkspaceItem folder = ws.getRoot();
//		System.out.println(folder.getPath());
//		List<? extends WorkspaceItem> children = folder.getChildren();
//		for(WorkspaceItem child: children){
//			System.out.println(child.getPath(	));
//			
//		}
				
				
//				HashMap<String,FolderConfiguration> folderConfigurations=new HashMap<>();
//				folderConfigurations.put("be451663-4d4f-4e23-a2c8-060cf15d83a7", new FolderConfiguration()); //NETCDF DATASETS
//		 
//		 
//				TransferRequestServer server=null;
//				for(Entry<String,FolderConfiguration> entry:folderConfigurations.entrySet()){			
//					try{
//						handleFolder(entry.getKey(),entry.getValue(),server);
//					}catch(WorkspaceException e){
//						System.err.println("WORKSPACE EXC ");
//						e.printStackTrace(System.err);
//					}catch(HomeNotFoundException e){
//						System.err.println("WORKSPACE EXC ");
//						e.printStackTrace(System.err);
//					}catch(InternalErrorException e){
//						System.err.println("WORKSPACE EXC ");
//						e.printStackTrace(System.err);
//					}catch(UserNotFoundException e){
//						System.err.println("WORKSPACE EXC ");
//						e.printStackTrace(System.err);
//					}catch(Exception e){
//						System.err.println("UNEXPECTED EXC");
//						e.printStackTrace(System.err);
//					}
//				}
				
	}

}
