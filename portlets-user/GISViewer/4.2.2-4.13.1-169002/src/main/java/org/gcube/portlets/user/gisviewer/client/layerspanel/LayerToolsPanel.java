package org.gcube.portlets.user.gisviewer.client.layerspanel;

import java.util.List;

import org.gcube.portlets.user.gisviewer.client.Constants;
import org.gcube.portlets.user.gisviewer.client.GisViewer;
import org.gcube.portlets.user.gisviewer.client.commons.beans.ExportFormat;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.commons.beans.Property;
import org.gcube.portlets.user.gisviewer.client.commons.utils.MapServerRecognize;
import org.gcube.portlets.user.gisviewer.client.commons.utils.MapServerRecognize.SERVERTYPE;
import org.gcube.portlets.user.gisviewer.client.resources.Images;
import org.gcube.portlets.user.gisviewer.client.util.SizedLabel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class LayerToolsPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 10, 2016
 */
public class LayerToolsPanel extends VerticalPanel {
	/**
	 *
	 */
//	private static final String TOOLS_PADDING = "20px";

	private static final String OPACITY_SLIDER_WIDTH = "145px";
	private static final String CHECKBOX_CELL_WIDTH = "28px";
	private static final String OPENBUTTON_CELL_WIDTH = "20px";
//	private static final int LAYER_TOOLS_PANEL_HEIGHT = 84;
	private ImageResource ICON_TRANSECT_TIP = GisViewer.resources.iconTransectTip();
	private ImageResource ICON_TRANSECT_TIP_DELETE = GisViewer.resources.iconTransectTipDelete();
	private ImageResource ICON_CQL_TIP = GisViewer.resources.iconCqlTip();
	private ImageResource ICON_CQL_TIP_DELETE = GisViewer.resources.iconCqlTipDelete();
	private ImageResource ICON_TRIANGLE_RIGHT = GisViewer.resources.iconTriangleRight();
	private ImageResource ICON_TRIANGLE_DOWN = GisViewer.resources.iconTriangleDown();
	private ImageResource ICON_CLOSE_LAYER = GisViewer.resources.iconCloseLayer();
	private ImageResource ICON_CLOSE_LAYER_OVER = GisViewer.resources.iconCloseLayerOver();

	private LayerItem layerItem;
	private VerticalPanel tools;
	private Image imgOpenButton;
	private Image imgCloseLayerButton;
	private Image imgCqlTip;
	private Image imgTransectTip;
	private CheckBox checkBox;
	private Slider opacitySlider;
	boolean isToolsOpened = false;

	private String fieldTransect=null;
	private String tableTransect=null;
	private LayersPanelHandler layersPanelHandler;
	private HorizontalPanel cp;

	private boolean cqlTipInserted=false;
	private boolean transectTipInserted=false;

	// TOOLBAR TRANSECT, FILTER AND EXPORT
	private ToolBar toolBar1 = new ToolBar();
	private SimpleComboBox<String> scbZAxis;

	/**
	 * Instantiates a new layer tools panel.
	 *
	 * @param layerItem the layer item
	 * @param layersPanelHandler the layers panel handler
	 */
	public LayerToolsPanel(LayerItem layerItem, LayersPanelHandler layersPanelHandler) {
		// create the vertical panel that represents all the layer ui
		super();
		this.layerItem = layerItem;
		this.layersPanelHandler = layersPanelHandler;

		this.setStyleAttribute("margin-bottom", "2px");
		activeCQLIfAvailable();
		boolean showTransect = true;
		updatePropertyInfo(showTransect);
		this.setTableWidth("100%");

		// layer info
		cp = new HorizontalPanel();
		cp.setWidth("100%");

		createCheckBox();
		Html text = createText();
		createOpenButton();
		createTipImages();
		createOpacitySlider();
		createZAxisCombo();
		createTools(layerItem.isCqlFilterAvailable(), showTransect);
		createCloseLayerButton();

		cp.add(new Html("&nbsp;"));
		cp.add(checkBox);
		cp.add(imgOpenButton);
		cp.add(text);
		cp.add(imgCloseLayerButton);

		cp.setCellWidth(checkBox, CHECKBOX_CELL_WIDTH);
		cp.setCellWidth(imgOpenButton, OPENBUTTON_CELL_WIDTH);
		cp.setCellWidth(text, "100%");
		cp.setStyleName("layersPanel1");

		this.add(cp);
	}

	/**
	 * Active cql if available.
	 */
	private void activeCQLIfAvailable() {

		SERVERTYPE mapServerType = MapServerRecognize.recongnize(layerItem.getGeoserverUrl());

		if(mapServerType!=null){

			if(mapServerType.equals(MapServerRecognize.SERVERTYPE.MAPSERVER)){
				layerItem.setCqlFilterAvailable(false);
			}else
				layerItem.setCqlFilterAvailable(true);
		}
	}

	/**
	 * Active transect.
	 */
	private void activeTransect(){

		if (tableTransect!=null && fieldTransect!=null) {
			// transect button
			Button btnTransect = new Button("", Images.iconTransect(), new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					setTransectTip(true);
					layersPanelHandler.activateTransect(layerItem, tableTransect, fieldTransect);
				}
			});
			btnTransect.setToolTip("Generate a Transect function");
			//btnTransect.setEnabled((tableTransect!=null && fieldTransect!=null));
//			toolBar1.layout();
			toolBar1.add(btnTransect);
			tools.layout(true);
		}
	}

	/**
	 * Creates the tools.
	 *
	 * @param showCQLFilter the show cql filter
	 * @param showTransect the show transect
	 */
	private void createTools(boolean showCQLFilter, boolean showTransect) {
		tools = new VerticalPanel();
//		tools.setStyleAttribute("padding-left", TOOLS_PADDING);
		//tools.setStyleAttribute("background-color", "#E5E5E5");
		tools.setStyleName("layerToolbarFunctionality");
//		tools.setHeight(LAYER_TOOLS_PANEL_HEIGHT);

		// TOOLBAR TRANSECT, FILTER AND EXPORT
		toolBar1.setStyleName("myToolbar"); // TODO define style
		toolBar1.add(new SizedLabel("General", 10));
		toolBar1.add(new SeparatorToolItem());
//		this.toolBar1.add(btnTransect);

		if(showTransect)
			activeTransect();

		if(showCQLFilter){
			// filter button
			Button btnFilter = new Button("", Images.iconFilter(), new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					int left = ce.getButton().getAbsoluteLeft();
					int top = ce.getButton().getAbsoluteTop();
					layersPanelHandler.showFilterQuery(layerItem, left, top);
				}
			});
			btnFilter.setToolTip("Set a CQL filter to the layer " + this.layerItem.getName() + " (or remove previous filter)");
			toolBar1.add(btnFilter);
		}

		// layer export
		Button btnExportLayer = new Button("", Images.iconExport());
		btnExportLayer.setToolTip("Export layer view");
		Menu menuExportLayer = new Menu();
		menuExportLayer.setStyleName("gisViewerMenu");
		int i=0;
		for (final ExportFormat exportItem : ExportFormat.values()) {
			menuExportLayer.add(new MenuItem(exportItem.getLabel(), exportItem.getImg(), new SelectionListener<MenuEvent>(){
				@Override
				public void componentSelected(MenuEvent ce) {
					layersPanelHandler.openBrowserLayerImage(layerItem, exportItem, true);
				}
			}));
		}
		btnExportLayer.setMenu(menuExportLayer);
		toolBar1.add(btnExportLayer);

		// layer save
		if (layersPanelHandler.isSaveSupported()) {
			Button btnSaveLayer = new Button("", Images.iconSave());
			btnSaveLayer.setToolTip("Save layer view");
			Menu menuSaveLayer = new Menu();
			menuSaveLayer.setStyleName("gisViewerMenu");
			i=0;
			for (final ExportFormat exportItem : ExportFormat.values()) {
				menuSaveLayer.add(new MenuItem(exportItem.getLabel(), exportItem.getImg(), new SelectionListener<MenuEvent>(){
					@Override
					public void componentSelected(MenuEvent ce) {
						layersPanelHandler.saveLayerImage(layerItem, exportItem, true);
						// TODO manage isWms
					}
				}));
			}
			btnSaveLayer.setMenu(menuSaveLayer);
			toolBar1.add(btnSaveLayer);
		}

		tools.add(toolBar1);
		//	    tools.add(generalToolBar);


		// OPACITY TOOLBAR
		ToolBar toolBar2 = new ToolBar();
		toolBar2.setStyleName("myToolbar"); // TODO define style
		toolBar2.add(new SizedLabel("Opacity", 10));
		toolBar2.add(new SeparatorToolItem());
		toolBar2.add(opacitySlider);

		tools.add(toolBar2);

		if(scbZAxis!=null){

			HorizontalPanel toolbar4 = new HorizontalPanel();
			toolbar4.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			toolbar4.setStyleName("myToolbar");
			Label label = new SizedLabel("Z-Axis", 10);
			toolbar4.add(label);
			toolbar4.add(new SeparatorToolItem());
			toolbar4.add(scbZAxis);

			tools.add(toolbar4);
		}


		// LEGEND TOOLBAR
		if (layerItem.isHasLegend()) {
			HorizontalPanel toolbar3 = new HorizontalPanel();
			toolbar3.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			toolbar3.setStyleName("myToolbar");
			Label label = new SizedLabel("Legend", 10);
			toolbar3.add(label);
			toolbar3.add(new SeparatorToolItem());

			//legend button
			final Button btnLegend = new Button("", Images.iconLegend(), new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					int left = ce.getButton().getAbsoluteLeft();
					int top = ce.getButton().getAbsoluteTop();
					layersPanelHandler.showLegend(layerItem, left, top);
				}
			});
			btnLegend.setToolTip("Show Legend");
			toolbar3.add(btnLegend);

			String defaultStyle = layerItem.getStyle()!=null && !layerItem.getStyle().isEmpty()?layerItem.getStyle():Constants.UNKNOWN_STYLE_NAME;

			//		legend list
			if (layerItem.getStyles().size()==0 || layerItem.getStyles().size()==1) {
				SizedLabel sizedLabel = new SizedLabel(defaultStyle, 12, 18);
				sizedLabel.setTitle(defaultStyle);
				toolbar3.add(sizedLabel);
			}else {
				String styleName = defaultStyle.length()<18 ? defaultStyle : defaultStyle.substring(0, 18) + "...";
				final Button btnStyle = new Button(styleName);
				btnStyle.setToolTip("Change the style");
				btnStyle.setTitle(styleName);
				Menu menuStyle = new Menu();
				menuStyle.setStyleName("gisViewerMenu");

				for (final String style : layerItem.getStyles()) {
					menuStyle.add(new MenuItem(style, new SelectionListener<MenuEvent>(){
						@Override
						public void componentSelected(MenuEvent ce) {
							btnStyle.setText(style.length()<18 ? style : style.substring(0, 18) + "...");
							btnStyle.setTitle(style);
							layerItem.setStyle(style);
							layersPanelHandler.applyStyleForLayer(layerItem, style);
							Constants.log("style: " + style);
						}
					}));
				}

				btnStyle.setMenu(menuStyle);
				toolbar3.add(btnStyle);
			}

			tools.add(toolbar3);
		}
	}


	/**
	 * Creates the opacity slider.
	 */
	private void createOpacitySlider() {
		opacitySlider = new Slider();
		opacitySlider.setMinValue(0);
		opacitySlider.setMaxValue(100);
		opacitySlider.setIncrement(5);
		opacitySlider.setWidth(OPACITY_SLIDER_WIDTH);
		opacitySlider.addListener(Events.Change, new Listener<SliderEvent>() {
			public void handleEvent(SliderEvent be) {
				double newValue = (double)be.getNewValue()/100;
				layerItem.setOpacity(newValue);
				layersPanelHandler.setOpacityLayer(layerItem, newValue);
			}
		});

		opacitySlider.setValue((int)(layerItem.getOpacity()*100));
	}


	/**
	 * Creates the z axis combo.
	 */
	private void createZAxisCombo() {
		if(layerItem.getZAxis()!=null && layerItem.getZAxis().getValues()!=null && layerItem.getZAxis().getValues().size()>0){
			scbZAxis = new SimpleComboBox<String>();
			scbZAxis.setEmptyText("Choose Z-Value");
			scbZAxis.setTypeAhead(true);
			scbZAxis.setEditable(false);
			scbZAxis.setTriggerAction(TriggerAction.ALL);

			String unit = layerItem.getZAxis().getUnits();
			for (Double value : layerItem.getZAxis().getValues()) {
				String key = value+unit;
				scbZAxis.setData(key, value);
				scbZAxis.add(key);
			}

			String firstElementDefaultSelect = layerItem.getZAxis().getValues().get(0)+unit;
			scbZAxis.setSimpleValue(firstElementDefaultSelect);
			//CHECK IF SIMPLE VALUE IS VALID
			if(scbZAxis.getSimpleValue().compareTo(firstElementDefaultSelect)==0){
				//SET DEFAULT VALUE CHANGED
				Double defZValue = layerItem.getZAxis().getValues().get(0);
				GWT.log("Selecting default z-axis: "+defZValue);
				layersPanelHandler.zAxisValueChanged(layerItem, defZValue);
			}

			scbZAxis.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

				@Override
				public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {

					String key = se.getSelectedItem().getValue();
					Double value = (Double) scbZAxis.getData(key);

					GWT.log("Selected z-axis key: "+key + ", value: "+value);
					layerItem.setZAxisSelected(value);
					layersPanelHandler.zAxisValueChanged(layerItem, value);
				}
			});
		}
	}


	/**
	 * Creates the text.
	 *
	 * @return the html
	 */
	private Html createText() {

		//Added by Francesco M.
		String title = layerItem.getTitle();
		int maxChars = 35;

		if(title.length()>maxChars){
			title = title.substring(0, maxChars)+"...";
		}

		Html newHtml = new Html("<div style='font-size:12px;'>"+title+"</div>");
		newHtml.setToolTip(layerItem.getTitle());
		newHtml.setStyleAttribute("overflow", "hidden");
		newHtml.addListener(Events.OnClick, new Listener<BaseEvent>(){
			@Override
			public void handleEvent(BaseEvent be) {
				toggleTools();
			}
		});
		return newHtml;
	}

	/**
	 * Creates the check box.
	 */
	private void createCheckBox() {
		checkBox = new CheckBox();
		checkBox.addListener(Events.Change, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent fe) {
				layerItem.setVisible(checkBox.getValue());
				if (checkBox.getValue())
					layersPanelHandler.showLayer(layerItem);
				else
					layersPanelHandler.hideLayer(layerItem);
            }
        });
		checkBox.setValue(layerItem.isVisible());
	}

	/**
	 * Creates the open button.
	 */
	private void createOpenButton() {
		imgOpenButton = new Image(ICON_TRIANGLE_RIGHT);
		imgOpenButton.setStyleName("imgCursor");
		//imgOpenButton.setSize("16px", "16px");
		imgOpenButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				toggleTools();
			}
		});
	}

	/**
	 * Creates the close layer button.
	 */
	private void createCloseLayerButton() {
		imgCloseLayerButton = new Image(ICON_CLOSE_LAYER);
//		imgOpenButton.setSize("16px", "16px");
		imgCloseLayerButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				layersPanelHandler.removeLayer(layerItem);
			}
		});
		imgCloseLayerButton.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				imgCloseLayerButton.setResource(ICON_CLOSE_LAYER_OVER);
			}
		});
		imgCloseLayerButton.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				imgCloseLayerButton.setResource(ICON_CLOSE_LAYER);
			}
		});
		imgCloseLayerButton.setTitle("Remove Layer");
		imgCloseLayerButton.setStyleName("imgCql");
	}

	/**
	 * Creates the tip images.
	 */
	private void createTipImages() {
		imgCqlTip = new Image(ICON_CQL_TIP);
		//imgOpenButton.setSize("16px", "16px");
		imgCqlTip.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				layersPanelHandler.removeFilterQuery(layerItem);
			}
		});
		imgCqlTip.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				imgCqlTip.setResource(ICON_CQL_TIP_DELETE);
			}
		});
		imgCqlTip.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				imgCqlTip.setResource(ICON_CQL_TIP);
			}
		});
		imgCqlTip.setTitle("Remove CQL Filter");
		imgCqlTip.setStyleName("imgCql");


		imgTransectTip = new Image(ICON_TRANSECT_TIP);
		//imgOpenButton.setSize("16px", "16px");
		imgTransectTip.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				imgTransectTip.setResource(ICON_TRANSECT_TIP);
				layersPanelHandler.deactivateTransect(layerItem);
			}
		});
		imgTransectTip.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				imgTransectTip.setResource(ICON_TRANSECT_TIP_DELETE);
			}
		});
		imgTransectTip.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				imgTransectTip.setResource(ICON_TRANSECT_TIP);
			}
		});
		imgTransectTip.setTitle("Remove Transect interaction");
		imgTransectTip.setStyleName("imgCql");
	}

	// open/close tools
	/**
	 * Toggle tools.
	 */
	protected void toggleTools() {
		if (isToolsOpened)
			this.remove(tools);
		else
			this.add(tools);
		this.layout();
		isToolsOpened = !isToolsOpened;
		imgOpenButton.setResource(isToolsOpened ? ICON_TRIANGLE_DOWN : ICON_TRIANGLE_RIGHT);
	}

	// sets transect info of a layer into node
	/**
	 * Sets the transect info if available.
	 */
	private void setTransectInfoIfAvailable() {
		if (!layerItem.isHasLegend())
			return;

		// search for transect by stylename
		boolean foundDefaultStyle = false;
		for (int i = 0; i < Constants.defaultStyleTransects.length && !foundDefaultStyle; i++) {
			String styleName = Constants.defaultStyleTransects[i][0];
			String fieldTransect = Constants.defaultStyleTransects[i][1];
			String tableTransect = Constants.defaultStyleTransects[i][2];

//			System.out.println("layerItem.getStyle() qui "+layerItem.getStyle());

			if (layerItem.getStyle()!=null && layerItem.getStyle().contentEquals(styleName)) {
				this.fieldTransect = fieldTransect;
				this.tableTransect = tableTransect;
				foundDefaultStyle = true;
			}
		}

		List<Property> result = layerItem.getProperties();

		System.out.println("property "+layerItem.getProperties());
		if(result!=null && result.size()>0){

			boolean cointainsPropertyTransect = false;

			if (layerItem.containsProperty("maxspeciescountinacell")) {
				fieldTransect = "maxspeciescountinacell";
				tableTransect = validateTableName(":", layerItem.getName());
				cointainsPropertyTransect = true;
			} else if (layerItem.containsProperty("probability")) {

//				System.out.println("layerItem.containsProperty ");
				fieldTransect = "probability";
				tableTransect = validateTableName(":", layerItem.getName());
				cointainsPropertyTransect = true;
			}

			GWT.log("layerItem: "+layerItem.getName() + " has properties "+layerItem.getProperties()+" contains property transect: "+cointainsPropertyTransect);

			if(cointainsPropertyTransect)
				activeTransect();
		}
	}

	/**
	 * Update property info.
	 *
	 * @param activeTransectIfAvailable the active transect if available
	 */
	private void updatePropertyInfo(final boolean activeTransectIfAvailable){

		GisViewer.service.getListProperty(layerItem.getGeoserverUrl(), layerItem, new AsyncCallback<List<Property>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("error on get list property", caught);
				GWT.log("updatePropertyInfo FAIL!");
			}

			@Override
			public void onSuccess(List<Property> result) {
				layerItem.setProperties(result);

				if(activeTransectIfAvailable)
					setTransectInfoIfAvailable();
			}
		});
	}



	/**
	 * Remove namespace from layer name.
	 * Used for transect
	 *
	 * @param charSplit the char split
	 * @param layerName the layer name
	 * @return the string
	 */
	public static String validateTableName(String charSplit, String layerName){

		if(layerName!=null && layerName.length()>1){

			int index = layerName.indexOf(charSplit);

			if(index>=0)
				return layerName.substring(index+1, layerName.length());
		}

		return layerName;
	}

	/**
	 * Gets the layer item.
	 *
	 * @return the layer item
	 */
	public LayerItem getLayerItem() {
		return layerItem;
	}

	/**
	 * Sets the cql tip.
	 *
	 * @param show the new cql tip
	 */
	public void setCqlTip(boolean show) {
		if (show) {
			if (!cqlTipInserted) {
				cp.insert(imgCqlTip, 3);
				cqlTipInserted = true;
			}
		} else
			if (cqlTipInserted) {
				cp.remove(imgCqlTip);
				cqlTipInserted = false;
			}
	}

	/**
	 * Sets the transect tip.
	 *
	 * @param show the new transect tip
	 */
	public void setTransectTip(boolean show) {
		if (show) {
			if (!transectTipInserted) {
				cp.insert(imgTransectTip, cqlTipInserted ? 4 : 3);
				transectTipInserted = true;
			}
		} else
			if (transectTipInserted) {
				cp.remove(imgTransectTip);
				transectTipInserted = false;
			}
	}

	/**
	 * Sets the check visible.
	 *
	 * @param isVisible the new check visible
	 */
	protected void setCheckVisible(boolean isVisible) {
		checkBox.setValue(isVisible);
	}

}
