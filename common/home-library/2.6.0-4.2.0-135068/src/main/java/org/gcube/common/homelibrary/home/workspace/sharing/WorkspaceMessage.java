/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.sharing;

import java.util.Calendar;
import java.util.List;

import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WorkspaceMessage {
	
	/**
	 * Return this request id.
	 * @return the id.
	 */
	public String getId();
	
	public String getSubject();
	
	public String getBody();
	
	/**
	 * Return this request sender.
	 * @return the sender.
	 */
	public User getSender();
	
	/**
	 * Return the request send time.
	 * @return the send time.
	 */
	public Calendar getSendTime();
	
	/**
	 * @return
	 */
	boolean isRead();

	/**
	 * @param status TODO
	 * @throws InternalErrorException
	 */
	void setStatus(boolean status) throws InternalErrorException;

	/**
	 * @return
	 */
	List<String> getAttachmentsIds();

	/**
	 * @param destinationFolderId
	 * @throws InternalErrorException
	 * @throws WrongDestinationException
	 * @throws ItemNotFoundException
	 */
	void saveAttachments(String destinationFolderId)
			throws InternalErrorException, WrongDestinationException,
			ItemNotFoundException;

	/**
	 * @return
	 * @throws InternalErrorException
	 */
	List<WorkspaceItem> getAttachments() throws InternalErrorException;

	/**
	 * @param attachmentId
	 * @param destinationFolderId
	 * @return the attachment saved on user workspace.
	 * @throws InternalErrorException
	 * @throws WrongDestinationException
	 * @throws ItemNotFoundException
	 */
	WorkspaceItem saveAttachment(String attachmentId, String destinationFolderId)
			throws InternalErrorException, WrongDestinationException,
			ItemNotFoundException;

	/**
	 * @throws InternalErrorException
	 */
	void open() throws InternalErrorException;

	/**
	 * @return
	 */
	List<String> getAddresses();

	/**
	 * @return
	 */
	List<String> getCopyAttachmentsIds();





}
