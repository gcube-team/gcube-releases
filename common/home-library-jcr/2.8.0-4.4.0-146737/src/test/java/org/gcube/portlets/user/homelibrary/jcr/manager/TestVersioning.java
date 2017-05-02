package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.PathNotFoundException;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
//import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
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
import org.gcube.common.homelibrary.home.workspace.privilegemanager.PrivilegeManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.scope.api.ScopeProvider;

public class TestVersioning {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {

		WorkspaceFolder folder = null;
		try {
			//								String scope = "/gcube/devsec";
			//			String username = "valentina.marioli";
			//			ScopeProvider.instance.set("/gcube/devNext/NextNext");
//			ScopeProvider.instance.set("/gcube/preprod/preVRE");
//			SecurityTokenProvider.instance.set("9ddb6670-ec61-4dfb-b5bd-70cdfe01351a-98187548");
			
						ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
			//			SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");

			//					SecurityTokenProvider.instance.set("8920abf2-54e5-4e35-82ae-abd31dca65c2-98187548");
			//						SecurityTokenProvider.instance.set("aac13fb7-d074-45ae-aa47-61718956a5e6-98187548");

			// Obtained the factory you can retrieve the HomeManager:
			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();
			ws = (JCRWorkspace) manager.getHome("scarponi").getWorkspace();
			System.out.println(ws.getRoot().getPath());
		
			List<WorkspaceItem> children = (List<WorkspaceItem>) ws.getItemByPath("/Workspace/DataMiner/Input Data Sets/").getChildren();
			ArrayList<String> ids = new ArrayList<String>();
			for (WorkspaceItem child: children) {
				ids.add(child.getId());
				System.out.println(child.getPath());
			}
			
			
			ws.removeItems(ids.toString());
			
			
//			JCRUserManager um = ws.getRepository().getUserManager();
//			String version = "3.1.1";
//			List<String> users = Arrays.asList("");
//			for (String user: users){
//				String pwd = getSecurePassword(user);
//				if (um.createUser(user, pwd, version))
//					System.out.println(user + " created");
//			}

//			JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder) ws.getItemByAbsPath("/Share/30f98438-9f30-4d46-86b9-7d8b9fde97a3");
//			List<String> users = new ArrayList<>();
//			
//			
//			users.add("valentina.marioli");
//			users.add("massimiliano.assante");
//			shared.share(users);
			
			
//			PrivilegeManager am = HomeLibrary.getHomeManagerFactory().getPrivilegeManager();
//
//			am.createCostumePrivilege("hl:noOwnershipLimit", new String[] {});
//			am.createCostumePrivilege("hl:writeAll", new String[] {"jcr:write", "hl:noOwnershipLimit"});
//			am.createCostumePrivilege("hl:removeSharedRoot", new String[] {});
//			System.out.println("done");
//			shared.setACL(users, ACLType.s);
		    
//		    TITLE  gcube-preprod-preVRE
//		    Owner  mister.orange and string 982fbbd9-e342-4f39-91ec-1b70acb02b97/gcube-preprod-preVRE
//		    Owner  valentina.marioli and string 3ddd9da3-95c3-4cd8-bf63-923c279e627b/gcube-preprod-preVRE
//		    Owner  andrea.rossi and string 02c58e34-ed39-4608-a8ad-387a8e664603/gcube-preprod-preVRE
//		    Owner  mister.brown and string bfe6cb23-cf57-48ca-b1b3-dc22a13d6e28/gcube-preprod-preVRE
//		    Owner  efthymios and string 34539a1c-9740-4e8a-91ab-6f5f38f18050/gcube-preprod-preVRE
//		    Owner  dkatris and string 908e95ec-c7ac-4649-bbd9-212cfd912d50/gcube-preprod-preVRE
//		    Owner  babisflou87 and string 46665585-ec2f-48a4-9e26-8d491a58feec/gcube-preprod-preVRE
//		    Owner  gantzoulatos and string dd35d82b-f5a1-4f88-b813-384008d7af55/gcube-preprod-preVRE
//		    Owner  statistical.manager and string bce2758f-82b8-4d5e-bf1a-a4b0a230d4fb/gcube-preprod-preVRE
//		    Owner  roberto.cirillo and string 388ac238-c7da-4df6-9783-9d5168be21c6/gcube-preprod-preVRE
//		    Owner  ciro.formisano and string 25bc3759-0482-4769-8710-bffba6a338f9/gcube-preprod-preVRE
//		    Owner  giorgosalex1521 and string 7a0a1d88-8888-42a7-9bed-f61b032db1ad/gcube-preprod-preVRE
//		    Owner  bimbominka and string a951a115-b34c-4c99-b43c-7e4efd198728/gcube-preprod-preVRE
//		    Owner  mister.blonde and string 5b4e6cc2-9907-4a29-a7cf-04c16f1ef420/gcube-preprod-preVRE
//		    Owner  costantino8 and string dfca31e0-6480-4411-8380-87bb9ed8d34e/gcube-preprod-preVRE
//		    Owner  massimiliano.assante and string 00fc30dd-dedc-47c5-8164-22f8749e1529/gcube-preprod-preVRE
//		    Owner  madigiro and string 5ea49c12-1810-4154-a4b9-84323ef19252/gcube-preprod-preVRE
//		    Owner  mister.pink and string 393f175c-2ce8-4907-8f6a-9b6d861333b7/gcube-preprod-preVRE
//		    Owner  mister.blue and string f7dab074-4a3e-4e42-b691-af553d24439f/gcube-preprod-preVRE
//		    Owner  testone and string c40a520f-21f3-4167-872e-34073d4ee2df/gcube-preprod-preVRE
//		    Owner  jcr:primaryType and string nt:unstructured
//		    Owner  sandu and string 18027a97-c1de-4547-a36b-b11f0ea0246d/gcube-preprod-preVRE
//		    Owner  gkoltsida and string 019aa953-799e-412f-a786-5f92723509f2/gcube-preprod-preVRE
//		    Owner  yannis.marketakis and string e483591d-d3f3-4059-88b8-5c2a6510a887/gcube-preprod-preVRE
//		    Owner  grsf.publisher and string fb159979-b11f-457f-9298-46490876d66b/gcube-preprod-preVRE
//		    Owner  kgiannakelos and string 72851377-ef05-485e-a1d2-20ec7b046c8a/gcube-preprod-preVRE
//		    Owner  gcube-preprod-preVRE-Manager and string cb7e18ad-ca5c-4c36-bfa5-f752a916ce73/gcube-preprod-preVRE
//		    Owner  denispyr and string 148d8b1f-94e5-4aec-a1f8-988c683fcb67/gcube-preprod-preVRE
//		    Owner  florosvassilhs and string f77b4b04-2294-4ab4-918e-10e56d163355/gcube-preprod-preVRE
		    
		    
		    


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




}
