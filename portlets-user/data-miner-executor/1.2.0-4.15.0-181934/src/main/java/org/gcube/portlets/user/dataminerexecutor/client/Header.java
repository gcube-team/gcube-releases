/**
 * 
 */
package org.gcube.portlets.user.dataminerexecutor.client;

import org.gcube.portlets.user.dataminerexecutor.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminerexecutor.client.events.MenuSwitchEvent;
import org.gcube.portlets.user.dataminerexecutor.client.type.MenuType;
import org.gcube.portlets.user.dataminerexecutor.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class Header extends HorizontalPanel {

	// private Image menuGoBack;
	//private Image menuExperiment; 
	private Image menuHelp;
	//private Enum<MenuType> currentSelection;

	public Header() {
		super();
		create();
		bind();
	}

	private void bind() {
		EventBusProvider.INSTANCE.addHandler(MenuSwitchEvent.TYPE, new MenuSwitchEvent.MenuSwitchEventHandler() {

			@Override
			public void onSelect(MenuSwitchEvent event) {
				Log.debug("Catch MenuSwitchEvent");
				

			}
		});
	}

	private void create() {
		// this.setStyleAttribute("background-color", "#FFFFFF");
		Image logo = new Image(DataMinerExecutor.resources.logoLittle());
		logo.setAltText("Data Miner Executor");
		logo.setTitle("Data Miner Executor");
		logo.addStyleName("menuImgLogo");

		/*
		 * menuGoBack = new Image(DataMinerExecutor.resources.goBack());
		 * menuGoBack.addStyleName("menuItemImage");
		 * menuGoBack.addClickHandler(new ClickHandler() {
		 * 
		 * @Override public void onClick(ClickEvent event) { MenuEvent menuEvent
		 * = new MenuEvent(MenuType.HOME);
		 * EventBusProvider.INSTANCE.fireEvent(menuEvent);
		 * 
		 * } });
		 */

		/*menuExperiment = new Image(DataMinerExecutor.resources.menuItemExperiment());
		menuExperiment.addStyleName("menuItemImage");
		menuExperiment.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Log.debug("Click Menu Experiment");
				MenuEvent menuEvent = new MenuEvent(MenuType.EXPERIMENT);
				EventBusProvider.INSTANCE.fireEvent(menuEvent);
			}
		});*/

		menuHelp = new Image(DataMinerExecutor.resources.menuItemHelp());
		menuHelp.addStyleName("menuItemImage");
		menuHelp.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Log.debug("Click Menu Help");
				showHelp();
			}
		});

		add(logo);
		// add(menuGoBack);
		//add(menuExperiment);
		add(menuHelp);

		this.setCellWidth(logo, "100px");
		// this.setCellWidth(menuGoBack, "100px");
		//this.setCellWidth(menuExperiment, "80px"); //
		this.setCellWidth(menuHelp, "80px");

		// menuGoBack.setVisible(false);
		//menuExperiment.setVisible(true);
		menuHelp.setVisible(true);
	}

	public void setMenu(MenuType menuType) {
		Log.debug("SetMenu: " + menuType);
		menuHelp.setVisible(true);
		
		/*if (currentSelection == null) {
			// menuGoBack.setVisible(true);
			//menuExperiment.setVisible(true);
		}

		if (currentSelection != null && currentSelection.compareTo(MenuType.EXPERIMENT) == 0)
			menuExperiment.removeStyleName("menuItemImage-selected");

		Image imgNew = menuExperiment;

		imgNew.addStyleName("menuItemImage-selected");
		 */
		//currentSelection = menuType;
		return;
	}

	/*
	private void menuSwitch(MenuSwitchEvent event) {
		Log.debug("MenuSwitch: " + event);

		if (event.getMenuType().compareTo(MenuType.HOME) == 0) {
			// menuGoBack.setVisible(false);
			menuExperiment.setVisible(false);
			menuHelp.setVisible(false);

			if (currentSelection != null && currentSelection.compareTo(MenuType.EXPERIMENT) == 0)
				menuExperiment.removeStyleName("menuItemImage-selected");
		} else {
			if (currentSelection == null
					|| (currentSelection != null && currentSelection.compareTo(MenuType.HOME) == 0)) {
				// menuGoBack.setVisible(true);
				menuExperiment.setVisible(true);
				menuHelp.setVisible(true);
			}

			if (currentSelection != null && currentSelection.compareTo(MenuType.EXPERIMENT) == 0)
				menuExperiment.removeStyleName("menuItemImage-selected");

			Image imgNew = menuExperiment;

			imgNew.addStyleName("menuItemImage-selected");
		}

		currentSelection = event.getMenuType();
		return;
	}*/

	private void showHelp() {
		try {
			DataMinerExecutor.resources.wikiLink().getText(new ResourceCallback<TextResource>() {
				public void onError(ResourceException e) {
					Log.error("Error retrieving wiki link!: " + e.getLocalizedMessage());
					UtilsGXT3.alert("Error", "Error retrieving wiki link!");
				}

				public void onSuccess(TextResource r) {
					String s = r.getText();
					Window.open(s, "DMExecutor Wiki", "");
				}
			});
		} catch (ResourceException e) {
			Log.error("Error retrieving wiki link!: " + e.getLocalizedMessage());
			UtilsGXT3.alert("Error", "Error retrieving wiki link!");
			e.printStackTrace();

		}

	}

}
