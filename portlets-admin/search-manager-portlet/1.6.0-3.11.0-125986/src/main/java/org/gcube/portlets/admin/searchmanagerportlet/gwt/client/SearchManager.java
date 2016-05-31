package org.gcube.portlets.admin.searchmanagerportlet.gwt.client;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.interfaces.SearchManagerService;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.interfaces.SearchManagerServiceAsync;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets.ExceptionAlertWindow;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets.FieldCell;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets.FieldFormPanel;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets.InformationWidget;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets.LoadingWidget;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.FieldInfoBean;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget; 
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SearchManager implements EntryPoint {

	public static SearchManagerServiceAsync smService = (SearchManagerServiceAsync)GWT.create(SearchManagerService.class);
	private static ServiceDefTarget endpoint = (ServiceDefTarget) smService;

	private static TabPanel mainPanel = new TabPanel();
	private SplitLayoutPanel hp = new SplitLayoutPanel();
	private VerticalPanel fieldsPanel = new VerticalPanel();
	private VerticalPanel formPanel = new VerticalPanel();
	private FieldsListPanel fieldsTreePanel;
	private CollectionsFieldsPanel collectionsFieldsPanel;
	private FieldsAnnotationPanel fieldsAnnotationsPanel;

	protected static DialogBox loading = new LoadingWidget(false);
	private static DialogBox infoWidget; 
	
	protected static HTML noFieldAvailableMsg = new HTML("<span style=\"color: darkblue\">" +
			"There is no field selected. Select or create a new field", true);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "SearchManagerServlet");
		
		mainPanel.setWidth("100%");
		mainPanel.addStyleName("myTabPanel");
		fieldsPanel.setWidth("100%");
		formPanel.setWidth("100%");
		formPanel.setSpacing(10);
		formPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hp.setSize("1401px", "800px");

		
		
		collectionsFieldsPanel = new CollectionsFieldsPanel();
		fieldsAnnotationsPanel = new FieldsAnnotationPanel();
		
		mainPanel.add(hp, "Fields Management");
		mainPanel.add(collectionsFieldsPanel, "Collections' Fields");
		
		mainPanel.add(fieldsAnnotationsPanel, "Annotate Fields");
		mainPanel.selectTab(0);

		fieldsTreePanel = new FieldsListPanel();
		ScrollPanel scroller = new ScrollPanel(fieldsTreePanel);
		scroller.setHeight("700px");
		fieldsPanel.add(scroller);

		formPanel.add(noFieldAvailableMsg);

		fieldsTreePanel.setSelectionHandler(new SelectionHandler<TreeItem>() {

			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem selectedItem = event.getSelectedItem();
				Widget selectedWidget = selectedItem.getWidget();
				if (selectedWidget instanceof FieldCell) {
					FieldInfoBean fBean = ((FieldCell)selectedWidget).getField();
					if (fBean != null) {
						FieldFormPanel fieldPropPanel = new FieldFormPanel(fBean, fieldsTreePanel);
						formPanel.clear();
						formPanel.add(fieldPropPanel);
					}
				}
				else {
					formPanel.clear();
					formPanel.add(noFieldAvailableMsg);
				}
			}
		});

		fieldsTreePanel.setCreateBtnClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you want to create a new field? The unsaved changes will be lost");
				if (confirmed) {
					FieldInfoBean newFBean = new FieldInfoBean();
					FieldFormPanel fieldPropPanel = new FieldFormPanel(newFBean, fieldsTreePanel);
					formPanel.clear();
					formPanel.add(fieldPropPanel);
				}
			}
		});

		hp.addWest(fieldsPanel, 200);
		hp.add(formPanel);
		
		RootPanel.get("SMDiv").add(mainPanel);

		updateSize();

		/* Add a handler for the resizing of the window */
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler(){

			public void onResize(ResizeEvent event) {
				updateSize();
			}

		});
	}

	private void updateSize() {
		Log.trace("Updating panels' size");
		com.google.gwt.user.client.ui.Panel root =RootPanel.get("SMDiv");

		int leftBorder = root.getAbsoluteLeft();

		int rightScrollBar = 17;

		int rootWidth = com.google.gwt.user.client.Window.getClientWidth() - 2* leftBorder - rightScrollBar;
		int rootHeight = com.google.gwt.user.client.Window.getClientHeight() - 2 * com.google.gwt.user.client.Window.getScrollTop();
		hp.setSize(new Integer(rootWidth).toString()+"px", new Integer(rootHeight).toString()+"px");
		Log.trace("HP new size is " + hp.getOffsetWidth() + "x " + hp.getOffsetHeight());
	}

	public static void showLoading() {
		loading.setPopupPosition(RootPanel.get("SMDiv").getAbsoluteLeft() + RootPanel.get("SMDiv").getOffsetWidth()/2, RootPanel.get("SMDiv").getAbsoluteTop() + RootPanel.get("SMDiv").getOffsetHeight()/2);
		loading.setStyleName("");
		loading.show();
	}

	public static void hideLoading() {
		loading.hide();
	}
	
	public static void showInfoPopup(String msg) {
		infoWidget = new InformationWidget("", msg);
		infoWidget.setPopupPosition(RootPanel.get("SMDiv").getAbsoluteLeft() + RootPanel.get("SMDiv").getOffsetWidth()/2, RootPanel.get("SMDiv").getAbsoluteTop() + RootPanel.get("SMDiv").getOffsetHeight()/2);
		infoWidget.show();
	}
	
	public static void displayErrorWindow(String userMsg, Throwable caught) {
		ExceptionAlertWindow alertWindow = new ExceptionAlertWindow(userMsg, true);
		alertWindow.addDock(caught);
		int left = com.google.gwt.user.client.Window.getClientWidth()/2;
		int top = com.google.gwt.user.client.Window.getClientHeight()/2;
		alertWindow.setPopupPosition(left, top);
		alertWindow.show();
	}
}
