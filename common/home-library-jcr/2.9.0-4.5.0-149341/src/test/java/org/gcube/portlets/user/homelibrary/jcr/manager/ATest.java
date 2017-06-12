
package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRFile;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Ignore;
import org.junit.Test;

public class ATest {

	private final String itemId = "a8a94bb0-4c0b-483b-a23c-204d148a975c";

	
	
	@Ignore
	public void getFile() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");
		WorkspaceItem item = ws.getItemByPath("/Workspace/footer-bg.jpg");
		System.out.println(item.getParent().getId());		
		System.out.println(item.getPath());		
		System.out.println(item.isHidden());
		item.setHidden(false);
		System.out.println(item.isHidden());
//		JCRExternalFile file = (JCRExternalFile) item;
//		InputStream stream = file.getData();
//		System.out.println(stream.read());
//		stream.close();
		
//		System.out.println(file.getPublicLink());
	}
	
	
	@Ignore
	public void zipFolder() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");
		WorkspaceItem item = ws.getItemByPath("/Workspace/test-zip/");
		File tmpZip = ZipUtil.zipFolder((WorkspaceFolder) item);
		System.out.println(tmpZip.getAbsolutePath());
	}





	@Ignore
	public void renameFolder() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");
		ws.renameItem(itemId, "A-"+UUID.randomUUID().toString());
	}

	@Ignore
	public void moveFolder() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");
		ws.moveItem(itemId, "c51382fe-3f9c-4c1b-b0b5-298aa7a9179e");
	}

	@Ignore
	public void copyFolder() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");
		ws.copy(itemId, "c51382fe-3f9c-4c1b-b0b5-298aa7a9179e");
	}

	@Ignore
	public void removeFolder() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");
		WorkspaceFolder folder = ws.createFolder("AAAfolder-"+ UUID.randomUUID(), "description", ws.getRoot().getId());
		folder.remove();
	}


	@Ignore
	public void removeSharedFolder() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");
		//		WorkspaceFolder folder = ws.createFolder("AAAfolder-"+ UUID.randomUUID(), "description", ws.getRoot().getId());
		JCRWorkspaceFolder folder = (JCRWorkspaceFolder) ws.getItemByPath("/Workspace/AAAfolder-7f1dd760-3fc3-47cd-b9b9-9c63ec59bf56");
		//JCRWorkspaceFolder folder = (JCRWorkspaceFolder) myfolder.unShare();
		//		JCRWorkspaceFolder folder = (JCRWorkspaceFolder) ws.getItemByPath("/Workspace/AAAfolder-7f1dd760-3fc3-47cd-b9b9-9c63ec59bf56");
		List<String> users = new ArrayList<String>();
		users.add("roberto.cirillo");
		JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder) folder.share(users);
		WorkspaceFolder unshared = shared.unShare();
		System.out.println(unshared.getPath());
	}

	@Ignore
	public void updateFile() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");
		//		FileInputStream is = new FileInputStream("/home/valentina/BlueBRIDGE_Part_B_1.3_20141217.docx");
		//		ExternalFile item = ws.createExternalFile("AFile-"+UUID.randomUUID()+ ".docx", "description", null, is, ws.getRoot().getId());
		//		System.out.println(item.getPath() + " created");
		//		if (is!=null)
		//			is.close();
		FileInputStream is1 = new FileInputStream("/home/valentina/Locandina WEST con logo del 2108.pdf");

		//		WorkspaceItem item = ws.getItemByPath("/Home/valentina.marioli/Workspace/aaa/");
		//		List<? extends WorkspaceItem> children = item.getChildren();
		//		for (WorkspaceItem child: children){
		//			System.out.println(child.getPath() + " - " + child.getId());
		//		}
		WorkspaceItem item = ws.getItemByPath("/Home/valentina.marioli/Workspace/test/Locandina WEST con logo del 2108.pdf");
		ws.updateItem(item.getId(), is1);
		//		System.out.println(item.getPath() + " updated");
		if (is1!=null)
			is1.close();

	}


	@Ignore
	public void uploadFile() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");
		
		WorkspaceItem folder = ws.getItemByPath("/Workspace/20160725");
		FileInputStream is = new FileInputStream("/home/valentina/Downloads/summer-flowers-1.jpg");
		//		FolderItem item = WorkspaceUtil.createExternalFile(ws.getRoot(), "AFile-"+UUID.randomUUID()+ ".docx", "desc", null, is);
		ExternalImage item = ws.createExternalImage("AFile-"+UUID.randomUUID()+ ".png", "description", null, is, folder.getId());
		System.out.println(item.getPath() + " created");
		if (is!=null)
			is.close();
		//		return item;

	}

	
	@Test
	public void forceDelete() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("gianpaolo.coro");
		
		WorkspaceItem folder = ws.getItemByPath("/Workspace/DataMiner/");
		System.out.println(folder.getPath());
		folder.remove();

	}

	@Ignore
	public void getChildren() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");

		WorkspaceItem item = ws.getItemByPath("/Home/valentina.marioli/Workspace/aaa/");
		List<? extends WorkspaceItem> children = item.getChildren();	
		for(WorkspaceItem child: children){
			//			System.out.println("getName " + child.getName());
			//			System.out.println("getDescription "+ child.getDescription());
			//			System.out.println("getId " + child.getId());
			//			System.out.println("getIdSharedFolder " + child.getIdSharedFolder());
			//			System.out.println("getLastUpdatedBy " +child.getLastUpdatedBy());

			System.out.println("**** getPath ***");
			System.out.println("getPath " + child.getPath());

		}

		Thread.sleep(600000);
	}

	@Ignore
	public void searchByNameItem() throws InternalErrorException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		Workspace ws = getWorkspace("valentina.marioli");
		String query = "test";
		String folderId = ws.getRoot().getId();

		//		List<SearchItem> list = ws.searchByName(query, folderId);
		//		for (SearchItem item:list){
		//			System.out.println(item.getName());
		//		}

		WorkspaceSmartFolder smart = ws.createSmartFolder(query+"-"+ UUID.randomUUID(), "smart folder " + query, query, folderId);
		//		List<SearchItem> list1 =(List<SearchItem>) smart.getSearchItems();
		//		for (SearchItem item:list1){
		//			System.out.println(item.getName());
		//		}

		List<WorkspaceSmartFolder> list = ws.getAllSmartFolders();
		for (WorkspaceSmartFolder folder: list){

			System.out.println(folder.getId());
			//			System.out.println(folder.getSearchItems());


			//			smart.remove();//	
			//			ws.removeItem(smart.getId());
			List<SearchItem> list1 =(List<SearchItem>) smart.getSearchItems();
			for (SearchItem item:list1){
				System.out.println(item.getId());
				System.out.println(item.getName());
			}
		}
	}


	@Ignore
	public void messages() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {

		Workspace ws = getWorkspace("valentina.marioli");
		WorkspaceMessageManager msgManager = ws.getWorkspaceMessageManager();

		String subject = "testHL2.1";
		String body  = "test body HL2.1";
		List<String> attachmentIds = new ArrayList<String>();
		List<String> portalLogins = new ArrayList<String>();
		portalLogins.add("valentina.marioli");
		portalLogins.add("roberto.cirillo");


		WorkspaceItem attach01 = ws.getItemByPath("/Workspace/AFile-ae4e9bb2-608a-41c5-b41b-719027fc8449.docx");
		System.out.println("attach01 " + attach01.getPath() + " - id: " + attach01.getId());
		WorkspaceItem attach02 = ws.getItemByPath("/Workspace/AFile-9686385f-4ce7-4516-ac85-08429687be93.docx");
		System.out.println("attach02 " + attach02.getPath() + " - id: " + attach02.getId());
		attachmentIds.add(attach01.getId());
		attachmentIds.add(attach02.getId());

		String sentMsg = msgManager.sendMessageToPortalLogins(subject, body, attachmentIds, portalLogins);
		System.out.println("-----> " + sentMsg);

		//		WorkspaceMessage mymsg = msgManager.getReceivedMessage(sentMsg);
		//		System.out.println(mymsg.getSubject());

		List<WorkspaceMessage> receivedMsgs = msgManager.getReceivedMessages();
		for(WorkspaceMessage msg: receivedMsgs){
			System.out.println(msg.getId() + " - " + msg.getSubject());
			WorkspaceMessage mymsg = msgManager.getReceivedMessage(msg.getId());
			System.out.println(mymsg.getSubject());
			System.out.println(mymsg.getAddresses().toString());
			mymsg.open();
			mymsg.setStatus(true);
		}

	}


	@Ignore
	public void unshare() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {

		Workspace ws = getWorkspace("valentina.marioli");

		WorkspaceItem item = ws.getItemByPath("/Home/valentina.marioli/Workspace/ShareWithValentina");
		JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder) item;
		WorkspaceFolder folder = shared.unShare();
		System.out.println(folder.getPath());
		

	}


	@Ignore
	public void trash() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		JCRWorkspace ws = (JCRWorkspace) getWorkspace("valentina.marioli");
		ws.getStorage().removeRemoteFolder("/Home/valentina.marioli/Workspace/");
		
		//		ExternalImage file = uploadFile();
		//		System.out.println(file.getName());
		////		ws.removeItem(file.getId());
		//		file.remove();

		//		WorkspaceTrashFolder trash = ws.getTrash();
		//		List<WorkspaceTrashItem> list = trash.listTrashItems();
		//		for(WorkspaceTrashItem item: list){
		//			System.out.println("---> " + item.getName() + " - " + item.getDeletedFrom());
		////			System.out.println("***** getRemotePath: " + item);
		//			item.restore();
		//		}

	}

	@Ignore
	public void cleanFrancesco() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {

		JCRWorkspace ws = (JCRWorkspace) getWorkspace("francesco.mangiacrapa");
		//		WorkspaceTrashFolder trash = ws.getTrash();
		//		trash.emptyTrash();
		//		try {
		//			WorkspaceItem item = ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/reports");
		//			item.remove();
		//		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		List<WorkspaceItem> children = ws.getRoot().getChildren();


		for (WorkspaceItem child: children){
			//			System.out.println("remove dir " + child.getPath());
			//		
			try{
				if(child.getType().toString().equals("SHARED_FOLDER")){
					Workspace ws1 = getWorkspace(child.getOwner().getPortalLogin());
					System.out.println(child.getOwner().getPortalLogin() + " removes file as " + child.getOwner().getPortalLogin());
					ws1.removeItem(child.getId());
					//					child.remove();
				}
				else
					child.remove();
			}catch (Exception e) {
				System.out.println(child.getName() + " will not be removed ");
				e.printStackTrace();

			}
		}
		//		ExternalImage file = uploadFile();
		//		System.out.println(file.getName());
		////		ws.removeItem(file.getId());
		//		file.remove();

		//		WorkspaceTrashFolder trash = ws.getTrash();
		//		List<WorkspaceTrashItem> list = trash.listTrashItems();
		//		for(WorkspaceTrashItem item: list){
		//			System.out.println(item.getPath());
		////			System.out.println("---> " + item.getName() + " - " + item.getDeletedFrom());
		////			System.out.println("***** getRemotePath: " + item);
		//					item.deletePermanently();
		//		}

	}

	private Workspace getWorkspace(String user) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
//		ScopeProvider.instance.set("/gcube");
					ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();
		//		System.out.println(ws.getTotalItems());
		//		System.out.println(ws.getDiskUsage());
		return ws;

	}

}
