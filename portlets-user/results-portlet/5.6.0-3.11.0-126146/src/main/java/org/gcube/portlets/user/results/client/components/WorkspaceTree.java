package org.gcube.portlets.user.results.client.components;

import org.gcube.portlets.user.results.client.constants.ImageConstants;
import org.gcube.portlets.user.results.client.constants.StringConstants;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.panels.LeftPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkspaceTree extends Composite implements TreeListener {

	private CellPanel mainLayout = new VerticalPanel();

	private CellPanel headerPanel = new HorizontalPanel();
	private HorizontalPanel treePanel = new HorizontalPanel();
	private String defaultBaskedId;

	private PopupPanel myPanel;
	private Controller control;

	public WorkspaceTree(Controller controller, final PopupPanel myPanel, TreeNode folder, String defaultBaskedId) {
		super();
		control = controller;
		this.defaultBaskedId = defaultBaskedId;
		this.myPanel = myPanel;
		// Create a static tree and a container to hold it


		int width = LeftPanel.LEFTPANEL_WIDTH - 10;


		Image closeButton = new Image(ImageConstants.CLOSE);
		HTML text = new HTML("Workspace Tree:");

		headerPanel.add(text);
		headerPanel.add(closeButton);
		headerPanel.setPixelSize(LeftPanel.LEFTPANEL_WIDTH, 15);
		headerPanel.setCellHorizontalAlignment(closeButton, HasAlignment.ALIGN_RIGHT);
		headerPanel.setCellHorizontalAlignment(text, HasAlignment.ALIGN_LEFT);

		ScrollPanel staticTreeWrapper = new ScrollPanel();
		Tree staticTree = createStaticTree(folder);
		staticTree.setAnimationEnabled(true);
		staticTreeWrapper.add(staticTree);
		staticTreeWrapper.setPixelSize(width, LeftPanel.LEFTPANEL_WIDTH);


		treePanel.add(staticTreeWrapper);

		mainLayout.add(headerPanel);
		mainLayout.add(treePanel);

		//mainLayout.setCellHorizontalAlignment(headerPanel, HasAlignment.ALIGN_RIGHT);

		initWidget(mainLayout);


		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				myPanel.hide();					
			}
		});

		closeButton.setStyleName("selectable");




	}


	/**
	 * Create a static tree with some data in it.
	 * 
	 * @return the new tree
	 */
	private Tree createStaticTree(TreeNode folder) {
		Tree staticTree = new Tree();
		staticTree.addTreeListener(this);
		if (folder != null)  {
			staticTree.addItem(listFolders(folder));

		}
		else 
			staticTree.addItem(new WPTreeItem("root", "00", StringConstants.TYPE_HOME));

		// Return the tree
		return staticTree;
	}

	/**
	 * 
	 * @param folder
	 * @return
	 */
	protected TreeItem listFolders(TreeNode folder) {
		WPTreeItem tmp = null;
		if (folder.isRoot())
			tmp = new WPTreeItem(folder.getLabel(), folder.getId(), StringConstants.TYPE_HOME);

		else {
			if (folder.getType() == StringConstants.TYPE_BASKET) {
				tmp = new WPTreeItem(folder.getLabel(), folder.getId(), StringConstants.TYPE_BASKET);
				if (folder.getId().equals(defaultBaskedId)) {
					openDefaultBasket(tmp, folder.getPath());
				}
			}
			else 
				tmp = new WPTreeItem(folder.getLabel(), folder.getId(), StringConstants.TYPE_FOLDER);
		}

		TreeItem item = new TreeItem(tmp);
//		for (TreeNode child : folder.getChildren()) {
//			item.addItem(listFolders(child));			
//		}

		return item;
	}

	/**
	 * 
	 * @param selectedBasket
	 * @param path
	 */	
	private void openDefaultBasket(WPTreeItem selectedBasket, String path) {
		//String currItemName = selectedBasket.getItemText().getText();
		control.setCurrBasketPath(path);
		String name = selectedBasket.getLabel();
		control.setCurrBasketName(name);
		String basketid = selectedBasket.getId();
		control.openFolder(basketid);	
	}

	/**
	 * 
	 */
	public void onTreeItemSelected(TreeItem item) {
		WPTreeItem selectedBasket = (WPTreeItem) item.getWidget();
		//if is a leaf (a basket) or the home
		WPTreeItem wpclicked = (WPTreeItem) item.getWidget();
		if (item.getChildCount() == 0 && 
				! (wpclicked.getType() == StringConstants.TYPE_HOME || wpclicked.getType() == StringConstants.TYPE_FOLDER) ) {

			//String currItemName = wpclicked.getItemText().getText();

			String path = "";

			while (item.getParentItem() != null) {
				WPTreeItem clicked = (WPTreeItem) item.getWidget();
				path = clicked.getItemText().getText() + "/" + path;
				item = item.getParentItem();
			}
			WPTreeItem clicked = (WPTreeItem) item.getWidget();
			path = "/" + clicked.getItemText().getText() + "/" + path;
			item = item.getParentItem();

			control.setCurrBasketPath(path);

			String name = selectedBasket.getLabel();
			control.setCurrBasketName(name);

			String basketid = selectedBasket.getId();
			control.openFolder(basketid);

			myPanel.hide();
		}
		else {
			final PopupPanel simplePopupPanel = new PopupPanel(true);
			VerticalPanel vpanel = new VerticalPanel();
			vpanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			vpanel.add(new Label("Only baskets are selectable"));
			Button closeButton = new Button("Close");
			closeButton.addClickListener(new ClickListener() {
				public void onClick(Widget sender) { simplePopupPanel.hide(); }				
			});

			vpanel.add(closeButton);
			simplePopupPanel.setTitle("");
			simplePopupPanel.setWidget(vpanel);

			simplePopupPanel.setPopupPosition(item.getAbsoluteLeft(), item.getAbsoluteTop());
			simplePopupPanel.show();
		}

	}


	public void onTreeItemStateChanged(TreeItem item) {
		// TODO Auto-generated method stub

	}
}
