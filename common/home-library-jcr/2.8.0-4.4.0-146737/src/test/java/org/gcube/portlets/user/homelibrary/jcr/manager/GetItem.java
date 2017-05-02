package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalUrl;
import org.gcube.common.scope.api.ScopeProvider;

public class GetItem {
	static Workspace ws = null;
	static BufferedWriter bw = null;
	static int i;
	static int size;
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {


		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		//		ScopeProvider.instance.set("/gcube");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		List<String> users = HomeLibrary
				.getHomeManagerFactory().getUserManager().getUsers();	
		size = users.size();
		System.out.println(size + "USERS");
		i = 1;
		for (String user : users){
			i++;
			System.out.println("***** USER: " + user);
			ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(user).getWorkspace();

			WorkspaceFolder folder = (WorkspaceFolder)ws.getItemByPath("/Workspace/");
			try{
			checkFolder(folder, user);
			}catch (Exception e){
//				System.out.println("NO OWNER");
			}
		}
		System.out.println("END");
	}

	private static void checkFolder(WorkspaceFolder folder, String user) throws InternalErrorException, IOException {
		bw = new BufferedWriter(new FileWriter("/home/valentina/CHECK-all0.csv", true));
		List<WorkspaceItem> list = folder.getChildren();
		for (WorkspaceItem item: list){
			
			String owner = "";
			try{
				owner = item.getOwner().getPortalLogin();
			}catch (InternalErrorException e){
				System.out.println("NO OWNER");
			}
			Boolean shared = false;
			try{
				shared = item.isShared();
			}catch (InternalErrorException e){
				System.out.println("NO SHARED INFO");
			}
			if (item!= null){
				try{
					//				System.out.println("***************************");
					if (!item.isFolder()){
						System.out.println(i + " OF " + size );			
						System.out.println(item.getPublicLink(false));
					}else if ((shared) && (owner.equals(user))){
						checkFolder((WorkspaceFolder) item, user);
					}else if ((!shared) && (!item.getName().equals("MySpecialFolders")))
						checkFolder((WorkspaceFolder) item, user);
					else
						continue;
				}catch (Exception e){
					//				System.out.println("***************************");
					//				System.out.println("ERROR " + item.getPath() + " owner: " + item.getOwner().getPortalLogin());
					try{
						write(item.getPath() + "\t" + item.getRemotePath() + "\t" +item.getOwner().getPortalLogin() + "\t" + item.getCreationTime().getTime()+ "\t" + item.getLastModificationTime().getTime() + "\t" + ((JCRExternalFile)item).getLength());
						//				e.printStackTrace();
					}catch (Exception e1){
						try{
							write(item.getPath() + "\t" + item.getRemotePath() + "\t" +item.getOwner().getPortalLogin() + "\t" + item.getCreationTime().getTime()+ "\t" + item.getLastModificationTime().getTime());
						}catch (Exception e2){
							write(item.getPath() + "\t" + "NO REMOTE PATH" + "\t" +item.getOwner().getPortalLogin() + "\t" + item.getCreationTime().getTime()+ "\t" + item.getLastModificationTime().getTime());
						}
					}
				}
			}
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
