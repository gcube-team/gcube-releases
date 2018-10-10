package org.gcube.portlets.widgets.workspacesharingwidget.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WorkspaceSharingWidgetEntryPoint implements EntryPoint {

	public static String ITEMID = "3cf934a0-decf-4104-8e23-47eb64010017";

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		try {
			

			/*WorkspaceSharingController controller = new WorkspaceSharingController(ITEMID, true, ACL_TYPE.READ_ONLY);

			final Window sharingWindow = controller.getSharingDialog();

			Button openSharingWindow = new Button("Show Sharing Window",
					new ClickHandler() {
						public void onClick(ClickEvent event) {

							sharingWindow.show();
						}
					});

			RootPanel.get("workpacesharingwidget").add(openSharingWindow);
			
			*/
			
			/*FileModel file = new FileModel("123", "test", false);
			WorkspaceSmartSharingController controller = new WorkspaceSmartSharingController(file, null, false, false);
			
			SmartConstants.HEADER_TITLE = "puppa";
			SmartConstants.ITEM_NAME = "aa";
			
			final SmartShare sharingWindow = controller.getSharingDialog();
			Button openSharingWindow = new Button("Show Smart Share Window",
					new ClickHandler() {
						public void onClick(ClickEvent event) {

							sharingWindow.show();
						}
					});
			
			RootPanel.get("workpacesharingwidget").add(openSharingWindow);
			
			sharingWindow.getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {
				 
				@Override
				public void handleEvent(BaseEvent be) {
	 
					if(sharingWindow.isValidForm(true)){
						//THAT'S OK
	 
						sharingWindow.getSharedListUsers(); //@return the selected contacts (as InfoContactModel)
	 
						sharingWindow.getSharedListUsersCredential(); //@return the selected contacts (as CredentialModel)
	 
						for (InfoContactModel contact : sharingWindow.getSharedListUsers()) {
							System.out.println(contact);
						}
	 
						for (CredentialModel credential : sharingWindow.getSharedListUsersCredential()) {
							System.out.println(credential);
						}
	 
					}
	 
				}
			});

			 */
			
			showSimpleMultiDrag();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showSimpleMultiDrag(){
		
		final SimpleMultiDragWorkspaceContact multiDragContact = new SimpleMultiDragWorkspaceContact(ConstantsSharing.LOAD_CONTACTS_AS.SHARED_USER, ITEMID, true, false, true);
		
		Button openSharingWindow = new Button("Show Simple Multi Drag",
				new ClickHandler() {
					public void onClick(ClickEvent event) {

						multiDragContact.getDialogMultiDragContact().show();
						
					}
				});
		
		RootPanel.get("workpacesharingwidget").add(openSharingWindow);
	}

}
