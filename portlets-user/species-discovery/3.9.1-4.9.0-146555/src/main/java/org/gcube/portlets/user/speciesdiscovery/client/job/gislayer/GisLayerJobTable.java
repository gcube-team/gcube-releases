package org.gcube.portlets.user.speciesdiscovery.client.job.gislayer;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class GisLayerJobTable {
	
	public static ContentPanel getJobTableWithoutHeading(String name, String startTime, String endTime, GisLayerJobSpeciesProgressBar status, String elapsedTime, Button btnInfo, Button btnCancel, Button btnSave){
		
		ContentPanel cpTableLayout = new ContentPanel();
		cpTableLayout.setHeaderVisible(false);
		cpTableLayout.setStyleAttribute("margin-top", "10px");
		cpTableLayout.setAutoHeight(true);
		cpTableLayout.setWidth(964);
		cpTableLayout.setScrollMode(Scroll.AUTO);
		
		TableLayout tablelayout = new TableLayout(5);
		tablelayout.setCellHorizontalAlign(HorizontalAlignment.CENTER);
		cpTableLayout.setLayout(tablelayout);

		ContentPanel panel;
		
		//NAME
		TableData layoutData = new TableData();
		layoutData.setWidth("300px");
		panel = new ContentPanel();
		panel.setScrollMode(Scroll.AUTO);
		panel.setHeading("Name");
		panel.setHeight(55);
		panel.add(new Label(name));
		panel.setWidth(300);
		cpTableLayout.add(panel, layoutData);
		
		//Status
		layoutData = new TableData();
		layoutData.setWidth("302px");
		panel = new ContentPanel();
		panel.setHeight(55);
		panel.setHeading("Status");
		panel.setScrollMode(Scroll.AUTO);
		panel.add(status);
		panel.setWidth(302);
		cpTableLayout.add(panel, layoutData);

		//Start Time
		layoutData = new TableData();
		layoutData.setWidth("120px");
		panel = new ContentPanel();
		panel.setScrollMode(Scroll.AUTO);
		panel.setHeight(55);
		panel.setHeading("Start Time");
		panel.addText(startTime);
		panel.setWidth(120);
		cpTableLayout.add(panel, layoutData);

		//End Time
		layoutData = new TableData();
		layoutData.setWidth("120px");
		panel = new ContentPanel();
		panel.setHeading("End Time");
		panel.setScrollMode(Scroll.AUTO);
		panel.setHeight(55);
		if(endTime!=null)
			panel.addText(endTime);
		panel.setWidth(120);
		cpTableLayout.add(panel, layoutData);
		
		//Elapsed Time
		layoutData = new TableData();
		layoutData.setWidth("120px");
		panel = new ContentPanel();
		panel.setHeading("Elapsed Time");
		panel.setScrollMode(Scroll.AUTO);
		panel.setHeight(55);
		if(elapsedTime!=null)
			panel.addText(elapsedTime);
		panel.setWidth(120);
		cpTableLayout.add(panel, layoutData);
		
	
		ToolBar toolBar = new ToolBar();
		
		toolBar.add(btnInfo);
		toolBar.add(new SeparatorToolItem());
		
		toolBar.add(btnSave);
		toolBar.add(new SeparatorToolItem());
		
		toolBar.add(btnCancel);
		toolBar.add(new SeparatorToolItem());

		cpTableLayout.setBottomComponent(toolBar);
		
		return cpTableLayout;
		
	}
}
