package org.gcube.portlets.widgets.workspaceuploader.client;

import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("workspaceUploaderService")
public interface WorkspaceUploaderService extends RemoteService {
	/**
	 * @param identifier
	 * @return
	 * @throws Exception
	 */
	WorkspaceUploaderItem getUploadStatus(String identifier) throws Exception;

	/**
	 * @param parentId
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	String itemExistsInWorkpaceFolder(String parentId, String itemName) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	String getWorkspaceId() throws Exception;
}
