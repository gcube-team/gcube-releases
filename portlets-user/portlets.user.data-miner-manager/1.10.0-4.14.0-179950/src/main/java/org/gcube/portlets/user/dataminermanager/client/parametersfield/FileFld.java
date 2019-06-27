/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.FileParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;
import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.client.rpc.DataMinerPortletServiceAsync;
import org.gcube.portlets.user.dataminermanager.client.util.UtilsGXT3;
import org.gcube.portlets.user.dataminermanager.client.workspace.DownloadWidget;
import org.gcube.portlets.user.dataminermanager.shared.exception.SessionExpiredServiceException;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent.SelectVariableEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets.NetCDFPreviewDialog;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class FileFld extends AbstractFld {

	private VerticalLayoutContainer vp;

	// FileSelector fileSelector;
	private WorkspaceExplorerSelectDialog wselectDialog;
	private TextButton selectButton, selectButton2, cancelButton;
	private ItemDescription selectedFileItem = null;

	private FileParameter fileParameter;

	private SimpleContainer fieldContainer;

	private HBoxLayoutContainer horiz;

	private TextButton downloadButton;
	private TextButton netcdfButton;

	/**
	 * @param parameter
	 *            parameter
	 */
	public FileFld(Parameter parameter) {
		super(parameter);
		fileParameter = (FileParameter) parameter;

		SimpleContainer tabContainer = new SimpleContainer();
		vp = new VerticalLayoutContainer();
		init();
		tabContainer.add(vp, new MarginData(new Margins(0)));

		fieldContainer = new SimpleContainer();
		horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		HtmlLayoutContainer descr;

		if (fileParameter.getDescription() == null || fileParameter.getDescription().isEmpty()) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			if (fileParameter.isNetcdf()) {
				String des = fileParameter.getDescription().replaceFirst("\\[NETCDF\\]", "");
				descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'>" + des + "</p>");
				descr.addStyleName("workflow-fieldDescription");
			} else {
				descr = new HtmlLayoutContainer(
						"<p style='margin-left:5px !important;'>" + fileParameter.getDescription() + "</p>");
				descr.addStyleName("workflow-fieldDescription");
			}
		}

		horiz.add(tabContainer, new BoxLayoutData(new Margins()));
		horiz.add(descr, new BoxLayoutData(new Margins()));

		fieldContainer.add(horiz);
		showNoSelectionField();

	}

	private void init() {

		
		wselectDialog = new WorkspaceExplorerSelectDialog("Select File", false);
		
		WorskpaceExplorerSelectNotificationListener handler = new WorskpaceExplorerSelectNotificationListener() {

			@Override
			public void onSelectedItem(Item item) {

				if (item == null || item.getType() == ItemType.FOLDER
						|| item.getType() == ItemType.PRIVATE_FOLDER 
						|| item.getType() == ItemType.SHARED_FOLDER
						|| item.getType() == ItemType.VRE_FOLDER) {
					UtilsGXT3.info("Attention", "Select a valid file!");

				} else {
					retrieveFileInformation(item);
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
				UtilsGXT3.info("Attention", "Select a valid file!");
			}
		};

		wselectDialog.addWorkspaceExplorerSelectNotificationListener(handler);
		wselectDialog.setZIndex(XDOM.getTopZIndex());

		selectButton = new TextButton("Select File");
		selectButton.setIcon(DataMinerManager.resources.folderExplore());
		selectButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wselectDialog.show();

			}
		});
		selectButton.setToolTip("Select File");

		selectButton2 = new TextButton("");
		selectButton2.setIcon(DataMinerManager.resources.folderExplore());
		selectButton2.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wselectDialog.show();
			}
		});
		selectButton2.setToolTip("Select Another File");

		cancelButton = new TextButton("");
		cancelButton.setIcon(DataMinerManager.resources.cancel());
		cancelButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				selectedFileItem = null;
				showNoSelectionField();
			}
		});

		downloadButton = new TextButton("");
		downloadButton.setIcon(DataMinerManager.resources.download());
		downloadButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				downloadFile();
			}
		});

		netcdfButton = new TextButton("");
		netcdfButton.setIcon(DataMinerManager.resources.netcdf());
		netcdfButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				showNetCDFFile();
			}
		});

	}

	private void retrieveFileInformation(final Item item) {
		Log.debug("Retrieved: " + item);
		final ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
				item.getPath(), item.getType().name());

		DataMinerPortletServiceAsync.INSTANCE.getPublicLink(itemDescription, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error in retrieveFileInformation: " + caught.getMessage());
				if (caught instanceof SessionExpiredServiceException) {
					UtilsGXT3.alert("Error", "Expired Session");

				} else {
					UtilsGXT3.alert("Error", "Error retrieving file informations: " + caught.getLocalizedMessage());
				}

			}

			@Override
			public void onSuccess(String result) {
				itemDescription.setPublicLink(result);
				selectedFileItem = itemDescription;
				Log.debug("SelectedFileItem: " + selectedFileItem);
				showFieldWithSelection();

			}
		});

	}

	private void downloadFile() {
		if (selectedFileItem != null) {
			DownloadWidget downloadWidget = new DownloadWidget();
			downloadWidget.download(selectedFileItem.getId());
		} else {
			UtilsGXT3.info("Attention", "Select a file!");
		}
		
	}

	private void showNetCDFFile() {
		if (selectedFileItem != null) {
			GWT.log("NetcdfBasicWidgetsManager");

			// Example
			SelectVariableEventHandler handler = new SelectVariableEventHandler() {

				@Override
				public void onResponse(SelectVariableEvent event) {
					GWT.log("SelectVariable Response: " + event);

				}
			};

			NetCDFPreviewDialog netcdfDialog = new NetCDFPreviewDialog(selectedFileItem.getPublicLink());
			netcdfDialog.addSelectVariableEventHandler(handler);
			netcdfDialog.setZIndex(XDOM.getTopZIndex());

		}
	}

	private void showNoSelectionField() {
		vp.clear();
		vp.add(selectButton);
		vp.forceLayout();
		fieldContainer.forceLayout();
	}

	private void showFieldWithSelection() {
		String fileName = selectedFileItem.getName();

		if (fileName == null || fileName.isEmpty()) {
			fileName = "NoName";
		}

		TextField tableDescription = new TextField();
		tableDescription.setValue(fileName);
		tableDescription.setReadOnly(true);

		HBoxLayoutContainer h = new HBoxLayoutContainer();
		h.add(tableDescription, new BoxLayoutData(new Margins()));
		h.add(selectButton2, new BoxLayoutData(new Margins()));
		h.add(downloadButton, new BoxLayoutData(new Margins()));
		if (fileParameter.isNetcdf()) {
			h.add(netcdfButton, new BoxLayoutData(new Margins()));
		}
		h.add(cancelButton, new BoxLayoutData(new Margins()));
		vp.clear();
		vp.add(h);
		vp.forceLayout();
		fieldContainer.forceLayout();

	}

	/**
	 * 
	 */
	@Override
	public boolean isValid() {
		return (selectedFileItem != null);
	}

	@Override
	public Widget getWidget() {
		return fieldContainer;
	}

	@Override
	public String getValue() {
		return (selectedFileItem == null) ? null : selectedFileItem.getPublicLink();
	}

}
