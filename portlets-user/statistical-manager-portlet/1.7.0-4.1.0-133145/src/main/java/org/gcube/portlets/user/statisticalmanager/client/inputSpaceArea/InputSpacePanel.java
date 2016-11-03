/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.inputSpaceArea;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * @author ceras
 *
 */
public class InputSpacePanel extends LayoutContainer {
	
	private TabPanel tabPanel;
	private Importer importerPanel = new Importer();
	private ResourcesGrid tablesGrid = new ResourcesGrid();
	

	/**
	 * 
	 */
	public InputSpacePanel() {
		super();
		
		this.setStyleAttribute("background-color", "#FFFFFF");
//		this.getHeader().setIcon(Images.table());
//		this.setHeading(".: Data Space");
		this.setScrollMode(Scroll.NONE);
		

		this.setLayout(new FitLayout());
		
		tabPanel = new TabPanel();
		tabPanel.setBodyBorder(true);
		
		tabPanel.add(tablesGrid);
		tabPanel.add(importerPanel);
		
		tabPanel.addListener(Events.Select, new Listener<TabPanelEvent>() {
			@Override
			public void handleEvent(TabPanelEvent be) {
				TabItem tabItem = be.getItem();
				if (tabItem == tablesGrid)
					tablesGrid.gridAttached();
			}
		});
		
		this.add(tabPanel);
	}
	
}
