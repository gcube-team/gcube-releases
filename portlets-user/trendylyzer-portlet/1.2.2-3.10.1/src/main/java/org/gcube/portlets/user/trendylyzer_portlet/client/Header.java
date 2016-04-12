package org.gcube.portlets.user.trendylyzer_portlet.client;






import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet.MenuItem;
import org.gcube.portlets.user.trendylyzer_portlet.client.occurences.OccurencePanel;
import org.gcube.portlets.user.trendylyzer_portlet.client.species_info.SpeciesInfoPanel;

import weka.gui.sql.ResultPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public abstract class Header  extends HorizontalPanel  {

	public abstract void select(MenuItem menuItem);

	Image menuGoBack, menuOcc, infoSp, menuComputations;
	MenuItem currentSelection = null;
	Image currentImageSelection = null;
//	private OccurencePanel obsPanel = new OccurencePanel();
//	private SpeciesInfoPanel infoSpPanel= new SpeciesInfoPanel();
//	private ResultPanel resultsPanel= new ResultPanel();
	/**
	 * 
	 */
	public Header() {
		super();
//		this.setStyleAttribute("background-color", "#FFFFFF");
		Image logo = new Image(TrendyLyzer_portlet.resources.logo());
		
		logo.addStyleName("menuImgLogo");
		
//		menuGoBack = new Image(TrendyLyzer_portlet.resources.goBack());
//		menuGoBack.addStyleName("menuItemImage");
//		menuGoBack.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				if (setMenuSelected(null))
//					select(null);
//			}
//		});
		
		menuOcc = new Image(TrendyLyzer_portlet.resources.menuOcc());
		menuOcc.addStyleName("menuItemImage");
		menuOcc.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (setMenuSelected(MenuItem.OCCURENCES))
					select(MenuItem.OCCURENCES);
			}
		});
//		
//		infoSp = new Image(TrendyLyzer_portlet.resources.infoSp());
//		infoSp.addStyleName("menuItemImage");
//		infoSp.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				if (setMenuSelected(MenuItem.INFOSP))
//					select(MenuItem.INFOSP);
//			}
//		});
		menuComputations = new Image(TrendyLyzer_portlet.resources.menuItemComputations());
		menuComputations.addStyleName("menuItemImage");
		menuComputations.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (setMenuSelected(MenuItem.COMPUTATIONS))
					select(MenuItem.COMPUTATIONS);
			}
		});
//
//		menuComputations = new Image(StatisticalManager.resources.menuItemComputations());
//		menuComputations.addStyleName("menuItemImage");
//		menuComputations.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				if (setMenuSelected(MenuItem.COMPUTATIONS))
//					select(MenuItem.COMPUTATIONS);
//			}
//		});
//		
		
		this.add(logo);
		//this.add(menuGoBack);
		this.add(menuOcc);
//		this.add(infoSp);
		this.add(menuComputations);

		this.setCellWidth(logo, "100px");
		//this.setCellWidth(menuGoBack, "100px");
		this.setCellWidth(menuOcc, "80px");
//		this.setCellWidth(infoSp, "80px");
		this.setCellWidth(menuComputations, "80px");
		
		//menuGoBack.setVisible(false);
		menuOcc.setVisible(false);
//		infoSp.setVisible(false);
		menuComputations.setVisible(false);
	}

	/**
	 * @param inputSpace
	 */
	public boolean setMenuSelected(MenuItem menuItem) {
		// return true if the menu was changed
		
		if (menuItem==currentSelection)
			return false; // nothing to change
		
		if (menuItem==null) {
				//menuGoBack.setVisible(false);
				menuOcc.setVisible(false);
				menuComputations.setVisible(false);
//				infoSp.setVisible(false);
				menuComputations.setVisible(false);

				if (currentSelection==MenuItem.OCCURENCES)
					menuOcc.removeStyleName("menuItemImage-selected");
				else if (currentSelection==MenuItem.INFOSP)
					infoSp.removeStyleName("menuItemImage-selected");
				else if (currentSelection==MenuItem.COMPUTATIONS)
					menuComputations.removeStyleName("menuItemImage-selected");
		} else {
			if (currentSelection==null) {
				//menuGoBack.setVisible(true);
			menuOcc.setVisible(true);
//				infoSp.setVisible(true);
				menuComputations.setVisible(true);
			}
		
			if (currentSelection==MenuItem.OCCURENCES)
				menuOcc.removeStyleName("menuItemImage-selected");
//			else if (currentSelection==MenuItem.INFOSP)
//				infoSp.removeStyleName("menuItemImage-selected");
//			else
			if (currentSelection==MenuItem.COMPUTATIONS)
				menuComputations.removeStyleName("menuItemImage-selected");
//			
		Image imgNew = (menuItem==MenuItem.OCCURENCES ? menuOcc :  menuComputations );				
		imgNew.addStyleName("menuItemImage-selected");				
		}
		
		currentSelection = menuItem;
		return true;
	
	}

	

}
