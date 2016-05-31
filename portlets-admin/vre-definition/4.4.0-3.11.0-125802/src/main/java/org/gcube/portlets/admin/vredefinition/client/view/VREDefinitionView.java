package org.gcube.portlets.admin.vredefinition.client.view;

import org.gcube.portlets.admin.vredefinition.client.presenter.VREDefinitionPresenter;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Window;




public class VREDefinitionView extends Composite implements VREDefinitionPresenter.Display {
	
	
	private LayoutContainer upCenterPanel;  
	private ContentPanel westPanel;
	private ToolBar bottomCenterPanel;
	private ContentPanel eastPanel;
	private LayoutContainer upContainer;
	
	
	public VREDefinitionView() {
				
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("padding", "2px");
		
		
		upContainer = new LayoutContainer();
		upContainer.setLayout(new BorderLayout());
		
		
		westPanel = new ContentPanel();
		ContentPanel centerPanel = new ContentPanel();
		centerPanel.setFrame(true);
		centerPanel.setBodyStyle("backgroundColor: white;");
		eastPanel = new ContentPanel();
		eastPanel.setVisible(false);
		eastPanel.setCollapsible(false);
		
		
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 150);  
		westData.setSplit(false);  
		westData.setMargins(new Margins(0, 5, 0, 0)); 
		westData.setCollapsible(false);
	
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER, 500);
		centerData.setSplit(true);  
		centerData.setCollapsible(false); 
		centerData.setMargins(new Margins(0,0,0,0));

		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 500);  
		eastData.setSplit(true);  
		eastData.setCollapsible(false);  
		eastData.setMargins(new Margins(0,0,0,5)); 

		upContainer.add(westPanel, westData);  
		
		upCenterPanel = new LayoutContainer();
		upCenterPanel.setSize("100%", "100%");
		centerPanel.add(upCenterPanel);
		upContainer.add(centerPanel, centerData);  
		upContainer.add(eastPanel, eastData);  
		
		container.add(upContainer);
		
		
		bottomCenterPanel = new ToolBar();
		bottomCenterPanel.setSize("100%", "100%");
		//toolBar.add(bottomCenterPanel);
		
		container.add(bottomCenterPanel);
		initComponent(container);
		
		setHeight(Window.getClientHeight());
		setWidth(Window.getClientWidth());
		
		eastPanel.setCollapsible(false);
	}


	
	public ToolBar getBottomCenterPanel() {
		// TODO Auto-generated method stub
		return bottomCenterPanel;
	}



	public ContentPanel getEastPanel() {
		return eastPanel;
	}



	public LayoutContainer getUpCenterPanel() {
		// TODO Auto-generated method stub
		return upCenterPanel;
	}



	public ContentPanel getwestPanel() {
		// TODO Auto-generated method stub
		return westPanel;
	}
	
	public LayoutContainer getUpContainer() {
		return upContainer;
	}
	
	

}
