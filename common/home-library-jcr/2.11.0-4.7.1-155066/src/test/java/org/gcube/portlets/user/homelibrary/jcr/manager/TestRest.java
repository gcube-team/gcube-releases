package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class TestRest {
	static JCRWorkspace ws = null;
	private static final String NAME = "ISExporter";
	private static final String VRE_PATH = "/Workspace/MySpecialFolders/";
	private static final String HOME = "Home";
	private static final String SEPARATOR = "/";
	private static final Object MY_SPECIAL_FOLDER = "MySpecialFolders";
	private static final int BYTES_DOWNLOAD = 4096;
	
	
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {


		try {

//			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
						SecurityTokenProvider.instance.set("a56347eb-9528-4af1-ae9d-366a30325905-843339462");

			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();
			ws = (JCRWorkspace) manager.getHome().getWorkspace();


			Map<String, Boolean> children = new HashMap<String, Boolean>();
			Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			System.out.println("*** Get workspace of " + workspace.getRoot().getPath());
				
			String absPath = "/Workspace/DataMinerAlgorithms";
			absPath = cleanPath(workspace, absPath);
			System.out.println("absPath " +absPath);
			WorkspaceItem item = workspace.getItemByPath(absPath);
			System.out.println("item "+  item.getPath());
			if(item.isFolder()){
				System.out.println("is folder? " + item.isFolder());
				WorkspaceFolder folder = (WorkspaceFolder) item;
				java.util.List<WorkspaceItem> list = folder.getAllChildren(false);
				System.out.println(list.toString());
				for(WorkspaceItem child: list){			
					String name = null;
					if (child.getId().equals(child.getIdSharedFolder())){
						JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder) child;
						if (shared.isVreFolder())
							name = shared.getDisplayName();
					}
					if (name==null)
						name = child.getName();
					children.put(name, child.isFolder());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	public static String cleanPath(Workspace workspace, String absPath) throws ItemNotFoundException, InternalErrorException {
		System.out.println("Clean path " + absPath);
		String myVRE = null;
		String longVRE = null;
		
		String [] splitPath = absPath.split(SEPARATOR);
		if(absPath.contains(VRE_PATH) && (!splitPath[splitPath.length-1].equals(MY_SPECIAL_FOLDER))){
					
			if (splitPath[1].equals(HOME))
				myVRE = splitPath[5];
			else
				myVRE = splitPath[3];

			java.util.List<WorkspaceItem> vres = workspace.getMySpecialFolders().getChildren();
			for (WorkspaceItem vre: vres){
				if (vre.getName().endsWith(myVRE)){
					longVRE = vre.getName();
					break;
				} 
			}

			if (longVRE!=null)
				absPath = absPath.replace(myVRE, longVRE);
		}
		
//		System.out.println("CLEAN PATH " + absPath);
		return absPath;

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
