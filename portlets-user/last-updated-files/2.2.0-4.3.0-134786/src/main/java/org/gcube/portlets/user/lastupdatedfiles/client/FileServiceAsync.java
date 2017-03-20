package org.gcube.portlets.user.lastupdatedfiles.client;

import org.gcube.portlets.user.lastupdatedfiles.shared.FileItemsWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FileServiceAsync {

	void getLastUpdateFiles(AsyncCallback<FileItemsWrapper> callback);

	void setRead(String workspaceItemId, AsyncCallback<Void> callback);

	void getWorkspaceFolderURL(AsyncCallback<String> callback);

}
