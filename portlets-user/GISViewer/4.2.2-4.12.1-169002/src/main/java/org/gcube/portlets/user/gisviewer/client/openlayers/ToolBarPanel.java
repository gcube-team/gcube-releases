package org.gcube.portlets.user.gisviewer.client.openlayers;


import org.gcube.portlets.user.gisviewer.client.commons.beans.ExportFormat;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.resources.Images;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Label;


public class ToolBarPanel extends ToolBar {

	private OpenLayersMap om;

	private HorizontalPanel transectPanel = new HorizontalPanel();
	private Label labelTransect = new Label("Draw a line for a transect on the layer ");

	private ToolbarHandler toolBarHandler;
	private LayerItem layerItemTransect;

	private Button maxExtentButton = new Button();
	private ToggleButton panToggle = new ToggleButton();
	private ToggleButton zoomInToggle = new ToggleButton();
	private Button zoomOutButton = new Button();
	private ToggleButton clickDataToggle = new ToggleButton();
	private ToggleButton boxDataToggle = new ToggleButton();

	private Button removeDataButton = new Button();
	private Button exportButton;
	private Button saveMapImageButton;

	private final static String TOGGLE_GROUP = "toggleGroup";

	private static final String BOX_DATA_TOOLTIP = "Data box selection";
	private static final String CLICK_DATA_TOOLTIP = "Data point selection";
	private static final String PAN_TOOLTIP = "Pan";
	private static final String ZOOM_IN_TOOLTIP = "Zoom in";
	private static final String ZOOM_OUT_TOOLTIP = "Zoom out";
	private static final String MAX_EXTENT_TOOLTIP = "Zoom to max extent";
	private static final String REMOVE_DATA_TOOLTIP = "Remove spatial query selection";
	private static final String EXPORT_TOOLTIP = "Export a map snapshot";
	private static final String SAVE_TOOLTIP = "Save a map snapshot into the Workspace";

	private enum TOGGLES {PAN, ZOOMIN, CLICKDATA, BOXDATA};

	private static final String[][] FORMATS = {
		{"GIF", "image/gif"},
		{"JPEG", "image/jpeg"},
		{"PNG", "image/png"},
	};
	private static final AbstractImagePrototype[] FORMAT_IMAGES = {
		Images.iconGif(),
		Images.iconJpeg(),
		Images.iconPng(),
	};



	public ToolBarPanel(ToolbarHandler toolbarHandler, OpenLayersMap om) {
		super();
		this.om = om;
		this.toolBarHandler = toolbarHandler;

		setmaxExtentButton();
		setPanToggle();
		setZoomInToggle();
		setZoomOutButton();
		setClickDataToggle();
		setBoxDataToggle();
		setRemoveDataButton();
		setOpenBrowserMapImageBox();

		this.getAriaSupport().setLabel("Openlayers Toolbar");
		this.add(maxExtentButton);
		this.add(panToggle);
		this.add(zoomInToggle);
		//this.add(zoomOutButton);
		this.add(clickDataToggle);
		this.add(boxDataToggle);
		this.add(removeDataButton);

		//COMMENTED BY FRANCESCO M.
		this.add(new SeparatorMenuItem());

		addProjectionBox();
		addTransectBox();
		this.add(exportButton);

		if (toolbarHandler.isSaveSupported()) {
			setSaveMapImageBox();
			this.add(saveMapImageButton);
		}
	}



	/**
	 *
	 */
	private void setOpenBrowserMapImageBox() {
		// layer export
		exportButton = new Button("", Images.iconExport());
		exportButton.setToolTip(EXPORT_TOOLTIP);
		Menu menuExport = new Menu();
		menuExport.setStyleName("gisViewerMenu");
		int i=0;
		for (final ExportFormat saveItem : ExportFormat.values()) {
			menuExport.add(new MenuItem(saveItem.getLabel(), saveItem.getImg(), new SelectionListener<MenuEvent>(){
				@Override
				public void componentSelected(MenuEvent ce) {
					toolBarHandler.openBrowserMapImage(saveItem, true);
				}
			}));
		}
		exportButton.setMenu(menuExport);
	}



	/**
	 *
	 */
	private void setSaveMapImageBox() {
		// layer export
		saveMapImageButton = new Button("", Images.iconSave());
		saveMapImageButton.setToolTip(SAVE_TOOLTIP);
		Menu menuExport = new Menu();
		menuExport.setStyleName("gisViewerMenu");
		int i=0;
		for (final ExportFormat saveItem : ExportFormat.values()) {
			menuExport.add(new MenuItem(saveItem.getLabel(), saveItem.getImg(), new SelectionListener<MenuEvent>(){
				@Override
				public void componentSelected(MenuEvent ce) {
					toolBarHandler.saveMapImage(saveItem, true);
				}
			}));
		}
		saveMapImageButton.setMenu(menuExport);
	}



	/**
	 *
	 */
	private void addTransectBox() {
		labelTransect.setStyleName("vertical_middle");

		transectPanel.setBorders(true);
		transectPanel.add(labelTransect);

		transectPanel.setStyleName("geo-panel-body");
		add(new Html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));

		transectPanel.setVisible(false);

		add(transectPanel);
	}



	/**
	 * @param button
	 * @param boxDataTooltip
	 * @param abstractImagePrototype
	 */
	private void setUpBotton(Button button, AbstractImagePrototype img, String toolTip) {
		button.setIcon(img);
//		button.addStyleName("iconOpenlayersToolbar");
		button.setToolTip(toolTip);
//		button.setSize(25, 25);
	}


	private void setBoxDataToggle() {
		setUpBotton(boxDataToggle, Images.iconBoxData(), BOX_DATA_TOOLTIP);
		boxDataToggle.setToggleGroup(TOGGLE_GROUP);
		boxDataToggle.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				boolean isPressed = ((ToggleButton)be.getButton()).isPressed();
				togglePressed(isPressed, TOGGLES.BOXDATA);
			}
		});
	}


	private void setZoomInToggle() {
		zoomInToggle = new ToggleButton();
		zoomInToggle.setIcon(Images.iconZoomIn());
		zoomInToggle.setToolTip(ZOOM_IN_TOOLTIP);
//TODO
		//zoomInToggle.setToggleGroup(TOGGLE_GROUP);
		zoomInToggle.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				boolean isPressed = ((ToggleButton)be.getButton()).isPressed();
				togglePressed(isPressed, TOGGLES.ZOOMIN);
			}
		});
	}

	private void setClickDataToggle() {
		setUpBotton(clickDataToggle, Images.iconClickData(), CLICK_DATA_TOOLTIP);
		clickDataToggle.setToggleGroup(TOGGLE_GROUP);
		clickDataToggle.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				boolean isPressed = ((ToggleButton)be.getButton()).isPressed();
				togglePressed(isPressed, TOGGLES.CLICKDATA);
			}
		});
	}

	private void setPanToggle() {
		setUpBotton(panToggle, Images.iconPan(), PAN_TOOLTIP);
		panToggle.toggle(true);
		panToggle.setToggleGroup(TOGGLE_GROUP);
		panToggle.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				boolean isPressed = ((ToggleButton)be.getButton()).isPressed();
				togglePressed(isPressed, TOGGLES.PAN);
			}
		});
	}

	private void setZoomOutButton() {
		setUpBotton(zoomOutButton, Images.iconZoomOut(), ZOOM_OUT_TOOLTIP);
		zoomOutButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				om.zoomOut();
			}
		});
	}

	private void setmaxExtentButton() {
		setUpBotton(maxExtentButton, Images.iconMaxExtent(), MAX_EXTENT_TOOLTIP);
		maxExtentButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				om.zoomToMaxExtent();
			}
		});
	}

	/**
	 *
	 */
	private void setRemoveDataButton() {
		setUpBotton(removeDataButton, Images.iconToolbarRemove(), REMOVE_DATA_TOOLTIP);
		removeDataButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				om.removeDataFeature();
			}
		});
	}

	private void togglePressed(boolean isPressed, TOGGLES toggleType) {
		if (!isPressed) {
			panToggle.toggle(true);
			if (toggleType!=TOGGLES.PAN)
				togglePressed(true, TOGGLES.PAN);
		} else {
			om.activateClickData(toggleType==TOGGLES.CLICKDATA);
			om.activateDrawBoxControl(toggleType==TOGGLES.BOXDATA);
			om.activateZoomIn(toggleType==TOGGLES.ZOOMIN);
			om.activatePan(toggleType==TOGGLES.PAN);
			toolBarHandler.deactivateTransect(layerItemTransect);
		}
	}

	/**
	 * @param layerItem
	 * @param b
	 */
	public void setTransectPanelVisible(LayerItem layerItem, boolean visible) {
		if (visible)
			this.labelTransect.setText("Draw a transect line for the layer "+layerItem.getName());
		else
			om.activateTransectDraw(false);
		this.transectPanel.setVisible(visible);
		this.layerItemTransect = layerItem;
	}


	/**
	 *
	 */
	public void setAllUp() {
		om.activateClickData(false);
		om.activateDrawBoxControl(false);
		om.activateZoomIn(false);
		om.activatePan(false);
	}

	private void addProjectionBox(){
		// TODO
	}

	/**
	 * Added by Francesco M.
	 * @return
	 */
	public boolean isPointDataTogglePressed(){
		return clickDataToggle.isPressed();
	}

	/**
	 * Added by Francesco M.
	 * @return
	 */
	public boolean isBoxDataTogglePressed(){
		return boxDataToggle.isPressed();
	}

	/**
	 * @return the clickDataToggle
	 */
	public ToggleButton getClickDataToggle() {

		return clickDataToggle;
	}

	/**
	 * @return the boxDataToggle
	 */
	public ToggleButton getBoxDataToggle() {

		return boxDataToggle;
	}
}