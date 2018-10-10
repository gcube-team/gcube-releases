/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingServiceAsync;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.UserStore;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest.DialogMultiDragContact;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 30, 2014
 *
 * A simple multi drag dialog to manage users (shared or administrators) of a workspace item
 */
public class SimpleMultiDragWorkspaceContact {
	
	private UserStore userStore = new UserStore();
	private ConstantsSharing.LOAD_CONTACTS_AS loadContactAs; 
	private DialogMultiDragContact dialogMultiDragContact = new DialogMultiDragContact(MultiDragConstants.HEADING_DIALOG, MultiDragConstants.ALL_CONTACTS_LEFT_LIST, MultiDragConstants.SHARE_WITH_RIGHT_LIST,false, false);
	private List<InfoContactModel> targets = new ArrayList<InfoContactModel>();
	private boolean readGroupsFromPortal;
	private boolean readGroupsFromHL;
	private boolean hiddenMySelf;
	private String workspaceItemId;
	private String myLogin;
	private InfoContactModel myContact;
	
	/**
	 * Load administrators or shared users to workspace item id
	 * @param load 
	 * 			if LOAD_CONTACTS_AS.SHARED_USER loads target users from Shared Users
	 * 			if LOAD_CONTACTS_AS.ADMINISTRATOR loads target users from Administrators
	 * 
	 * @param workspaceItemId
	 * @param readGroupsFromHL
	 * @param readGroupsFromPortal
	 * @param hiddenMySelf if true the login read from ASL is hidden (so it's not removable to target users), the login returned anyway among the target users
	 */
	public SimpleMultiDragWorkspaceContact(ConstantsSharing.LOAD_CONTACTS_AS load, String workspaceItemId, final boolean readGroupsFromHL, final boolean readGroupsFromPortal, final boolean hiddenMySelf) {
		this.loadContactAs = load;
		this.readGroupsFromHL = readGroupsFromHL;
		this.readGroupsFromPortal = readGroupsFromPortal;
		this.hiddenMySelf = hiddenMySelf;
		this.workspaceItemId = workspaceItemId;
		
		if(hiddenMySelf)
			loadMyLogin(true);
		else
			loadSharedContacts();
	}
	
	
	private void loadMyLogin(final boolean loadContacts){
		
		WorkspaceSharingServiceAsync.Util.getInstance().getMyLogin(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error on loading my login is empty");
				myLogin = "";
				loadSharedContacts();
			}

			@Override
			public void onSuccess(String result) {
				GWT.log("My login is: "+result);
				myLogin = result;	
				
				if(loadContacts)
					loadSharedContacts();
			}
		});
	}
	
	/**
	 * Load the target contacts
	 */
	private void loadSharedContacts(){
		
		switch (loadContactAs) {
		
		case ADMINISTRATOR:
			
			//LOADING LIST OF ALREADY SHARED USER
		    userStore.getAdministratorsByFolderId(workspaceItemId, new AsyncCallback<List<InfoContactModel>>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Error on loading admnistrators");
					MessageBox.alert("Error on loading admnistrators", caught.getMessage(), null);
				}

				@Override
				public void onSuccess(List<InfoContactModel> result) {
					GWT.log("Returned "+result.size()+" admin/s");
					fillMultiDrag(result);
	 				loadAllContacts(readGroupsFromHL, readGroupsFromPortal);
					
				}
			});
		    
			break;

		case SHARED_USER:

			//LOADING LIST OF ALREADY SHARED USER
		    userStore.getListSharedUserByFolderId(workspaceItemId, new AsyncCallback<List<InfoContactModel>>() {
				
		 			@Override
		 			public void onSuccess(List<InfoContactModel> result) {
		 				GWT.log("Returned "+result.size()+" contact/s");
		 				fillMultiDrag(result);
		 				loadAllContacts(readGroupsFromHL, readGroupsFromPortal);
		 			}
		 			
		 			@Override
		 			public void onFailure(Throwable caught) {
		 				GWT.log("Error on loading shared contacts");
		 				MessageBox.alert("Error on shared contacts", caught.getMessage(), null);
		 				
		 			}
		 		});
		    
			break;
		default:
			
			loadAllContacts(readGroupsFromHL, readGroupsFromPortal);
			break;
		}
	}
	
	public void addTargetContact(List<InfoContactModel> listContacts){
		if(listContacts!=null){
			for (InfoContactModel infoContactModel : listContacts) {
				dialogMultiDragContact.getMultiDrag().addTargetContact(infoContactModel);
			}
		}
		
	}
	
	/**
	 * 
	 * @param result
	 */
	private void fillMultiDrag(List<InfoContactModel> result){
		
		GWT.log("Filling multi-drag..");
		GWT.log("Hidden my self: "+hiddenMySelf);
		
		for (InfoContactModel infoContactModel : result) {
				if(infoContactModel.getLogin()!=null){
					if(hiddenMySelf && (infoContactModel.getLogin().compareTo(myLogin)==0)){
						myContact = infoContactModel;
						GWT.log("Skipping myLogin as: "+myContact);
					}else{
						dialogMultiDragContact.getMultiDrag().addTargetContact(infoContactModel);
						targets.add(infoContactModel);
					}
				}
		}
	}
	
	/**
	 * Load all contacts
	 * @param readGroupsFromHL
	 * @param readGroupsFromPortal
	 */
	private void loadAllContacts(boolean readGroupsFromHL, boolean readGroupsFromPortal){
		userStore.getListContact(addSourceContacts, false, readGroupsFromHL, readGroupsFromPortal);
	}
	
	private AsyncCallback<List<InfoContactModel>> addSourceContacts = new AsyncCallback<List<InfoContactModel>>() {

		@Override
		public void onFailure(Throwable caught) {
			GWT.log("Error on loading contacts");
			MessageBox.alert("Error", caught.getMessage(), null);
		}

		@Override
		public void onSuccess(List<InfoContactModel> result) {
			
			List<InfoContactModel> contactTargets = new ArrayList<InfoContactModel>(targets.size()+1);
			contactTargets.addAll(targets);
			if(result!=null && result.size()>0){
				if(hiddenMySelf)
					contactTargets.add(myContact);
				
				List<InfoContactModel> exclusiveContacts = userStore.getExclusiveContactsFromAllContact(contactTargets);
				dialogMultiDragContact.getMultiDrag().addSourceContacts(exclusiveContacts);
			}
		}
	};
	
	/**
	 * 
	 * @return the multi drag DialogMultiDragContact
	 */
	public DialogMultiDragContact getDialogMultiDragContact() {
		return dialogMultiDragContact;
	} 
	
	
	public void show(){
		dialogMultiDragContact.show();
	}
	/**
	 * 
	 * @return
	 */
	public  List<InfoContactModel> getTargetContacts() {
		return dialogMultiDragContact.getMultiDrag().getTargetListContact();
	} 
	
	/**
	 * 
	 * @return
	 */
	public  List<InfoContactModel> getTargetContactsWithMyLogin() {
		List<InfoContactModel> contacts = getTargetContacts();
		
		if(myContact == null){
			GWT.log("TargetContactsWithMyLogin my Contact is null, skipping!");
		}else
			contacts.add(myContact);
		
		return contacts;
	} 
}
