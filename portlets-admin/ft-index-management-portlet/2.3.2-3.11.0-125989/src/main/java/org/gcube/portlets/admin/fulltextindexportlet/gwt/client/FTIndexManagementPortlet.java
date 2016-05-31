package org.gcube.portlets.admin.fulltextindexportlet.gwt.client;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementService;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementServiceAsync;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.indexmanagement.index.AddIndexPanel;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.indexmanagement.index.IndexDetail;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.intextypemanagement.IndexTypeEditorPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point class for the FullTextIndexPortlet application.
 */
public class FTIndexManagementPortlet implements EntryPoint, WindowResizeListener, TreeListener {

	/** An GWT RPC interface to the ManagementService */
	public static ManagementServiceAsync mgmtService = (ManagementServiceAsync) GWT.create(ManagementService.class);

	/**
	 * A panel to hold the index information and manipulation panels on the
	 * right side of the screen
	 */
	private VerticalPanel indexPanel;

	/** A panel to hold the panels used to administer a chosen index */
	private IndexDetail indexDetail;

	/** A panel used to add an index */
	private AddIndexPanel addIndexPanel;

	private TabPanel mainTabPanel = new TabPanel();

	private IndexTypeEditorPanel idxTypePanel;

	/**
	 * {@inheritDoc}
	 */
	public void onModuleLoad() {
		ServiceDefTarget mgmtEndpoint = (ServiceDefTarget) mgmtService;
		mgmtEndpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "/MgmtService");
		
		try {
			IndexExplorer explorer = new IndexExplorer(this);

			indexPanel = new VerticalPanel();
			indexDetail = new IndexDetail();
			indexDetail.setVisible(false);

			addIndexPanel = new AddIndexPanel();
			addIndexPanel.setVisible(false);
			addIndexPanel.addItemAddedListener(explorer);

			indexPanel.add(indexDetail);
			indexPanel.add(addIndexPanel);
			indexDetail.addItemRemovedListener(explorer);

			idxTypePanel = new IndexTypeEditorPanel();
			HorizontalSplitPanel outer = new HorizontalSplitPanel();
			outer.setSize("99%", "450px");
			outer.setLeftWidget(explorer);
			outer.setRightWidget(indexPanel);

			NodeList<Element> nl = mainTabPanel.getElement().getElementsByTagName("div");
			for (int i=0; i<nl.getLength(); i++) {
				Element n = nl.getItem(i);
				if (n.getClassName().contains("gwt-TabPanelBottom"))
					n.getStyle().setPropertyPx("height", 467);
			}

			mainTabPanel.setWidth("100%");
			mainTabPanel.setHeight("500px");
			mainTabPanel.add(outer, "Index manager");
			mainTabPanel.add(idxTypePanel, "IndexType manager");
			mainTabPanel.selectTab(0);

			RootPanel.get("FullTextIndexManager").add(mainTabPanel);
			indexPanel.setWidth("100%");
			indexDetail.setWidth("100%");
		
			Window.addWindowResizeListener(this);

			DeferredCommand.addCommand(new Command() {
				public void execute() {
					onWindowResized(Window.getClientWidth(), Window
							.getClientHeight());
				}
			});
		} catch (Exception e) {
			Window.alert(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void onWindowResized(int width, int height) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void onTreeItemSelected(TreeItem item) {
		if (item instanceof IndexExplorer.IndexTreeItem) {
			indexDetail.setVisible(true);
			addIndexPanel.setVisible(false);
			indexDetail.updateDetail(((IndexExplorer.IndexTreeItem) item).getIndexID(), ((IndexExplorer.IndexTreeItem) item).getRelatedCollectionID());
		} else if (item instanceof IndexExplorer.AdditionTreeItem) {
			IndexExplorer.AdditionTreeItem addItem = (IndexExplorer.AdditionTreeItem) item;
			if (addItem.getType().equals(
					IndexExplorer.AdditionTreeItem.COLLECTION)) {
				addIndexPanel.update(null);
				addIndexPanel.setVisible(true);
			} else if (addItem.getType().equals(
					IndexExplorer.AdditionTreeItem.INDEX)) {
				// TODO changed the input to null
				addIndexPanel.update(null);
				addIndexPanel.setVisible(true);
			}
			indexDetail.setVisible(false);
		} else {
			indexDetail.setVisible(false);
			addIndexPanel.setVisible(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void onTreeItemStateChanged(TreeItem item) {
	}
}
