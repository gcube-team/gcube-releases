/**
 * 
 */
package org.gcube.portlets.user.messages;

import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.messages.server.GWTMessagesBuilder;
import org.gcube.portlets.user.messages.server.MessagesServiceImpl;
import org.gcube.portlets.user.messages.shared.GXTCategoryItemInterface;
import org.gcube.portlets.user.messages.shared.MessageModel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 20, 2013
 *
 */
public class TestMessage {

	static MessagesServiceImpl servlet = new MessagesServiceImpl();
	public static Logger _log = Logger.getLogger(MessagesServiceImpl.class);
	
	private static boolean withinPortal = false;
	
	static Workspace workspace;
	
	public static void main(String[] args) throws Exception {
		
		ScopeProvider.instance.set("/gcube/devsec");
		workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome("francesco.mangiacrapa").getWorkspace();
//		
//		String messageIdentifier = null;
//		String messageType = GXTCategoryItemInterface.MS_RECEIVED;
//		
//		
//		servlet.getMessageById(messageIdentifier, messageType);
		
		
//		List<MessageModel> list = getAllMessagesReceived();
		

		String messageIdentifier = "2039d127-f8f0-4a65-b327-3e9372588382";
		String messageType = GXTCategoryItemInterface.MS_RECEIVED;
		String attachmentId = "b1277304-682f-4d08-aa88-5aed1698d90c";
		
		saveAttachment(messageIdentifier, attachmentId, messageType);

	}



	public static List<MessageModel> getAllMessagesReceived() throws Exception {
		try {

			_log.trace("get All Messages Received ");

			GWTMessagesBuilder builder = new GWTMessagesBuilder();

			List<WorkspaceMessage> listMessages = workspace.getWorkspaceMessageManager().getReceivedMessages();

			return builder.buildGXTListMessageModelForGrid(listMessages, GXTCategoryItemInterface.MS_RECEIVED, withinPortal); 

		} catch (Exception e) {
			_log.error("Error in server getAllMessagesReceived ", e);
			//			workspaceLogger.trace("Error in server get getAllMessagesReceived " + e.getMessage());
			throw new Exception(e.getMessage());
		}
	}
	
	

	public static String saveAttachment(String messageIdentifier, String attachmentId, String messageType) throws Exception {
		try {


			_log.info("save attachment by attachmentId");

			_log.trace(" save attachment by attachmentId " + attachmentId);
			
			System.out.println("messageIdentifier "+messageIdentifier + " attachmentId "+ attachmentId + " messageType " + messageType);
			
			
			WorkspaceItem item;
			
			if(messageType.equals(GXTCategoryItemInterface.MS_SENT))
				item = workspace.getWorkspaceMessageManager().getSentMessage(messageIdentifier).saveAttachment(attachmentId, workspace.getRoot().getId());
			else
				item = workspace.getWorkspaceMessageManager().getReceivedMessage(messageIdentifier).saveAttachment(attachmentId, workspace.getRoot().getId());

			if(item!=null)
				return item.getId();
			
			return null;

		} catch (Exception e) {
			_log.error("Error in server attachment by attachmentId ", e);
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
