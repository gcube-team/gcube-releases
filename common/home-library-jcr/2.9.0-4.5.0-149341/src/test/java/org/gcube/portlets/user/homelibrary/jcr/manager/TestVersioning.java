package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.privilegemanager.PrivilegeManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class TestVersioning {
	static JCRWorkspace ws = null;
	private static final String NAME = "ISExporter";
	
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {

		WorkspaceFolder folder = null;
		try {
//											String scope = "/gcube/devsec";
			//			String username = "valentina.marioli";
//						ScopeProvider.instance.set("/gcube");
						ScopeProvider.instance.set("/gcube/preprod/preVRE");
			//			SecurityTokenProvider.instance.set("9ddb6670-ec61-4dfb-b5bd-70cdfe01351a-98187548");

//			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
			//			SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");

//								SecurityTokenProvider.instance.set("8920abf2-54e5-4e35-82ae-abd31dca65c2-98187548");
			//						SecurityTokenProvider.instance.set("aac13fb7-d074-45ae-aa47-61718956a5e6-98187548");

			// Obtained the factory you can retrieve the HomeManager:
			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();
			ws = (JCRWorkspace) manager.getHome("valentina.marioli").getWorkspace();
			System.out.println(ws.getRoot().getPath());
			
			JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath("/Workspace/PreProd/aa/");
			
			System.out.println(item.getRemotePath());
			folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/PreProd/test");
			item.move(folder);
			
			JCRWorkspaceItem item1 = (JCRWorkspaceItem) ws.getItemByPath("/Workspace/PreProd/test/aa/pesce.jpg");
			System.out.println(item1.getRemotePath());
			
//			folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/PreProd/");
//			
//			List<String> users = new ArrayList<>();
//			users.add("francesco.mangiacrapa");
//			folder.share(users);
//			
//			folder.setACL(users, ACLType.WRITE_ALL);
			
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
