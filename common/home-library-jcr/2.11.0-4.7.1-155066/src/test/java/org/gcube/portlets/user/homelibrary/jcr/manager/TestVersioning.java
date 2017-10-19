package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.jcr.PathNotFoundException;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
//import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class TestVersioning {
	static JCRWorkspace ws = null;

	private static String path = "/Workspace/fusion-forms/LNK001339116J_ModuloRID.pdf";
	//	private static String path = "/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-EllinikaPsariaVRE";
	//	private static String path1 = "/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-AlieiaVRE";
	//	private static String path11 = "/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-ForkysVRE";


	private static  String user = "fabio.sinibaldi";

	private static FileWriter writer;

	private static final String NAME = "ISExporter";

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {
		writer = new FileWriter("vreFolders.txt",false);
		//		WorkspaceFolder folder = null;
		try {
			//											String scope = "/gcube/devsec";
			//			String username = "valentina.marioli";
			//			ScopeProvider.instance.set("/gcube/devNext/NextNext");
//						ScopeProvider.instance.set("/gcube");
//						ScopeProvider.instance.set("/gcube/preprod");
			//						SecurityTokenProvider.instance.set("019b7d49-70dd-432f-b085-b4a7f8498f06-843339462");

			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
			//			SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");

			//											SecurityTokenProvider.instance.set("5406fece-8f01-445e-856e-e780748448ad-843339462");
			//						SecurityTokenProvider.instance.set("aac13fb7-d074-45ae-aa47-61718956a5e6-98187548");
			//
			//			List<String> users = HomeLibrary.getHomeManagerFactory().getUserManager().getUsers();
			//			for (String user: users){
			HomeManager manager1 = HomeLibrary.getHomeManagerFactory().getHomeManager();
			ws = (JCRWorkspace) manager1.getHome(user).getWorkspace();	
			
			
//			WorkspaceItem node = ws.getItemByPath("/Home/valentina.marioli/Workspace/test00+");
			WorkspaceItem node = ws.getItem("be451663-4d4f-4e23-a2c8-060cf15d83a7");
			List<? extends WorkspaceItem> children = node.getChildren();
			for (WorkspaceItem child:children){
				System.out.println(child.getPath());
				System.out.println(child.getPublicLink(false));
			}
						
//			System.out.println("Remote path " + node.getRemotePath());
//			System.out.println("Storage ID " + node.getStorageID());
//			System.out.println(ws.getStorage().getStorageId(node.getRemotePath()));
			
			
//			System.out.println(ws.getStorage().getFolderTotalItems("/Share/ecf79885-464d-45ec-b0f4-213324dba185/WP3_Regional_Resource_Models/Task3.1_Integrated_regional_models/LosHumeros_literature/"));
//			System.out.println(ws.getStorage().getRemotePathByStorageId(node.getStorageID()));
			
//			Remote path /Share/ecf79885-464d-45ec-b0f4-213324dba185/WP3_Regional_Resource_Models/Task3.1_Integrated_regional_models/LosHumeros_literature/Alfonso Aragón-Aguilar_2012.pdf
//			Storage ID 5936b2aa02cadc704344c9aa
			
			
//			WorkspaceItem node = ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-GEMex/WP3_Regional_Resource_Models/Task3.1_Integrated_regional_models/LosHumeros_literature/");
		
			
//			WorkspaceFolder folder = (WorkspaceFolder) node;
//			List<WorkspaceItem> children = folder.getChildren();
//			for (WorkspaceItem child: children){
//				if (child.getName().startsWith("Alfonso Arag")){
//
//					System.out.println(child.getId() + " - "+child.getPath());
//					
//					System.out.println("Remote path " + child.getRemotePath());
//					System.out.println("Storage ID " + child.getStorageID());
////					System.out.println(ws.getStorage().getRemotePathByStorageId(child.getStorageID()));
//				}
//			}
		
			
//			System.out.println("StorageID " + node.getStorageID());
//			
//			JCRWorkspaceItem item = (JCRWorkspaceItem) node;
					
//			item.setStorageId(ws.getStorage().getStorageId(node.getRemotePath()));
//			System.out.println(ws.getStorage().getStorageId(node.getRemotePath()));
		

//			String initialFile = "/home/valentina/Downloads/Science 2.0-Survey 2014_PDF_A.indd.pdf";
//			InputStream is = new FileInputStream(initialFile);
//			ws.getStorage().putStream(is, node.getRemotePath());
			//				ws.getTrash().emptyTrash();

			//				try{
			//					//				List<WorkspaceItem> children = ws.getRoot().getChildren();
			//					List<WorkspaceItem> children = ws.getMySpecialFolders().getChildren();
			//					for (WorkspaceItem child: children){
			//						//					System.out.println("****");
			//						//					WorkspaceSharedFolder folder = (WorkspaceSharedFolder) child;
			//						//					System.out.println(folder.getName());
			//						//					System.out.println(folder.getUsers());
			//						//					JCRWorkspaceItem item = (JCRWorkspaceItem) child;
			//						//					item.get
			//						//						if (child.getName().startsWith("d4science.research-infrastructures.eu-gCubeApps-ForkysVRE"))
			//						try{
			//							//						System.out.println(child.getName());
			//							System.out.println(child.getPath());
			//							//					List<AccountingEntry> history = child.getAccounting();
			//							//					for (AccountingEntry entry : history){
			//							//						System.out.println(entry.getDate().getTime() + " - " + entry.getEntryType());
			//							//					}
			//						} catch (Exception e) {
			//							generateCsvFile(child.getName(), user);
			//							e.printStackTrace();
			//						}
			//					}
			//
			//
			//				} catch (Exception e) {
			//
			//					e.printStackTrace();
			//				}	
			//			}
			//			writer.close();

			//			List<String> users = new ArrayList<>();
			//			users.add("luca.frosini");
			//			users.add("pasquale.pagano");
			//			WorkspaceSharedFolder share = folder.share(users);
			//			share.setACL(users, ACLType.WRITE_ALL);

			//			JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-BlueBridgeProject/Meetings/Project Meetings");
			//
			//			ACLType acl  = item.getACLUser();
			//
			//			System.out.println(item.getACLByUser(portalLogin));
			//
			//
			//			if ( !(item.getOwner().getPortalLogin().equals(ws.getOwner().getPortalLogin())) && !(acl.equals(ACLType.ADMINISTRATOR)))
			//				throw new InsufficientPrivilegesException("Insufficient Privileges to set the folder as public.");

			//			for(WorkspaceItem child: item.getChildren()){
			//				System.out.println(child.getPath());
			//				System.out.println(child.getId());
			////				child.remove();
			//				
			//			}
			//			System.out.println(ws.getRoot().getPath());
			//			
			//			JCRWorkspaceFolder special = (JCRWorkspaceFolder) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-D4Research-E-RIHS/Work packages/");
			//			System.out.println(special.getPath());
			//			List<WorkspaceItem> children = special.getChildren();
			//			for (WorkspaceItem child: children){
			//				if (child.getName().startsWith("WP 9")){
			//					System.out.println(child.getId() + " - " + child.getName());
			//				ws.removeItem(child.getId());
			//				String name = child.getPath();
			//				System.out.println(name);
			//				
			////				WorkspaceItem item = ws.getItemByPath(name);
			////				System.out.println("---> " + item.getPath());
			//				System.out.println("---> " + URLEncoder.encode(name, "UTF-8"));
			//				String result = URLDecoder.decode(name, "UTF-8");
			////				String path = new String(name.getBytes("iso-8859-1"), "UTF-8");
			//				System.out.println("path " +  result);
			//				}
			//			}
			//			ws.moveItem(special.getId(), ws.getRoot().getId());
			//			
			//			WorkspaceItem target = ws.getItemByPath("/Workspace/DataMiner/");
			//			ws.removeItem(target.getId());

			//			List<? extends WorkspaceItem> children = target.getChildren();
			//			for (WorkspaceItem item: children){
			//				System.out.println(item.getPath());
			//				ws.removeItem(item.getId());
			//			}

			//			//script to associate users to groups
			//			UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
			//			um.createUser("nagios", "checker01");

			//			System.out.println(um.getUsers().contains("nagios"));
			//			List<String> homes = um.getUsers();

			//			//			boolean skip = true;
			//			//			for (String login:homes){
			//			String login = "giancarlo.panichi";
			//			System.out.println("-----> " + login);
			//
			//			//				if (login.equals("masetti"))
			//			//					skip = false;
			//			//\
			//			//				if (skip)
			//			//					continue;
			////			HomeLibrary.getHomeManagerFactory().getUserManager().deleteAuthorizable("nagios");
			//			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();
			//			ws = (JCRWorkspace) manager.getHome().getWorkspace();
			//			System.out.println(ws.getRoot().getPath());

			//			/Home/statistical.manager/Workspace/DataMiner/Computations/
			//			System.out.println(getSecurePassword(login));
			//
			//		WorkspaceItem item = ws.getItemByAbsPath("/Share/ffc6a2b3-3b6a-46d7-a081-4c133789e5cc/.catalogue/fishery/2/2c8ed3ff-c5ed-3894-8f2c-01416bcf0a9b/csv/Patagonian grenadier   Argentine   Argentina_Catches.csv");
			//
			//String storageId = item.getStorageID();
			//
			//System.out.println("---> " + ws.getStorage().getRemotePathByStorageId(storageId));
			//System.out.println(item.getPublicLink(false));
			//			String itemId = ws.getItemByPath("/Workspace/BlackBoxSAI/RBlackBox").getId();
			//			ws.removeItem(itemId);
			//			ws.getStorage().removeRemoteFolder("/Home/valentina.marioli/Workspace");

			//			WorkspaceFolder folders = ws.getMySpecialFolders();
			//			List<WorkspaceItem> children = folders.getChildren();

			//			JCRGroup group = null;
			//			for(WorkspaceItem item: children){
			//
			//
			//				WorkspaceSharedFolder shared = (WorkspaceSharedFolder) item;
			//
			//				if(!item.getName().equals("d4science.research-infrastructures.eu-SmartArea-SmartApps"))
			//					continue;
			//
			//				System.out.println(shared.getPath() + " - " +shared.getId());
			//				children = shared.getChildren();
			//				for(WorkspaceItem child: children){
			//					System.out.println(child.getName());
			//				}


			//				group = (JCRGroup) um.getGroup(shared.getName());
			//	List<String> list = new ArrayList<>();
			//	list.add(group.getName());
			//	System.out.println("set acl " + list.toString());
			//	shared.setACL(list, ACLType.WRITE_OWNER);

			//									

			//					System.out.println(item.getName());
			//
			//				if (group!=null){
			//					group = (JCRGroup) um.createGroup(item.getName());

			//				System.out.println(group.getMembers());
			//						if (group.getMembers().contains(login))
			//							System.out.println("is in");
			//						else{
			//							System.out.println("NOT in");



			//				System.out.println("* " +shared.getACLUser());
			//														System.out.println(shared.getACLOwner().toString());
			//							System.out.println(shared.getUsers().toString());
			//					List<String> users = shared.getUsers();
			//							System.out.println("Add users: ");
			//					for (String user: users){
			//						System.out.println("* " +user);
			//						group.addMember(user);	
			//													}
			//					}
			//			}

			//					}
			//				}
			//			}


			//END SCRIPT
			//			ws.getTrash().emptyTrash();


			//			WorkspaceItem item = ws.getItem("92ffba1e-b0b7-4ec3-a56f-0bc5dae60e87");
			//			System.out.println(item.getPath());

			//			afa86347-22f7-4f03-8665-a09484ae6754 - VSURF
			//			92ffba1e-b0b7-4ec3-a56f-0bc5dae60e87 - vsurf.R
			//			d1ddcf7c-58d6-4ab2-a193-abcc439c86b8 - stat_algo.project
			//			5f0321f4-9354-424a-bab1-b0c0d91cdb82 - Backup
			//			6ee5d762-ba19-4136-880b-82bd07245cf4 - y.txt
			//			9a1585a6-01f3-4ab7-8b37-f6a08aa645ba - x.txt
			//			568c0c10-42df-4a30-9050-b2d4a9b2289e - Target



			//			WorkspaceItem item = ws.getItemByPath("/Workspace/MySpecialFolders/gcube-devsec-SmartCamera/camera-test/dataset/2016-10-14/2016-10-14_11^48.jpg");
			//			System.out.println(item.getRemotePath());
			//			WorkspaceItem target = ws.getItemByPath("/Workspace/MySpecialFolders/gcube-devsec-SmartCamera/camera02/dataset/2016-10-14/2016-10-14_11^48.jpg");
			//			System.out.println(target.getRemotePath());

			//			System.out.println(item.getId() + " - " + item.getName());
			//			List<WorkspaceItem> children = item.getChildren();
			//			for (WorkspaceItem child: children){
			//			System.out.println(child.getId() + " - " + child.getName());
			//			}

			//			WorkspaceItem item = ws.getItemByPath("/Workspace/importTest/coord.pdf");
			//			WorkspaceItem target = ws.getItemByPath("/Workspace/exportTest");
			//			
			//			ws.moveItem(item.getId(), target.getId());

			//			ws.getTrash().emptyTrash();
			//			GCUBEStorage storage = ws.getStorage();
			//			storage.removeRemoteFolder("/Share/07d6112:34639-4bfc-4bc0-8c0c-e902d419215c");

			//
			//			WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/DataMinerAlgorithms");
			//			System.out.println(folder.getPath());

			//			List<WorkspaceItem> children = folder.getChildren();
			//			for( WorkspaceItem child: children){
			//				
			//				String storageID =storage.getStorageId(child.getRemotePath());
			//				System.out.println(storageID);
			//				JCRWorkspaceItem myitem = (JCRWorkspaceItem) child;
			//				myitem.setStorageId(storageID);
			//			}
			//			

			//			/Home/statistical.manager/Workspace/DataMinerAlgorithms
			//			/Home/statistical.manager/Workspace/DataMinerAlgorithms
			//			WorkspaceItem target = ws.getItemByPath("/Workspace/D4Science Operation Data/productionKeys2015/d4science.research-infrastructures.eu.gcubekey");
			//		
			//			
			//			System.out.println(storage.getStorageId(target.getRemotePath()));
			//			System.out.println(target.getStorageID());
			//			
			//			ws.getStorage().getRemotePathByStorageId(target.getStorageID());
			//			System.out.println(target.getRemotePath());
			//			WorkspaceItem workSpaceItem = ws.getItemByPath("/Workspace/SAIBlackBox/WindowsBlackBox/Target/Deploy/JavaBlackBox.zip");
			//			WorkspaceItem item = ws.copy(workSpaceItem.getId(), "JavaBlackBox_backup_20170621_132956-test.zip", target.getId());
			//			System.out.println(item.getPath());


			//			WorkspaceItem workSpaceItem = ws.getItem("4172eb51-a28d-4e76-8466-9dc849da704c");
			//			WorkspaceFolder folder = (WorkspaceFolder) workSpaceItem;
			//
			//			List<String> idsToExclude = new ArrayList<String>();
			//			File fileZip = ZipUtil.zipFolder(folder, false, idsToExclude); 
			//
			//			System.out.println(fileZip.getAbsolutePath());


			//		WorkspaceMessageManager msgManager = ws.getWorkspaceMessageManager();
			//			List<WorkspaceMessage> list =msgManager.getSentMessages();
			//			for (WorkspaceMessage msg: list){
			//				msgManager.deleteSentMessage(msg.getId());
			//				System.out.println("Deleted " +  msg.getId());
			//			}

			//			JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-KnowledgeBridging/2017.05.08-12 CNR - 20h Course (5 Moduled) @ PhD Pisa - Biodiversity Conservation/BiodivConservation_Module 1 - eInfra and Bio Data Access.pptx");



			//			folder = ws.createFolder("test000", "", ws.getRoot().getId());
			//			folder.rename("test : test");
			//			WorkspaceItem item = ws.getItemByPath("/Workspace/Project_SimulFishKPIs_DEV/Target/Deploy/ProjectSimulFishKPIsDEV.zip");
			//			System.out.println(item.getPath());
			//			
			//			ExternalFile file = (ExternalFile) item;
			//			List<WorkspaceVersion> history = file.getVersionHistory();
			//			for (WorkspaceVersion version: history){
			//				System.out.println(version.getRemotePath());
			//			}
			//			System.out.println(ws.getItem("50a5bbef-de7b-4169-b312-306cac4c1455").getPath());

			//			System.out.println(item.getPublicLink(false));


			//			http://data.d4science.org/TG1Ib1pyR0MvRVJpVmI0dEpOcitqekM0Y1g0UUtWVVBHbWJQNStIS0N6Yz0 (1.1)
			//			http://data.d4science.org/Sy9PY01ySXV2M01HVmZMUUdvR2Urbkd4b1grdU93WUZHbWJQNStIS0N6Yz0 (1.0)

			//			System.out.println(ws.getVersioning().getCurrentVersion(item.getId()));
			//			
			//			WorkspaceVersion version = ws.getVersioning().getVersion(item.getId(), "1.0");
			//			System.out.println(ws.getStorage().getPublicLink(version.getRemotePath()));
			//			List<WorkspaceVersion> list = ws.getVersioning().getCurrentVersion(id)
			//			for (WorkspaceVersion version: list){
			//				System.out.println(version.get);
			//			}

			//			ws.renameItem(item.getId(), item.getName().trim());
			//			
			//			System.out.println(item.getName().getBytes("iso-8859-1"));
			//			System.out.println(URLEncoder.encode(item.getName(), "UTF-8"));
			//			System.out.println(ws.getRoot().getPath());
			//			
			//			folder = ws.createFolder("rtes .. sfdsd", "", ws.getRoot().getId());
			//			System.out.println(folder.getId());
			//			String name = "TestShareAdmin";
			//			String description = "";
			//			List<String> users = new ArrayList<String>();
			//			users.add("francesco.mangiacrapa");
			//			users.add("massimiliano.assante");
			//			WorkspaceSharedFolder shared = ws.createSharedFolder(name, description, users, ws.getRoot().getId());
			//			shared.setACL(users, ACLType.WRITE_ALL);
			//			List<String> logins = new ArrayList<String>();
			//			logins.add("francesco.mangiacrapa");
			//			logins.add("valentina.marioli");
			//			shared.setAdmins(logins);


			//			String scope = "/d4science.research-infrastructures.eu/gCubeApps/GEMex";
			//			WorkspaceSharedFolder share = ws.getVREFolderByScope(scope);
			////			System.out.println(share.getOwner().getPortalLogin());
			////			System.out.println(share.getACLOwner().toString());
			//			
			//			System.out.println(share.getUsers().toString());
			//			folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-GEMex/WP3_Regional_Resource_Models/Task3.1_Integrated_regional_models/");
			//			System.out.println(folder.getACLOwner().toString());
			//			

			//			List<String> users = new ArrayList<>();
			//			users.add("massimiliano.assante");
			//			folder.setACL(users, ACLType.WRITE_OWNER);
			//			List<WorkspaceItem> children = ws.getRoot().getChildren();
			//			for (WorkspaceItem child: children){
			//				System.out.println(child.getId() + " - " + child.getPath() + " - " + child.getName());
			//				
			//				System.out.println(ws.getItemByPath(child.getPath()).getPath());
			//			}
			//			ws.createFolder("test[5555]", "desc", ws.getRoot().getId());
			//			WorkspaceItem item = ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-Parthenos/Project Meetings/2017 02 15 WP5-6 Joint Tech. Meeting @Athens/Slides");
			////			WorkspaceItem item = ws.getItem("2c58ae55-ade1-4f5e-92fa-b5ae2d67b38e");
			//			System.out.println(item.getPath());

			//			System.out.println(item.getOwner().getPortalLogin());
			//			System.out.println("** " + item.getDescription());

			//			System.out.println(item.toString());
			//			if (item.getDescription()==null)
			//				System.out.println("null");

			//			WorkspaceSharedFolder shared = (WorkspaceSharedFolder) ws.getItemByAbsPath("/Share/c2649c4c-5974-42fe-9797-17614f79f740");

			//			List<String> users = new ArrayList<>();
			//			users.add("costantino.perciante");
			//			shared.share(users);
			//			shared.setACL(users, ACLType.WRITE_ALL);
			//			System.out.println(ws.getItemByAbsPath("/Share/c2649c4c-5974-42fe-9797-17614f79f740").getPath());

			//			String scope = "/d4science.research-infrastructures.eu/gCubeApps/AlieiaVRE";
			//			WorkspaceSharedFolder vre = ws.getVREFolderByScope(scope);
			//			System.out.println(vre.getAdministrators().toString());
			//			System.out.println(vre.getUsers().toString());
			//			HomeLibrary.getHomeManagerFactory().getUserManager().associateUserToGroup(scope, "denispyr");
			//			HomeLibrary.getHomeManagerFactory().getUserManager().get

			//			File file = new File("/home/valentina/Downloads/AsfisSpecies.txt");
			//			FolderItem item = publishFileToWorkspace(file);
			//			System.out.println(item.getPath());
			//			
			//			folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/ISExporter");
			//			List<String> users = new ArrayList<>();
			//			users.add("valentina.marioli");
			//			WorkspaceSharedFolder shared = folder.share(users);
			//			
			//			shared.setACL(users, ACLType.WRITE_ALL);


			//			JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-D4Research-AGINFRAplus/Legal Documents/Consortium Agreement/ConsortiumAgreement-V2-AGINFRA_20170407.docx");
			//			System.out.println(item.getPath());
			//		
			//			String storageId = item.getStorageID();
			//			System.out.println(storageId);
			//			
			//			String remotePath = ws.getStorage().getRemotePathByStorageId(storageId);
			//			System.out.println("** "+ remotePath);
			//			
			////			item.setRemotePath(remotePath);
			//			System.out.println("** " +item.getRemotePath());
			//			System.out.println(item.getPublicLink(true));


			//			System.out.println(ws.getRoot().getPath());

			//			List<WorkspaceItem> children = (List<WorkspaceItem>) ws.getItemByPath("/Workspace/DataMiner/Input Data Sets/").getChildren();
			//			ArrayList<String> ids = new ArrayList<String>();
			//			for (WorkspaceItem child: children) {
			//				ids.add(child.getId());
			//				System.out.println(child.getPath());
			//			}
			//			
			//			
			//			ws.removeItems(ids.toString());


			//						UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
			//						String version = "3.1.1";
			//						List<String> users = Arrays.asList("valentina.marioli");
			//						for (String user: users){
			//							String pwd = getSecurePassword(user);
			//							if (um.createUser(user, pwd, version))
			//								System.out.println(user + " created");
			//						}

			//			JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder) ws.getItemByAbsPath("/Share/30f98438-9f30-4d46-86b9-7d8b9fde97a3");
			//			List<String> users = new ArrayList<>();
			//			
			//			
			//			users.add("valentina.marioli");
			//			users.add("massimiliano.assante");
			//			shared.share(users);


			//						PrivilegeManager am = HomeLibrary.getHomeManagerFactory().getPrivilegeManager();
			//			
			//						am.createCostumePrivilege("hl:noOwnershipLimit", new String[] {});
			//						am.createCostumePrivilege("hl:writeAll", new String[] {"jcr:write", "hl:noOwnershipLimit"});
			//						am.createCostumePrivilege("hl:removeSharedRoot", new String[] {});
			//						System.out.println("done");





			//			String path = "/Workspace/versions/version.png";
			//			download(manager, "valentina.marioli", path);
			//
			//			String path01 = "/Workspace/Part-B_I3_Partner_Profile_NEAFC_V0.doc";
			//			download(manager, "massimiliano.assante", path01);
			//
			//			download(manager, "valentina.marioli", path);
			//			download(manager, "massimiliano.assante", path01);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void generateCsvFile(String url, String user)
	{
		try
		{


			writer.append(url);
			writer.append(',');
			writer.append(user);
			writer.append('\n');


			//generate whatever data you want

			writer.flush();

		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 
	}

	public static String getSecurePassword(String message) throws InternalErrorException {
		String digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(message.getBytes("UTF-8"));

			//converting byte array to Hexadecimal String
			StringBuilder sb = new StringBuilder(2*hash.length);
			for(byte b : hash){
				sb.append(String.format("%02x", b&0xff));
			}
			digest = sb.toString();

		} catch (UnsupportedEncodingException e) {
			throw new InternalErrorException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new InternalErrorException(e);
		}
		return digest;
	}

	private static void download(HomeManager manager, String user, String path) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException, IOException, WorkspaceFolderNotFoundException {
		Home home = manager.getHome(user);

		JCRWorkspace ws = (JCRWorkspace) home.getWorkspace();


		WorkspaceItem item = ws.getItemByPath(path);
		JCRExternalFile file = (JCRExternalFile) item;
		System.out.println(file.getPath());

		InputStream stream;
		for (int i=0; i<2 ;i++){
			stream = file.getData();
			System.out.println(stream.available());
		}

	}

	private static void navigate(WorkspaceItem item) throws InternalErrorException {
		//		if (!item.isFolder()){
		System.out.println(item.isFolder());
		System.out.println(item.getId());
		System.out.println(item.getPath());
		System.out.println(item.getName());
		System.out.println(item.getDescription());
		//		}else{
		if (item.isFolder()){
			WorkspaceFolder folder = (WorkspaceFolder) item;
			List<WorkspaceItem> children = folder.getChildren();
			for (WorkspaceItem child: children){
				navigate(child);

			}
		}

	}
	protected static String getUsername() throws Exception {
		String token = SecurityTokenProvider.instance.get();
		return Constants.authorizationService().get(token).getClientInfo().getId();
	}

	@SuppressWarnings("deprecation")
	protected static Home getHome(HomeManager manager) throws Exception{
		Home home = null;
		String scope = ScopeProvider.instance.get();
		if(scope!=null){
			home = manager.getHome(getUsername());
		}else{
			home = manager.getHome();
		}
		return home;
	}

	protected static WorkspaceFolder getExporterFolder(Workspace ws) throws Exception {
		WorkspaceFolder root = ws.getRoot();

		WorkspaceFolder exporterFolder = null;
		if(!ws.exists(NAME, root.getId())){
			String folderDescription = String.format("The folder is used by %s plugin to store informations regarding failures exporting old GCore Resource to new Resource Registry", NAME);
			exporterFolder = ws.createFolder(NAME, folderDescription, root.getId());
		}else{
			exporterFolder = (WorkspaceFolder) ws.find(NAME, root.getId());
		}
		return exporterFolder;
	}

	public static final String APPLICATION_JSON_MIMETYPE = "text/plain";

	private static final String FOLDER_DESCRIPTION = "Failures Report Folder for " + NAME;

	protected static FolderItem publishFileToWorkspace(File file) throws Exception {
		String scope = ScopeProvider.instance.get();

		HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
		HomeManager manager = factory.getHomeManager();
		Home home = getHome(manager);
		Workspace ws = home.getWorkspace();
		WorkspaceFolder exporterFolder = getExporterFolder(ws);
		FileInputStream fileInputStream = new FileInputStream(file);

		FolderItem folderItem = WorkspaceUtil.createExternalFile(
				exporterFolder, scope.replace("/", "") + file.getName(), 
				FOLDER_DESCRIPTION,
				APPLICATION_JSON_MIMETYPE, 
				fileInputStream);
		return folderItem;
	}



}
