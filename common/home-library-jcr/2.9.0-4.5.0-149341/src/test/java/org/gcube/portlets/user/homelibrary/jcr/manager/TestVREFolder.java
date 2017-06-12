package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceVREFolder;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestVREFolder {


	protected static Logger logger = LoggerFactory.getLogger(TestVREFolder.class);
	public static final String MEMBERS 		=	"hl:members";
	public static final String USERS 			=	"hl:users";
	public static final String ATTACHMENT_FOLDER ="Shared attachments";
	public static void main(String[] args) {

		try {

			//			ScopeProvider.instance.set("/gcube/preprod/devVRE");
			//			ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps");
			//						ScopeProvider.instance.set("/gcube/devNext/NextNext");
			//					SecurityTokenProvider.instance.set("8920abf2-54e5-4e35-82ae-abd31dca65c2-98187548");



			//			String designer = null;
			//						String manager  = "valentina.marioli";
			//						String description  = "create vre for test";
			//			ScopeProvider.instance.set("/CNR.it");		
			//
			//			String scope = "/gcube/devsec/SmartCamera";

			//						String scope = "/gcube/";

			String scope = "/gcube/devNext/NextNext";
			ScopeProvider.instance.set(scope);

			JCRWorkspace ws = (JCRWorkspace) HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome("francesco.mangiacrapa")
					.getWorkspace();




			//			ws.getItemByPath("");
			JCRWorkspaceVREFolder shared = (JCRWorkspaceVREFolder) ws.getVREFolderByScope(scope);
			//			System.out.println(shared.getPath());

			//			shared.changeOwner("francesco.mangiacrapa");
//			shared.addAdmin("valentina.marioli");
						List<String> logins = new ArrayList<>();
						logins.add("valentina.marioli");
						shared.setAdmins(logins);
						System.out.println(shared.getAdministrators().size());
			//			
			//			System.out.println(ws.getRoot().getPath());
			////			[josiel, pasquale.pagano, matej, massimiliano.assante, leonardo.candela, rodrigues, d4science.research-infrastructures.eu-gCubeApps-PGFA-UFMT-Manager, lucio.lelii, 
			//			//donatella.castelli, gianpaolo.coro, roberto.cirillo]
			//
			//			String scope = "/gcube/preprod/preVRE";
			//			
			//			JCRUserManager um = (JCRUserManager) HomeLibrary
			//					.getHomeManagerFactory().getUserManager();
			//			um.setAdministrator(scope, username);
			//			JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder) ws.getVREFolderByScope(scope);
			//			List<String> logins =  new ArrayList<String>();
			//			logins.add("d4science.research-infrastructures.eu-gCubeApps-PGFA-UFMT-Manager");
			//			shared.setAdmins(logins);
			//			System.out.println(shared.getUsers().toString());
			//			System.out.println(shared.getOwner().getPortalLogin());
			//			System.out.println(shared.getAdministrators().toString());
			//			shared.remove();

			//			shared.unShare("matej");
			//			shared.unShare("massimiliano.assante");
			//			shared.unShare("leonardo.candela");
			//			shared.unShare("lucio.lelii");
			//			shared.unShare("donatella.castelli");
			//			shared.unShare("gianpaolo.coro");
			//			shared.unShare("roberto.cirillo");
			//	
			//			shared.unShare("rodrigues");
			//			shared.unShare("pasquale.pagano");
			//			String dest_scope = "/d4science.research-infrastructures.eu/D4Research/PGFA-UFMT";
			//			JCRWorkspaceSharedFolder new_folder = (JCRWorkspaceSharedFolder) ws.getVREFolderByScope(dest_scope);
			//			
			//			System.out.println(shared.getPath());
			//			List<WorkspaceItem> children = shared.getChildren();
			//			for (WorkspaceItem item: children){
			//				System.out.println(item.getPath() + " copy to " + new_folder.getPath());
			//				ws.moveItem(item.getId(), new_folder.getId());
			//			}
			//			File file= ZipUtil.zipFolder(shared, true, null);
			//			System.out.println(file.getAbsolutePath());


			//			JCRWorkspaceVREFolder item = (JCRWorkspaceVREFolder) ws.getWorkspaceItem(shared.getDelegate());
			//			item.changeOwner("massimiliano.assante");


			//			System.out.println(shared.getUsers().toString());
			//			shared.unShare();
			//			
			//			System.out.println(shared.getUsers().toString());
			//					System.out.println(shared.getOwner().getPortalLogin());

			//					shared.remove();
			//						shared.unShare();
			//			WorkspaceFolder destinationFolder = ws.getVREFolderByScope(scope);
			//			
			//			WorkspaceItem item = ws.getItemByPath("/Workspace/Proposal/731001-AGINFRA PLUS-Evaluation Summary Report.pdf");
			//			ExternalFile file = (ExternalFile) item;
			//			System.out.println(file.getStorageId());
			//			System.out.println(file.getRemotePath());
			//			InputStream inputStream = file.getData();
			//
			//			FileOutputStream outputStream = new FileOutputStream(new File("/home/valentina/Downloads/TEST_REST/731001-AGINFRA PLUS-Evaluation Summary Report.pdf"));
			//						File initialFile = new File("/home/valentina/Downloads/d4science.research-infrastructures.eu-gCubeApps-PGFA-UFMT.zip");
			//					    InputStream is = new FileInputStream(initialFile);
			//			//			WorkspaceFolder destinationFolder = ws.getRoot();
			//					    WorkspaceFolder destinationFolder = UnzipUtil.unzip(shared, is, "myfolder");
			//					    System.out.println(destinationFolder.getPath());

			//			System.out.println(ws.getRoot().getPath());
			//			[pasquale.pagano, massimiliano.assante, leonardo.candela, d4science.research-infrastructures.eu-D4Research-BlueBRIDGE-Assessment-Manager]
			//			[pasquale.pagano, d4science.research-infrastructures.eu-D4Research-AgInfra+-Manager, leonardo.candela, d4science.research-infrastructures.eu-D4Research-AgInfra+]


			//			WorkspaceItem item = ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-D4Research-AgInfra+");
			//			
			//			System.out.println(item.getPath());
			//			[pan.zervas, george.kakaletris, donatella.castelli]
			//			ws.getMySpecialFolders();
			//			String scope = "/d4science.research-infrastructures.eu/D4Research/AGINFRAplus";
			//			WorkspaceVREFolder shared = (WorkspaceVREFolder) ws.getVREFolderByScope(scope);
			//			
			//		shared.addUserToVRE("d4science.research-infrastructures.eu-D4Research-AGINFRAplus");
			//			List<String> logins = new ArrayList<>();
			//			logins.add("d4science.research-infrastructures.eu-D4Research-AGINFRAplus");
			//			
			//			shared.share(logins);
			//		shared.setACL(logins, ACLType.WRITE_OWNER);
			//			shared.share(logins);
			//			logins.add("pasquale.pagano");
			//			shared.setAdmins(logins);	
			//			shared.setACL(logins, ACLType.WRITE_OWNER);
			//			shared.setACL(logins, ACLType.ADMINISTRATOR);
			////			shared.setOwnerToCurrentUser();grsf.publisher
			////			System.out.println("owner " + shared.getOwner().getPortalLogin());
			//			System.out.println(shared.getUsers().toString());


			//			shared.unShare( "d4science.research-infrastructures.eu-D4Research-AGINFRAplus");
//			JCRUserManager um = (JCRUserManager) HomeLibrary
//					.getHomeManagerFactory().getUserManager();
			//			um.deleteAuthorizable("d4science.research-infrastructures.eu-D4Research-AGINFRAplus-Manager");
			//			um.deleteAuthorizable("d4science.research-infrastructures.eu-D4Research-AGINFRAplus");
			//			
			//			GCubeGroup group = um.getGroup("d4science.research-infrastructures.eu-D4Research-AGINFRAplus");
			//			System.out.println(group.getName());
			//			System.out.println(group.getMembers().toString());
			//						um.setAdministrator(scope, "franco.zoppi");
			//			um.setAdministrator(scope, "pasquale.pagano");
			//			um.removeUserFromGroup(scope, "d4science.research-infrastructures.eu-D4Research-AGINFRAplus");
			//			List<String> users = new ArrayList<>();
			//			users.add("pasquale.pagano");
			//			shared.share(users);
//			um.associateUserToGroup(scope, "grsf.publisher");

			//						um.removeUserFromGroup(scope, "leonardo");


			//			System.out.println(shared.getOwner().getPortalLogin());
			//			WorkspaceSharedFolder shared = (WorkspaceSharedFolder) item;
			//			System.out.println(shared.getName());
			//			shared.unShare();
			//			shared.remove();



			//			System.out.println(item.getRemotePath());
			//
			//			System.out.println(item.getStorageID());
			//
			//			System.out.println("** " + ws.getStorage().getRemotePathByStorageId(item.getStorageID()));


			//JCRExternalFile file = (JCRExternalFile) item;
			//InputStream stream = file.getData();
			//System.out.println(stream.read());
			//stream.close();
			//					WorkspaceSharedFolder vre = ws.getVREFolderByScope(scope);
			//					System.out.println(vre.getPath());
			//					List<String> logins = new ArrayList<String>();
			//					logins.add("raspberry.camera"); 
			//					vre.setAdmins(logins);

			//					String group = Utils.getGroupByScope(scope);
			//					List<String> users = new ArrayList<String>();
			//								users.add(group);
			//								vre.setACL(users, ACLType.WRITE_OWNER);
			//					HomeLibrary.getHomeManagerFactory().getUserManager().removeUserFromGroup(scope, "valentina.marioli");
			//					HomeLibrary.getHomeManagerFactory().getUserManager().associateUserToGroup(scope, "valentina.marioli");
			//					vre.unShare();
			//						 List<WorkspaceItem> list = ws.getMySpecialFolders().getChildren();
			//						 for (WorkspaceItem item: list){											
			//							String group = Utils.getGroupByScope(scope);
			//							JCRWorkspaceFolder folder = (JCRWorkspaceFolder) item;
			//							List<String> users = new ArrayList<String>();
			//							users.add(group);
			//							List<String> admins = new ArrayList<String>();
			//							admins.add("gcube-devsec-SmartCamera-Manager");
			//							ws.shareFolder(users, folder.getId(), "SmartCamera", true, admins);
			////							WorkspaceSharedFolder vre = ws.convertToVREFolder(scope, folder.getId(), "SmartCamera VRE", "SmartCamera");
			//						 }
			//						
			//						createVRESharedGroupFolder(designer, description);
			//
			//			UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
			//			//						String scope = "/d4science.research-infrastructures.eu/D4Research/AGINFRAplus";	
			//
			//			String VREname = "gcube-devNext-Test";
			//			//						um.setAdministrator(scope, "pasquale.pagano");
			//			GCubeGroup group = um.createGroup(VREname);
			//			//					group.addMember("pasquale.pagano");
			//			group.addMember("massimiliano.assante");
			//			group.addMember("francesco.mangiacrapa");
			//			//			
			//			WorkspaceSharedFolder vreFolder = ws.createSharedFolder(VREname, "Special Shared folder for VRE Test", group.getName(), ws.getMySpecialFolders().getId(), "Test", true);
			//			List<String> users = new ArrayList<String>();
			//			users.add(group.getName());
			//			vreFolder.setACL(users, ACLType.WRITE_OWNER);
			//
			//						System.out.println(vreFolder.getPath());
			//						System.out.println(vreFolder.getId());

			//						String itemId =  "113b66a6-999c-42d2-bcd6-21d2271ad919";
			//						WorkspaceItem item = ws.getItem(itemId);
			//						JCRWorkspaceSharedFolder folder = (JCRWorkspaceSharedFolder) item;


			//						System.out.println("**** associate");
			//									boolean flagAssociate = true  ;
			//									if (flagAssociate){
			//										String user = "george.kakaletris";
			////										String adminVREFolder = "gcube-devsec-testVREc4b347c7-0151-48c4-9cb3-795b203225dd-Manager";
			//										boolean f = um.associateUserToGroup(scope, user);
			//										System.out.println("associateUserToGroup? " + f);
			//			//							System.out.println(group.getMembers().toString());
			//									}
			//			
			//									boolean flagRemove = false  ;
			//									if (flagRemove){
			//											String user = "valentina.marioli";
			////										String adminVREFolder = "gcube-devsec-testVREc4b347c7-0151-48c4-9cb3-795b203225dd-Manager";
			//										boolean f = um.removeUserFromGroup(scope, user);
			//										System.out.println("removeUserFromGroup? " + f);
			//										//				System.out.println(group.getMembers().toString());
			//									}



			//		WorkspaceFolder folder = ws.getMySpecialFolders();
			//	List<WorkspaceItem> children = folder.getChildren();
			//	for (WorkspaceItem item: children){
			//		JCRWorkspaceSharedFolder shared  = (JCRWorkspaceSharedFolder) item;
			//	
			//		if (shared.getDisplayName().startsWith("<string>&lt;string&gt;"))
			//		{
			//			System.out.println("REMOVE " + shared.getDisplayName());
			//			shared.remove();
			//		}
			//	}

			//			//			String path = "/Workspace/MySpecialFolders/CNR.it-ISTI-InfraScience/Loghi/D4Science-Sphere_Transparent.png";
			//			//			WorkspaceItem folder = ws.getItemByPath(path);
			//			//			folder.rename("D4Science-Sphere_Transparent00.png");
			//			//			System.out.println(folder.getACLUser().toString());
			//			//			WorkspaceItem item = ws.getItemByPath("/Workspace/MySpecialFolders/gcube-VREValeT/dita,-lo-smiley,-cravatta-168001.jpg");
			//			//			item.remove();
			//			//			System.out.println(folder.getACLOwner());
			//			//			System.out.println(folder.getACLUser());
			//			//			folder.unShare("angela.italiano");
			//			//			folder.setACL(new ArrayList<String>(){/**
			//			//				 * 
			//			//				 */
			//			//				private static final long serialVersionUID = 1L;
			//			//
			//			//			{add("roberto.cirillo");}}, ACLType.ADMINISTRATOR);
			//			//			folder.setACL(folder.getUsers(), ACLType.WRITE_ALL);
			//
			//
			//			//			WorkspaceFolder folder = ws.getMySpecialFolders();
			//			//			System.out.println(folder.getPath());
			//
			//			String adminVREFolder = "CNR.it-ISTI-NeMIS-Manager";
			//			UserManager um = HomeLibrary
			//					.getHomeManagerFactory().getUserManager();
			//			//
			//			//			JCRAccessManager am = new JCRAccessManager();
			//
			//
			//			//create a new group
			//			String VREname = "/gcube/devsec/devVRE";			
			//			//							GCubeGroup group = createGroup(VREname);
			//			//			GCubeGroup group = null;
			//
			//
			//
			//			GCubeGroup group = um.getGroup("gcube-devsec-MyVRE-Vale");
			//
			////			System.out.println(group.getMembers().toString());
			//			////			String VREname = group.getName();
			//			//	
			//
			//
			//			//CHANGE OWNER
			//						String path = "/Workspace/MySpecialFolders/CNR.it-ISTI-NeMIS/";
			//						JCRWorkspaceSharedFolder folder = (JCRWorkspaceSharedFolder) ws.getItemByPath(path);
			//			
			//						System.out.println(folder.getPath());
			//						System.out.println(folder.getOwner());
			//			
			//						Session session = JCRRepository.getSession();
			//						Node node = session.getNode("/Home/CNR.it-ISTI-NeMIS-Manager" + path);


			//
			//
			//			String userToRemove = "luca.frosini";
			//
			//			System.out.println("**** associate");
			//			boolean flagAssociate = false  ;
			//			if (flagAssociate){
			//				boolean f = um.associateUserToGroup(VREname, "luca.frosini", adminVREFolder);
			//				System.out.println("associateUserToGroup? " + f);
			//				System.out.println(group.getMembers().toString());
			//			}
			//
			//			boolean flagRemove = false  ;
			//			if (flagRemove){
			//				boolean f = um.removeUserFromGroup(VREname, userToRemove, adminVREFolder);
			//				System.out.println("removeUserFromGroup? " + f);
			//				//				System.out.println(group.getMembers().toString());
			//			}

		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void createVRESharedGroupFolder(String designer, String description) throws Exception {

		String currScope = ScopeProvider.instance.get();
		//		ScopeProvider.instance.set("/"+getRootOrganizationName());
		//		GroupManager gm = new LiferayGroupManager();
		//		UserManager um = new LiferayUserManager();

		//		GroupModel group = gm.getGroup(""+vreCreated.getOrganizationId());
		String groupId = "groupId"+ UUID.randomUUID();

		String vreName = "vreName"+ UUID.randomUUID();
		String vreScope = "/vreScope"+ UUID.randomUUID();

		//		List<UserModel> users = um.listUsersByGroup(group.getGroupId());
		String vreDesignerUserName = designer;		
		if (vreDesignerUserName != null) {
			Workspace ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(vreDesignerUserName)
					.getWorkspace();

			List<String> users = new ArrayList<String>();
			GCubeGroup gGroup = createGroup(vreScope, users);
			String groupid = (gGroup == null) ? vreScope :  gGroup.getName();
			WorkspaceSharedFolder wSharedFolder = createVREFolder(vreScope, vreName, groupid, ws);

			List<String> groups = new ArrayList<String>();
			groups.add(gGroup.getName());
			wSharedFolder.setACL(groups, ACLType.WRITE_OWNER);

		} else {
			logger.error("NO VRE-MANAGER FOUND IN THIS VRE");			
		}
		ScopeProvider.instance.set(currScope);
	}

	private static GCubeGroup createGroup(String vreScope, List<String> usersToAdd) throws InternalErrorException {
		org.gcube.common.homelibrary.home.workspace.usermanager.UserManager gm = HomeLibrary
				.getHomeManagerFactory().getUserManager();
		GCubeGroup group = gm.createGroup(vreScope);
		for (String user : usersToAdd) {
			group.addMember(user);
		}
		return group;
	}

	private static WorkspaceSharedFolder createVREFolder(String vreScope, String vreName, String groupId, Workspace ws) throws Exception {		

		WorkspaceSharedFolder folder = ws.createSharedFolder(vreScope, "Special Shared folder for VRE " + vreName, groupId, ws.getRoot().getId(), vreName, true);
		System.out.println(folder.getPath());
		return folder;

	}

}



