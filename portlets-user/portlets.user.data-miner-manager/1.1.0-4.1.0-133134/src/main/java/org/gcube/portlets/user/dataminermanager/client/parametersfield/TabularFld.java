/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminermanager.client.events.SessionExpiredEvent;
import org.gcube.portlets.user.dataminermanager.client.events.TabularFldChangeEvent;
import org.gcube.portlets.user.dataminermanager.client.events.TabularFldChangeEvent.HasTabularFldChangeEventHandler;
import org.gcube.portlets.user.dataminermanager.client.events.TabularFldChangeEvent.TabularFldChangeEventHandler;
import org.gcube.portlets.user.dataminermanager.client.rpc.DataMinerPortletServiceAsync;
import org.gcube.portlets.user.dataminermanager.client.util.UtilsGXT3;
import org.gcube.portlets.user.dataminermanager.shared.data.TableItemSimple;
import org.gcube.portlets.user.dataminermanager.shared.exception.SessionExpiredServiceException;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.TabularParameter;
import org.gcube.portlets.user.dataminermanager.shared.workspace.ItemDescription;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Format;
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
public class TabularFld extends AbstractFld implements
		HasTabularFldChangeEventHandler {

	private SimpleContainer fieldContainer;
	private HBoxLayoutContainer horiz;
	private VerticalLayoutContainer vp;

	private WorkspaceExplorerSelectDialog wselectDialog;
	private TextButton selectButton, selectButton2, cancelButton,
			downloadButton;
	private HtmlLayoutContainer templatesList;
	private TableItemSimple selectedTableItem = null;
	private ItemDescription itemDescriptionSelected;
	private TabularParameter tabularParameter;
	

	/**
	 * @param parameter
	 */
	public TabularFld(Parameter parameter) {
		super(parameter);
		Log.debug("TabularField");

		try {
			tabularParameter = (TabularParameter) parameter;

			SimpleContainer tabContainer = new SimpleContainer();
			vp = new VerticalLayoutContainer();
			init();

			createField(tabContainer);
			showNoSelectionField();
		} catch (Throwable e) {
			Log.error("TabularField: " + e.getLocalizedMessage());
			UtilsGXT3.alert("Error",
					"Error creating Tabular Field: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void createField(SimpleContainer tabContainer) {
		List<String> templates = tabularParameter.getTemplates();
		if (templates == null || templates.isEmpty()) {
			templatesList = new HtmlLayoutContainer("<p></p>");
			templatesList.addStyleName("workflow-parameters-description");
		} else {
			String list = "";
			boolean first = true;
			for (String template : templates) {
				list += (first ? "" : ", ") + Format.ellipse(template, 50);
				first = false;
			}

			templatesList = new HtmlLayoutContainer(
					"<p>Suitable Data Set Templates: <br>" + list + "</p>");
			templatesList.addStyleName("workflow-parameters-description");
		}

		tabContainer.add(vp, new MarginData(new Margins(0)));

		fieldContainer = new SimpleContainer();
		horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		HtmlLayoutContainer descr;

		if (tabularParameter.getDescription() == null) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'>"
					+ tabularParameter.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}

		horiz.add(tabContainer, new BoxLayoutData(new Margins()));
		horiz.add(descr, new BoxLayoutData(new Margins()));

		fieldContainer.add(horiz);
	}

	private void init() throws Exception {
		try {
			List<ItemType> selectableTypes = new ArrayList<ItemType>();
			selectableTypes.add(ItemType.EXTERNAL_FILE);
			List<ItemType> showableTypes = new ArrayList<ItemType>();
			showableTypes.addAll(Arrays.asList(ItemType.values()));

			try {
				/*
				 * "application/zip", "application/x-zip",
				 * "application/x-zip-compressed", "application/octet-stream",
				 * "application/x-compress", "application/x-compressed",
				 * "multipart/x-zip"
				 */
				// List<String> allowedMimeTypes =
				// Arrays.asList("text/csv","text/plain","text/plain; charset=ISO-8859-1");

				/**
				 * "zip"
				 */

				/*
				 * List<String> allowedFileExtensions = Arrays.asList("csv");
				 * 
				 * FilterCriteria filterCriteria = new
				 * FilterCriteria(allowedMimeTypes, allowedFileExtensions, new
				 * HashMap<String, String>());
				 */
				wselectDialog = new WorkspaceExplorerSelectDialog("Select CSV",
						false);
				// filterCriteria, selectableTypes);

				WorskpaceExplorerSelectNotificationListener handler = new WorskpaceExplorerSelectNotificationListener() {

					@Override
					public void onSelectedItem(Item item) {

						if (item.isFolder() || item.isRoot()) {
							UtilsGXT3.info("Attention", "Select a valid csv!");

						} else {
							retrieveTableInformation(item);

						}

					}

					@Override
					public void onFailed(Throwable throwable) {
						Log.error("Error in create project: "
								+ throwable.getLocalizedMessage());
						UtilsGXT3.alert("Error",
								throwable.getLocalizedMessage());
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

				wselectDialog
						.addWorkspaceExplorerSelectNotificationListener(handler);
				wselectDialog.setZIndex(XDOM.getTopZIndex());

			} catch (Throwable e) {
				Log.error("TabularField error using WorkspaceExplorerSelectDialog: "
						+ e.getLocalizedMessage());
				e.printStackTrace();
				throw new Exception(
						"TabularField error using WorkspaceExplorerSelectDialog: "
								+ e.getLocalizedMessage());
			}

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
			selectButton2.addSelectHandler(new SelectEvent.SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					wselectDialog.show();
				}
			});

			selectButton2.setIcon(DataMinerManager.resources.folderExplore());
			selectButton2.setToolTip("Select Another Data Set");

			cancelButton = new TextButton("");
			cancelButton.setIcon(DataMinerManager.resources.cancel());
			cancelButton.addSelectHandler(new SelectEvent.SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					selectedTableItem = null;
					showNoSelectionField();
					updateListeners(null);
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
		} catch (Throwable e) {
			Log.error("TabularField init: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}
	}

	private void retrieveTableInformation(Item item) {
		Log.debug("Retrieved: " + item);
		itemDescriptionSelected = new ItemDescription(
				item.getId(), item.getName(), item.getOwner(), item.getPath(),
				item.getType().name());

		
		DataMinerPortletServiceAsync.INSTANCE.retrieveTableInformation(item,
				new AsyncCallback<TableItemSimple>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.error("Error in retrieveTableInformation "
								+ caught.getMessage());
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session");

						} else {
							UtilsGXT3.alert("Error",
									"Error retrieving table information: "
											+ caught.getLocalizedMessage());
						}

					}

					@Override
					public void onSuccess(TableItemSimple result) {
						Log.debug("Retrieved: " + result);
						selectedTableItem = result;
						showFieldWithSelection();
						updateListeners(selectedTableItem);
					}
				});
	}
	
	private void downloadFile() {
		if (itemDescriptionSelected != null) {
			DataMinerPortletServiceAsync.INSTANCE.getPublicLink(
					itemDescriptionSelected, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							if (caught instanceof SessionExpiredServiceException) {
								EventBusProvider.INSTANCE
										.fireEvent(new SessionExpiredEvent());
							} else {
								Log.error("Error downloading table: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error",
										caught.getLocalizedMessage());
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
	private void showNoSelectionField() {
		vp.clear();
		vp.add(selectButton);
		vp.add(templatesList);
		vp.forceLayout();
		fieldContainer.forceLayout();
	}

	/**
	 * 
	 */
	private void showFieldWithSelection() {
		String tableName = selectedTableItem.getName();

		if (tableName == null || tableName.isEmpty()) {
			tableName = "NoName";
		}

		TextField tableDescription = new TextField();
		tableDescription.setValue(tableName);
		tableDescription.setReadOnly(true);

		HBoxLayoutContainer h = new HBoxLayoutContainer();
		h.add(tableDescription, new BoxLayoutData(new Margins()));
		h.add(selectButton2, new BoxLayoutData(new Margins()));
		h.add(downloadButton, new BoxLayoutData(new Margins()));
		h.add(cancelButton, new BoxLayoutData(new Margins()));
		vp.clear();
		vp.add(h);
		vp.add(templatesList);
		vp.forceLayout();
		fieldContainer.forceLayout();
	}

	/**
	 * 
	 */
	@Override
	public String getValue() {
		return (selectedTableItem == null) ? null : selectedTableItem.getId();
	}

	/**
	 * 
	 */
	@Override
	public Widget getWidget() {
		return fieldContainer;
	}

	/**
	 * 
	 */
	@Override
	public boolean isValid() {
		return (selectedTableItem != null);
	}

	@Override
	public HandlerRegistration addTabularFldChangeEventHandler(
			TabularFldChangeEventHandler handler) {
		return fieldContainer.addHandler(handler,
				TabularFldChangeEvent.getType());

	}

	private void updateListeners(TableItemSimple tableItemSimple) {
		TabularFldChangeEvent event = new TabularFldChangeEvent(tableItemSimple);
		fireEvent(event);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		fieldContainer.fireEvent(event);
	}
}
