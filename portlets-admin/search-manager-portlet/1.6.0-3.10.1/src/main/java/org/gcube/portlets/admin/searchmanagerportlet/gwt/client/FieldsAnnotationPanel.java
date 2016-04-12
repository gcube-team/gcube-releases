package org.gcube.portlets.admin.searchmanagerportlet.gwt.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets.AnnotationDialogBox;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets.FieldCell;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets.KeywordsEditDialogBox;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.FieldInfoBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Tree.Resources;

public class FieldsAnnotationPanel extends Composite{

	private SplitLayoutPanel hp = new SplitLayoutPanel();
	private VerticalPanel treePanel = new VerticalPanel();
	private HorizontalPanel annotationsPanel = new HorizontalPanel();
	private HorizontalPanel toolbar = new HorizontalPanel();
	private HTML splitLine = new HTML("<div style=\"width:100%; height:2px;  background-color:#D0E4F6\"></div>"); //#D0E4F6, #92C1F0
	private Tree fieldsTree = new Tree((Resources) GWT.create(FieldsImageResources.class), true);
	private TreeItem treeRoot = new TreeItem("Fields");
	private AnnotationsTable annotationsTable = null;

	private HTML noFieldsAvailableMsg = new HTML("<span style=\"color: darkred\">No fields are available.</span>", true);
	private HTML noAnnsAvailableMsg = new HTML("<span style=\"color: darkred\">Semantic keywords are not available.</span>", true);
	private HTML bridgingInProgressMsg = new HTML("<span style=\"color: darkred\">The fields are being synchronized with the server. Please wait for a while and click on the refresh button to retrieve the fields</span>", true);

	private Button refreshBtn = new Button();
	private Button addAnnotationBtn = new Button();
	private Button removeAnnotationBtn = new Button();


	public FieldsAnnotationPanel() {

		treePanel.setWidth("100%");
		treePanel.setSpacing(5);

		annotationsPanel.setWidth("100%");
		annotationsPanel.setSpacing(20);
		annotationsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		annotationsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		addAnnotationBtn.setTitle("Creates a new annotation");
		addAnnotationBtn.setStyleName("createKeywordButton");
		addAnnotationBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				AsyncCallback<ArrayList<String>> getGroupsCallback = new AsyncCallback<ArrayList<String>>() {

					public void onFailure(Throwable caught) {

					}

					public void onSuccess(ArrayList<String> result) {
						if (result != null && result.size() > 0) {
							AnnotationDialogBox db = new AnnotationDialogBox(FieldsAnnotationPanel.this, result);
							db.show();
							db.center();
						}
						else
							SearchManager.showInfoPopup("Failed to retrieve the available groups. Cannot create a new annotation. Please try again");

					}
				};SearchManager.smService.getGroups(getGroupsCallback);
			}
		});


		removeAnnotationBtn.setTitle("Removes annotations from group");
		removeAnnotationBtn.setStyleName("deleteKeywordButton");
		removeAnnotationBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {

				KeywordsEditDialogBox db = new KeywordsEditDialogBox(FieldsAnnotationPanel.this);
				db.show();
				db.center();

			}
		});

		refreshBtn.setTitle("Refresh the fields' information");
		refreshBtn.setStyleName("refreshButton");
		fieldsTree.setAnimationEnabled(true);

		toolbar.setSpacing(8);
		toolbar.add(refreshBtn);
		toolbar.add(removeAnnotationBtn);
		toolbar.add(addAnnotationBtn);
		// Get the available fields and create the Tree or an error message if something failed
		refreshTreeInfo();
		loadAnnotations();

		ScrollPanel scroller = new ScrollPanel();
		scroller.add(treePanel);

		hp.setSize("1400px", "800px");
		//hp.setWidth("100%");
		hp.addWest(scroller, 400);
		hp.add(annotationsPanel);

		//initWidget(mainPanel);
		initWidget(hp);

		refreshBtn.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you want to refresh the fields' list? The unsaved changes will be lost");
				if (confirmed) {
					refreshTreeInfo();
					loadAnnotations();
				}

			}
		});

		fieldsTree.addSelectionHandler(new SelectionHandler<TreeItem>() {

			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem selectedItem = event.getSelectedItem();
				Widget selectedWidget = selectedItem.getWidget();
				if (selectedWidget instanceof FieldCell) {
					final FieldInfoBean fBean = ((FieldCell)selectedWidget).getField();
					if (fBean != null) {
						AsyncCallback<ArrayList<String>> getFieldAnnotations = new AsyncCallback<ArrayList<String>>() {

							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub

							}

							public void onSuccess(ArrayList<String> result) {
								annotationsTable.selectAnnotationsOfField(fBean.getID(), result);
							}
						};SearchManager.smService.getFieldAnnotations(fBean.getID(), getFieldAnnotations);
					}
				}
				else {

				}
			}
		});

		updateSize();

		/* Add a handler for the resizing of the window */
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler(){

			public void onResize(ResizeEvent event) {
				updateSize();
			}

		});
	}

	private void updateSize() {
		com.google.gwt.user.client.ui.Panel root =RootPanel.get("SMDiv");

		int leftBorder = root.getAbsoluteLeft();

		int rightScrollBar = 17;

		int rootWidth = com.google.gwt.user.client.Window.getClientWidth() - 2* leftBorder - rightScrollBar;
		int rootHeight = com.google.gwt.user.client.Window.getClientHeight() - 2 * com.google.gwt.user.client.Window.getScrollTop();
		hp.setSize(new Integer(rootWidth).toString()+"px", new Integer(rootHeight).toString()+"px");
	}

	public void loadAnnotations() {
		annotationsPanel.clear();
		AsyncCallback<ArrayList<String>> getAnnotationsCallback = new AsyncCallback<ArrayList<String>>() {

			public void onFailure(Throwable caught) {


			}

			public void onSuccess(ArrayList<String> result) {
				if (result != null && !result.isEmpty()) {
					annotationsTable = new AnnotationsTable(null, result);
					annotationsPanel.add(annotationsTable);
				}
				else {
					annotationsPanel.add(noAnnsAvailableMsg);
					annotationsPanel.setCellHorizontalAlignment(noAnnsAvailableMsg, HasHorizontalAlignment.ALIGN_CENTER);
				}
			}
		};SearchManager.smService.getSemanticAnnotations(getAnnotationsCallback);
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
				treePanel.clear();
				treePanel.add(toolbar);
				treePanel.add(splitLine);
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
					treePanel.add(fieldsTree);
					// Display the tree open. SetState after items are added
					treeRoot.setState(true);
				}
				else {
					AsyncCallback<Boolean> getBridgingStatusCallback = new AsyncCallback<Boolean>() {

						public void onFailure(Throwable caught) {
							treePanel.add(noFieldsAvailableMsg);		
						}

						public void onSuccess(Boolean result) {
							if (result != null && result.equals(false))
								treePanel.add(bridgingInProgressMsg);
							else
								treePanel.add(noFieldsAvailableMsg);	
						}
					};SearchManager.smService.getBridgingStatusFromSession(getBridgingStatusCallback);

				}

				SearchManager.hideLoading();
			}
		};SearchManager.smService.getFieldsInfo(false, retrieveFieldsCallback);
		SearchManager.showLoading();
	}

	public void addItemToTree(FieldInfoBean field, boolean setSelected) {
		final FieldCell fc = new FieldCell(field, true);
		TreeItem item = new TreeItem(fc);
		treeRoot.addItem(item);

		if (setSelected)
			fieldsTree.setSelectedItem(item);

	}

}
