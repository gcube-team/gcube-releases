package org.gcube.portlets.widgets.dataminermanagerwidget.client.parametersfield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.shared.data.TableItemSimple;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.TabularParameter;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.DataMinerManagerPanel;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.common.EventBusProvider;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.SessionExpiredEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.TabularResourceInfoEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.TabularResourceInfoRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.rpc.DataMinerPortletServiceAsync;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.tr.TabularResourceData;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.util.UtilsGXT3;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.exception.SessionExpiredServiceException;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.workspace.ItemDescription;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TabItem extends HBoxLayoutContainer {

	private TabularListFld parent;
	private TableItemSimple selectedTableItem;

	private TextButton addBtn;
	private TextButton removeBtn;
	private TextButton selectButton;
	private TextButton selectButton2;
	private TextButton selectTRButton;
	private TextButton selectTRButton2;
	private TextButton downloadButton;
	private TextField tableDescription;

	private WorkspaceExplorerSelectDialog wselectDialog;
	private ItemDescription itemDescriptionSelected;

	private TabularResourceData tabularResourceData;
	private boolean created = false;
	private boolean first = true;

	/**
	 * 
	 * @param parent
	 *            parent
	 * @param tabularParameter
	 *            tabular parameter
	 * @param first
	 *            true if is first
	 */
	public TabItem(TabularListFld parent, TabularParameter tabularParameter, boolean first) {
		super();
		this.parent = parent;
		this.first = first;
		Log.debug("TabItem");
		retrieveInfo();
	}

	private void retrieveInfo() {
		bind();
		retrieveTabularResourceInfo();

	}

	private void bind() {
		EventBusProvider.INSTANCE.addHandler(TabularResourceInfoEvent.TYPE,
				new TabularResourceInfoEvent.TabularResourceInfoEventHandler() {

					@Override
					public void onInfoReceived(TabularResourceInfoEvent event) {
						Log.debug("Catch TabularResourceInfoEvent");
						tabularResourceData = event.getTabularResourceData();
						if (!created) {
							created = true;
							initDialog();
							create();
						}

					}
				});

	}

	private void retrieveTabularResourceInfo() {
		TabularResourceInfoRequestEvent event = new TabularResourceInfoRequestEvent();
		EventBusProvider.INSTANCE.fireEvent(event);
	}

	private void create() {

		tableDescription = new TextField();
		tableDescription.setReadOnly(true);
		tableDescription.setVisible(false);

		selectTRButton = new TextButton("Use Tabular Resource");
		selectTRButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				useTabularResource();
			}
		});
		selectTRButton.setIcon(DataMinerManagerPanel.resources.tabularResource());
		selectTRButton.setToolTip("Use Tabular Resource");

		selectTRButton2 = new TextButton("");
		selectTRButton2.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				useTabularResource();
			}
		});
		selectTRButton2.setIcon(DataMinerManagerPanel.resources.tabularResource());
		selectTRButton2.setToolTip("Use Tabular Resource");

		selectButton = new TextButton("Select Data Set");
		selectButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wselectDialog.show();
			}
		});

		selectButton.setIcon(DataMinerManagerPanel.resources.folderExplore());
		selectButton.setToolTip("Select Data Set");

		selectButton2 = new TextButton("");
		selectButton2.setIcon(DataMinerManagerPanel.resources.folderExplore());
		selectButton2.setToolTip("Select Another Data Set");
		selectButton2.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wselectDialog.show();
			}
		});

		downloadButton = new TextButton("");
		downloadButton.setIcon(DataMinerManagerPanel.resources.download());
		downloadButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				downloadFile();
			}
		});
		downloadButton.setToolTip("Download Data Set");

		addBtn = new TextButton("");
		addBtn.setIcon(DataMinerManagerPanel.resources.add());
		addBtn.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				parent.addField(TabItem.this);

			}
		});

		removeBtn = new TextButton("");
		removeBtn.setIcon(DataMinerManagerPanel.resources.cancel());
		removeBtn.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				selectedTableItem = null;
				parent.removeField(TabItem.this);

			}
		});

		setPack(BoxLayoutPack.START);
		setEnableOverflow(false);

		add(tableDescription, new BoxLayoutData(new Margins()));
		add(selectTRButton, new BoxLayoutData(new Margins()));
		add(selectTRButton2, new BoxLayoutData(new Margins()));
		add(selectButton, new BoxLayoutData(new Margins()));
		add(selectButton2, new BoxLayoutData(new Margins()));
		add(downloadButton, new BoxLayoutData(new Margins()));
		add(addBtn, new BoxLayoutData(new Margins()));
		add(removeBtn, new BoxLayoutData(new Margins()));

		selectTRButton2.setVisible(false);
		selectButton2.setVisible(false);
		downloadButton.setVisible(false);
		removeBtn.setVisible(!first);

		forceLayout();

	}

	private void initDialog() {

		List<ItemType> selectableTypes = new ArrayList<ItemType>();
		selectableTypes.add(ItemType.EXTERNAL_FILE);
		List<ItemType> showableTypes = new ArrayList<ItemType>();
		showableTypes.addAll(Arrays.asList(ItemType.values()));

		/*
		 * "application/zip", "application/x-zip",
		 * "application/x-zip-compressed", "application/octet-stream",
		 * "application/x-compress", "application/x-compressed",
		 * "multipart/x-zip"
		 */
		// List<String> allowedMimeTypes =
		// Arrays.asList("text/csv","text/plain","text/plain;
		// charset=ISO-8859-1");

		/**
		 * "zip"
		 */

		/*
		 * List<String> allowedFileExtensions = Arrays.asList("csv");
		 * 
		 * FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes,
		 * allowedFileExtensions, new HashMap<String, String>());
		 */
		wselectDialog = new WorkspaceExplorerSelectDialog("Select CSV", false);
		// filterCriteria, selectableTypes);

		WorskpaceExplorerSelectNotificationListener handler = new WorskpaceExplorerSelectNotificationListener() {

			@Override
			public void onSelectedItem(Item item) {

				if (item.isFolder() || item.isRoot()) {
					UtilsGXT3.info("Attention", "Select a valid csv!");

				} else {
					TabItem.this.retrieveTableInformation(item);

				}

			}

			@Override
			public void onFailed(Throwable throwable) {
				Log.error("Error in create project: " + throwable.getLocalizedMessage());
				UtilsGXT3.alert("Error", throwable.getLocalizedMessage());
				throwable.printStackTrace();
			}

			@Override
			public void onAborted() {

			}

			@Override
			public void onNotValidSelection() {
				UtilsGXT3.info("Attention", "Select a valid csv!");
			}
		};

		wselectDialog.addWorkspaceExplorerSelectNotificationListener(handler);
		wselectDialog.setZIndex(XDOM.getTopZIndex());

	}

	private void retrieveTableInformation(Item item) {
		Log.debug("Retrieved: " + item);
		itemDescriptionSelected = new ItemDescription(item.getId(), item.getName(), item.getOwner(), item.getPath(),
				item.getType().name());

		DataMinerPortletServiceAsync.INSTANCE.retrieveTableInformation(item, new AsyncCallback<TableItemSimple>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error in retrieveTableInformation " + caught.getMessage());
				if (caught instanceof SessionExpiredServiceException) {
					UtilsGXT3.alert("Error", "Expired Session");

				} else {
					UtilsGXT3.alert("Error", "Error retrieving table information: " + caught.getLocalizedMessage());
				}

			}

			@Override
			public void onSuccess(TableItemSimple result) {
				Log.debug("Retrieved: " + result);
				selectedTableItem = result;
				showFieldWithFileSelection();

			}
		});
	}

	private void downloadFile() {
		if (itemDescriptionSelected != null) {
			DataMinerPortletServiceAsync.INSTANCE.getPublicLink(itemDescriptionSelected, new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
					if (caught instanceof SessionExpiredServiceException) {
						EventBusProvider.INSTANCE.fireEvent(new SessionExpiredEvent());
					} else {
						Log.error("Error downloading table: " + caught.getLocalizedMessage());
						UtilsGXT3.alert("Error", caught.getLocalizedMessage());
					}
					caught.printStackTrace();

				}

				@Override
				public void onSuccess(String link) {
					Log.debug("Retrieved link: " + link);
					Window.open(link, itemDescriptionSelected.getName(), "");
				}

			});

		} else {
			UtilsGXT3.info("Attention", "Select a Table!");
		}

	}

	private void useTabularResource() {
		Log.debug("TabularResourceData: " + tabularResourceData);
		selectedTableItem = new TableItemSimple(tabularResourceData.getTabularResourceId(),
				tabularResourceData.getName(), tabularResourceData.getDescription(), tabularResourceData.getType());
		selectedTableItem.setColumns(tabularResourceData.getColumns());
		showFieldWithTRSelection();
	}

	private void showFieldWithTRSelection() {
		try {

			String tableName = selectedTableItem.getName();

			if (tableName == null || tableName.isEmpty()) {
				tableName = "NoName";
			}

			tableDescription.setValue(tableName);
			tableDescription.setVisible(true);
			selectTRButton.setVisible(false);
			selectTRButton2.setVisible(false);
			selectButton.setVisible(false);
			selectButton2.setVisible(true);
			downloadButton.setVisible(false);
			parent.forceLayout();

		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void showFieldWithFileSelection() {
		try {

			String tableName = selectedTableItem.getName();

			if (tableName == null || tableName.isEmpty()) {
				tableName = "NoName";
			}

			tableDescription.setValue(tableName);
			tableDescription.setVisible(true);
			selectTRButton.setVisible(false);
			selectTRButton2.setVisible(true);
			selectButton.setVisible(false);
			selectButton2.setVisible(true);
			downloadButton.setVisible(true);
			parent.forceLayout();

		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	public void showCancelButton() {
		removeBtn.setVisible(true);
		forceLayout();
		onResize();
	}

	public void hideCancelButton() {
		removeBtn.setVisible(false);
		forceLayout();
		onResize();
	}

	public String getValue() {
		return (selectedTableItem == null) ? null : selectedTableItem.getId();
	}

	public boolean isValid() {
		return (selectedTableItem != null);
	}

}