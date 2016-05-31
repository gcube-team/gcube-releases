package org.gcube.portlets.user.results.client.dialogBox;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.user.results.client.TreeImageResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Tree.Resources;

/**
 * Accepts a list of URLs per type and displays them in a tree hierarchy
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class URLContentViewerPopup extends GCubeDialog {

	private Tree serversTree = new Tree((Resources) GWT.create(TreeImageResources.class));
	private TreeItem treeRoot = new TreeItem();
	private VerticalPanel treePanel = new VerticalPanel();
	
	ScrollPanel treePanelScroller = new ScrollPanel();
	
	TreeMap<String, List<String>> urls;
	private VerticalPanel vPanel = new VerticalPanel();

	public URLContentViewerPopup(TreeMap<String, List<String>> urls, boolean autoShow) {
		new URLContentViewerPopup(urls, -1, -1, autoShow);
	}


	public URLContentViewerPopup(TreeMap<String, List<String>> urls, int width, int height, boolean autoShow) {
		this.urls = urls;
		serversTree.setAnimationEnabled(true);
		treePanel.setSpacing(5);
		treePanelScroller.add(treePanel);
		treePanel.add(serversTree);
		if (width != -1 && height != -1)
			this.setPixelSize(width, height);
		this.setText("Available Content");
		this.setModal(true);
		createContentTree();
		Button close = new Button("Close");
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();		
			}    	  
		});
		vPanel.add(treePanelScroller);
		vPanel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
		HorizontalPanel hozPanel = new HorizontalPanel();
		hozPanel.setSpacing(5);
		hozPanel.add(close);
		hozPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		hozPanel.setWidth("100%");	      
		vPanel.add(hozPanel);

		this.add(vPanel);
		if (autoShow) {
			this.show();
			this.center();
		}
	}

	/**
	 * Creates a tree to display the available URLs per type in a descendant order
	 */
	private void createContentTree() {
		serversTree.addItem(treeRoot);
		Iterator<Entry<String, List<String>>> it = urls.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, List<String>> item = it.next();
			String type = item.getKey();
			List<String> contentURLs = urls.get(type);
			TreeItem typeNode = treeRoot.insertItem(0, type);
			if (contentURLs != null) {
				for (int i=0; i<contentURLs.size(); i++) {
					String htmlText = "<a href=\"" + contentURLs.get(i) + "\" target=\"_blank\">" + contentURLs.get(i) + "</a>";
					typeNode.addItem(new HTML(htmlText));
				}
			}
			typeNode.setState(true);
		}
		treeRoot.setState(true);
	}
}

