package org.gcube.portlets.widgets.openlayerbasicwidgets.client.widgets;

import org.gcube.portlets.widgets.openlayerbasicwidgets.client.event.SelectAreaDialogEvent;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.event.SelectAreaDialogEventType;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.resource.OLBasicResources;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.util.GWTMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AreaSelectionDialog extends DialogBox implements
		SelectAreaDialogEvent.HasSelectAreaDialogEventHandler {
	private static final String COMBO_GEOMETY_TYPE_WIDTH = "406px";
	private static final boolean RESIZABLE = false;
	private static final boolean COLLAPSIBLE = true;
	private String dialogTitle = "Draw a Geometry";
	private HandlerRegistration resizeHandlerRegistration;
	private Node closeEventTarget = null;
	private int zIndex = -1;

	private boolean ShowAllGeometryGeometry;

	private ListBox comboGeometryType;
	private TextArea wktGeometry;
	private String wktData;
	private GeometryType initialGeometry;

	public void setWktGeometry(String wktData) {
		// wktData = wktData;
		GWT.log("WktData: " + wktData);
		wktGeometry.setValue(wktData);

	}

	public AreaSelectionDialog() {
		try {
			ShowAllGeometryGeometry = true;
			initialGeometry = GeometryType.Polygon;
			initWindow();
			initHandler();
			addToolIcon();
			create();
		} catch (Throwable e) {
			GWT.log(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	public AreaSelectionDialog(GeometryType geometryType) {
		try {
			ShowAllGeometryGeometry = false;
			initialGeometry = geometryType;
			dialogTitle = "Draw a " + geometryType.getLabel();
			initWindow();
			initHandler();
			addToolIcon();
			create();
		} catch (Throwable e) {
			GWT.log(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void initWindow() {
		GWT.log(dialogTitle);
		OLBasicResources.INSTANCE.olBasicCSS().ensureInjected();
		setModal(true);
		setGlassEnabled(true);
		setAnimationEnabled(true);
		setText(dialogTitle);

	}

	private void initHandler() {
		resizeHandlerRegistration = Window
				.addResizeHandler(new ResizeHandler() {

					@Override
					public void onResize(ResizeEvent event) {
						center();

					}
				});

	}

	private void create() {

		SimplePanel areaSelectionPanel = new SimplePanel();
		areaSelectionPanel.setStyleName(OLBasicResources.INSTANCE.olBasicCSS()
				.getAreaSelectionPanel());

		HTML mapContainer = new HTML(
				"<div id='openLayerMap' class='openLayerMap' style='width:500px;height:250px;'></div>");

		if (ShowAllGeometryGeometry) {
			comboGeometryType = new ListBox();
			comboGeometryType.getElement().setId("combo-geometry-type");
			
			//comboGeometryType.getElement().getStyle().setProperty("width", "406px!important");
						
			/*comboGeometryType.addStyleName(OLBasicResources.INSTANCE
					.olBasicCSS().getComboGeometryType());*/

			comboGeometryType.setWidth(COMBO_GEOMETY_TYPE_WIDTH);
			comboGeometryType.setTabIndex(10001);
			comboGeometryType.ensureDebugId("comboGeometryType");
			for (GeometryType gType : GeometryType.asList()) {
				comboGeometryType.addItem(gType.getLabel());
			}

			comboGeometryType.setSelectedIndex(initialGeometry.ordinal());
			comboGeometryType.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int selected = comboGeometryType.getSelectedIndex();
					if (selected >= 0) {
						String geoLabel = comboGeometryType.getValue(selected);
						GeometryType gType = GeometryType
								.getFromLabel(geoLabel);
						onChangeTypeSelect(gType.getId());
					}

				}
			});
		}

		wktGeometry = new TextArea();
		wktGeometry.setReadOnly(true);
		wktGeometry.getElement().getStyle().setProperty("resize", "none");
		wktGeometry.setVisibleLines(5);
		wktGeometry.setTabIndex(10002);
		wktGeometry.getElement().setId("wkt-geometry-text-area");
			
		/*
		wktGeometry.addStyleName(OLBasicResources.INSTANCE.olBasicCSS()
				.getWKTGeometryTextArea());
		*/
		// ////////
		// Form
		FlexTable layout = new FlexTable();
		layout.setCellSpacing(10);

		FlexTable selectGeometryFlexTable = new FlexTable();
		selectGeometryFlexTable.setStyleName(OLBasicResources.INSTANCE
				.olBasicCSS().getAreaSelectionContent());
		selectGeometryFlexTable.setCellSpacing(2);

		if (ShowAllGeometryGeometry) {

			selectGeometryFlexTable.setHTML(1, 0, "Geometry:");
			selectGeometryFlexTable.setWidget(1, 1, comboGeometryType);

			selectGeometryFlexTable.setHTML(2, 0, "WKT:");
			selectGeometryFlexTable.setWidget(2, 1, wktGeometry);
		} else {
			selectGeometryFlexTable.setHTML(1, 0, "WKT:");
			selectGeometryFlexTable.setWidget(1, 1, wktGeometry);
		}

		areaSelectionPanel.add(selectGeometryFlexTable);

		// setContent(areaSelectionPanel);

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);

		dialogContents.add(mapContainer);
		dialogContents.setCellHorizontalAlignment(mapContainer,
				HasHorizontalAlignment.ALIGN_CENTER);

		dialogContents.add(areaSelectionPanel);
		dialogContents.setCellHorizontalAlignment(areaSelectionPanel,
				HasHorizontalAlignment.ALIGN_CENTER);

		// Add Button
		Button btnSave = new Button("Save");
		btnSave.getElement().getStyle().setMarginLeft(4, Unit.PX);
		btnSave.getElement().getStyle().setMarginRight(4, Unit.PX);
		btnSave.setTabIndex(10003);
		btnSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				btnSavePressed();

			}
		});

		Button btnClose = new Button("Close");
		btnClose.getElement().getStyle().setMarginLeft(4, Unit.PX);
		btnClose.getElement().getStyle().setMarginRight(4, Unit.PX);
		btnClose.setTabIndex(10004);
		btnClose.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				btnClosePressed();

			}
		});

		FlowPanel buttonPack = new FlowPanel();
		buttonPack.setWidth("100%");
		buttonPack.add(btnSave);
		buttonPack.add(btnClose);

		dialogContents.add(buttonPack);
		dialogContents.setCellHorizontalAlignment(buttonPack,
				HasHorizontalAlignment.ALIGN_CENTER);

		setWidget(dialogContents);

	}

	@Override
	public void show() {
		super.show();
		center();
		initMap(this, initialGeometry.getId());

	};

	private void btnClosePressed() {
		SelectAreaDialogEvent event = new SelectAreaDialogEvent(
				SelectAreaDialogEventType.Aborted);
		fireEvent(event);
		hide();

	}

	private void btnSavePressed() {
		String area = wktGeometry.getValue();
		if (area == null || area.isEmpty()) {
			GWTMessages.alert("Attention", "Select a valid area!", zIndex);
		} else {
			SelectAreaDialogEvent event = new SelectAreaDialogEvent(
					SelectAreaDialogEventType.Completed);
			event.setArea(area);
			fireEvent(event);
			hide();
		}

	}

	private static native void onChangeTypeSelect(String value) /*-{
		//window.alert("Map: "+$wnd.olMap)
		$wnd.olMap.removeInteraction($wnd.draw)
		$wnd.addInteraction(value);
	}-*/;

	private static native void initMap(AreaSelectionDialog instance,
			String initialGeometry) /*-{

		//window.alert("ol: "+$wnd.ol);
		var ol = $wnd.ol;
		$wnd.raster = new ol.layer.Tile({
			source : new ol.source.OSM()
		});

		$wnd.source = new ol.source.Vector({
			wrapX : false
		});

		$wnd.vector = new ol.layer.Vector({
			source : $wnd.source,
			style : new ol.style.Style({
				fill : new ol.style.Fill({
					color : 'rgba(255, 255, 255, 0.2)'
				}),
				stroke : new ol.style.Stroke({
					color : '#ffcc33',
					width : 2
				}),
				image : new ol.style.Circle({
					radius : 7,
					fill : new ol.style.Fill({
						color : '#ffcc33'
					})
				})
			})
		});

		//window.alert("Retrieve map Id: "+$doc.getElementById('map'));

		$wnd.olMap = new ol.Map({
			layers : [ $wnd.raster, $wnd.vector ],
			target : 'openLayerMap',
			view : new ol.View({
				projection: 'EPSG:4326',
				center : [ 0, 0 ],
				zoom : 0
			})
		});
		//window.alert("olMap: "+$wnd.olMap);

		//
		var draw; // global so we can remove it later
		var features = new ol.Collection();
		$wnd.ondrawstart = function(e) {
			$wnd.source.clear();
		};

		$wnd.ondrawend = function(e) {
			console.log(e.feature);
			var wkt = new ol.format.WKT;
			var geoWKT = wkt.writeFeature(e.feature);
			console.log(geoWKT);
			instance.@org.gcube.portlets.widgets.openlayerbasicwidgets.client.widgets.AreaSelectionDialog::setWktGeometry(Ljava/lang/String;)(geoWKT);
		};

		$wnd.addInteraction = function addInteraction(value) {
			if (value !== 'None') {
				var geometryFunction, maxPoints;
				if (value === 'Square') {
					value = 'Circle';
					geometryFunction = ol.interaction.Draw
							.createRegularPolygon(4);
				} else {
					if (value === 'Box') {
						value = 'LineString';
						maxPoints = 2;
						geometryFunction = function(coordinates, geometry) {
							if (!geometry) {
								geometry = new ol.geom.Polygon(null);
							}
							var start = coordinates[0];
							var end = coordinates[1];
							geometry.setCoordinates([ [ start,
									[ start[0], end[1] ], end,
									[ end[0], start[1] ], start ] ]);
							return geometry;
						}
					} else {
						if (value === 'Circle') {
							geometryFunction = ol.interaction.Draw
									.createRegularPolygon(32);

						} else {
							if (value === 'Triangle') {
								value = 'Circle';
								geometryFunction = ol.interaction.Draw
										.createRegularPolygon(3);

							} else {
								if (value === 'Pentagon') {
									value = 'Circle';
									geometryFunction = ol.interaction.Draw
											.createRegularPolygon(5);

								} else {
									if (value === 'Hexagon') {
										value = 'Circle';
										geometryFunction = ol.interaction.Draw
												.createRegularPolygon(6);

									}
								}
							}
						}

					}
				}
				$wnd.draw = new ol.interaction.Draw({
					source : $wnd.source,
					type : (value),
					features : $wnd.features,
					geometryFunction : geometryFunction,
					maxPoints : maxPoints
				});
				$wnd.draw.on('drawend', $wnd.ondrawend);
				$wnd.draw.on('drawstart', $wnd.ondrawstart);
				$wnd.olMap.addInteraction($wnd.draw);
			}
		}

		// Handle change event.

		//typeSelect.onchange = function() {
		//	olMap.removeInteraction(draw);
		//	addInteraction();
		//};

		$wnd.addInteraction(initialGeometry);
	}-*/;

	private void addToolIcon() {

		// get the "dialogTopRight" class td
		Element dialogTopRight = getCellElement(0, 2);

		// close button image html
		dialogTopRight.setInnerHTML("<div  class='"
				+ OLBasicResources.INSTANCE.olBasicCSS()
						.getDialogToolButtonText()
				+ "'>"
				+ "<img src='"
				+ OLBasicResources.INSTANCE.toolButtonClose20().getSafeUri()
						.asString()
				+ "' class='"
				+ OLBasicResources.INSTANCE.olBasicCSS()
						.getDialogToolButtonIcon() + "' /></div>");

		// set the event target
		closeEventTarget = dialogTopRight.getChild(0).getChild(0);
	}

	@Override
	public void hide() {
		if (resizeHandlerRegistration != null) {
			resizeHandlerRegistration.removeHandler();
			resizeHandlerRegistration = null;
		}
		super.hide();
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();

		if (!event.isCanceled() && (event.getTypeInt() == Event.ONCLICK)
				&& isCloseEvent(nativeEvent)) {
			final SelectAreaDialogEvent wizardEvent = new SelectAreaDialogEvent(
					SelectAreaDialogEventType.Aborted);
			fireEvent(wizardEvent);
			this.hide();
		}
		super.onPreviewNativeEvent(event);
	}

	// see if the click target is the close button
	private boolean isCloseEvent(NativeEvent event) {
		return event.getEventTarget().equals(closeEventTarget); // compares
																// equality of
																// the
																// underlying
																// DOM elements
	}

	@Override
	public HandlerRegistration addSelectAreaDialogEventHandler(
			SelectAreaDialogEvent.SelectAreaDialogEventHandler handler) {
		return addHandler(handler, SelectAreaDialogEvent.getType());
	}

	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
		getGlassElement().getStyle().setZIndex(zIndex);
		getElement().getStyle().setZIndex(zIndex + 1);

	}

	public int getZIndex() {
		return zIndex;
	}

}
