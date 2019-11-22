/**
 *
 */
package org.gcube.portlets.widgets.ckandatapublisherwidget.client.openlayerwidget;

import org.gcube.portlets.widgets.openlayerbasicwidgets.client.event.SelectAreaDialogEvent;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.event.SelectAreaDialogEvent.SelectAreaDialogEventHandler;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.event.SelectAreaDialogEventType;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.widgets.AreaSelectionDialog;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.widgets.GeometryType;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Command;



/**
 * The Class GeoJsonAreaSelectionDialog.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 20, 2019
 */
public class GeoJsonAreaSelectionDialog extends AreaSelectionDialog{


	private String wktArea;
	private Command onResponseCommand;
	private ListBox geometries;

	/**
	 * Instantiates a new geo json area selection dialog.
	 *
	 * @param geometry the geometry
	 */
	public GeoJsonAreaSelectionDialog(GeometryType geometry) {
		super(geometry);

		//THE HANDLER
		SelectAreaDialogEventHandler handler = new SelectAreaDialogEventHandler() {

			@Override
			public void onResponse(SelectAreaDialogEvent event) {
				print("SelectAreaDialog Response: "+event);

				if(event==null)
					return;

				SelectAreaDialogEventType closedType = event.getSelectAreaDialogEventType();

				if(closedType==null)
					return;

				wktArea = null;
				if(closedType.equals(SelectAreaDialogEventType.Completed)){
					wktArea = event.getArea();
					onResponseCommand.execute();
				}
			}
		};

		addSelectAreaDialogEventHandler(handler);
		this.getElement().addClassName("GeoJson-DialogBox");
		setZIndex(10000);
	}


	/**
	 * Fire command on response.
	 *
	 * @param command the command
	 */
	public void fireCommandOnResponse(Command command){
		this.onResponseCommand = command;
	}


	/**
	 * Convert wkt to geo json.
	 *
	 * @param wktData the wkt data
	 * @return the string
	 */
	private static native String convertWKTToGeoJSON(String wktData) /*-{
		try {
			var ol = $wnd.ol;
			var geojson_options = {};
			var wkt_format = new ol.format.WKT();
			var wktFeature = wkt_format.readFeature(wktData);
			//console.log('WKT feature: '+wktFeature);
			var wkt_options = {};
			var geojson_format = new ol.format.GeoJSON(wkt_options);
			console.log('geojson_format: '+geojson_format);
			var geoJsonFeature = geojson_format.writeFeature(wktFeature);
			//console.log('GeoJSON Feature: '+geoJsnameonFeature);
			return geoJsonFeature;
		}catch(err) {
  			console.log(err.message);
  			return null;
		}
	}-*/;

	/**
	 * Prints the.
	 *
	 * @param txt the txt
	 * @return the string
	 */
	public static native String print(String txt) /*-{
		console.log(txt)
	}-*/;


	/**
	 * Wkt to geo json.
	 *
	 * @param wktTxt the wkt txt
	 * @return the string
	 * @throws Exception the exception
	 */
	public String wktToGeoJSON(String wktTxt) throws Exception{
		try {
			String geoJSON = convertWKTToGeoJSON(wktTxt);
			//Window.alert("geoJSON: "+geoJSON);
			print("geoJSON: "+geoJSON);
	
			if(geoJSON==null)
				throw new Exception();
	
			JavaScriptObject toJSON = JsonUtils.safeEval(geoJSON);
			JSONObject objJson = new JSONObject(toJSON);
			return objJson.get("geometry").toString();
		}catch(Exception e) {
			//silent
			throw new Exception("Sorry, an error occurred while getting GeoJSON format for the drawn Geometry");
		}
	}

	/**
	 * Gets the WKT to geo json.
	 *
	 * @return the WKT to geo json
	 * @throws Exception the exception
	 */
	public String getWKTToGeoJSON() throws Exception{

		if(wktArea==null){
			print("wktArea is null");
			throw new Exception("Sorry, an error occurred while reading the drawn Geometry");
		}
		//print("wktArea is: "+wktArea);
		return wktToGeoJSON(wktArea);
	}


	/**
	 * Gets the wkt area.
	 *
	 * @return the wktArea
	 */
	public String getWktArea() {

		return wktArea;
	}

	/**
	 * Gets the geometries.
	 *
	 * @return the geometries
	 */
	public static ListBox getGeometries() {
		ListBox geometries = new ListBox();
		
		//see Feature #13074
//		for (GeometryType geometry : GeometryType.values()) {
//			geometries.addItem(geometry.name(),geometry.name());
//		}
		
		geometries.addItem(GeometryType.Point.name(), GeometryType.Point.name());
		geometries.addItem(GeometryType.LineString.name(), GeometryType.LineString.name());
		geometries.addItem(GeometryType.Triangle.name(), GeometryType.Triangle.name());
		geometries.addItem(GeometryType.Square.name(), GeometryType.Square.name());
		geometries.addItem(GeometryType.Box.name(), GeometryType.Box.name());
		return geometries;
	}
	
	/**
	 * To geometry.
	 *
	 * @param name the name
	 * @return the geometry type
	 */
	public static GeometryType toGeometry(String name){
		
		GeometryType theGeom = null;
		try {
			theGeom = GeometryType.valueOf(name);
		}catch (Exception e) {
			//silent
		}
		
		return theGeom;
	}
	
	
}
