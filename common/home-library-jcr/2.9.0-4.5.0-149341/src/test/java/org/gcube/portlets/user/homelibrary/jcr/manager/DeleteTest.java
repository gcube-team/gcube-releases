package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
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
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeleteTest {
	protected static Logger logger = LoggerFactory.getLogger(DeleteTest.class);
	public static void main(String[] args) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, org.gcube.common.homelibrary.model.exceptions.RepositoryException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
					
		
		createAndRemove();
		
//		JCRWorkspace ws = (JCRWorkspace) getWorkspace("valentina.marioli");
//		ws.getTrash().emptyTrash();
		//			WorkspaceTrashFolder trash = ws.getTrash();
		//	System.out.println(trash.getPath());
		//		ws.removeItems(ws.getRoot().getId());
	}


	public static void createAndRemove() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException, org.gcube.common.homelibrary.model.exceptions.RepositoryException {
		List<String> ids = new ArrayList<String>();

		JCRWorkspace ws = (JCRWorkspace) getWorkspace("valentina.marioli");

		for (int i=0; i<10; i++){

			JCRWorkspaceFolder folder = (JCRWorkspaceFolder) ws.createFolder(i + "A-" + UUID.randomUUID().toString(),"description", ws.getItemByPath("/Workspace/AAAA/").getId());
			ids.add(folder.getId());

			JCRWorkspaceFolder folder01 = (JCRWorkspaceFolder) ws.createFolder(i + "B-" + UUID.randomUUID().toString(),"description", folder.getId());			
			uploadFile(folder01);	
		}

//		logger.info("*********** START ***********");
//
//		String[] strarray = (String[]) ids.toArray(new String[ids.size()]);      
//		Map<String, String> error = ws.removeItems(strarray);
//		System.out.println("error with ids " + error.keySet().toString());
//
//		logger.info("DONE");
//
//		logger.info("GETTING CHILDREN...");
//		List<WorkspaceItem> children = ws.getRoot().getChildren();
//		for(WorkspaceItem item: children){
//			logger.info(item.toString());
//		}

		//		ws.getTrash().emptyTrash();


	}

	private static void uploadFile(WorkspaceFolder subFolder) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException {

		for (int i=0; i<10; i++){
			String fileName = i + "-" + ".jpg";
			InputStream is = null;
			try {
				is = new FileInputStream("/home/valentina/Downloads/4737062744_9dd84a2df2_z.jpg");
				ExternalFile f = subFolder.createExternalImageItem(fileName, "test", "image/jpg", is);
//				System.out.println(f.getPublicLink());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally{
				if (is!=null)
					is.close();
			}
		}

	}


	private static Workspace getWorkspace(String user) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		ScopeProvider.instance.set("/gcube");
		//							ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

		return ws;

	}

}
