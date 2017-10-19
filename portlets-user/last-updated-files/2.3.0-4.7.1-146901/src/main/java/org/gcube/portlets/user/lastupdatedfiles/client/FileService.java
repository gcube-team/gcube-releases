package org.gcube.portlets.user.lastupdatedfiles.client;

import org.gcube.portlets.user.lastupdatedfiles.shared.FileItemsWrapper;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("filesServlet")
public interface FileService extends RemoteService {
	FileItemsWrapper getLastUpdateFiles();
	
	void setRead(String workspaceItemId);
	
	String getWorkspaceFolderURL();
}
