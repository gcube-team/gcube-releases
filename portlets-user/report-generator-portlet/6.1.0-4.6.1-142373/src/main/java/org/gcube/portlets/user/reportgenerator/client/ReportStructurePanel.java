package org.gcube.portlets.user.reportgenerator.client;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.gcube.portlets.d4sreporting.common.shared.AttributeArea;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.BasicSection;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.d4sreporting.common.shared.ReportReferences;
import org.gcube.portlets.user.reportgenerator.client.events.ItemSelectionEvent;
import org.gcube.portlets.user.reportgenerator.client.resources.FimesReportTreeStructureResources;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;


public class ReportStructurePanel extends ScrollPanel{

	Tree t;
	TreeItem root ;
	public ReportStructurePanel(final HandlerManager eventBus, final Model report, 	final String width, final String height) {
		
		setWidth(width);
		setHeight(height);
		
		
		t = new Tree();
		t.setAnimationEnabled(true);
		root = loadReportRootTree(report);
		t.addItem(root);
		t.addSelectionHandler(new SelectionHandler<TreeItem>() {
			
			@SuppressWarnings("unchecked")
			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem item = event.getSelectedItem();
				HashMap<String,Object> map = (HashMap<String, Object>)item.getUserObject();
				eventBus.fireEvent(new ItemSelectionEvent(map));
			}
		});
	
		this.add(t);
		root.setState(true);
		getElement().getStyle().setPaddingLeft(10, Unit.PX);
		getElement().getStyle().setPaddingTop(10, Unit.PX);
		
		expandtree();
	}
	
	private void expandtree() {
		Timer t = new Timer() {
			
			@Override
			public void run() {
				for (int i = 0; i < root.getChildCount(); i++) {
					root.getChild(i).setState(true);
				}				
			}
		};
		t.schedule(500);
	}
	
	private TreeItem loadReportRootTree(final Model report) {
		// Add root node
		ImageResource image = FimesReportTreeStructureResources.INSTANCE.root();
		HorizontalPanel node = createNodeWidget(image, "Structure View","gwt-label-rootTree");
		TreeItem root = new TreeItem(node);

		root.setStyleName("treeContainer");
		
		
		Vector<BasicSection> sections = report.getSections();
		for(int i = 1; i <= sections.size(); i++) {
			BasicSection s = sections.get(i - 1);
			// Add section item
			image = FimesReportTreeStructureResources.INSTANCE.section();
			node = createNodeWidget(image, "Section " + i,"gwt-label-sectionTree");
			
			TreeItem sectionItem = addChildItemToParentItem(root, "Section", Integer.toString(i -1), node);
			sectionItem.setState(true);
			addItemsComponent(sectionItem, s.getComponents());
			
		}		
		return root;
	}
	
	private void addItemsComponent(final TreeItem sectionItem,final List<BasicComponent> components ) {
		
		for(int i = 0; i < components.size(); i++) {
			BasicComponent c = components.get(i);
			
			HorizontalPanel node = null;
			switch (c.getType()) {
			case TITLE: {
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.heading1();
				node = createNodeWidget(image, (String)c.getPossibleContent(),"gwt-label-componentTree");
				break;
			}
			case HEADING_1: {
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.heading1();
				node = createNodeWidget(image, (String)c.getPossibleContent(),"gwt-label-componentTree");
				break;
			}
			case HEADING_2: {
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.heading2();
				node = createNodeWidget(image, (String)c.getPossibleContent(),"gwt-label-componentTree");
				break;
			}
			case HEADING_3: {
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.heading3();
				node = createNodeWidget(image, (String)c.getPossibleContent(),"gwt-label-componentTree");
				break;
			}
			case HEADING_4: {
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.heading4();
				node = createNodeWidget(image, (String)c.getPossibleContent(),"gwt-label-componentTree");
				break;
			}
			case INSTRUCTION: {
//				ImageResource image = FimesReportTreeStructureResources.INSTANCE.instructions();
//				node = createNodeWidget(image, "Instructions","gwt-label-componentTree");
				break;
			}
			case COMMENT: {
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.comments();
				node = createNodeWidget(image, "Comment","gwt-label-componentTree");
				break;
			}
			case BODY: {
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.text();
				node = createNodeWidget(image, "Text","gwt-label-componentTree");
				break;
			}
			case DYNA_IMAGE: {
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.image();
				node = createNodeWidget(image, "Image","gwt-label-componentTree");
				break;
			}
			case FLEX_TABLE: {
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.table();
				node = createNodeWidget(image, "Table","gwt-label-componentTree");
				break;
			}
			case ATTRIBUTE_MULTI:  {
				AttributeArea ar = (AttributeArea) c.getPossibleContent();
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.checkbox();
				node = createNodeWidget(image, ar.getAttrName(),"gwt-label-componentTree");
				break;
			}			
			case ATTRIBUTE_UNIQUE: {
				AttributeArea ar = (AttributeArea) c.getPossibleContent();
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.radio();
				node = createNodeWidget(image, ar.getAttrName(),"gwt-label-componentTree");
				break;
			}
			case BODY_TABLE_IMAGE: {
				RepeatableSequence	rs = (RepeatableSequence) c.getPossibleContent();
				addItemsComponent(sectionItem, rs.getGroupedComponents());
				break;
			}
			case REPEAT_SEQUENCE: {
				RepeatableSequence	rs = (RepeatableSequence) c.getPossibleContent();
				addItemsComponent(sectionItem, rs.getGroupedComponents());
				break;
			}
			case REPORT_REFERENCE: {
				ReportReferences rf = (ReportReferences)  c.getPossibleContent();			
				ImageResource image = FimesReportTreeStructureResources.INSTANCE.reference();
				node = createNodeWidget(image, 	rf.getRefType(),"gwt-label-componentTree");
				break;
			}
			case TOC:
				// TODO
				// componentItem = new TreeItem("TOC");
				break;
			case PAGEBREAK:
				// TODO
				// componentItem = new TreeItem("Pagebreak");
				break;
			case TIME_SERIES:
				// TODO
				//componentItem = new TreeItem("Time_Series");
				break;
			}
			
			if (node != null && c.getType() != ComponentType.REPEAT_SEQUENCE) {
				addChildItemToParentItem(sectionItem, "Component",
						Integer.toString(i), node);
			}
			
		}
	}
	
	private TreeItem addChildItemToParentItem(final TreeItem root, final String typeItem, 
			final String indexList, final Widget content) {
		
		TreeItem item = new TreeItem(content);
		// Create userObjet 
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("item",typeItem );
		map.put("index", indexList);
		map.put("parent", root.getUserObject());
		item.setUserObject(map);
		root.addItem(item);
		return item;
	}
	
	/**
	 * Generates HTML for a tree item with an attached icon.
	 * @param imageResource the image resource to use
	 * @param title the title of the item
	 * @return the resultant HTML
	 */
	private HorizontalPanel createNodeWidget(ImageResource imageResource, String title,
			String cssStyle) {
		HorizontalPanel node = new HorizontalPanel();

		Image image = new Image(imageResource);
		image.setPixelSize(image.getWidth() + 5, image.getHeight());
		node.add(image);
		
		String shortTitle = title;
		if (shortTitle.length() > 20)
			shortTitle = shortTitle.substring(0,18) + "..";
		
		Label text = new Label(shortTitle);
		node.add(text);
		return node;
	}
}
