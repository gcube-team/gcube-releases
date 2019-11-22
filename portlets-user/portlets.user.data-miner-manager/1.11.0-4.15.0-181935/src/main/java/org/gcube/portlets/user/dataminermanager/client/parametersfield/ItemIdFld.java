/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ItemIdParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;
import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.client.util.UtilsGXT3;
import org.gcube.portlets.user.dataminermanager.client.workspace.DownloadWidget;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.allen_sauer.gwt.log.client.Log;
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
public class ItemIdFld extends AbstractFld {

	private VerticalLayoutContainer vp;

	// FileSelector fileSelector;
	private WorkspaceExplorerSelectDialog wselectDialog;
	private TextButton selectButton, selectButton2, cancelButton;
	private ItemDescription selectedItem = null;

	private ItemIdParameter itemIdParameter;

	private SimpleContainer fieldContainer;

	private HBoxLayoutContainer horiz;

	private TextButton downloadButton;

	/**
	 * @param parameter
	 *            parameter
	 */
	public ItemIdFld(Parameter parameter) {
		super(parameter);
		itemIdParameter = (ItemIdParameter) parameter;

		SimpleContainer tabContainer = new SimpleContainer();
		vp = new VerticalLayoutContainer();
		init();
		tabContainer.add(vp, new MarginData(new Margins(0)));

		fieldContainer = new SimpleContainer();
		horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		HtmlLayoutContainer descr;

		if (itemIdParameter.getDescription() == null || itemIdParameter.getDescription().isEmpty()) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");
		} else {
			descr = new HtmlLayoutContainer(
					"<p style='margin-left:5px !important;'>" + itemIdParameter.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");

		}

		horiz.add(tabContainer, new BoxLayoutData(new Margins()));
		horiz.add(descr, new BoxLayoutData(new Margins()));

		fieldContainer.add(horiz);
		showNoSelectionField();

	}

	private void init() {

		wselectDialog = new WorkspaceExplorerSelectDialog("Select Item", false);

		WorskpaceExplorerSelectNotificationListener handler = new WorskpaceExplorerSelectNotificationListener() {

			@Override
			public void onSelectedItem(Item item) {

				if (item == null) {
					UtilsGXT3.info("Attention", "Select a valid item!");

				} else {
					retrieveItemInformation(item);
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
				UtilsGXT3.info("Attention", "Select a valid item!");
			}
		};

		wselectDialog.addWorkspaceExplorerSelectNotificationListener(handler);
		wselectDialog.setZIndex(XDOM.getTopZIndex());

		selectButton = new TextButton("Select Item");
		selectButton.setIcon(DataMinerManager.resources.folderExplore());
		selectButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wselectDialog.show();

			}
		});
		selectButton.setToolTip("Select Item");

		selectButton2 = new TextButton("");
		selectButton2.setIcon(DataMinerManager.resources.folderExplore());
		selectButton2.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wselectDialog.show();
			}
		});
		selectButton2.setToolTip("Select Another Item");

		cancelButton = new TextButton("");
		cancelButton.setIcon(DataMinerManager.resources.cancel());
		cancelButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				selectedItem = null;
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

	}

	private void retrieveItemInformation(final Item item) {
		Log.debug("Retrieved: " + item);
		final ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
				item.getPath(), item.getType().name());
		selectedItem = itemDescription;
		Log.debug("SelectedItem: " + selectedItem);
		showFieldWithSelection();
	}

	private void downloadFile() {
		if (selectedItem != null) {
			DownloadWidget downloadWidget = new DownloadWidget();
			downloadWidget.download(selectedItem.getId());
		} else {
			UtilsGXT3.info("Attention", "Select a Item!");
		}

	}

	private void showNoSelectionField() {
		vp.clear();
		vp.add(selectButton);
		vp.forceLayout();
		fieldContainer.forceLayout();
	}

	private void showFieldWithSelection() {
		String fileName = selectedItem.getName();

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
		return (selectedItem != null);
	}

	@Override
	public Widget getWidget() {
		return fieldContainer;
	}

	@Override
	public String getValue() {
		return (selectedItem == null) ? null : selectedItem.getId();
	}

}
