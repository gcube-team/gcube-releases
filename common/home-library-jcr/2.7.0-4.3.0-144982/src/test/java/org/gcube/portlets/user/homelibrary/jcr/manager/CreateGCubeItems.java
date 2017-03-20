package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.jcr.PathNotFoundException;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.items.MetadataProperty;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQueryBuilder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

import com.thoughtworks.xstream.XStream;

public class CreateGCubeItems {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {
		SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");
//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome().getWorkspace();

//		WorkspaceFolder folder = ws.createFolder("gCubeItems", "gcube items test", ws.getRoot().getId());

				WorkspaceFolder folder =  (WorkspaceFolder) ws.getItemByPath("/Workspace/gCubeItems");

		for (int i=0; i<5 ; i++){
			String name = "gCubeItem-" + UUID.randomUUID().toString();
			String description = "gCubeItem description";
			List<String> scopes = new ArrayList<String>();
			scopes.add("/gcube/devsec");

			String creator = "valentina.marioli";
			String itemType = "myType04";

			Map<String, String> properties = new HashMap<String, String>();
			properties.put("key05", "value05");
			properties.put("key06", "value06");
			properties.put("key07", "value07");

			WorkspaceItem item = ws.createGcubeItem(name, description, scopes, creator, itemType, properties, folder.getId());
//			System.out.println(item.getProperties().toString());
			
			WorkspaceItem myitem = ws.getItem(item.getId());
			Map<String, String> myproperties = myitem.getProperties().getProperties();
			Set<String> keys = myproperties.keySet();
			for (String key: keys) {
				System.out.println(myproperties.get(key));
			}
		}
		
		SearchQueryBuilder query = new SearchQueryBuilder();
		query.contains("key07");
		query.ofType("myType04");
	

		List<GCubeItem> list = ws.searchGCubeItems(query.build());
//		List<WorkspaceItem> list = ws.searchByProperties(query.build());
		for (WorkspaceItem item: list){
			System.out.println(item.getPath());
		}

	}


}
