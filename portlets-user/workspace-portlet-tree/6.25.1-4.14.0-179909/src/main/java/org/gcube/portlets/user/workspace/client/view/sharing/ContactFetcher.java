package org.gcube.portlets.user.workspace.client.view.sharing;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContactFetcher {
	
	public void getListContact(AsyncCallback<List<InfoContactModel>> callback, boolean reloadList);
	public void getListSharedUserByFolderId(String sharedFolderId, AsyncCallback<List<InfoContactModel>> callback);
	public List<InfoContactModel> getExclusiveContactsFromAllContact(List<InfoContactModel> listSharedUser);
	public void getOwner(String sharedFolderId, AsyncCallback<InfoContactModel> callback);

}
