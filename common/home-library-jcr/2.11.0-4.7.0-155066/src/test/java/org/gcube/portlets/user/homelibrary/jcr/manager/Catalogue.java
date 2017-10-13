package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.catalogue.WorkspaceCatalogue;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

public class Catalogue {

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		//				ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
//		SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");
//	ScopeProvider.instance.set("/gcube/devNext/NextNext");
	ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		Workspace ws = HomeLibrary
				.getHomeManagerFactory().getHomeManager().getHome("paultaconet").getWorkspace();
		System.out.println(ws.getRoot().getPath());
		
		WorkspaceItem item = ws.getItemByPath("/Workspace/Tuna_Atlas/R_scripts/Transform/IOTC/IOTC_functions.R");
		List<AccountingEntry> history = item.getAccounting();
		for (AccountingEntry entry: history){
			System.out.println(entry.toString());
		}

		
		
//		WorkspaceSharedFolder vre = ws.getVREFolderByScope("/d4science.research-infrastructures.eu/SoBigData/ResourceCatalogue");
//		List<WorkspaceItem> children = vre.getLastItems(10);
//		for (WorkspaceItem child: children){
//			System.out.println(child.getPublicLink(true));
//		}
		
//		WorkspaceCatalogue userCatalogue = ws.getCatalogue();
//		WorkspaceItem mycat = userCatalogue.getCatalogueItemByPath("/bbb");
//		
//		WorkspaceFolder bbb = (WorkspaceFolder) ws.getItemByPath("/Workspace/BBB");
//		System.out.println(bbb.getPath());
//		for (WorkspaceItem item: bbb.getChildren()){
//			userCatalogue.addWorkspaceItem(item.getId(), mycat.getId());
//		}
//		 
////		 look at catalogue structure
//		List<WorkspaceItem> catalogueChildrens = userCatalogue.getChildren(true); // hidden?
//		if(catalogueChildrens.isEmpty())
//			System.out.println("Catalogue children list is empty");
//		else for (WorkspaceItem catalogueItem : catalogueChildrens) {
//			System.out.println("Catalogue Child is " + catalogueItem.getName());
//			if(catalogueItem.isFolder()){
//				System.out.println("it is a folder, printing children");
//				WorkspaceFolder catalogueFolder = (WorkspaceFolder) catalogueItem;
//				System.out.println("");
//				List<WorkspaceItem> copiedFolderChildren = catalogueFolder.getChildren(true);
//				 for (WorkspaceItem copiedFolderChildrenItem : copiedFolderChildren) {
//					System.out.println("** Child is " + copiedFolderChildrenItem.getName() + " - hidden? " + copiedFolderChildrenItem.isHidden());
//				}
//			}
//		}


	}


}
