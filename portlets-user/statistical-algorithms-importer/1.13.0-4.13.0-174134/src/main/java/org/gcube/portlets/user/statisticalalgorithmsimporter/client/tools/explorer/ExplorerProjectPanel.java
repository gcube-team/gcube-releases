package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.explorer;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.BinaryCodeSetEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.DeleteItemEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.MainCodeSetEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.ProjectStatusEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.utils.UtilsGXT3;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.workspace.DownloadWidget;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBashEdit;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBlackBox;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.DialogUpload.UPLOAD_TYPE;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload;
import org.gcube.portlets.widgets.wsexplorer.client.explore.WorkspaceResourcesExplorerPanel;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonScale;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ExplorerProjectPanel extends ContentPanel {

	private EventBus eventBus;
	private Item selectedItem;
	private MultipleDNDUpload dnd;
	private WorkspaceResourcesExplorerPanel wsResourcesExplorerPanel;
	private TextButton btnSetMain;
	private TextButton btnOpen;
	private TextButton btnDelete;
	private TextButton btnReload;

	public ExplorerProjectPanel(EventBus eventBus) {
		super();
		Log.debug("ExplorerProjectPanel");
		this.eventBus = eventBus;

		// msgs = GWT.create(ServiceCategoryMessages.class);
		init();
		bindToEvents();

	}

	public ExplorerProjectPanel(EventBus eventBus, AccordionLayoutAppearance appearance) {
		super(appearance);
		Log.debug("ExplorerProjectPanel");
		this.eventBus = eventBus;
		init();
		bindToEvents();

	}

	private void init() {
		setId("ExplorerProjectPanel");
		forceLayoutOnResize = true;
		setBodyBorder(true);
		setBorders(true);
		setHeaderVisible(true);
		setResize(true);
		setAnimCollapse(false);
		setHeadingText("Project Explorer");

	}

	private void bindToEvents() {

		eventBus.addHandler(ProjectStatusEvent.TYPE, new ProjectStatusEvent.ProjectStatusEventHandler() {

			@Override
			public void onProjectStatus(ProjectStatusEvent event) {
				Log.debug("ExplorerProjectPanel Catch Event ProjectStatus: " + event);

				manageProjectStatusEvents(event);

			}
		});

		Log.debug("ExplorerProjectPanel bind to Event do!");
	}

	private void manageProjectStatusEvents(ProjectStatusEvent event) {
		if (event == null || event.getProjectStatusEventType() == null) {
			Log.error("Invalid event: " + event);
			return;
		}
		Log.debug("Project Status Event: " + event.getProjectStatusEventType());
		switch (event.getProjectStatusEventType()) {
		case START:
			break;
		case OPEN:
		case UPDATE:
		case ADD_RESOURCE:
		case DELETE_RESOURCE:
		case DELETE_MAIN_CODE:
			create(event);
			break;
		case SAVE:
		case MAIN_CODE_SET:
		case BINARY_CODE_SET:
		case SOFTWARE_PUBLISH:
		case SOFTWARE_REPACKAGE:
		case EXPLORER_REFRESH:
			reloadWSResourceExplorerPanel();
			break;
		default:
			break;
		}
	}

	private void create(ProjectStatusEvent event) {
		try {

			wsResourcesExplorerPanel = new WorkspaceResourcesExplorerPanel(
					event.getProject().getProjectFolder().getFolder().getId(), false);

			WorskpaceExplorerSelectNotificationListener wsResourceExplorerListener = new WorskpaceExplorerSelectNotificationListener() {
				@Override
				public void onSelectedItem(Item item) {
					Log.debug("Listener Selected Item " + item);
					selectedItem = item;

				}

				@Override
				public void onFailed(Throwable throwable) {
					Log.error(throwable.getLocalizedMessage());
					throwable.printStackTrace();
				}

				@Override
				public void onAborted() {

				}

				@Override
				public void onNotValidSelection() {
					selectedItem = null;
				}
			};

			wsResourcesExplorerPanel.addWorkspaceExplorerSelectNotificationListener(wsResourceExplorerListener);
			wsResourcesExplorerPanel.ensureDebugId("wsResourceExplorerPanel");

			VerticalLayoutContainer vResourcesExplorerContainer = new VerticalLayoutContainer();
			vResourcesExplorerContainer.setScrollMode(ScrollMode.AUTO);
			vResourcesExplorerContainer.add(wsResourcesExplorerPanel, new VerticalLayoutData(1, -1, new Margins(0)));

			// DND

			dnd = new MultipleDNDUpload();
			dnd.setParameters(event.getProject().getProjectFolder().getFolder().getId(), UPLOAD_TYPE.File);
			dnd.addUniqueContainer(vResourcesExplorerContainer);
			WorskpaceUploadNotificationListener workspaceUploaderListener = new WorskpaceUploadNotificationListener() {

				@Override
				public void onUploadCompleted(String parentId, String itemId) {
					Log.debug("Upload completed: [parentID: " + parentId + ", itemId: " + itemId + "]");
					wsResourcesExplorerPanel.refreshRootFolderView();
					forceLayout();

				}

				@Override
				public void onUploadAborted(String parentId, String itemId) {
					Log.debug("Upload Aborted: [parentID: " + parentId + ", itemId: " + itemId + "]");
				}

				@Override
				public void onError(String parentId, String itemId, Throwable throwable) {
					Log.debug("Upload Error: [parentID: " + parentId + ", itemId: " + itemId + "]");
					throwable.printStackTrace();
				}

				@Override
				public void onOverwriteCompleted(String parentId, String itemId) {
					Log.debug("Upload Override Completed: [parentID: " + parentId + ", itemId: " + itemId + "]");
					wsResourcesExplorerPanel.refreshRootFolderView();
					forceLayout();
				}
			};

			dnd.addWorkspaceUploadNotificationListener(workspaceUploaderListener);

			// ToolBar
			if (event.getProject().getProjectConfig().getProjectSupport() instanceof ProjectSupportBlackBox
					|| event.getProject().getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
				btnSetMain = new TextButton("Set Code");
				btnSetMain.setIcon(StatAlgoImporterResources.INSTANCE.add16());
				btnSetMain.setScale(ButtonScale.SMALL);
				btnSetMain.setIconAlign(IconAlign.LEFT);
				btnSetMain.setToolTip("Set code");
				btnSetMain.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						setBinaryCode(event);
					}

				});
			} else {

				btnSetMain = new TextButton("Set Main");
				btnSetMain.setIcon(StatAlgoImporterResources.INSTANCE.add16());
				btnSetMain.setScale(ButtonScale.SMALL);
				btnSetMain.setIconAlign(IconAlign.LEFT);
				btnSetMain.setToolTip("Set main code");
				btnSetMain.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						setMainCode(event);
					}

				});
			}

			btnOpen = new TextButton("Open");
			btnOpen.setIcon(StatAlgoImporterResources.INSTANCE.download16());
			btnOpen.setScale(ButtonScale.SMALL);
			btnOpen.setIconAlign(IconAlign.LEFT);
			btnOpen.setToolTip("Open");
			btnOpen.addSelectHandler(new SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					openFile();
				}

			});

			btnDelete = new TextButton("Delete");
			btnDelete.setIcon(StatAlgoImporterResources.INSTANCE.delete16());
			btnDelete.setScale(ButtonScale.SMALL);
			btnDelete.setIconAlign(IconAlign.LEFT);
			btnDelete.setToolTip("Delete");
			btnDelete.addSelectHandler(new SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					deleteItem(event);
				}

			});

			btnReload = new TextButton("Reload");
			btnReload.setIcon(StatAlgoImporterResources.INSTANCE.reload16());
			btnReload.setScale(ButtonScale.SMALL);
			btnReload.setIconAlign(IconAlign.LEFT);
			btnReload.setToolTip("Reload");
			btnReload.addSelectHandler(new SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					reloadWSResourceExplorerPanel();
				}

			});

			ToolBar toolBar = new ToolBar();
			toolBar.add(btnSetMain, new BoxLayoutData(new Margins(0)));
			toolBar.add(btnOpen, new BoxLayoutData(new Margins(0)));
			toolBar.add(btnDelete, new BoxLayoutData(new Margins(0)));
			toolBar.add(btnReload, new BoxLayoutData(new Margins(0)));

			VerticalLayoutContainer v = new VerticalLayoutContainer();

			v.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
			v.add(dnd, new VerticalLayoutData(1, 1, new Margins(0)));
			add(v, new MarginData(new Margins(0)));
			forceLayout();

		} catch (Throwable e) {
			Log.error("Error opening wsResourceExplorerPanel: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
	}

	protected void reloadWSResourceExplorerPanel() {
		if (wsResourcesExplorerPanel != null) {
			wsResourcesExplorerPanel.refreshRootFolderView();
		}

	}

	private void deleteItem(SelectEvent event) {
		Log.debug("Selected Item: " + selectedItem);
		if (selectedItem != null) {
			ItemDescription itemDescription = new ItemDescription(selectedItem.getId(), selectedItem.getName(),
					selectedItem.getOwner(), selectedItem.getPath(), selectedItem.getType().name());
			DeleteItemEvent deleteItemEvent = new DeleteItemEvent(itemDescription);
			Log.debug("DeleteItemEvent: " + itemDescription);
			eventBus.fireEvent(deleteItemEvent);
			Log.debug("Fired: " + deleteItemEvent);
		} else {
			UtilsGXT3.info("Attention", "Select a item!");
		}
	}

	private void setMainCode(SelectEvent event) {
		Log.debug("Set Code");
		if (selectedItem != null && selectedItem.getType() != ItemType.FOLDER
				&& selectedItem.getType() != ItemType.PRIVATE_FOLDER && selectedItem.getType() != ItemType.SHARED_FOLDER
				&& selectedItem.getType() != ItemType.VRE_FOLDER) {
			setMainCodeData();
		} else {
			UtilsGXT3.info("Attention", "Select a valid file to be used as main!");
		}
	}

	private void setMainCodeData() {
		ItemDescription itemDescription = new ItemDescription(selectedItem.getId(), selectedItem.getName(),
				selectedItem.getOwner(), selectedItem.getPath(), selectedItem.getType().name());
		MainCodeSetEvent mainCodeSetEvent = new MainCodeSetEvent(itemDescription);
		eventBus.fireEvent(mainCodeSetEvent);
		Log.debug("Fired: " + mainCodeSetEvent);

	}

	private void setBinaryCode(SelectEvent event) {
		Log.debug("Set Code");
		if (selectedItem != null && selectedItem.getType() != ItemType.FOLDER
				&& selectedItem.getType() != ItemType.PRIVATE_FOLDER && selectedItem.getType() != ItemType.SHARED_FOLDER
				&& selectedItem.getType() != ItemType.VRE_FOLDER) {
			setBinaryCodeData();
		} else {
			UtilsGXT3.info("Attention", "Select a valid file to be used!");
		}
	}

	private void setBinaryCodeData() {
		ItemDescription itemDescription = new ItemDescription(selectedItem.getId(), selectedItem.getName(),
				selectedItem.getOwner(), selectedItem.getPath(), selectedItem.getType().name());
		BinaryCodeSetEvent binaryCodeSetEvent = new BinaryCodeSetEvent(itemDescription);
		eventBus.fireEvent(binaryCodeSetEvent);
		Log.debug("Fired: " + binaryCodeSetEvent);

	}

	private void openFile() {
		if (selectedItem != null) {
			DownloadWidget downloadWidget = new DownloadWidget(eventBus);
			downloadWidget.download(selectedItem.getId());
		} else {
			UtilsGXT3.info("Attention", "Select a file!");
		}
	}

}
