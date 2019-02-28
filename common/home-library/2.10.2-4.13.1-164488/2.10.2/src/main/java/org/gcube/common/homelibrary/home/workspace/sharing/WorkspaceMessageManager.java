/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.sharing;

import java.util.List;

import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WorkspaceMessageManager {
	
	/**
	 * Create a new send request.
	 * @param message the sent object.
	 * @param addressees the request addressees.
	 * @return the message id
	 * @throws ItemNotFoundException if the sent item is not found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws RepositoryException 
	 */
	public String sendMessageToUsers(String subject, String body,
			List<String> attachmentIds, List<User> addressees) throws InternalErrorException;
		
	
	/**
	 * Decline a request.
	 * @param id the request id.
	 */
	public void deleteReceivedMessage(String messageId);

	/**
	 * @param requestId
	 * @return
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException
	 */
	public WorkspaceMessage getReceivedMessage(String requestId) throws InternalErrorException,
			ItemNotFoundException;

	/**
	 * @param subject
	 * @param body
	 * @param attachmentIds
	 * @param portalLogins
	 * @return the message id
	 * @throws InternalErrorException
	 */
	String sendMessageToPortalLogins(String subject, String body,
			List<String> attachmentIds, List<String> portalLogins)
			throws InternalErrorException;


	/**
	 * @return
	 */
	int getMessagesNotOpened();

	/**
	 * @return
	 */
	List<WorkspaceMessage> getReceivedMessages();


	/**
	 * @param id
	 * @return
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException
	 */
	WorkspaceMessage getSentMessage(String id) throws InternalErrorException,
			ItemNotFoundException;


	/**
	 * @param id
	 */
	void deleteSentMessage(String id);


	/**
	 * @return
	 */
	List<WorkspaceMessage> getSentMessages();


	/**
	 * @param name
	 * @return
	 * @throws InternalErrorException
	 */
	List<WorkspaceMessage> searchInMessages(String name)
			throws InternalErrorException;


	/**
	 * @param word
	 * @return
	 * @throws InternalErrorException
	 */
	List<WorkspaceMessage> searchOutMessages(String word)
			throws InternalErrorException;

	
}
