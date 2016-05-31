package org.gcube.portlets.user.collectionsnavigatorportlet.client;

import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionInfo;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Tree.Resources;

public class CollectionsNavigatorPortletG extends Composite implements EntryPoint, ResizeHandler, OpenHandler<TreeItem>, CloseHandler<TreeItem> {	

	private Tree tree = new Tree((Resources) GWT.create(CollectionsImageBundle.class));
	private TreeItem treeRoot = null;

	public static CollectionsNavigatorServiceAsync collectionsService = (CollectionsNavigatorServiceAsync) GWT.create(CollectionsNavigatorService.class);
	private static ServiceDefTarget endpoint = (ServiceDefTarget) collectionsService;

	private HorizontalPanel titlePanel = new HorizontalPanel();	
	private HorizontalPanel searchOverCollectionsHorPanel = new HorizontalPanel();
	public static  VerticalPanel container = new VerticalPanel();
	private ScrollPanel scroller = new ScrollPanel();
	private ScrollPanel horizontalScroller = new ScrollPanel();
	private GCubePanel mainLayout = new GCubePanel( "Collections navigator", "https://gcube.wiki.gcube-system.org/gcube/index.php/Common_Functionality#Search");

	private RootPanel rootPanel = RootPanel.get("collectionsnavigatorExternal");
	private TreeListItems listItem = null;
	public static ListBox schemata = new ListBox();
	public static Button descSchemaButton = new Button();

	private static TextBox searchColsTextBox = new TextBox();
	private static Button searchColsButton = new Button();
	private static Button refreshButton = new Button();
	private static LoadingPopUp refreshLoading = new LoadingPopUp("Refreshing collections' list. Please wait....", false);
	private static LoadingPopUp loading = new LoadingPopUp("Loading. Please wait....", false);
	
	private static final String ROOT_TITLE = "All Collections";

	public void onModuleLoad() {
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()+"CollectionsNavigatorServlet");
		StackPanel itemContent = new StackPanel();

		// Store information to a hidden field so that it can be used later depending on user's selection
		Hidden hidden = new Hidden();
		hidden.setName("hidden_collections[]");
		hidden.setValue("all_collections_1");

		// create the root node. Represents all collections
		final MyCheckBox myCheck = new MyCheckBox(ROOT_TITLE, null);
		MyCompositeCheckBox myCompCheck = new MyCompositeCheckBox(myCheck, "", true);
		myCheck.setName("collection_name_all_collections");
		
		AsyncCallback<Boolean> isAllBoxSelectedcallback = new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught)
			{
			}

			public void onSuccess(Boolean result)
			{
				myCheck.setValue(result);
			}
		};collectionsService.isAllCollectionsBoxSelected(isAllBoxSelectedcallback);

		itemContent.add(myCompCheck);
		itemContent.add(hidden);

		//animation for tree
		tree.setAnimationEnabled(true);
		treeRoot = new TreeItem(itemContent);
		myCompCheck.setTreeItem(treeRoot);
		tree.addItem(treeRoot);
		listItem = new TreeListItems(treeRoot, null);
		//tree.addTreeListener(this);
		//tree.addSelectionHandler(this);

		// Panel that holds all the widgets for the search in collections 
		searchOverCollectionsHorPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		HTML searchForColsHtml = new HTML("&nbsp;&nbsp;" + CollectionsConstants.searchForCollections + "&nbsp;&nbsp;");
		searchOverCollectionsHorPanel.add(searchForColsHtml);
		searchOverCollectionsHorPanel.add(searchColsTextBox);
		searchOverCollectionsHorPanel.add(new HTML("&nbsp;"));
		searchOverCollectionsHorPanel.add(searchColsButton);
		searchColsButton.setStyleName("go-button");
		searchOverCollectionsHorPanel.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
		searchOverCollectionsHorPanel.add(refreshButton);


		searchColsButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if (searchColsTextBox.getText().trim().length() == 0 || searchColsTextBox.getText().trim().equals("")) {
					Window.alert("No keywords to search for. Please type keywords and click Go");
				}
				else {
					// note that in general, events can have sources that are not Widgets.
					Widget sender = (Widget) event.getSource();
					final int left = sender.getAbsoluteLeft() + sender.getOffsetWidth() + 5;
					final int top =  sender.getAbsoluteTop() + sender.getOffsetHeight();

					AsyncCallback<CollectionInfo[]> callback = new AsyncCallback<CollectionInfo[]>() {
						public void onFailure(Throwable caught)
						{
							Window.alert("Failed to search the collections. Please try again");
						}

						public void onSuccess(CollectionInfo[] result)
						{	
							if (result == null)
								Window.alert("No collections found");
							else if (result.length > 0) {

								CollectionPopupPanel collectionsPopup = new CollectionPopupPanel(result, CollectionsNavigatorPortletG.this);
								collectionsPopup.show();
								collectionsPopup.setPopupPosition(left, top);
							}
							else
								Window.alert("No collections found");
						}
						};
						/* Update the collections and the cache file */
						CollectionsNavigatorPortletG.collectionsService.searchForCollections(searchColsTextBox.getText(), callback);
				}
			}
		});



		refreshButton.setStyleName("refresh-button");
		refreshButton.setTitle(CollectionsConstants.refreshCollections);



		// Number of widgets in container = 6 only when collections are also available else it will be 3
		container.add(titlePanel);
		container.add(new HTML("<br>", true));

		container.add(searchOverCollectionsHorPanel);
		container.add(new HTML("<br>", true));
		container.add(tree);
		container.add(new HTML("<br>", true));

		refreshButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) {
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
						{
					public void onFailure(Throwable caught)
					{
					}

					public void onSuccess(Void result)
					{	
						Window.open(getURL(), "_self", "");
					}
						};
						/* Update the collections and the cache file */
						CollectionsNavigatorPortletG.collectionsService.refreshInformation(callback);
						int left = RootPanel.get("collectionsnavigatorExternal").getAbsoluteLeft() + RootPanel.get("collectionsnavigatorExternal").getOffsetWidth()/2;
						int top = RootPanel.get("collectionsnavigatorExternal").getAbsoluteTop() + RootPanel.get("collectionsnavigatorExternal").getOffsetHeight()/2;
						container.clear();
						refreshLoading.setStyleName("unknown");
						refreshLoading.setPopupPosition(left, top);
						refreshLoading.show();

			}
		}
				);

		scroller.add(container);
		container.setWidth("100%");
		scroller.setWidth("100%");
		horizontalScroller.setSize("100%", "590px");
		horizontalScroller.add(scroller);
		mainLayout.setWidth("100%");
		mainLayout.add(horizontalScroller);
		rootPanel.add(mainLayout);
	}


	public void onTreeItemSelected(TreeItem item) {
		
		//TODO find the parent and make it set as an open node
		 
	}


	public static native String getURL()/*-{
	 return $wnd.location;
	 }-*/;


	/**
	 * Select or unselect a given collection in the tree.
	 * @param colName the name of the collection to select/unselect
	 * @param select true to select the collection, or false to unselect
	 */
	public void selectCollection(String colName, boolean select) {
		((MyCompositeCheckBox) ((StackPanel) treeRoot.getWidget()).getWidget(0)).selectCollection(colName, select);
	}

	protected static void showLoading() {
		loading.setStyleName("unknown");
		int left = RootPanel.get("collectionsnavigatorExternal").getAbsoluteLeft() + RootPanel.get("collectionsnavigatorExternal").getOffsetWidth()/2;
		int top = RootPanel.get("collectionsnavigatorExternal").getAbsoluteTop() + RootPanel.get("collectionsnavigatorExternal").getOffsetHeight()/2;
		loading.setPopupPosition(left, top);
		loading.show();
	}

	protected static void hideLoading() {
		loading.hide();
	}


	/**
	 * This class extends the dialog box of GWT and shows the loading image.
	 * This pop-up is displayed when the search button are clicked
	 * 
	 * @author Panagiota Koltsida, NKUA
	 *
	 */
	private  static class LoadingPopUp extends DialogBox implements ClickHandler {
		public LoadingPopUp(String loadingMsg, boolean autoHide) {
			super(autoHide);		
			Image loadingIcon = new Image(GWT.getModuleBaseURL() + "../images/loading.gif");
			DockPanel dock = new DockPanel();
			dock.setSpacing(4);
			dock.add(loadingIcon, DockPanel.CENTER);
			dock.add(new HTML(loadingMsg), DockPanel.WEST);
			setWidget(dock);
		}

		public void onClick(ClickEvent event) {
			hide();
		}
	}


	public void onResize(ResizeEvent event) {
		int width = RootPanel.get("collectionsnavigatorExternal").getOffsetWidth();
		int height = event.getHeight();
		scroller.setPixelSize(width, height);
	}


	public void onSelection(SelectionEvent<TreeItem> event) {

	}


	public void onClose(CloseEvent<TreeItem> event) {
		String value = DOM.getElementAttribute(((StackPanel) event.getSource()).getWidget(1).getElement(), "value");
		value = value.substring(0, value.length()-2) + "_0";
		String changedColID = ((MyCompositeCheckBox) ((StackPanel) event.getSource()).getWidget(0)).getCheckBox().getName();
		Window.alert("open: before substring " + changedColID);
		changedColID = changedColID.substring("collection_name_".length());
		Window.alert("open: after substring " + changedColID);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onFailure(Throwable caught)
			{
			}

			public void onSuccess(Void result)
			{
			}
		};CollectionsNavigatorPortletG.collectionsService.setCollectionOpenStatus(changedColID, false, callback);
	}


	public void onOpen(OpenEvent<TreeItem> event) {
		String value = DOM.getElementAttribute(((StackPanel) event.getSource()).getWidget(1).getElement(), "value");
		value = value.substring(0, value.length()-2) + "_1";
		DOM.setElementProperty(((StackPanel) event.getSource()).getWidget(1).getElement(),"value",value);
		String changedColID = ((MyCompositeCheckBox) ((StackPanel) event.getSource()).getWidget(0)).getCheckBox().getName();
		Window.alert("open: before substring " + changedColID);
		changedColID = changedColID.substring("collection_name_".length());
		Window.alert("open: after substring " + changedColID);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onFailure(Throwable caught)
			{
			}

			public void onSuccess(Void result)
			{
			}
		};
		CollectionsNavigatorPortletG.collectionsService.setCollectionOpenStatus(changedColID, true, callback);
	}

}
