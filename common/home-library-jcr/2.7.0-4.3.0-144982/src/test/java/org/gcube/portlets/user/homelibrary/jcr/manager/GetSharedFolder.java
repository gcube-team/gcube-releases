package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
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
import org.gcube.common.homelibrary.home.workspace.catalogue.WorkspaceCatalogue;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQueryBuilder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.scope.api.ScopeProvider;

public class GetSharedFolder {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		createLibrary();

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {
		ScopeProvider.instance.set("/gcube");
		//			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();

//		String id = "ae107e60-fc08-4986-a8e5-e871c55f9d15";
		//		System.out.println(ws.getItem(id).getPath());
		System.out.println(ws.getRoot().getPath());
		
//		ws.getTrash().emptyTrash();

		//		WorkspaceItem myI =	ws.getItemByPath("/Workspace/.catalogue/storage-manager-core-2.4.1-SNAPSHOT.jar");
		//		myI.remove();

				WorkspaceCatalogue catalogue = ws.getCatalogue();
//				catalogue.remove();
				List<WorkspaceItem> children = (List<WorkspaceItem>) catalogue.getChildren(true);
				System.out.println(children.size());
				for(WorkspaceItem item: children){
		
//					System.out.println("Remove " + item.getPath());
//						item.remove();
						JCRWorkspaceItem myitem = (JCRWorkspaceItem) item;
//					System.out.println(myitem.getProperties().getPropertyValue("originalID"));
//						myitem.remove();
		//		
		//			}
				}
			
				
//				System.out.println("****** " + catalogue.getCatalogueItem("/test 2016-09-08 17:37:450001/Tv-Philips-23-pollici-LCD-decoder-mini-4cbff4eb.jpg").getPath());
		WorkspaceItem folder =	ws.getItemByPath("/Workspace/Preprod/aaa/");	
		
//		System.out.println("is hidden?? " + folder.isHidden());
//		WorkspaceItem item =	ws.getItemByPath("/Workspace/Preprod/Listino-Villa-Livia-ITA.jpg");
		WorkspaceFolder dest = (WorkspaceFolder) catalogue.addWorkspaceItem(folder.getId(), catalogue.getId());
//		WorkspaceItem aa = ws.addWorkspaceItemToCatalogue(item.getId(), dest.getId());


		////				System.out.println(folder.getPublicLink(true));
		//		@SuppressWarnings("unchecked")
		List<WorkspaceItem> children1 = (List<WorkspaceItem>) folder.getChildren();
		System.out.println(children1.size());
		for(WorkspaceItem item1: children1){

			//			if (item.getId().equals(id))
			//			if (item.getOwner().getPortalLogin().equals("valentina.marioli")){
//			System.out.println(item1.getId() + " - " + item1.getName());

			
			System.out.println("START ID " + item1.getId());
			System.out.println("TO FOLDER ID " + dest.getId());
			WorkspaceItem myItem = catalogue.addWorkspaceItem(item1.getId(), dest.getId());
			System.out.println("COPY ID " + myItem.getId());
			
			String originalID = catalogue.getWorkspaceItemByCatalogueID(myItem.getId()).getId();
			System.out.println("GET ORIGINAL ID " + originalID);
			if (item1.getId().equals(originalID))
				System.out.println("EQUALS!!");
			else
				System.out.println("DIFFERTENT!!");
			
			List<WorkspaceItem> list = catalogue.getCatalogueItemByWorkspaceID(originalID);
			for(WorkspaceItem item11: list){
				System.out.println("* " + item11.getPath());
			}
			// Create a SearchQuery 
//			SearchQueryBuilder query = new SearchQueryBuilder();
//			 
//			query.contains("originalID",item1.getId());
//			 
//			List<GCubeItem> aa = ws.searchGCubeItems(query.build());
			
			
//			WorkspaceItem myItem = catalogue.addWorkspaceItem(item1.getId(), catalogue.getCatalogueItem("/BBB/").getId());
//			System.out.println(myItem.getPublicLink(true));
			//				JCRWorkspaceItem myitem = (JCRWorkspaceItem) item;
			//				myitem.remove();	
			//			}
		}
		

		

		
//		ws.searchByProperties(properties);

		//		List<WorkspaceItem> children = (List<WorkspaceItem>) ws.getMySpecialFolders().getChildren();
		//		for(WorkspaceItem item: children){
		////			if (item.getId().equals(id))
		//			System.out.println(item.getId() + " - " + item.getName());
		//		}
	}

}
