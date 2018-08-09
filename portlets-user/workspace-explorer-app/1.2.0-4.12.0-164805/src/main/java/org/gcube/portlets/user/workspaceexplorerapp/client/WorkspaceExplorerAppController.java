/**
 *
 */

package org.gcube.portlets.user.workspaceexplorerapp.client;

import org.gcube.portlets.user.workspaceexplorerapp.client.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.user.workspaceexplorerapp.client.grid.DisplayField;
import org.gcube.portlets.user.workspaceexplorerapp.client.resources.WorkspaceExplorerResources;
import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * The Class WorkspaceExplorerAppController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 3, 2017
 */
public class WorkspaceExplorerAppController {

	private WorkspaceExplorerAppPanel workspaceERP;
	private WorkspaceResourcesExplorerPanel wsResourcesExplorerPanel;
	private final HandlerManager eventBus = new HandlerManager(null);
	private WorkspaceExplorerApp app;

	/**
	 * Instantiates a new workspace explorer app controller.
	 */
	public WorkspaceExplorerAppController() {
	}


	/**
	 * Go.
	 *
	 * @param app the app
	 */
	public void go(WorkspaceExplorerApp app) {
		this.app = app;
		RootPanel.getBodyElement().getStyle().setPadding(0, Unit.PX);
//		mainPanel.setWidth("100%");

		try {
			String folderId = Window.Location.getParameter("folderId");

			//TODO CHECK FOLDER ID
			if(folderId==null){
				String msg = "Folder Id not found, It is not possible to retrieve a folder without a valid id";
//				Window.alert("Folder Id not found, It is not possible to retrieve a folder without a valid id");
				showErrorPanel(msg);
				return;
			}

			RootPanel.get().addDomHandler(new ContextMenuHandler() {

				@Override
				public void onContextMenu(ContextMenuEvent event) {
					event.preventDefault();
					event.stopPropagation();
				}
			}, ContextMenuEvent.getType());

			WorkspaceExplorerAppConstants.workspaceNavigatorService.getFolderIdFromEncrypted(folderId, new AsyncCallback<String>() {

				@Override
				public void onSuccess(String folderId) {

					if(folderId!=null && !folderId.isEmpty())
						try {
							initWorkspaceExplorer(folderId);
						}
						catch (Exception e) {
//							Window.alert("Folder Id not valid. An occurred when converting folder id");
							showErrorPanel("Folder Id not valid. An occurred when converting folder id");
						}
				}

				@Override
				public void onFailure(Throwable caught) {
//					Window.alert(caught.getMessage());
					showErrorPanel(caught.getMessage());
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**
	 * Show error panel.
	 *
	 * @param message the message
	 */
	private void showErrorPanel(String message){
		Image error = new Image(WorkspaceExplorerResources.INSTANCE.eUnhappy().getSafeUri());
		error.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		Alert alert = new Alert();
		alert.setType(AlertType.ERROR);
		alert.add(error);
		alert.setClose(false);
		alert.setHeading(message);
		app.updateToError(alert);
	}

	/**
	 * Inits the workspace explorer.
	 *
	 * @param folderId the folder id
	 * @throws Exception the exception
	 */
	private void initWorkspaceExplorer(String folderId) throws Exception{

		wsResourcesExplorerPanel = new WorkspaceResourcesExplorerPanel(eventBus, folderId, false);
		wsResourcesExplorerPanel.loadParentBreadcrumbByItemId(folderId, true);
		WorskpaceExplorerSelectNotificationListener listener = new WorskpaceExplorerSelectNotificationListener() {

				@Override
				public void onSelectedItem(Item item) {

					GWT.log("Listener Selected Item " + item);
				}

				@Override
				public void onFailed(Throwable throwable) {

					GWT.log("There are networks problem, please check your connection.");
				}

				@Override
				public void onAborted() {

				}

				@Override
				public void onNotValidSelection() {

				}
		};
		wsResourcesExplorerPanel.addWorkspaceExplorerSelectNotificationListener(listener);

		/*new com.google.gwt.user.client.Timer() {

			@Override
			public void run() {

				//TODO  //IS A TEST REMOVE
				ArrayList<Item> tests = new ArrayList<Item>();
				for (int i = 0; i < 50; i++) {
					tests.add(new Item(i+""+Random.nextInt(), "name"+i, false));
				}
				wsResourcesExplorerPanel.getWsExplorer().updateExplorer(tests);
			}
		}.schedule(1000);*/

		workspaceERP = new WorkspaceExplorerAppPanel(wsResourcesExplorerPanel);
		app.updateExplorerPanel(workspaceERP);
	}


	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	public HandlerManager getEventBus(){
		return eventBus;
	}

	/**
	 * Gets the display fields.
	 *
	 * @return the display fields
	 */
	public DisplayField[] getDisplayFields(){
		return WorkspaceResourcesExplorerPanel.displayFields;
	}
}
