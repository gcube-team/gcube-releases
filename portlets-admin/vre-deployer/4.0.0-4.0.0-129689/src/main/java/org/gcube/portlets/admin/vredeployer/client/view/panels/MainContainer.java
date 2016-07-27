package org.gcube.portlets.admin.vredeployer.client.view.panels;

import org.gcube.portlets.admin.vredeployer.client.VREDeployerConstants;
import org.gcube.portlets.admin.vredeployer.client.control.Controller;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;


/**
 * 
 * @author Massimiliano Assante - ISTI-CNR
 *
 */
public class MainContainer extends LayoutContainer {  
	
	final BorderLayout layout = new BorderLayout();  
	
	private WestPanel west;  
	private CenterPanel center;  
	private EastPanel east;  
	private Controller controller;
	
	public MainContainer(Controller c) {
		controller = c;
		west = new WestPanel(controller);  
		center = new CenterPanel(controller);  
		east = new EastPanel(controller);  
	}
	
	protected void onRender(Element target, int index) {  
		super.onRender(target, index);  
	
		setLayout(layout);  
		setStyleAttribute("padding", "2px");  


		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 150);  
		westData.setSplit(true);  
		westData.setMargins(new Margins(0, 5, 0, 0)); 
		westData.setCollapsible(false);  


		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setSplit(false);  
		centerData.setCollapsible(false);  
		centerData.setMargins(new Margins(0));  

		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 350);  
		eastData.setSplit(true);  
		eastData.setCollapsible(true);  
		eastData.setMargins(new Margins(0,0,0,5)); 
		
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 100);  
		northData.setCollapsible(false);  
	    northData.setFloatable(true);  
		northData.setHideCollapseTool(false);  
		northData.setSplit(false);  
		northData.setMargins(new Margins(0, 0, 5, 0));  

		//add(north, northData);  
		add(west, westData);  
		add(center, centerData);  
		add(east, eastData);  
		
		east.setIcon(VREDeployerConstants.ICONS.inforpanel());	
	}  
	
	@Override
	protected void onAfterLayout() {
		super.onAfterLayout();
		east.collapse();
	}

	/**
	 * 
	 * @return -
	 */
	public WestPanel getWestPanel() {
		return west;
	}

	/**
	 * 
	 * @return -
	 */
	public CenterPanel getCenterPanel() {
		return center;
	}
	/**
	 * 
	 * @return -
	 */
	public EastPanel getEastPanel() {
		return east;
	}
	

	/**
	 * 
	 * @param show
	 */
	public void showEastPanel(boolean show) {
		east.show();
		if (show && east.isCollapsed()) {
			layout.expand(LayoutRegion.EAST);				
		}
		else if (!show && east.isExpanded())
			layout.collapse(LayoutRegion.EAST);
		
	}
}
 