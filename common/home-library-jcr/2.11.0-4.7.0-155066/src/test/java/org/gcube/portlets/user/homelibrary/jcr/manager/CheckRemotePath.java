package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.swing.plaf.synth.SynthSpinnerUI;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRGroup;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckRemotePath {


	static JCRWorkspace ws = null;
	public static final String REMOTE_STORAGE_PATH 		=  	"hl:remotePath";
	public static final String DATA 	  				= 	"jcr:data";

	static BufferedWriter bw = null;
	static List<String> lista =null;
	static Logger logger = LoggerFactory.getLogger(CheckRemotePath.class);

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		//		ScopeProvider.instance.set("/gcube/devsec");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		//		String portalLogin = "gianpaolo.coro";

		UserManager um = HomeLibrary
			.getHomeManagerFactory().getUserManager();
		
		
		List<GCubeGroup> groups = um.getGroups();
		for (GCubeGroup gro: groups){
			JCRGroup group = (JCRGroup) gro;
			String name = group.getName();
//			System.out.println(name);
//			String groupname = "d4science.research-infrastructures.eu-D4Research-AnalyticsLab";
			try{
			group.getDisplayName();
			} catch (Exception e) {
			
				String displayname = null;
			
				if (name.startsWith("d4science.research-infrastructures.eu-D4Research-"))
					displayname = name.replace("d4science.research-infrastructures.eu-D4Research-", "");
				else if (name.startsWith("d4science.research-infrastructures.eu-gCubeApps-"))
					displayname = name.replace("d4science.research-infrastructures.eu-gCubeApps-", "");
				else if (name.startsWith("d4science.research-infrastructures.eu-SoBigData-"))
					displayname = name.replace("d4science.research-infrastructures.eu-SoBigData-", "");
				else if (name.startsWith("d4science.research-infrastructures.eu-FARM-"))
					displayname = name.replace("d4science.research-infrastructures.eu-FARM-", "");
				else
					System.out.println("!!! " + name);
//				System.out.println(displayname.trim());
				
				try{
				group.setDisplayName(displayname.trim());
				} catch (Exception e1) {
					
					System.out.println("delete " + name);
					um.deleteAuthorizable(name);
					
					System.out.println("cannot set displayname " + displayname);
				}
			}
		}
		
		

//		List<String> users =um.getUsers();
//
//		bw = new BufferedWriter(new FileWriter("/home/valentina/missing.csv", true));
//
//		try{
//			for (String user:users){
//				ws = (JCRWorkspace) HomeLibrary
//						.getHomeManagerFactory()
//						.getHomeManager()
//						.getHome(user).getWorkspace();
//
//				checkStorage(ws.getRoot(), user);
//
//			}
//		}finally {                
//			if (bw != null) try {
//				bw.close();
//			} catch (IOException ioe2) {
//
//			}
//		} 
	}


	private static void checkStorage(WorkspaceItem item, String portalLogin) throws InternalErrorException, InsufficientPrivilegesException, IOException {

		try{

//			System.out.println(item.getPath());
			if (item.isFolder()){
//				System.out.println("is folder or gcubeITem -> skip");

				List<? extends WorkspaceItem> children = item.getChildren();
				for (WorkspaceItem child: children)
					checkStorage(child, portalLogin);

			}else{
				if(item.getOwner().getPortalLogin().equals(portalLogin))
					try{

						String storagePath = ws.getStorage().getRemotePathByStorageId(item.getStorageID());
						String remotePath = item.getRemotePath();
					
						if(!storagePath.equals(remotePath))
							System.out.println(storagePath + " : " + remotePath );
//							System.out.println("!!!!!!!!!!!!!!!!");

						//										write(item.getPath() + "\t" + item.getCreationTime().getTime().toString() + "\t" + "OK");
					} catch (Exception e) {
//						System.out.println("CREATED: " + item.getCreationTime().getTime());
						//				child.remove();
						write(item.getPath() + "\t" + item.getCreationTime().getTime().toString() + "\t" + "MISSING");
						e.printStackTrace();
					}finally{
						//					if (stream!=null)
						//						stream.close(); 
					}
			}
		}finally {                       // always close the file
			//			if (bw != null) try {
			//				bw.close();
			//			} catch (IOException ioe2) {
			//				// just ignore it
			//			}
		} 



	}



	private static void write(String line) throws IOException {


		try {
			// APPEND MODE SET HERE

			bw.write(line);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 


	}


}
