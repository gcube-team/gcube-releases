/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService;
import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingServiceAsync;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.SmartShare;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.google.gwt.core.client.GWT;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 25, 2014
 * 
 */
public class WorkspaceSmartSharingController {

	public static final WorkspaceSharingServiceAsync rpcWorkspaceSharingService = (WorkspaceSharingServiceAsync) GWT.create(WorkspaceSharingService.class);

	private SmartShare smartShare = null;

	private List<CredentialModel> listAlreadySharedContact;

	private FileModel fileModel;

	private boolean readGroupsFromHL;

	private boolean readGroupsFromPortal;


	/**
	 * 
	 * @param file - a fake file to display the field name ("filename") into dialog
	 * @param listAlreadySharedContact - list of already shared contacts to show into dialog
	 * 
	 * base constructor by default does not retrieve the groups
	 * 
	 */
	public WorkspaceSmartSharingController(FileModel file, List<CredentialModel> listAlreadySharedContact) {
		this(file, listAlreadySharedContact, false, false);
	}
	
	/**
	 * 
	 * @param file - a fake file to display the field name ("filename") into dialog
	 * @param listAlreadySharedContact - list of already shared contacts to show into dialog
	 * @param readGroupsFromHL - if true, read group names from HL
	 * @param readGroupsFromPortal - if true, read group names from Portal (as VRE)
	 * 
	 */
	public WorkspaceSmartSharingController(FileModel file, List<CredentialModel> listAlreadySharedContact, boolean readGroupsFromHL, boolean readGroupsFromPortal) {
		this.listAlreadySharedContact = listAlreadySharedContact;
		this.fileModel = file;
		this.readGroupsFromHL = readGroupsFromHL;
		this.readGroupsFromPortal = readGroupsFromPortal;
		this.smartShare = new SmartShare(readGroupsFromHL, readGroupsFromPortal);
		this.smartShare.setSize(ConstantsSharing.WIDTH_DIALOG+20, ConstantsSharing.HEIGHT_DIALOG-170);
		updateSharingDialog();
		addListenersSharingDialog();
	}
	

	/**
	 * Example of listeners
	 */
	private void addListenersSharingDialog(){
		
		smartShare.getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

//				if(smartShare.isValidForm(true)){
//					//THAT'S OK
//					
//					smartShare.getSharedListUsers(); //@return the selected contacts (as InfoContactModel)
//					
//					smartShare.getSharedListUsersCredential(); //@return tthe selected contacts (as CredentialModel)
//					
//					for (InfoContactModel contact : smartShare.getSharedListUsers()) {
//						System.out.println(contact);
//					}
//					
//					for (CredentialModel credential : smartShare.getSharedListUsersCredential()) {
//						System.out.println(credential);
//					}
//					
//				}

			}
		});
	}
	
	/**
	 * 
	 * @param fileModel
	 */
	private void updateSharingDialog(){
		smartShare.unmask();
		smartShare.updateSharingDialog(fileModel, listAlreadySharedContact);
//		dialogShareItem.show();
		
		smartShare.layout();
	}
	
	

	/**
	 * 
	 * @return SmartShare An extension of gxt dialog 2.2.5
	 */
	public SmartShare getSharingDialog() {
		return smartShare;
	}

	public boolean isReadGroupsFromHL() {
		return readGroupsFromHL;
	}

	public boolean isReadGroupsFromPortal() {
		return readGroupsFromPortal;
	}

}
