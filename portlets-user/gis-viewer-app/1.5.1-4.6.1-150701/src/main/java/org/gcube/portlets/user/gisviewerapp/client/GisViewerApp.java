package org.gcube.portlets.user.gisviewerapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GisViewerApp implements EntryPoint {

  public static final String GISVIEWERAPPDIV = "gisviewerapplication";

  private ApplicationController appController;
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  
	  appController = new ApplicationController();
	  appController.go(RootPanel.get(GISVIEWERAPPDIV));
	  
	  Window.addResizeHandler(new ResizeHandler() {
          @Override
          public void onResize(ResizeEvent event) {
                  System.out.println("onWindowResized width: "+event.getWidth()+" height: "+event.getHeight());
                  updateSize();
          }
		 });
		 
	 updateSize();
    
	/*
	XYZOptions options = new XYZOptions();
	options.setNumZoomLevels(Constants.numZoomLevels);
	options.setSphericalMercator(true);
	options.setDisplayInLayerSwitcher(true);
	options.setIsBaseLayer(true);
	

	String name = "MapBox";
	String url = "https://b.tiles.mapbox.com/v4/examples.map-2k9d7u0c/${z}/${x}/${y}.png?access_token=pk.eyJ1IjoidHJpc3RlbiIsImEiOiJiUzBYOEJzIn0.VyXs9qNWgTfABLzSI3YcrQ";
	XYZ layer = new XYZ(name, url, options);

	MapWidget mapWidget = appController.getGisViewerPanel().getOpenLayersMap().getMapWidget();
	LonLat lonLat = new LonLat(6.95, 50.94);
    lonLat.transform("EPSG:4326", "EPSG:900913");
    mapWidget.getMap().addLayer(layer);
	mapWidget.getMap().setCenter(lonLat);*/

	 
	/* OSMOptions options = new OSMOptions();
     options.setNumZoomLevels(Constants.numZoomLevels);
     options.setProjection(Constants.defaultProjection);
     options.crossOriginFix();
    
     MapWidget mapWdg = appController.getGisViewerPanel().getOpenLayersMap().getMapWidget();
     OSM osm1 = new OSM("OSM Cycle","http://c.tile.opencyclemap.org/cycle/${z}/${x}/${y}.png", options);
     osm1.setIsBaseLayer(true);
     mapWdg.getMap().addLayer(osm1);
     
     OSM osm2 = new OSM("OSM Default","http://a.tile.openstreetmap.org/${z}/${x}/${y}.png", options);
     osm2.setIsBaseLayer(true);
     mapWdg.getMap().addLayer(osm2);

     OSM osm3 = new OSM("OSM Humaritarian","http://b.tile.openstreetmap.fr/hot/${z}/${x}/${y}.png", options);
     osm3.setIsBaseLayer(true);
     mapWdg.getMap().addLayer(osm3);*/
     
	 /*
	 OSM osmMapnik = OSM.Mapnik("Mapnik");
     OSM osmCycle = OSM.CycleMap("CycleMap");

     osmMapnik.setIsBaseLayer(true);
     osmCycle.setIsBaseLayer(true);

 	MapWidget mapWidget = appController.getGisViewerPanel().getOpenLayersMap().getMapWidget();
 	
     mapWidget.getMap().addLayer(osmMapnik);
     mapWidget.getMap().addLayer(osmCycle);

     LonLat lonLat = new LonLat(6.95, 50.94);
     lonLat.transform("EPSG:4326", mapWidget.getMap().getProjection()); //transform lonlat (provided in EPSG:4326) to OSM coordinate system (the map projection)
     mapWidget.getMap().setCenter(lonLat, Constants.numZoomLevels);
     */
     
	 appController.getGisViewerPanel().showIntro();
  }
  
  /**
	 * Update window size
	 */
  public void updateSize(){
  	
	    RootPanel workspace = RootPanel.get(GISVIEWERAPPDIV);
	    int topBorder = workspace.getAbsoluteTop();
	    int leftBorder = workspace.getAbsoluteLeft();
	    int footer = 2; //footer is bottombar + sponsor
	    int rootHeight = (Window.getClientHeight() - topBorder - 4 - footer);// - ((footer == null)?0:(footer.getOffsetHeight()-15));
	    int rootWidth = Window.getClientWidth() - 2* leftBorder; //- rightScrollBar;
	    
	    System.out.println("New workspace dimension Height: "+rootHeight+" Width: "+rootWidth);
	    
		appController.getMainPanel().setHeight(rootHeight);
	    appController.getMainPanel().setWidth(rootWidth);

		appController.moveWMSBalloonPosition();
		
//	    appController.getGisViewerPanel().getOpenLayersMap().centerMapCurrentZoom();
  }
}
