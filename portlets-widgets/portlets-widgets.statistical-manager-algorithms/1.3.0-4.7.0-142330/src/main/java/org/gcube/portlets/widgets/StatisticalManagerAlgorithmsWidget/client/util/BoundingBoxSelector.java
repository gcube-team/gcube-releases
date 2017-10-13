/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.util;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeature.FeatureAddedListener;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.NavToolbar;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Geometry;
import org.gwtopenmaps.openlayers.client.handler.RegularPolygonHandler;
import org.gwtopenmaps.openlayers.client.handler.RegularPolygonHandlerOptions;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

/**
 * @author ceras
 *
 */
public abstract class BoundingBoxSelector extends Dialog {

	public static Bounds defaultBounds = new Bounds(-180, -90, 180, 90);// -180, -90, 180, 90 //-20037508.34,-20037508.34,20037508.34,20037508.34);// TODO -180, -90, 180, 90);
	private MapWidget mapWidget;
	private Map map;
	private DrawFeature drawBoxControl;
	private Bbox bbox=null;

	public BoundingBoxSelector() {
		super();
		this.setLayout(new FitLayout());
		this.setSize("640", "480");
		this.setHeading(".: Bounding Box Selector");
				this.setButtons(Dialog.OKCANCEL);
				this.setHideOnButtonClick(true);

		MapOptions mapOptions = new MapOptions();
		mapOptions.removeDefaultControls();
		mapOptions.setNumZoomLevels(12);
		mapOptions.setProjection("EPSG:4326");

		initMapWidget(mapOptions);
	}
	
	public abstract void onBoundingBoxSelected(Bbox bbox);

	/**
	 * @param mapOptions
	 */
	private void initMapWidget(MapOptions mapOptions) {
		mapWidget = new MapWidget("350px", "350px", mapOptions);
		map = mapWidget.getMap();
		
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("truemarble");
//		wmsParams.setStyles("");

		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setUntiled();
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
		
		WMS wmsLayer = new WMS(
                "Basic WMS",
                "http://romeo.jrc.it/maps/mapserv.cgi?map=../mapfiles/acpmap_static.map&",
                wmsParams,
                wmsLayerParams);

		map.addLayers(new Layer[] { wmsLayer });
		map.addControl(new PanZoomBar());
		map.addControl(new NavToolbar());
		map.addControl(new MousePosition());
		map.addControl(new LayerSwitcher());
		map.setMaxExtent(defaultBounds);
		map.setCenter(new LonLat(0, 0), 2);

		this.add(mapWidget);
		addBoxControl();		
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);

		this.addListener(Events.Hide, new Listener<WindowEvent>() {  
			public void handleEvent(WindowEvent be) {  
				if (be.getButtonClicked()==getButtonById("ok") && bbox!=null)
					onBoundingBoxSelected(bbox);
			}  
		});  
	}


	private void addBoxControl() {
		VectorOptions vectoroptions = new VectorOptions();
		vectoroptions.setDisplayInLayerSwitcher(false);
		vectoroptions.setDisplayOutsideMaxExtent(true);
		vectoroptions.setProjection("EPSG:4326");
		final Vector vectorLayer = new Vector("transet", vectoroptions);
		map.addLayer(vectorLayer);

		FeatureAddedListener listener = new FeatureAddedListener() {
			private VectorFeature prevVf;

			@Override
			public void onFeatureAdded(VectorFeature vf) {
				if (prevVf!=null)
					vectorLayer.removeFeature(prevVf);
				prevVf = vf;
//				vectorLayer.setZIndex(MAX_ZINDEX);
				
				Geometry geo = vf.getGeometry();
                Bounds bounds = geo.getBounds();
                double x1 = bounds.getLowerLeftX();
                double y1 = bounds.getLowerLeftY();
                double x2 = bounds.getUpperRightX();
                double y2 = bounds.getUpperRightY();
                
                bbox = new Bbox(x1, y1, x2, y2);
                System.out.println(bbox.toString());
			}
		};
		
//		vectorLayer.setZIndex(MAX_ZINDEX);

		
		DrawFeatureOptions options = new DrawFeatureOptions();
		options.onFeatureAdded(listener);
		RegularPolygonHandlerOptions handlerOptions = new RegularPolygonHandlerOptions();
		handlerOptions.setSides(4);
		handlerOptions.setIrregular(true);
		options.setHandlerOptions(handlerOptions);
		drawBoxControl = new DrawFeature(vectorLayer, new RegularPolygonHandler(), options);
		this.map.addControl(drawBoxControl);
		drawBoxControl.activate();
	}

	
	public class Bbox {
		private double x1,y1,x2,y2;
		
		public Bbox(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
		/**
		 * @return the x1
		 */
		public double getX1() {
			return x1;
		}
		
		/**
		 * @return the x2
		 */
		public double getX2() {
			return x2;
		}
		
		/**
		 * @return the y1
		 */
		public double getY1() {
			return y1;
		}
		
		/**
		 * @return the y2
		 */
		public double getY2() {
			return y2;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "bbox ("+x1+","+y1+") - ("+x2+","+y2+")";
		}
		
	}

}
