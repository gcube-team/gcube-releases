/**
 * 
 */
package org.gcube.portlets.admin.searchmanagerportlet.gwt.client;

import java.util.List;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets.FieldCell;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.FieldInfoBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Tree.Resources;

/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class FieldsListPanel extends Composite {

	private HTML noFieldsAvailableMsg = new HTML("<span style=\"color: darkred\">No fields are available.</span>", true);
	private HTML bridgingInProgressMsg = new HTML("<span style=\"color: darkred\">The fields are being synchronized with the server. Please wait for a while and click on the refresh button to retrieve the fields</span>", true);

	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel toolbar = new HorizontalPanel();
	private HTML splitLine = new HTML("<div style=\"width:100%; height:2px;  background-color:#D0E4F6\"></div>"); //#D0E4F6, #92C1F0
	private Tree fieldsTree = new Tree((Resources) GWT.create(FieldsImageResources.class));
	// Create the root Item of the tree
	private TreeItem treeRoot = new TreeItem();
	private Button createFieldBtn = new Button();
	private Button refreshBtn = new Button();
	private Button resetBtn = new Button();

	public FieldsListPanel() {

		mainPanel.setWidth("100%");
		mainPanel.setSpacing(5);
		treeRoot.setHTML("Fields");
		createFieldBtn.setTitle("Create a new field");
		refreshBtn.setTitle("Refresh the fields' information");
		resetBtn.setTitle("Reset Registry information");
		// initialize the tree's properties
		fieldsTree.setAnimationEnabled(true);

		createFieldBtn.setStyleName("createFieldButton");
		refreshBtn.setStyleName("refreshButton");
		resetBtn.setStyleName("resetButton");

		toolbar.setWidth("100%");
		toolbar.setSpacing(2);
		toolbar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		toolbar.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		toolbar.add(createFieldBtn);
		toolbar.add(refreshBtn);
		toolbar.add(resetBtn);
		toolbar.setCellHorizontalAlignment(resetBtn, HasHorizontalAlignment.ALIGN_RIGHT);
		toolbar.setCellHorizontalAlignment(refreshBtn, HasHorizontalAlignment.ALIGN_RIGHT);
		// Get the available fields and create the Tree or an error message if something failed
		refreshTreeInfo();

		initWidget(mainPanel);

		refreshBtn.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you want to refresh the Field's list? The unsaved changes will be lost");
				if (confirmed)
					refreshTreeInfo();
			}
		});
		
		resetBtn.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you want to reset the available information?");
				if (confirmed)
					resetRegistry();
			}
		});
	}

	public void refreshTreeInfo() {
		AsyncCallback<List<FieldInfoBean>> retrieveFieldsCallback = new AsyncCallback<List<FieldInfoBean>>() {

			public void onFailure(Throwable caught) {
				SearchManager.hideLoading();
				SearchManager.displayErrorWindow("Failed to retrieve the available fields. Please click on the refresh button to try again.", caught);
			}

			public void onSuccess(List<FieldInfoBean> result) {
				treeRoot.removeItems();
				fieldsTree.removeItems();
				mainPanel.clear();
				mainPanel.add(toolbar);
				mainPanel.add(splitLine);
				if (result != null && result.size() > 0) {
					fieldsTree.addItem(treeRoot);
					// Add the available fields to the tree
					int i = 1;
					boolean last = false;
					for (FieldInfoBean f : result) {
						if (i == 1)
							last = true;
						addItemToTree(f, last);
						i++;
					}
					mainPanel.add(fieldsTree);
					// Display the tree open. SetState after items are added
					treeRoot.setState(true);
				}
				else {
					AsyncCallback<Boolean> getBridgingStatusCallback = new AsyncCallback<Boolean>() {

						public void onFailure(Throwable caught) {
							mainPanel.add(noFieldsAvailableMsg);		
						}

						public void onSuccess(Boolean result) {
							if (result != null && result.equals(false))
								mainPanel.add(bridgingInProgressMsg);
							else
								mainPanel.add(noFieldsAvailableMsg);	
						}
					};SearchManager.smService.getBridgingStatusFromSession(getBridgingStatusCallback);
					
				}
				
				SearchManager.hideLoading();
			}
		};SearchManager.smService.getFieldsInfo(true, retrieveFieldsCallback);
		SearchManager.showLoading();
	}
	
	private void resetRegistry() {
		AsyncCallback<Boolean> resetRRCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				
				
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					refreshTreeInfo();
				}
				
			}
		};SearchManager.smService.resetRegistry(resetRRCallback);
	}

	public void setSelectionHandler(SelectionHandler<TreeItem> handler) {
		fieldsTree.addSelectionHandler(handler);
	}

	public void setCreateBtnClickHandler(ClickHandler handler) {
		createFieldBtn.addClickHandler(handler);
	}

	public void addItemToTree(FieldInfoBean field, boolean setSelected) {
		final FieldCell fc = new FieldCell(field, false);
		TreeItem item = new TreeItem(fc);
		treeRoot.addItem(item);

		if (setSelected)
			fieldsTree.setSelectedItem(item);

		fc.setDeleteBtnClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you really want to delete the selected Field? This action cannot be undone.");
				if (confirmed) {
					AsyncCallback<Void> deleteFieldCallback = new AsyncCallback<Void>() {

						public void onFailure(Throwable caught) {
							SearchManager.hideLoading();
							SearchManager.showInfoPopup("Failed to delete the selected field");
						}

						public void onSuccess(Void result) {
							SearchManager.hideLoading();
							// TODO is this assumption correct?
							treeRoot.removeItem(fieldsTree.getSelectedItem());
							fieldsTree.setSelectedItem(treeRoot, true);
						}
					};SearchManager.smService.deleteFieldInfo(fc.getField().getID(), deleteFieldCallback);
					SearchManager.showLoading();
				}

			}
		});
	}

	public void updateExistingFieldItem(FieldInfoBean updatedField) {
		TreeItem selectedItem = fieldsTree.getSelectedItem();
		FieldCell cell = (FieldCell)selectedItem.getWidget();
		cell.updateField(updatedField, false);
	}
}
