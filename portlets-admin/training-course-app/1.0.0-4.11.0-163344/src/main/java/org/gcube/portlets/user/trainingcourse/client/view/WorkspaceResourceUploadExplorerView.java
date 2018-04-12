package org.gcube.portlets.user.trainingcourse.client.view;

import org.gcube.portlets.user.trainingcourse.client.event.SelectedWorkspaceItemEvent;
import org.gcube.portlets.user.trainingcourse.client.event.ShowMoreInfoEvent;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.DialogUpload.UPLOAD_TYPE;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent;
import org.gcube.portlets.widgets.wsexplorer.client.explore.WorkspaceResourcesBExplorerPanel;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectBNotification.WorskpaceExplorerSelectBNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.view.grid.SortedCellTable;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

// TODO: Auto-generated Javadoc
/**
 * The Class WorkspaceResourceExplorerController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 11, 2018
 */
public class WorkspaceResourceUploadExplorerView extends SimplePanel {

	/** The workspace explorer panel. */
	// private WorkspaceResourcesExplorerPanel workspaceExplorerPanel;
	private MultipleDNDUpload dnd = new MultipleDNDUpload();

	/** The no project selected. */
	private Heading noProjectSelected = new Heading(3, "Open a Training Course...");

	/** The root folder id. */
	private String rootFolderId;

	/** The breadcrumb showing folder. */
	private Item breadcrumbShowingFolder;

	/** The workspace explorer panel. */
	private WorkspaceResourcesBExplorerPanel workspaceExplorerPanel;

	/** The selected item. */
	private Item selectedItem;
	
	private boolean shownIntroFirstTime = false;

	/**
	 * Instantiates a new workspace resource explorer controller.
	 */
	public WorkspaceResourceUploadExplorerView() {
		resetView();
	}

	public void resetView() {
		this.clear();
		noProjectSelected.getElement().getStyle().setMarginLeft(20, Unit.PX);
		noProjectSelected.getElement().getStyle().setProperty("fontFamily:", "Roboto, sans-serif;");
		noProjectSelected.getElement().getStyle().setColor("#555");
		this.add(noProjectSelected);
	}

	private void showDnDIntro() {
		
		if(!shownIntroFirstTime) {
			this.addStyleName("myDragDropBorder");
			DOM.getElementById(dnd.getDropTargetID()).addClassName("myCenterPoup");
			workspaceExplorerPanel.getElement().getStyle().setOpacity(0.2);
			
			Timer t = new Timer() {
				@Override
				public void run() {
					WorkspaceResourceUploadExplorerView.this.removeStyleName("myDragDropBorder");
					DOM.getElementById(dnd.getDropTargetID()).removeClassName("myCenterPoup");
					workspaceExplorerPanel.getElement().getStyle().setOpacity(1.0);
				}
			};
			
			t .schedule(2000);
			
			shownIntroFirstTime = true;
		}
	}

	/**
	 * Load explorer to root folder ID.
	 *
	 * @param theFolderId
	 *            the the folder id
	 */
	public void loadExplorerToRootFolderID(String theFolderId) {
		this.rootFolderId = theFolderId;
		this.breadcrumbShowingFolder = new Item(rootFolderId, "", true);
		try {
			remove(noProjectSelected);
		} catch (Exception e) {
			GWT.log(e.getMessage());
		}

		try {
			remove(dnd);
			dnd = new MultipleDNDUpload();
		} catch (Exception e) {
			GWT.log(e.getMessage());
		}

		try {

			workspaceExplorerPanel = new WorkspaceResourcesBExplorerPanel(theFolderId, false);
			SortedCellTable<Item> cellTable = workspaceExplorerPanel.getWsExplorer().getItTables().getCellTable();
			try {
				Column<Item, ?> dateColumn = workspaceExplorerPanel.getWsExplorer().getItTables().getCellTable()
						.getColumn(2);
				if (dateColumn != null) {
					cellTable.setDefaultSortOrder(dateColumn, true);
					cellTable.setInitialSortColumn(dateColumn);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			dnd.setParameters(breadcrumbShowingFolder.getId(), UPLOAD_TYPE.File);
			dnd.addUniqueContainer(workspaceExplorerPanel);
			WorskpaceExplorerSelectBNotificationListener wsResourceExplorerListener = new WorskpaceExplorerSelectBNotificationListener() {

				@Override
				public void onSelectedItem(Item item) {
					GWT.log("Listener Selected Item " + item);
					selectedItem = item;
					TrainingCourseAppViewController.eventBus.fireEvent(new SelectedWorkspaceItemEvent(item.getId()));
				}

				@Override
				public void onFailed(Throwable throwable) {
					// Log.error(throwable.getLocalizedMessage());
					throwable.printStackTrace();
				}

				@Override
				public void onAborted() {

				}

				@Override
				public void onNotValidSelection() {
					selectedItem = null;
				}

				@Override
				public void onBreadcrumbChanged(Item item) {
					GWT.log("Breadcrumb Changed: " + item);
					breadcrumbShowingFolder = item;
					dnd.setParameters(breadcrumbShowingFolder.getId(), UPLOAD_TYPE.File);
					TrainingCourseAppViewController.eventBus.fireEvent(new ShowMoreInfoEvent(false));

				}
			};

			WorskpaceUploadNotificationListener workspaceUploaderListener = new WorskpaceUploadNotificationListener() {

				@Override
				public void onUploadCompleted(String parentId, String itemId) {
					GWT.log("Upload completed: [parentID: " + parentId + ", itemId: " + itemId + "]");
					if (parentId.compareTo(rootFolderId) == 0) {
						refreshRootFolder();
					} else {
						workspaceExplorerPanel.eventBus.fireEvent(new LoadFolderEvent<Item>(breadcrumbShowingFolder));
					}
				}

				@Override
				public void onUploadAborted(String parentId, String itemId) {
					GWT.log("Upload Aborted: [parentID: " + parentId + ", itemId: " + itemId + "]");
				}

				@Override
				public void onError(String parentId, String itemId, Throwable throwable) {
					GWT.log("Upload Error: [parentID: " + parentId + ", itemId: " + itemId + "]");
					throwable.printStackTrace();
					Window.alert("Sorry, an error occurred during the upload, try again later");
					refreshRootFolder();
				}

				@Override
				public void onOverwriteCompleted(String parentId, String itemId) {
					GWT.log("Upload Override Completed: [parentID: " + parentId + ", itemId: " + itemId + "]");
					if (parentId.compareTo(rootFolderId) == 0) {
						refreshRootFolder();
					} else
						workspaceExplorerPanel.eventBus.fireEvent(new LoadFolderEvent<Item>(breadcrumbShowingFolder));
				}
			};

			dnd.addWorkspaceUploadNotificationListener(workspaceUploaderListener);
			workspaceExplorerPanel.addWorkspaceExplorerSelectNotificationListener(wsResourceExplorerListener);

		} catch (Exception e) {
			GWT.log(e.getMessage());
		}

		this.add(dnd);
		showDnDIntro();
	}

	/**
	 * Refresh internal folder.
	 *
	 * @param item
	 *            the item
	 */
	public void refreshInternalFolder(Item item) {
		workspaceExplorerPanel.eventBus.fireEvent(new LoadFolderEvent<Item>(item));
	}

	/**
	 * Refresh root folder.
	 */
	public void refreshRootFolder() {
		if (workspaceExplorerPanel != null)
			workspaceExplorerPanel.refreshRootFolderView();
	}

	/**
	 * Sets the widget size.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public void setWidgetSize(int width, int height) {
		dnd.setSize(width + "px", height + "px");
		if (workspaceExplorerPanel != null)
			workspaceExplorerPanel.setHeightToInternalScroll(height);
	}

}
