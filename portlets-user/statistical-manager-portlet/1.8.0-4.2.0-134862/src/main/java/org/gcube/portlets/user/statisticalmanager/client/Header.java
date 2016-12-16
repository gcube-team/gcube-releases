/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client;

import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager.MenuItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ceras
 *
 */
public abstract class Header extends HorizontalPanel {

	public abstract void select(MenuItem menuItem);
	
	Image menuGoBack, menuInputSpace, menuExperiment, menuComputations;
	MenuItem currentSelection = null;
	Image currentImageSelection = null;
	/**
	 * 
	 */
	public Header() {
		super();
//		this.setStyleAttribute("background-color", "#FFFFFF");
		Image logo = new Image(StatisticalManager.resources.logoLittle());
		logo.setAltText("Statistical Manager ver. "+Constants.VERSION);
		logo.setTitle("Statistical Manager ver. "+Constants.VERSION);
		logo.addStyleName("menuImgLogo");
		
		menuGoBack = new Image(StatisticalManager.resources.goBack());
		menuGoBack.addStyleName("menuItemImage");
		menuGoBack.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (setMenuSelected(null))
					select(null);
			}
		});
		
		menuInputSpace = new Image(StatisticalManager.resources.menuItemInputspace());
		menuInputSpace.addStyleName("menuItemImage");
		menuInputSpace.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (setMenuSelected(MenuItem.INPUT_SPACE))
					select(MenuItem.INPUT_SPACE);
			}
		});
		
		menuExperiment = new Image(StatisticalManager.resources.menuItemExperiment());
		menuExperiment.addStyleName("menuItemImage");
		menuExperiment.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (setMenuSelected(MenuItem.EXPERIMENT))
					select(MenuItem.EXPERIMENT);
			}
		});
		

		menuComputations = new Image(StatisticalManager.resources.menuItemComputations());
		menuComputations.addStyleName("menuItemImage");
		menuComputations.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (setMenuSelected(MenuItem.COMPUTATIONS))
					select(MenuItem.COMPUTATIONS);
			}
		});
		
		
		this.add(logo);
		this.add(menuGoBack);
		this.add(menuInputSpace);
		this.add(menuExperiment);
		this.add(menuComputations);

		this.setCellWidth(logo, "100px");
		this.setCellWidth(menuGoBack, "100px");
		this.setCellWidth(menuInputSpace, "80px");
		this.setCellWidth(menuExperiment, "80px");
		this.setCellWidth(menuComputations, "80px");
		
		menuGoBack.setVisible(false);
		menuInputSpace.setVisible(false);
		menuExperiment.setVisible(false);
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
				menuGoBack.setVisible(false);
				menuInputSpace.setVisible(false);
				menuExperiment.setVisible(false);
				menuComputations.setVisible(false);

				if (currentSelection==MenuItem.INPUT_SPACE)
					menuInputSpace.removeStyleName("menuItemImage-selected");
				else if (currentSelection==MenuItem.EXPERIMENT)
					menuExperiment.removeStyleName("menuItemImage-selected");
				else if (currentSelection==MenuItem.COMPUTATIONS)
					menuComputations.removeStyleName("menuItemImage-selected");
		} else {
			if (currentSelection==null) {
//				logo.setResource(StatisticalManager.resources.logoLittle());
				menuGoBack.setVisible(true);
				menuInputSpace.setVisible(true);
				menuExperiment.setVisible(true);
				menuComputations.setVisible(true);
			}
		
			if (currentSelection==MenuItem.INPUT_SPACE)
				menuInputSpace.removeStyleName("menuItemImage-selected");
			else if (currentSelection==MenuItem.EXPERIMENT)
				menuExperiment.removeStyleName("menuItemImage-selected");
			else if (currentSelection==MenuItem.COMPUTATIONS)
				menuComputations.removeStyleName("menuItemImage-selected");
			
			Image imgNew = (menuItem==MenuItem.INPUT_SPACE ? menuInputSpace : (menuItem==MenuItem.EXPERIMENT ? menuExperiment : menuComputations));				
			imgNew.addStyleName("menuItemImage-selected");				
		}
		
		currentSelection = menuItem;
		return true;
	}
}
