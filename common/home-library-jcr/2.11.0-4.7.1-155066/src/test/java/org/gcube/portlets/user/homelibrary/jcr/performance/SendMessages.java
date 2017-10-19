package org.gcube.portlets.user.homelibrary.jcr.performance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.User;
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
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.search.SearchFolderItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRFile;
import org.gcube.common.scope.api.ScopeProvider;

import lombok.patcher.Symbols;

public class SendMessages {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		createLibrary();

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {
//		ScopeProvider.instance.set("/gcube");
					ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		
//		SecurityTokenProvider.instance.set("d423aed7-e9e2-424a-b9e7-2bbbd151d9c4-98187548");
		
		ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("massimiliano.assante").getWorkspace();
		
		
		JCRWorkspaceFolder folder = (JCRWorkspaceFolder) ws.getItemByPath("/Workspace/myscripts/");
		System.out.println(folder.getOwner().getPortalLogin());
		

		
//		List<WorkspaceItem> children = folder.getChildren();
//		for (WorkspaceItem item:children){
//			
//			JCRWorkspaceItem myitem = (JCRWorkspaceItem) item;
//			System.out.println(item.getPath());
//			try {
//				myitem.changeOwner("massimiliano.assante");
//			} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/a");
//	folder.remove();
//		System.out.println(folder.getPath());
//		WorkspaceFolder myFolder = (WorkspaceFolder) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-D4Research-Blue-Datathon/Datasets/Aqua-Alert");
//		WorkspaceFolder myFolder = ws.createFolder("Aqua-Alert", folder.getDescription(), folder.getParent().getId());
//		List<WorkspaceItem> children = folder.getChildren();
//		
//		for (WorkspaceItem child: children){			
////				System.out.println(child.getName());	
////				System.out.println(child.getPublicLink(true));
////				ws.copy(child.getId(), myFolder.getId());
//				
//			JCRExternalFile file = (JCRExternalFile) child;
//			InputStream in = file.getData();
//			System.out.println(file.getPublicLink());
//				FileInputStream in = new FileInputStream("/home/valentina/Downloads/HL/new/test.jpg");	
//			
//			ExternalFile file = ws.createExternalFile("test.jpg", "", null, in, folder.getId());
//				ExternalFile file = (ExternalFile) ws.getItemByPath("/Workspace/a/test.jpg");
////				ws.updateItem(file.getId(), in);
//			System.out.println(file.getRemotePath() +  " - " + file.getStorageId() + " - " + file.getStorageId());
			
//			if (in!=null)
//				in.close();
//		}
		
//		List<SearchFolderItem> items =ws.searchByMimeType("image/jpeg");
//		ItemDelegate att01 = JCRRepository.getServlets().getItemByPath("/Home/valentina.marioli/Workspace/00000/async-js-and-css.zip", "valentina.marioli");
//		ItemDelegate att02 = JCRRepository.getServlets().getItemByPath("/Home/valentina.marioli/Workspace/00000/autoptimize.1.9.4.zip", "valentina.marioli");
//		ItemDelegate att03 = JCRRepository.getServlets().getItemByPath("/Home/valentina.marioli/Workspace/00000/bambini_in_spiaggia.jpeg", "valentina.marioli");
		
		
//		System.out.println(item.getPath());
//		JCRWorkspaceMessageManager messageManager = (JCRWorkspaceMessageManager) ws.getWorkspaceMessageManager();

//		List<WorkspaceMessage> messages = messageManager.getReceivedMessages();
//		for (WorkspaceMessage msg : messages){
//			
//			System.out.println(msg.getId() + " - " + msg.getBody());
////			messageManager.deleteReceivedMessage(msg.getId());
////			System.out.println(msg.getSubject() + " - " + msg.JCRWorkspaceMessagegetAddresses().toString());
////			System.out.println(msg.getAttachmentsIds().toString() + " - " + msg.getSender().getPortalLogin());
////			System.out.println(msg.getSendTime().getTime() );
////			try{
////			List<WorkspaceItem> attachs = msg.getAttachments();
////			for (WorkspaceItem attach : attachs)
////			System.out.println(attach.getId());
////			} catch (Exception e) {
//////				throw new ItemNotFoundException(e.getMessage());
////			}
////			System.out.println(msg.getBody());
//		}
//		
//		String subject = "test subject";
//		String body = "test body";
//		List<String> addresses = new ArrayList<String>();
//		addresses.add(ws.getOwner().getPortalLogin());
//		addresses.add("massimiliano.assante");
//		List<String> attachmentIds = new ArrayList<String>();
//		messageManager.sendMessageToPortalLogins(subject, body, attachmentIds, addresses);

	}
}