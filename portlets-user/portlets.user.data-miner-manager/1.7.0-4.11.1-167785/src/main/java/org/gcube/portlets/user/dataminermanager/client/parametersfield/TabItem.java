package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminermanager.client.events.SessionExpiredEvent;
import org.gcube.portlets.user.dataminermanager.client.rpc.DataMinerPortletServiceAsync;
import org.gcube.portlets.user.dataminermanager.client.util.UtilsGXT3;
import org.gcube.data.analysis.dataminermanagercl.shared.data.TableItemSimple;
import org.gcube.portlets.user.dataminermanager.shared.exception.SessionExpiredServiceException;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.TabularParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;
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
	private TextButton selectButton;
	private TextButton selectButton2;
	private TableItemSimple selectedTableItem;
	private TextButton addBtn;
	private TextButton removeBtn;
	private TextField tableDescription;
	private WorkspaceExplorerSelectDialog wselectDialog;
	private TextButton downloadButton;
	private ItemDescription itemDescriptionSelected;

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
		initDialog();
		create(tabularParameter, first);
	}

	private void create(TabularParameter tabularParameter, boolean first) {

		tableDescription = new TextField();
		tableDescription.setReadOnly(true);
		tableDescription.setVisible(false);

		selectButton = new TextButton("Select Data Set");
		selectButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wselectDialog.show();
			}
		});

		selectButton.setIcon(DataMinerManager.resources.folderExplore());
		selectButton.setToolTip("Select Data Set");

		selectButton2 = new TextButton("");
		selectButton2.setIcon(DataMinerManager.resources.folderExplore());
		selectButton2.setToolTip("Select Another Data Set");
		selectButton2.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wselectDialog.show();
			}
		});
		selectButton2.setVisible(false);

		downloadButton = new TextButton("");
		downloadButton.setIcon(DataMinerManager.resources.download());
		downloadButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				downloadFile();
			}
		});
		downloadButton.setVisible(false);

		addBtn = new TextButton("");
		addBtn.setIcon(DataMinerManager.resources.add());
		addBtn.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				parent.addField(TabItem.this);

			}
		});

		removeBtn = new TextButton("");
		removeBtn.setIcon(DataMinerManager.resources.cancel());
		removeBtn.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				selectedTableItem = null;
				parent.removeField(TabItem.this);

			}
		});
		removeBtn.setVisible(!first);

		setPack(BoxLayoutPack.START);
		setEnableOverflow(false);

		add(tableDescription, new BoxLayoutData(new Margins()));
		add(selectButton, new BoxLayoutData(new Margins()));
		add(selectButton2, new BoxLayoutData(new Margins()));
		add(downloadButton, new BoxLayoutData(new Margins()));
		add(addBtn, new BoxLayoutData(new Margins()));
		add(removeBtn, new BoxLayoutData(new Margins()));

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
				showFieldWithSelection();

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

	/**
		 * 
		 */
	private void showFieldWithSelection() {
		try {

			String tableName = selectedTableItem.getName();

			if (tableName == null || tableName.isEmpty()) {
				tableName = "NoName";
			}

			tableDescription.setValue(tableName);
			tableDescription.setVisible(true);
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
	}

	public void hideCancelButton() {
		removeBtn.setVisible(false);
	}

	public String getValue() {
		return (selectedTableItem == null) ? null : selectedTableItem.getId();
	}

	public boolean isValid() {
		return (selectedTableItem != null);
	}

}