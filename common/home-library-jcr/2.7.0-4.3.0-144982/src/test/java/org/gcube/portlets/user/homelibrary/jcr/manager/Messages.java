
package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Ignore;
import org.junit.Test;

public class Messages {

	private Workspace getWorkspace(String user) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
//				ScopeProvider.instance.set("/gcube");
//				ScopeProvider.instance.set("/gcube/devNext/NextNext");
				String scope = "/gcube/preprod/preVRE";
		//							ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

//		String scope = "/d4science.research-infrastructures.eu/gCubeApps/BlueBridgeProject";
		ScopeProvider.instance.set(scope);
		
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();
		
		return ws;
	}
	
	
	@Ignore
	public void getMyHome() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException, org.gcube.common.homelibrary.model.exceptions.RepositoryException {

		JCRWorkspace ws = (JCRWorkspace) getWorkspace("francesco.mangiacrapa");
		List<WorkspaceItem> list = ws.getRoot().getChildren();
		for (WorkspaceItem item: list){
			System.out.println(item.getPath());
			if (!item.isFolder()){
				try{
					System.out.println("----> " + item.getPublicLink(false));
				} catch (Exception e) {
					System.out.println("Public link not available for " + item.getPath());
				}
			}
				
		}
		
	}
	
	@Test
	public void testMessages() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException, org.gcube.common.homelibrary.model.exceptions.RepositoryException {

		JCRWorkspace ws = (JCRWorkspace) getWorkspace("francesco.mangiacrapa");
		JCRWorkspaceMessageManager messageManager = (JCRWorkspaceMessageManager) ws.getWorkspaceMessageManager();


//		String subject = "Subj";
//		String body = "text in body";
//		List<String> attachmentIds = new ArrayList<String>();
//		String attachId = ws.getItemByPath("/Workspace/token_generator.png").getId();
//		System.out.println("Attach ID " + attachId);
//		attachmentIds.add(attachId);
//		List<String> addresses = new ArrayList<String>();
//		addresses.add("valentina.marioli");
//
//		String id = messageManager.sendMessageToPortalLogins(subject, body, attachmentIds, addresses);
//		System.out.println(id);


		System.out.println("*** receivedMessages ***");
		List<WorkspaceMessage> receivedMessages = messageManager.getReceivedMessages();
		for (WorkspaceMessage msg: receivedMessages){
//			System.out.println("*** " + msg.isRead());		
//			System.out.println(msg.getId());
//			System.out.println(msg.getBody());
			System.out.println(msg.getSubject());
//			System.out.println(msg.getAddresses().toString());
			System.out.println(msg.getAttachments().toString());
//			System.out.println(msg.getAttachmentsIds().toString());
			
			if (msg.getAttachments()!=null){
			List<WorkspaceItem> items = msg.getAttachments();
			for (WorkspaceItem item: items){
				System.out.println(item.getPath());
			}
			}
			
//			System.out.println(msg.getSender().getPortalLogin());
//			System.out.println(msg.getSendTime().getTime());
//
//			System.out.println("*** receivedMessages by id ***");
//			System.out.println(messageManager.getReceivedMessage(msg.getId()));
		}
		
		
//		System.out.println("*** sentMessages ***");
//		List<WorkspaceMessage> sentMessages = messageManager.getSentMessages();
//		for (WorkspaceMessage msg: sentMessages){
//			System.out.println(msg.getId());
////			System.out.println(msg.getBody());
////			System.out.println(msg.getSubject());
////			System.out.println(msg.getAddresses().toString());
////			System.out.println(msg.getAttachments().toString());
////			System.out.println(msg.getAttachmentsIds().toString());
//			
//			List<WorkspaceItem> items = msg.getAttachments();
//			for (WorkspaceItem item: items){
//				System.out.println(item.getPath());
//			}
////			System.out.println(msg.getSender().getPortalLogin());
////			System.out.println(msg.getSendTime().getTime());
//
////			System.out.println("*** sentMessage by id ***");
////			System.out.println(messageManager.getSentMessage(msg.getId()));
//		}

	}





}
