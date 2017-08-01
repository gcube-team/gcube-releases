/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client;

import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService;
import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingServiceAsync;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.DialogShareWItem;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.ACL_TYPE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.SessionExpiredException;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 25, 2014
 * 
 */
public class WorkspaceSharingController {

	public static final WorkspaceSharingServiceAsync rpcWorkspaceSharingService = (WorkspaceSharingServiceAsync) GWT.create(WorkspaceSharingService.class);

	private static String myLogin;

	private String workspaceItemId;

	private boolean shareOnlyOwner;

	private ACL_TYPE defaultPermission;
	
	private DialogShareWItem sharingDialog = null;

	private boolean readGroupsFromHL;

	private boolean readGroupsFromPortal;


	/**
	 * This controller instancing sharing dialog
	 * @param itemId workspace item id
	 * @param if true, only owner can share, otherwise an alert with an error message is displayed
	 * @param defaultPermission ACL_TYPE default permission, if is null default ACL_TYPE is loaded from server
	 * 
	 * base constructor by default does not retrieve the groups
	 */
	public WorkspaceSharingController(String itemId, boolean shareOnlyOwner, ACL_TYPE defaultPermission) {
		this(itemId, shareOnlyOwner, defaultPermission, false, false);
	}
	
	/**
	 * This controller instancing sharing dialog
	 * @param itemId workspace item id
	 * @param if true, only owner can share, otherwise an alert with an error message is displayed
	 * @param defaultPermission ACL_TYPE default permission, if is null default ACL_TYPE is loaded from server
	 * @param readGroupsFromHL - if true, read group names from HL
	 * @param readGroupsFromPortal - if true, read group names from Portal (as VRE)
	 */
	public WorkspaceSharingController(String itemId, boolean shareOnlyOwner, ACL_TYPE defaultPermission, boolean readGroupsFromHL, boolean readGroupsFromPortal) {
		this.workspaceItemId = itemId;
		this.shareOnlyOwner = shareOnlyOwner;
		this.defaultPermission = defaultPermission;
		this.readGroupsFromHL = readGroupsFromHL;
		this.readGroupsFromPortal = readGroupsFromPortal;
		
		
		if(workspaceItemId==null || workspaceItemId.isEmpty()){
			MessageBox.alert("Error", "Item id is null or empty", null);
			return;
		}
		
		sharingDialog = new DialogShareWItem(readGroupsFromHL, readGroupsFromPortal);
		sharingDialog.setSize(ConstantsSharing.WIDTH_DIALOG+20, ConstantsSharing.HEIGHT_DIALOG+20);
		sharingDialog.mask("Loading item information from Workspace", ConstantsSharing.LOADINGSTYLE);
		
		
		
		if(shareOnlyOwner){
			loadMyLogin(true, true);
		}else{
			loadFileModel(true);
		}
		
		addListenersSharingDialog();
	}
	
	private void loadFileModel(final boolean showSharingDialog){
		
		rpcWorkspaceSharingService.getFileModelByWorkpaceItemId(workspaceItemId, new AsyncCallback<FileModel>() {

			@Override
			public void onFailure(Throwable caught) {
				sharingDialog.unmask();
				sharingDialog.setAsError(caught.getMessage());
				MessageBox.alert("Error", caught.getMessage(), null);
			}

			@Override
			public void onSuccess(FileModel result) {
	
				if(showSharingDialog)
					updateSharingDialog(result);
			}
		});
	}
	
	
	private void addListenersSharingDialog(){
		
		
		
		sharingDialog.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if(sharingDialog.isValidForm(true)){
					
					FileModel fileModel = sharingDialog.getFileToShare();

					//create a lowest object to send to server
					fileModel = new FileModel(fileModel.getIdentifier(), fileModel.getName(), fileModel.getParentFileModel(), fileModel.isDirectory(), fileModel.isShared());
					
					fileModel.setDescription(sharingDialog.getDescription());

					//DEBUG
					/*
					System.out.println("FileModel id "+fileModel.getIdentifier() + " name: "+fileModel.getName() + " parent " + fileModel.getParentFileModel());
					for(InfoContactModel contact:finalDialog.getSharedListUsers() ){
						System.out.println("Share with Contact "+contact) ;
						
					}*/

					GWT.log("ACL selected is "+sharingDialog.getSelectedACL());
					
					
					final String itemName = fileModel.getName();
					sharingDialog.mask("Sharing and setting permissions", ConstantsSharing.LOADINGSTYLE);
					
					rpcWorkspaceSharingService.shareFolder(fileModel, sharingDialog.getSharedListUsers(), false, sharingDialog.getSelectedACL(), new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {

							if(caught instanceof SessionExpiredException){
								GWT.log("Session expired");
								sharingDialog.hide();
								MessageBox.alert("Alert", "Server session expired", null);
								return;
							}else
								MessageBox.alert("Error", caught.getMessage(), null);
							
							sharingDialog.unmask();
						}

						@Override
						public void onSuccess(Boolean result) {
							if(result){
								MessageBox.info("Info", "The item: "+itemName+" correctly shared", null);
								sharingDialog.hide();
							}
							sharingDialog.unmask();
						}
					});
				}

			}
				
		});
	}
	
	/**
	 * 
	 * @param fileModel
	 */
	private void updateSharingDialog(FileModel fileModel){
		sharingDialog.updateSharingDialog(fileModel, shareOnlyOwner, defaultPermission);
		sharingDialog.unmask();
//		dialogShareItem.show();
		
		sharingDialog.layout();
	}
	
	/**
	 * 
	 * @param loadFileModel
	 * @param showSharingDialog
	 */
	private void loadMyLogin(final boolean loadFileModel, final boolean showSharingDialog) {

		rpcWorkspaceSharingService.getMyLogin(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error on loading my login is empty");
				myLogin = "";

			}

			@Override
			public void onSuccess(String result) {
				GWT.log("My login is: " + result);
				myLogin = result;
				
				if(loadFileModel)
					loadFileModel(showSharingDialog);
			}
		});
	}

	public static String getMyLogin() {
		return myLogin;
	}

	public String getWorkspaceItemId() {
		return workspaceItemId;
	}


	public boolean isShareOnlyOwner() {
		return shareOnlyOwner;
	}

	
	public ACL_TYPE getDefaultPermission() {
		return defaultPermission;
	}


	/**
	 * 
	 * @return gxt 2.2.5 Dialog
	 */
	public Dialog getSharingDialog() {
		return sharingDialog;
	}

}
