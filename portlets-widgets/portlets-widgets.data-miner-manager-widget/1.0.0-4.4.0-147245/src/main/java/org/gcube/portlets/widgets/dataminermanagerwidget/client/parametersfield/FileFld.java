/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.parametersfield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.FileParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
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
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class FileFld extends AbstractFld {

	private VerticalLayoutContainer vp;
	private SimpleContainer fieldContainer;
	private HBoxLayoutContainer horiz;
	
	// FileSelector fileSelector;
	private WorkspaceExplorerSelectDialog wselectDialog;
	private TextButton selectButton;
	private TextButton selectButton2;
	private TextButton selectTRButton;
	private TextButton selectTRButton2;
	private TextButton cancelButton;
	private TextButton downloadButton;

	private ItemDescription selectedFileItem = null;
	private TabularResourceData tabularResourceData;
	private FileParameter fileParameter;
	private boolean created = false;

	
	
	/**
	 * @param parameter
	 */
	public FileFld(Parameter parameter) {
		super(parameter);
		fileParameter = (FileParameter) parameter;
		Log.debug("FileField");
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
							create();
						}

					}
				});

	}

	private void retrieveTabularResourceInfo() {
		TabularResourceInfoRequestEvent event = new TabularResourceInfoRequestEvent();
		EventBusProvider.INSTANCE.fireEvent(event);
	}
		
	private void create(){	
		SimpleContainer tabContainer = new SimpleContainer();
		vp = new VerticalLayoutContainer();
		init();
		tabContainer.add(vp, new MarginData(new Margins(0)));

		fieldContainer = new SimpleContainer();
		horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		HtmlLayoutContainer descr;

		if (fileParameter.getDescription() == null) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'>"
					+ fileParameter.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}

		horiz.add(tabContainer, new BoxLayoutData(new Margins()));
		horiz.add(descr, new BoxLayoutData(new Margins()));

		fieldContainer.add(horiz);
		showNoSelectionField();

	}

	private void init() {

		List<ItemType> selectableTypes = new ArrayList<ItemType>();
		selectableTypes.add(ItemType.EXTERNAL_FILE);
		List<ItemType> showableTypes = new ArrayList<ItemType>();
		showableTypes.addAll(Arrays.asList(ItemType.values()));

		wselectDialog = new WorkspaceExplorerSelectDialog("Select File", false);
		// filterCriteria, selectableTypes);

		WorskpaceExplorerSelectNotificationListener handler = new WorskpaceExplorerSelectNotificationListener() {

			@Override
			public void onSelectedItem(Item item) {

				if (item.isFolder() || item.isRoot()) {
					UtilsGXT3.info("Attention", "Select a valid file!");

				} else {
					retrieveFileInformation(item);
				}

			}

			@Override
			public void onFailed(Throwable throwable) {
				Log.error("Error in create project: "
						+ throwable.getLocalizedMessage());
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

		
		selectTRButton = new TextButton("Use Tabular Resource");
		selectTRButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				useTabularResource();
			}
		});
		selectTRButton.setIcon(DataMinerManagerPanel.resources
				.tabularResource());
		selectTRButton.setToolTip("Use Tabular Resource");

		selectTRButton2 = new TextButton("");
		selectTRButton2.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				useTabularResource();
			}
		});

		selectTRButton2.setIcon(DataMinerManagerPanel.resources
				.tabularResource());
		selectTRButton2.setToolTip("Use Tabular Resource");

		
		
		selectButton = new TextButton("Select File");
		selectButton.setIcon(DataMinerManagerPanel.resources.folderExplore());
		selectButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wselectDialog.show();

			}
		});
		selectButton.setToolTip("Select File");

		selectButton2 = new TextButton("");
		selectButton2.setIcon(DataMinerManagerPanel.resources.folderExplore());
		selectButton2.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wselectDialog.show();
			}
		});
		selectButton2.setToolTip("Select Another File");

		cancelButton = new TextButton("");
		cancelButton.setIcon(DataMinerManagerPanel.resources.cancel());
		cancelButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				selectedFileItem = null;
				showNoSelectionField();
			}
		});
		cancelButton.setToolTip("Cancel");

		downloadButton = new TextButton("");
		downloadButton.setIcon(DataMinerManagerPanel.resources.download());
		downloadButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				downloadFile();
			}
		});
		downloadButton.setToolTip("Download File");

	}
	
	private void useTabularResource() {
		Log.debug("TabularResourceData: " + tabularResourceData);
		selectedFileItem = new ItemDescription(
				tabularResourceData.getTabularResourceId(),
				tabularResourceData.getName(),
				"","",
				tabularResourceData.getType());
		selectedFileItem.setPublicLink(tabularResourceData.getTabularResourceId());		
		showFieldWithTRSelection();
	}
	

	private void retrieveFileInformation(final Item item) {
		Log.debug("Retrieved: " + item);
		final ItemDescription itemDescription = new ItemDescription(
				item.getId(), item.getName(), item.getOwner(), item.getPath(),
				item.getType().name());

		DataMinerPortletServiceAsync.INSTANCE.getPublicLink(itemDescription,
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.error("Error in retrieveFileInformation: "
								+ caught.getMessage());
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session");

						} else {
							UtilsGXT3.alert("Error",
									"Error retrieving file informations: "
											+ caught.getLocalizedMessage());
						}

					}

					@Override
					public void onSuccess(String result) {
						itemDescription.setPublicLink(result);
						selectedFileItem = itemDescription;
						Log.debug("SelectedFileItem: " + selectedFileItem);
						showFieldWithFileSelection();

					}
				});

	}

	private void downloadFile() {
		if (selectedFileItem != null) {
			DataMinerPortletServiceAsync.INSTANCE.getPublicLink(
					selectedFileItem, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							if (caught instanceof SessionExpiredServiceException) {
								EventBusProvider.INSTANCE
										.fireEvent(new SessionExpiredEvent());
							} else {
								Log.error("Error downloading file: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error",
										caught.getLocalizedMessage());
							}
							caught.printStackTrace();

						}

						@Override
						public void onSuccess(String link) {
							Log.debug("Retrieved link: " + link);
							Window.open(link, selectedFileItem.getName(), "");
						}

					});

		} else {
			UtilsGXT3.info("Attention", "Select a file!");
		}

	}


	private void showNoSelectionField() {
		vp.clear();
		vp.add(selectTRButton);
		vp.add(selectButton);
		vp.forceLayout();
		fieldContainer.forceLayout();
	}
	
	
	private void showFieldWithFileSelection() {
		String fileName = selectedFileItem.getName();

		if (fileName == null || fileName.isEmpty()) {
			fileName = "NoName";
		}

		TextField tableDescription = new TextField();
		tableDescription.setValue(fileName);
		tableDescription.setReadOnly(true);

		HBoxLayoutContainer h = new HBoxLayoutContainer();
		h.add(tableDescription, new BoxLayoutData(new Margins()));
		h.add(selectTRButton2, new BoxLayoutData(new Margins()));
		h.add(selectButton2, new BoxLayoutData(new Margins()));
		h.add(downloadButton, new BoxLayoutData(new Margins()));
		h.add(cancelButton, new BoxLayoutData(new Margins()));
		vp.clear();
		vp.add(h);
		vp.forceLayout();
		fieldContainer.forceLayout();

	}
	
	private void showFieldWithTRSelection() {
		String fileName = selectedFileItem.getName();

		if (fileName == null || fileName.isEmpty()) {
			fileName = "NoName";
		}

		TextField tableDescription = new TextField();
		tableDescription.setValue(fileName);
		tableDescription.setReadOnly(true);

		HBoxLayoutContainer h = new HBoxLayoutContainer();
		h.add(tableDescription, new BoxLayoutData(new Margins()));
		h.add(selectTRButton2, new BoxLayoutData(new Margins()));
		h.add(selectButton2, new BoxLayoutData(new Margins()));
		h.add(downloadButton, new BoxLayoutData(new Margins()));
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
		return (selectedFileItem == null) ? null : selectedFileItem
				.getPublicLink();
	}

}
