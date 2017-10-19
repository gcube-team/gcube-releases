package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceVREFolder;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

public class TestUserManager {

	//	static UserManager gm = null;

	public static void main(String[] args) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException {

		//					ScopeProvider.instance.set("/CNR.it");
		ScopeProvider.instance.set("/gcube");
		//	ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		JCRWorkspace ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory().getHomeManager().getHome("raspberry.camera").getWorkspace();
		//		System.out.println(ws.getItem("dc54a5bf-33a6-47aa-aece-80469ae4d48a").getPath());


		//		/Home/giancarlo.panichi/Workspace/DataMiner/Computations/FEED_FORWARD_ANN_ID_06db8762-1886-42fa-91dc-64efd4381228/FEED_FORWARD_ANN_ID_06db8762-1886-42fa-91dc-64efd4381228.xml
		//		/Home/giancarlo.panichi/Workspace/DataMiner/Computations/FEED_FORWARD_ANN_ID_06db8762-1886-42fa-91dc-64efd4381228/FEED_FORWARD_ANN_ID_06db8762-1886-42fa-91dc-64efd4381228.xml
		WorkspaceItem item = null;
		try {
			item = ws.getItemByPath("/Workspace/MySpecialFolders/gcube-devsec-SmartCamera");
			JCRWorkspaceVREFolder shared = (JCRWorkspaceVREFolder) item;
			shared.addUserToVRE("valentina.marioli");
			System.out.println(shared.getUsers());
			System.out.println(shared.getAdministrators());

//			System.out.println("** " + ws.getStorage().getRemotePathByStorageId(item.getStorageID()));
//			System.out.println(item.getPath());
//			System.out.println(item.getRemotePath());
//			System.out.println(item.getStorageID());
//			System.out.println(item.getPublicLink(true));
			//			WorkspaceSharedFolder shared =  (WorkspaceSharedFolder) item;
			//			System.out.println(shared.getUsers());
			//			System.out.println(item.getPath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//		WorkspaceItem item = ws.getItemByPath("/Workspace/mpa-web.zip");
//		System.out.println(item.getPath());
//		ExternalFile file = (ExternalFile) item;
//
//		InputStream initialStream = file.getData();
//
//		File targetFile = new File("/home/valentina/Downloads/TEST_REST/gc00.xml");
//
//		try {
//			FileUtils.copyInputStreamToFile(initialStream, targetFile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//System.out.println(item.getStorageID());
		//		System.out.println(item.getRemotePath());
		//		System.out.println(item.getPublicLink(true));


		//		gm.deleteAuthorizable("test01");
		//		gm.deleteAuthorizable("social.isti");
		//		gm.deleteAuthorizable("test.user00");
		//		gm.deleteAuthorizable("test00");

		//	GCubeGroup group = gm.createGroup("gcube-test-test");
		//	List<String> users = new ArrayList<String>();
		//	users.add("francesco.mangiacrapa");
		//	users.add("roberto.cirillo");
		//	users.add("valentina.marioli");
		//	users.add("massimiliano.assante");
		//	group.addMembers(users);

		//
		//		List<GCubeGroup> groups = gm.getGroups();
		//		for (GCubeGroup group: groups){
		//
		//			String display = group.getDisplayName();
		//			if (display == null){
		//				String name = group.getName();
		//
		//				String displayName;
		//
		//				if (name.equals("CNR.it-ISTI-D-NET"))
		//					displayName = "D-NET";
		//				else{
		//					String[] displayNameList = name.split("-");
		//					int size = displayNameList.length;
		//					displayName = displayNameList[size-1];
		//				}
		//
		//					System.out.println(name + " -> " + displayName);
		//					
		//					group.setDisplayName(displayName);
		//				
		//			}
		//
		//		}
		//
		//				System.out.println("check");
		//				for (GCubeGroup group: groups){
		//					String display = group.getDisplayName();
		//					String name = group.getName();
		//					System.out.println(name + " -> " + display);
		//		
		//				}



		//		String scope = "gcube-devsec-TestGio18e27";
		//		String username = "luciana.magliozzi";
		//		gm.removeUserFromGroup(scope, username, null);

		//	System.out.println(gm.getGroup(scope).getDisplayName());
		//	gm.getGroup(scope).setDisplayName("TestMyVreVale");
		//		
		//		String name = gm.getGroup(scope).getDisplayName();
		//		System.out.println("displayName: " + name);
		//		String portalLogin = "CNR.it-ISTI-Dipendenti";
		//		String  username = "alessio.ferrari";
		//		String portalLogin  = "valentina.marioli";
		//		gm.removeUserFromGroup(scope, username, portalLogin);
		//		gm.associateUserToGroup(scope, username, portalLogin);
		//TESTSERVLET
		//		System.out.println("****list Users****");
		//		List<String> listUsers = gm.getUsers();
		//		for (String user: listUsers)
		//			System.out.println("+++++++ " + user);
		//
		//				gm.createUser("workspacerep.imarine", "gcube2010*onan");


		//				addUserToJCRUserManager("valentina.marioli", "/Home/valentina.marioli");



		//		System.out.println("****deleteAuthorizable****");
		//				gm.deleteAuthorizable("workspacerep.imarine");

		//				System.out.println("****list Users****");
		//				List<String> listUsers1 = gm.getUsers();
		//				for (String user: listUsers1){
		//					if (!user.equals("workspacerep.imarine")){
		//						System.out.println("**** remove user " + user);
		//						System.out.println(gm.deleteAuthorizable(user));
		//						
		//						gm.createUser(user);
		//						System.out.println("**** created user " + user);
		//					}			
		//				}
		//		
		//		
		//		
		//		System.out.println("****list Users****");
		//		List<String> listUsers11 = gm.getUsers();
		//		for (String user: listUsers11)	
		//				System.out.println(user);
		//
		//		System.out.println("end");

		//		System.out.println("+++++++ " + user);
		//
		//		//		("workspacerep.imarine", "gcube2010*onan
		//		System.out.println("****createUserServlet****");
		//			System.out.println(gm.createUser("test","test"));
		//			
		//			System.out.println("****list Users****");
		//			List<String> listUsers1 = gm.getUsers();
		//			for (String user: listUsers1)
		//				System.out.println("+++++++ " + user);


		//				System.out.println(gm.createUser("test.user007"));
		//
		//				System.out.println("****list Groups****");
		//				List<GCubeGroup> listGrroups = gm.getGroups();
		//				for (GCubeGroup group: listGrroups){
		//					System.out.println("-> " + group.getName());
		//					System.out.println(group.getMembers().toString());
		//					System.out.println( " ");
		//				}


		//		String groupId = "gcube/devNext/NextNext";
		//		System.out.println("**** remove group " + groupId);
		//		System.out.println(gm.deleteAuthorizable(groupId));
		//
		//		String groupId1 = "/gcube/devNext/NextNext";
		//		System.out.println("**** remove group " + groupId1);
		//		System.out.println(gm.deleteAuthorizable(groupId1));

		//		String scope = "gcube-devsec-devVRE";
		//		String username = "lucio.lelii";
		//
		//		System.out.println("remove");
		//		gm.associateUserToGroup(scope, username, "massimiliano.assante");
		//		
		//		System.out.println("add");
		//		gm.associateUserToGroup(scope, "valentina.marioli", "massimiliano.assante");


		//		String groupId = "gcube-test-test05";
		//		System.out.println("****get Group " + groupId);
		//		GCubeGroup myGroup = gm.getGroup(groupId);
		//		System.out.println("**** Membership updated ****");
		//		System.out.println(myGroup.getMembers());


		//		String groupId = "group"+ UUID.randomUUID().toString();
		//		System.out.println("****create Group: " + groupId);
		//		System.out.println(gm.createGroup(groupId));
		//
		//		System.out.println("****list Groups****");
		//		List<GCubeGroup> listGrroups1 = gm.getGroups();
		//		for (GCubeGroup group: listGrroups1)
		//			System.out.println("+++++++ " + group);

		//				System.out.println("****get Group " + groupId);
		//				myGroup = gm.getGroup(groupId);
		//				System.out.println("**** Membership updated ****");
		//				System.out.println(myGroup.getMembers());
		//
		//		System.out.println("****Remove membership ****");
		//		System.out.println(myGroup.removeMembers(new ArrayList<String>(){/**
		//		 * 
		//		 */
		//			private static final long serialVersionUID = 1L;
		//
		//			{add ("roberto.cirillo"); add ("lucio.lelii");}}));
		//
		//		System.out.println("**** Membership updated ****");
		//		System.out.println(myGroup.getMembers());;
		//
		//		System.out.println("**** remove group " + groupId);
		//		System.out.println(gm.deleteAuthorizable(groupId));
		//
		//		System.out.println("****list Groups****");
		//		System.out.println(gm.getGroups());
		//

	}

	private static void addUserToJCRUserManager(String userId, String userHome) {

		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();            
			//			 System.out.println(url);
			String url = "http://workspace-repository-si.isti.cnr.it:8080/jackrabbit-webapp-patched-2.4.3";
			getMethod =  new GetMethod(url + "/CreateUserServlet?userName=" + userId + "&pwd=" +userHome);
			httpClient.executeMethod(getMethod);

			System.out.println("User set with status code " + getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

	}

}
