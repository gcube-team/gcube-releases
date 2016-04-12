package org.gcube.portlets.user.workspace.client.view.sharing;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class UserStore implements ContactFetcher{

	public static List<InfoContactModel> listAllContact = null;

//    public boolean syncronizeCleanSharedUser = false;
	
	public UserStore() {
	}
	
	@Override
	public void getListContact(AsyncCallback<List<InfoContactModel>> callback, boolean reloadList){
		
		if(reloadList || listAllContact==null)
			loadAllUsersFromServer(callback);
		else
			callback.onSuccess(listAllContact);	
	}
	
	@Override
	public void getListSharedUserByFolderId(String sharedFolderId, AsyncCallback<List<InfoContactModel>> callback){
		loadSharedUserBySharedFolderId(sharedFolderId, callback);
	}

	private void loadAllUsersFromServer(final AsyncCallback<List<InfoContactModel>> callback){
		
		listAllContact = new ArrayList<InfoContactModel>();
		
		AppControllerExplorer.rpcWorkspaceService.getAllContacts(new AsyncCallback<List<InfoContactModel>>() {
			
			@Override
			public void onSuccess(List<InfoContactModel> result) {
				GWT.log("loaded "+result.size() + " contacts from server");
				listAllContact = result;
				callback.onSuccess(listAllContact);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR +" retrieving user "  + ConstantsExplorer.TRY_AGAIN, null);
				callback.onFailure(caught);
			}
		});	
	}
	
	private void loadSharedUserBySharedFolderId(final String sharedFolderId, final AsyncCallback<List<InfoContactModel>> callback){
		
//		comboSharedUsers.mask("Loading users");
		
		AppControllerExplorer.rpcWorkspaceService.getListUserSharedByFolderSharedId(sharedFolderId, new AsyncCallback<List<InfoContactModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR +" retrieving user. "  + ConstantsExplorer.TRY_AGAIN, null);
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(List<InfoContactModel> result) {
				GWT.log("loaded "+result.size() + " contacts from server for "+sharedFolderId);
				callback.onSuccess(result);
			}
		});
	}
	
	@Override
	public List<InfoContactModel> getExclusiveContactsFromAllContact(List<InfoContactModel> listSharedUser){
		
		List<InfoContactModel> listExclusiveContact = new ArrayList<InfoContactModel>(listAllContact);
		for (InfoContactModel contact : listSharedUser) {
			if(listAllContact.contains(contact)){
				GWT.log("Removing not eclusive contact "+contact);
				listExclusiveContact.remove(contact);
			}
		}
		return listExclusiveContact;
	}

	@Override
	public void getOwner(final String sharedFolderId,final AsyncCallback<InfoContactModel> callback) {
		
		AppControllerExplorer.rpcWorkspaceService.getOwnerByItemId(sharedFolderId, new AsyncCallback<InfoContactModel>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("an error occured in get Owner by Id "+sharedFolderId + " "+caught.getMessage());
				
			}

			@Override
			public void onSuccess(InfoContactModel result) {
				callback.onSuccess(result);
				
			}
		});
	}
}