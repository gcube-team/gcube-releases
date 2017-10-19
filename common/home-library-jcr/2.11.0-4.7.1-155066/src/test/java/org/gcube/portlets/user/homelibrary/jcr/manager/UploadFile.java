package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
import org.gcube.common.homelibrary.home.Home;
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
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.jcr.JCRUser;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessage;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalUrl;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRFile;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class UploadFile {
	static JCRWorkspace ws = null;
	//	static WorkspaceFolder folder;
	static InputStream in = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, WrongItemTypeException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException, WrongItemTypeException {
//				ScopeProvider.instance.set("/gcube");
		//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		String user = "gergely.sipos";
		//		String user1 = "roberto.cirillo";
		//		String user = "gianpaolo.coro";


		//				Workspace ws = HomeLibrary.getUserWorkspace(user);


		Home home = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user);
		ws = (JCRWorkspace) home.getWorkspace();
		
		
//		ws.getItem("90b2b29e-3706-4703-abc3-460e688ff202").remove();
		
		
//		WorkspaceItem item = ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-D4Research-AGINFRAplus/Meetings/2017.06.15 - WP6 teleconference/WP6 meeting notes (Gergely Sipos).txt");
//System.out.println(item.getPath());
				
				//		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-D4Research-AGINFRAplus/Meetings/2017.06.15 - WP6 teleconference");
//		File initialFile = new File("/home/valentina/test.txt");
//	  InputStream fileData = FileUtils.openInputStream(initialFile);
////		
WorkspaceSharedFolder shared = (WorkspaceSharedFolder) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-BiodiversityLab");
//		ExternalFile item = ws.createExternalFile("test.txt", "", "text/plain", fileData, folder.getId());
//		System.out.println(item.getId());
//aballis
//System.out.println(shared.getUsers().toString());

System.out.println(shared.getAdministrators());
GCubeGroup group = HomeLibrary.getHomeManagerFactory().getUserManager().getGroup("d4science.research-infrastructures.eu-D4Research-AGINFRAplus");
System.out.println(group.getMembers().toString());
//List<String> list = group.getMembers();
//List<String> users = shared.getUsers();
//for (String login:users){
//	if(!list.contains(login))
//		System.out.println(login + " is missing");
//}
//
//HomeLibrary.getHomeManagerFactory().getUserManager().setAdministrator("/d4science.research-infrastructures.eu/D4Research/AGINFRAplus", "leonardo.candela");
//
//	
	
	
//[rob.knapen, george.kakaletris, maristavr, manager.projects, pasquale.pagano, gergely.sipos, danai.symeonidou, pascal.neveu, skonstan, aballis, pavel.e.stoev, taras.guenther, miguel.de-alba-aparicio, franco.zoppi, gianpaolo.coro, matthias.filter, lyubo.penev, preprint, pkaramiperis, pkarampiperis, enol.fernandez, rob.lokers, donatella.castelli, pan.zervas, akukurik, pressoffice, kontogiannis.thodoris, valentina.marioli, sophie.aubin, leonardo.candela, massimiliano.assante, esther.dzale-yeumo, giancarlo.panichi]
//[pasquale.pagano, donatella.castelli, valentina.marioli, pan.zervas, rob.lokers, esther.dzale-yeumo, sophie.aubin, george.kakaletris, matthias.filter, miguel.de-alba-aparicio, pavel.e.stoev, skonstan, pkaramiperis, lyubo.penev, danai.symeonidou, pressoffice, enol.fernandez, pascal.neveu, manager.projects, gergely.sipos, gianpaolo.coro, pkarampiperis, kontogiannis.thodoris, aballis, rob.knapen, akukurik, preprint, franco.zoppi, massimiliano.assante, taras.guenther, maristavr, giancarlo.panichi]

	
	}

	private static void download(WorkspaceItem item) throws InternalErrorException {
		InputStream inputStream = null;
		OutputStream outputStream = null;
	

		try{
//			WorkspaceItem item = ws.getItemByPath("/Workspace/Proposal/731001-AGINFRA PLUS-Evaluation Summary Report.pdf");
		
			JCRExternalFile file = (JCRExternalFile) item;
			inputStream = file.getData();

			outputStream = new FileOutputStream(new File("/home/valentina/Downloads/TEST_REST/"+ item.getName()));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			System.out.println("Done!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}	
		
	}





	//		private static void getItem(WorkspaceItem item, Writer writer) throws InternalErrorException, IOException {
	//			WorkspaceFolder folder = (WorkspaceFolder) item;
	//			for(WorkspaceItem child: folder.getChildren()){
	//				//			System.out.println(child.getRemotePath());
	//				//			System.out.println(child.getPath());
	//				//			System.out.println(child.getCreationTime().getTime());
	//
	//				if (!child.isFolder()){
	//					//				System.out.println("*** " + child.getRemotePath());
	//					try{
	//						System.out.println("*** " + child.getPublicLink(false));	
	//					} catch (Exception e) {
	//						System.out.println("*** -> Error");
	//						System.out.println(child.getRemotePath()  + "\t" + child.getCreationTime().getTime());
	//
	//						writer.write(child.getRemotePath()  + "\t" + child.getCreationTime().getTime());
	//						writer.write("\n");
	//						//												e.printStackTrace();
	//					}
	//
	//				}else
	//					getItem(child, writer);
	//
	//			}
	//		}




}
