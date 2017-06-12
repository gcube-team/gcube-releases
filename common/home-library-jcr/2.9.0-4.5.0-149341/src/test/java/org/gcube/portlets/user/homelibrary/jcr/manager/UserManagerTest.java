package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.scope.api.ScopeProvider;

public class UserManagerTest {
	

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		createLibrary();

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {
		ScopeProvider.instance.set("/gcube");
//			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		UserManager ws = HomeLibrary
				.getHomeManagerFactory().getUserManager();
		List<GCubeGroup> groups = ws.getGroups();
		for(GCubeGroup group: groups){
			System.out.println(group.getName());
		}
			
		
//		ws.getRoot();
		
			
//		String applicationName = "StatisticalManager";
//	WorkspaceFolder folder = getWorkspaceSMFolder("gianpaolo.coro");
//	System.out.println(folder.getPath());
	
	
//		 WorkspaceFolder folder = ws.getApplicationArea();
//		System.out.println(folder.getPath());
		
//		List<WorkspaceSmartFolder> folders = ws.getAllSmartFolders();
//		System.out.println(folders.size());
//		for(WorkspaceSmartFolder folder: folders){
//			System.out.println(folder.getName());
//			
////			ws.removeItem(folder.getId());
////			folder.remove();
////			System.out.println(folder.getSearchItems());
//		}
		
//		String folderId = "45e15eed-2779-42f0-9056-f5571db43b93";
//		String name = "testa";
//		try {
//			WorkspaceItem flag = ws.find(name, folderId);
//		
////			System.out.println(name + " already exists? " + flag);
//		} catch (WrongItemTypeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	 
//		String query = "test query";
//		ws.createSmartFolder("test", "test descr", query);
//		List<SearchItem> list = ws.searchByName("test");
//		for (SearchItem item: list){
//			try{
//			SearchFolderItem  myItem = (SearchFolderItem) item;
//			
//			System.out.println("getId: " + myItem.getId());
//			System.out.println("getName: " + myItem.getName());
//		
//			System.out.println("isShared: " + myItem.isShared());
//
//			System.out.println("getOwner: " + myItem.getOwner());
//			System.out.println("isVreFolder: " + myItem.isVreFolder());
//			System.out.println("getCreationDate: " + myItem.getCreationDate().getTime());
//			System.out.println("getLastModified: " + myItem.getLastModified().getTime());
//			System.out.println("getType: " + myItem.getType().toString());
//			
//			System.out.println("getMimeType: " + myItem.getMimeType());
//			System.out.println("getSize: " + myItem.getSize());
//			System.out.println("getFolderItemType: " + myItem.getFolderItemType());
//
//			}catch (Exception e) {
////				System.out.println("NO SearchFolderItem");
//			}
//			System.out.println("getId: " + item.getId());
//			System.out.println("getName: " + item.getName());
//		
//			System.out.println("isShared: " + item.isShared());
//
//			System.out.println("getOwner: " + item.getOwner());
//			System.out.println("isVreFolder: " + item.isVreFolder());
//			System.out.println("getCreationDate: " + item.getCreationDate().getTime());
//			System.out.println("getLastModified: " + item.getLastModified().getTime());
//			System.out.println("getType: " + item.getType().toString());
//			}
		
//		String query = "SELECT+*+FROM+%5Bnthl%3AworkspaceItem%5D+AS+node+WHERE+ISDESCENDANTNODE%28%27%2FHome%2Fvalentina.marioli%2FWorkspace%27%29+AND+%28UPPER%28%5Bjcr%3Atitle%5D%29+LIKE+%27%25TEST%25%27%29+AND+NOT%28ISDESCENDANTNODE+%28%27%2FHome%2Fvalentina.marioli%2FWorkspace%2FTrash%2F%27%29%29";		
//		System.out.println(URLDecoder.decode(query, "UTF-8"));
		
//		List<WorkspaceItem> children = ws.getRoot().getChildren();
//		for(WorkspaceItem item: children){
//			System.out.println(item.getPath());
//		}
		
//		JCRReport report = (JCRReport) ws.getItemByPath("/Home/massimiliano.assante/Workspace/rep 22 sep repi image.d4sR");
//		System.out.println(report.getLength());
//		InputStream stream = report.getData();
//		stream.close();
		
		
//		JCRGCubeItem item = (JCRGCubeItem) ws.getItemByPath("/Home/valentina.marioli/Workspace/00000");
//		System.out.println(item.getFolderItemType());
		
//	List<WorkspaceSmartFolder> folders = ws.getAllSmartFolders();
//	for(WorkspaceSmartFolder folder:folders){
//		System.out.println(folder.getId());
//	}
		
//		List<SearchItem> children = ws.getFolderItems(FolderItemType.EXTERNAL_IMAGE);
//		for(SearchItem item: children){
//			System.out.println(item.getName());
//		}
		
//		System.out.println(JCRRepository.getCredetials());
		
		//		
		//		List<String> list = WorkspaceUtil.getMembersByGroup("gcube-devNext-NextNext");


		//		List<WorkspaceItem> children = ws.getRoot().getChildren();
		//		for (WorkspaceItem child: children){
		//			System.out.println(child.getPath());
		//			System.out.println(child.getName());
		//		}

//		String path = "/Home/valentina.marioli/Workspace/gCubeItems/gCubeItem00/";
//		GCubeItem item = (GCubeItem) ws.getItemByPath(path);
//		Properties props = item.getProperties();
//		System.out.println(props.getId());
//		System.out.println(props.getPropertyValue("key04"));
//		Map<String, String> map = props.getProperties();
//		Set<String> keys = map.keySet();
//		for(String key: keys){
//			System.out.println(key + ": " +map.get(key));
//		}


////				String item = "/Home/valentina.marioli/Workspace/00000/test/ccc/bbb/75b7c9f0ee.doc";
////				WorkspaceItem folder0 =   ws.getItemByPath(item);
////				folder0.remove();
//				String id = "7d5fd78a-6543-4d20-b6e7-9ae490fa1ad8";
//				
//				GCubeItem item = (GCubeItem) ws.getItem(id);
////			Properties props = item.getProperties();			
//			item.getProperties().addProperty("key06", "test06");
//			item.getProperties().addProperty("key07", "test07");
//			item.getProperties().addProperty("key09", "test09");
//			
//			item.getProperties().update();
//			
//			
//			GCubeItem item2 = (GCubeItem) ws.getItem(id);
//			
//			Properties props1 = item2.getProperties();	
//			Map<String, String> map = props1.getProperties();
//			Set<String> set = map.keySet();
//			
//			
//			for (String key: set){
//				System.out.println("* " + key + ": " + map.get(key));
//			}
			
		String path = "/Home/massimiliano.assante/Workspace/Test Share Folder 1838 4 Jun";
//		WorkspaceFolder item = (WorkspaceFolder) ws.getItemByPath(path);
//		List<String> users = new ArrayList<String>();
//		users.add("roberto.cirillo");
//		WorkspaceSharedFolder shared = item.share(users);
//		shared.setACL(users, ACLType.WRITE_ALL);
		
		
//		JCRReport item = (JCRReport) ws.getItemByPath(path);
//		System.out.println(item.getRemotePath());
		
		
		//		String itemId =   ws.getItemByPath(item).getId();
//			String destinationFolderId =   ws.getItemByPath(folder).getId();			
//			String name = "gCubeItem01";
//			String description = "gCubeItem description";
//			List<String> scopes = new ArrayList<String>();
//			scopes.add("/gcube");
//			
//			String creator = "valentina.marioli";
//			String itemType = "myService";
//			Map<String, String> properties = new HashMap<String, String>();
//			properties.put("key00", "value00");
//			properties.put("key01", "value01");
//			properties.put("key02", "value02");
//			properties.put("key03", "value03");
//			properties.put("key04", "value04");
//			properties.put("key05", "value05");
//			properties.put("key06", "value06");
//			WorkspaceItem gCube = ws.createGcubeItem(name, description, scopes, creator, itemType, properties, destinationFolderId);
//			System.out.println(gCube.toString());
//		
			
			
//			//shared		
//			WorkspaceSharedFolder folder =   (WorkspaceSharedFolder) ws.getItemByPath(path);
//			System.out.println(folder.getMembers().toString());
//			System.out.println(folder.getUsers().toString());
//			System.out.println(folder.getACLOwner().toString());
			
			
			
//JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItem(gCube.getId());
//item.getProperties().addProperty("valeKey", "valeValue");
//item.save();
//
//GCubeItem item1 = (GCubeItem) ws.getItem(gCube.getId());
//Properties props = item1.getProperties();
//System.out.println(props.getId());
//System.out.println(props.getPropertyValue("key04"));
//Map<String, String> map = props.getProperties();
//Set<String> keys = map.keySet();
//for(String key: keys){
//	System.out.println(key + ": " +map.get(key));
//}
			
			

		//	JCRWorkspaceItem wi = (JCRWorkspaceItem) ws.getItemByPath(item);
		//wi.getProperties().addProperty("testKey00", "testValue000");
		//wi.save();
		//
		//Properties props = wi.getProperties();
		//
		//System.out.println(props.getProperties().toString());



		//		
		//		WorkspaceItem myItem = ws.copy(itemId, destinationFolderId);
		//		System.out.println(myItem.getRemotePath());

		//		ws.renameItem(destinationFolderId, "00000");
		//		 WorkspaceItem folder = ws.getItemByPath(path);
		//		 folder.remove();

		//	/Home/valentina.marioli/Workspace/00000/test/ccc/bbb/1.jpg
		//	/Home/valentina.marioli/Workspace/00000/test/1.jpg



		//		WorkspaceItem newItem = ws.copy(itemId, destinationFolderId);
		//		System.out.println(newItem.getRemotePath());

		//		WorkspaceFolder destinationFolder =  (WorkspaceFolder) ws.getItemByPath(path);
		//		String name = "ByStorageId";
		//		String description = "test storage id";
		//		String mimeType = null;
		//		String storageId = "5576fdece4b0525d804e3545";
		//		WorkspaceUtil.createExternalFile(destinationFolder, name, description, mimeType, storageId);

		//		 folder.markAsRead(true);

		//		List<AccountingEntry> accouting = folder.getAccounting();
		//		for (AccountingEntry entry: accouting){
		//			System.out.println(entry.toString());
		//		}


		//		List<WorkspaceItem> children = folder.getChildren();
		//		for(WorkspaceItem child: children){
		//			System.out.println(chil);
		//			System.out.println(child.getRemotePath());
		//		}

		//		Session session = JCRRepository.getSession();
		//		session.removeItem("/Home/valentina.marioli/Workspace/00/");
		//		session.save();
		//		 WorkspaceItem folder = ws.getItemByPath("/Home/valentina.marioli/Workspace/00000/");
		//		WorkspaceFolder newfolder = ws.createFolder("new", "", folder.getId());
		//		System.out.println(newfolder.getPath());
		//		 WorkspaceItem old = ws.getItemByPath("/Home/valentina.marioli/Workspace/00/old");
		//		 WorkspaceItem newFolder = ws.getItemByPath("/Home/valentina.marioli/Workspace/00/new");
		//		
		//		ws.moveItem(old.getId(), newFolder.getId());
		//		List<WorkspaceItem> list = vre.getLastItems(5);
		//		for (WorkspaceItem item: list)
		//			System.out.println(item.getPath());


		//		String path = "/Home/valentina.marioli/Workspace/00000/COLA";
		//		
		//		JCRWorkspaceFolder folder = (JCRWorkspaceFolder) ws.getItemByPath(path);
		//		
		//		List<String> users = new ArrayList<String>();
		//		users.add("roberto.cirillo");
		//		folder.share(users);
		//		folder.rename("VALE");

		//		folder.remove();


		//		String path = "/Home/valentina.marioli/Workspace/gcubeTest";
		//		
		//		JCRGCubeItem folder = (JCRGCubeItem) ws.getItemByPath(path);
		//		
		//		List<String> users = new ArrayList<String>();
		//		users.add("roberto.cirillo");
		//		folder.share(users);


		//	JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath("/Home/valentina.marioli/Workspace/COL1");
		//		System.out.println(item.getType());
		//		
		//		JCRReport report = (JCRReport) item;
		//		System.out.println(report.getPath());
		//		InputStream input = report.getData();
		//		input.close();
		//		String file = "/home/valentina/Downloads/homelibrary.ppt";
		//		File initialFile = new File(file);
		//		InputStream tmpFile = new FileInputStream(initialFile);
		//		
		////		ws.updateItem(item.getId(), tmpFile);
		//		JCRExternalFile pdf = (JCRExternalFile) ws.createExternalFile("homelibrary.ppt", "test description", null, tmpFile, item.getId());
		//		System.out.println(pdf.getAuthor());




		//		
		//		System.out.println(folder.isVreFolder());
		//		WorkspaceItem folder = ws.getItemByPath(path);

		//		JCRExternalImage image = (JCRExternalImage) folder ;
		//		
		//		System.out.println(image.getThumbnailHeight());
		//		System.out.println(image.getThumbnailWidth());
		//		
		//		
		//		InputStream stream = image.getThumbnail();
		//		System.out.println (stream.available());
		//		stream.close();
		//		System.out.println(item.getRemotePath());
		//		
		//		JCRExternalFile file = (JCRExternalFile) item;
		//		
		//		InputStream stream = file.getData();
		////		System.out.println(file.getd);
		//		stream.close();

		//				Session session = JCRRepository.getSession();
		//		Node item = session.getNode("/Home/valentina.marioli/Workspace/.applications/Workspace/");
		//		item.remove();
		//		session.save();

		//List<String> users = null;
		//String itemId = null;

		//  WorkspaceSharedFolder folder =  (WorkspaceSharedFolder) ws.getItemByPath("/Home/valentina.marioli/Workspace/aaa");
		// List<? extends WorkspaceItem> children = folder.
		// System.out.println(children.size());


		//		JCRGCubeItem item1 = (JCRGCubeItem) ws.getItemByPath("/Home/valentina.marioli/Workspace/.applications/.applications/testGcubeItem0a44/");
		//	System.out.println("is shared? " + item1.isShared());

		//		String tmpFile = "https://www.google.it/";
		//		JCRExternalUrl url = (JCRExternalUrl) ws.createExternalUrl("testUrl01", "testUrl description", tmpFile, ws.getRoot().getId());
		//System.out.println(url.getUrl());


		//QueryManager queryManager = session.getWorkspace().getQueryManager();
		//
		//		javax.jcr.query.Query q = queryManager.createQuery("/jcr:root/Home"+
		//				"//element(*,nthl:gCubeURLDocument)",
		//				javax.jcr.query.Query.XPATH);
		//
		//		QueryResult result = q.execute();
		//		NodeIterator nodes = result.getNodes();
		//		int i = 0;
		//		while(nodes.hasNext())
		//		{
		//			Node node = nodes.nextNode();
		//			WorkspaceItem item = ws.getItem(node.getIdentifier());
		//			System.out.println(item.getPath());
		//			i++;
		//			System.out.println(node.getPath());
		//		}
		//		System.out.println(i);


		//		SearchQueryBuilder query = new SearchQueryBuilder();
		//		query.contains("property01");
		//		query.contains("property02");
		//		query.contains("property01", "value property");
		//		
		//		query.ofType("itemTestType");
		//
		//		List<GCubeItem> list = ws.searchGCubeItems(query.build());
		//		
		//		for (WorkspaceItem item :list){
		//			GCubeItem gcube = (GCubeItem) item;
		//			System.out.println(item.getName() + ": " + gcube.getItemProperties() + " - type: " + gcube.getItemType() );
		//		}
		//		

		//		  <aggregate primaryType="nthl:gCubeItem">
		//          <include>hl:property</include>
		//  </aggregate>

		//		 <index-rule nodeType="nthl:workspaceItem">
		//         <property isRegexp="true">.*:.*</property>
		//         <property>hl:itemType</property>
		// </index-rule>



		//		
		//		System.out.println(app.getPath());
		//		
		//		WorkspaceFolder root = ws.getRoot();

		//		WorkspaceSharedFolder root = (WorkspaceSharedFolder) ws.getItemByPath("/Home/valentina.marioli/Workspace/00000/test");
		//		System.out.println(root.getName());
		//		System.out.println(root.getOwner().getPortalLogin());
		//root.rename("xx");

		//		JCRWorkspaceFolder folder = (JCRWorkspaceFolder) ws.createFolder("COL2", "test", root.getId());
		//		System.out.println(folder.getPath());
		//		WorkspaceItem item = ws.getItemByPath(root.getPath() + "/COL(0)");
		//		JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder) item;
		//		sharedFolder.unShare();


		///HERE 
		//		List<String> users = new ArrayList<String>();
		//				users.add("valentina.marioli");
		//				users.add("lucio.lelii");
		////				WorkspaceSharedFolder sharedFolder = ws.shareFolder(users, item.getId());
		////				System.out.println(sharedFolder.getPath());
		////				WorkspaceItem root = ws.getRoot();
		//				
		//				Map<String, String> properties = new HashMap<String, String>();
		//				properties.put("property01", "value property");
		//						properties.put("property02", "value property");
		//								properties.put("property03", "value property");
		//				List<String> scopes = new ArrayList<String>();
		//				scopes.add("/gCube");
		//				WorkspaceItem gcube = ws.createGcubeItem("testGcubeItem342aaadd", "testGcubeItem11", scopes, "valentina.marioli", "itemTestType", properties, ws.getItemByPath("/Home/valentina.marioli/Workspace/.applications/").getId());
		//				System.out.println(gcube.getPath());
		//
		//				WorkspaceSharedFolder folder = ws.shareFolder(users, gcube.getId());
		//				System.out.println(folder.getPath());
		//				System.out.println("is shared? " + gcube.isShared());
		//				System.out.println("getIdSharedFolder " + gcube.getIdSharedFolder());
		//UNTIL HERE

		//				WorkspaceFolder vreFolder = ws.getMySpecialFolders();
		//		//		System.out.println(vreFolder.getPath());
		//				List<WorkspaceItem> list = vreFolder.getChildren();
		//				for (WorkspaceItem item: list){
		//		//			System.out.println("VRE PATH: " + item.getPath());
		//					WorkspaceSharedFolder folder = (WorkspaceSharedFolder) item;
		//					System.out.println("DISPLAY NAME: " + folder.getDisplayName());
		//				}

		//		System.out.println("root id: " + root.getId());
		//		
		//	WorkspaceTrashFolder trash = ws.getTrash();
		//	List<WorkspaceTrashItem> list = trash.listTrashItems();
		//	for (WorkspaceTrashItem item : list){
		//		System.out.println(item.getPath());
		//		item.restore();
		//	}
		//	System.out.println();
		//		String absPath = "/Home/valentina.marioli/Workspace/test/1.jpg";
		//		WorkspaceItem item = ws.getItemByPath(absPath);
		//	item.remove();



		//		WorkspaceFolder destination = (WorkspaceFolder) ws.getItemByPath("/Home/valentina.marioli/Workspace/test");
		//		System.out.println("BEFORE PRINT");
		//		System.out.println(item.getName());
		//		item.move(destination);
		//		System.out.println("getCreationTime " + item.getCreationTime());
		//		System.out.println("getDescription " + item.getDescription());
		//		System.out.println("getId " + item.getId());
		//		System.out.println("getIdSharedFolder " + item.getIdSharedFolder());
		//		System.out.println("getLastUpdatedBy " + item.getLastUpdatedBy());
		//		System.out.println("getPath " + item.getPath());
		//		System.out.println("getOwner " + item.getOwner());
		//
		//		System.out.println("getRemotePath " + item.getRemotePath());
		//		System.out.println("getCreationTime " + item.getCreationTime());
		//		System.out.println("getLastAction " + item.getLastAction());
		//		System.out.println("getLastModificationTime " + item.getLastModificationTime().getTime().getDate());
		//		System.out.println("getParent " + item.getParent().getPath());
		//		System.out.println("getType " + item.getType());
		//	
		//		
		//		System.out.println("isTrashed? " + item.isTrashed());
		//		System.out.println("isMarkedAsRead? " + item.isMarkedAsRead());
		//		System.out.println("isRoot? " + item.isRoot());
		//		System.out.println("isShared? " + item.isShared());
		//
		//		item.rename("interno.jpg");
		//		item.remove();



		//		Session session = JCRRepository.getSession();
		//		LockManager lockManager = session.getWorkspace().getLockManager();
		//		System.out.println("is locked? "  + lockManager.isLocked(absPath));
		//		Node node = session.getNode(absPath);
		//		System.out.println(node.getPath());
		//		Lock lock = null;
		//		try {
		//			lock = lockManager.getLock(absPath);
		//			System.out.println("lock: " + lock.getLockToken());
		//		} catch (LockException ex) {      
		//			ex.printStackTrace();
		//		}
		//		System.out.println("is lock != null? " +  (lock != null));
		//		if (lock != null) {
		//			lockManager.addLockToken(lock.getLockToken());
		//			lockManager.unlock(absPath);
		//			System.out.println("unlocked");
		//		}




		//		WorkspaceTrashFolder trash = ws.getTrash();
		//	List<WorkspaceTrashItem> items = trash.listTrashItems();
		//	for (WorkspaceTrashItem item: items){
		//		System.out.println("** " + item.getId());
		//	}

		//		List<? extends WorkspaceItem> children = root.getChildren();
		//
		//		System.out.println("ALREADY GET CHILDREN");
		//		
		//		for(WorkspaceItem child: children)
		//			System.out.println(child.getName() + " - " +  child.getId());	
		//		
		//		System.out.println("list.size(): " + children.size());

		//		//create external file



		//				ExternalFile image = ws.createExternalFile("testHttP.jpg", "test description", "image/jpeg", tmpFile, root.getId());
		//						ExternalImage image = ws.createExternalImage("bibbona.jpg", "test description", "image/jpeg", tmpFile, folder.getId());
		//		//		ExternalImage image = ws.createExternalImage("testHtt21.jpg", "test description", "image/jpeg", root.getId(), initialFile);
		//				image.getThumbnail()
		//				System.out.println("IMAGE DOWN " + image.getId());

		//			System.out.println(root.getPath());

		//		System.out.println("Height: " + image.getHeight());
		//		System.out.println("getLength: " + image.getLength());
		//		System.out.println("getThumbnailHeight: " + image.getThumbnailHeight());
		//
		//		System.out.println("getThumbnailWidth: " + image.getThumbnailWidth());
		//		System.out.println("getWidth: " + image.getWidth());





		//		int i = 0;
		//		while (i < list.size()){
		//			System.out.println("before");
		//			WorkspaceItem item = list.get(i);
		//			
		////		System.out.println("--> " + item.getPath());
		////			System.out.println("getId: " + item.getId());
		//			System.out.println("--> getName: " + item.getName());
		//			try{
		////				System.out.println("parent Id: " + item.getParent().getId());
		//				WorkspaceFolder parent = item.getParent();
		//				System.out.println("parent Id: " + parent.getId());
		//				WorkspaceFolder bis = parent.getParent();
		//				if (bis == null)
		//					System.out.println("bis is null");
		//				System.out.println("bis Id: " + bis.getId());
		//			System.out.println("getParent: " + bis.getPath());
		////			System.out.println("getParent().getParent(): " + item.getParent().getParent().getPath());
		//			}catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//			System.out.println("getOwner: " +item.getOwner().getPortalLogin());
		//			System.out.println("getType: " +item.getType());
		//			System.out.println("getLastModificationTime: " +item.getLastModificationTime());
		//			if ((item.getType() == WorkspaceItemType.FOLDER_ITEM))
		//				System.out.println("getLength: " + ((FolderItem)item).getLength());
		//			System.out.println("end");
		//			i++;
		//			//			System.out.println("\n");
		//		}
		//		WorkspaceFolder item = (WorkspaceFolder) ws.getItemByPath("/Workspace/My Data/");
		//		
		//System.out.println(item.getPath());

		//InputStream stream = GCUBEStorage.getRemoteFile("/Home/pasquale.pagano/Workspace/My Data/Images/Chlorophyll Objectsea230767-cb78-4ee6-a7df-9387238dfa3c");
		//
		//if (stream!=null)
		//	System.out.println("stream ok");
		//
		//stream.close();
		//		search("biodiversitylab");
		//rename("/Workspace/valenti/", "Valentina");
		//downloadFol(item);
		//System.out.println(item.getRemotePath());


	}

//	private static String ss(String string, String string2) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	private static void rename(String absPath, String newName) throws ItemNotFoundException, InternalErrorException, ItemAlreadyExistException, InsufficientPrivilegesException {
//
//		String id = ws.getItemByPath(absPath).getId();
//		ws.renameItem(id, newName);
//
//	}
//
//	private static void search(String name) throws InternalErrorException {
//		List<SearchItem> items=	ws.searchByName(name);
//		System.out.println("seach");
//		int i = 1;
//		for (SearchItem item : items){
//			//			System.out.println("-----------" + i++ +"----------------");
//			//			System.out.println("--> " + item.getName());
//			System.out.println(i++ + ") " + item.getId() + " - " + item.getName()+ " - shared? " + item.isShared());
//			System.out.println(item.getOwner() + " - " + item.getCreationDate().getTimeInMillis()+ " - " + item.getLastModified().getTimeInMillis());
//			System.out.println("is vre folder? " + item.isVreFolder() + " - " + item.getName()+ " - " + item.getType().toString());
//			System.out.println("");
//		}
//
//	}

	//	private static void downloadFol(WorkspaceFolder item) throws IOException, InternalErrorException {
	//		File tmpZip = ZipUtil.zipFolder((WorkspaceFolder) item);
	//		return;
	//
	//	}
	//	
	public static WorkspaceFolder getWorkspaceSMFolder(String userName) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException
	{
		WorkspaceFolder appfolder= null;
		try{
			System.out.println("get Workspace Application Folder for the user "+ userName);
			Home home = HomeLibrary.getHomeManagerFactory().getHomeManager()
			.getHome(userName);
		
			System.out.println("get home");
		appfolder= home.getDataArea().getApplicationRoot("StatisticalManager");
		System.out.println("foldere created");

		}
		catch (Exception e) {

			e.printStackTrace();

		
		}
		return appfolder;
	}

}
