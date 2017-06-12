package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.List;

import javax.jcr.PathNotFoundException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRQuery;
import org.gcube.common.scope.api.ScopeProvider;

public class TestJCRQuery {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
//		ScopeProvider.instance.set("/gcube/devsec");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("massimiliano.assante").getWorkspace();


		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByAbsPath("/Home/massimiliano.assante/Workspace/My Default Basket");
		
		System.out.println(folder.getName());
	
		List<WorkspaceItem> children = folder.getChildren();
		for (WorkspaceItem child: children){
			System.out.println(child.getName() + " - " + child.getType());
			if (child instanceof JCRQuery){
				JCRQuery query = (JCRQuery) child;
				System.out.println(query.getId());
				System.out.println(query.getLength());
			}
			
		}
		
//		WorkspaceItem folder = ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/00000/ccc");
////		WorkspaceItem copied = ws.copy(item.getId(), folder.getId());
//		WorkspaceItem copied = ws.moveItem(item.getId(), folder.getId());
//		System.out.println(copied.getPath());
//		
//		Thread.sleep(100000);
	}


}
