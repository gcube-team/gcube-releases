package gr.uoa.di.madgik.visualisations.HorizontalBarsPlato.client;


import gr.uoa.di.madgik.visualisations.client.injectors.JSInjector;
import gr.uoa.di.madgik.visualisations.client.injectors.JsResources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class HorizontalBarsPlato extends AbsolutePanel implements EntryPoint{

	
//	private final static String sampleJSON = "[{\"name\":\"Νικόλας\",\"value\":15},{\"name\":\"Πλάτωνας\",\"value\":20},{\"name\":\"Σωκράτης\",\"value\":23},{\"name\":\"Κανένας\",\"value\":26}]";

//	private final static String sampleJSON = "[{\"key\": \"Series 1\",\"color\": \"#d67777\",\"values\": [{ \"label\" : \"Group A\" ,\"value\" : -1.8746444827653} , { \"label\" : \"Group B\" ,\"value\" : -8.0961543492239} , { \"label\" : \"Group C\" ,\"value\" : -0.57072943117674} , { \"label\" : \"Group D\" ,\"value\" : -2.4174010336624} , {\"label\" : \"Group E\" ,\"value\" : -0.72009071426284} , { \"label\" : \"Group F\" ,\"value\" : -0.77154485523777} , { \"label\" : \"Group G\" ,\"value\" : -0.90152097798131} , {\"label\" : \"Group H\" ,\"value\" : -0.91445417330854} , { \"label\" : \"Group I\" ,\"value\" : -0.055746319141851}]},{\"key\": \"Series 2\",\"color\": \"#4f99b4\",\"values\": [{ \"label\" : \"Group A\" ,\"value\" : 25.307646510375} , { \"label\" : \"Group B\" ,\"value\" : 16.756779544553} , { \"label\" : \"Group C\" ,\"value\" : 18.451534877007} , { \"label\" : \"Group D\" ,\"value\" : 8.6142352811805} , {\"label\" : \"Group E\" ,\"value\" : 7.8082472075876} , { \"label\" : \"Group F\" ,\"value\" : 5.259101026956} , { \"label\" : \"Group G\" ,\"value\" : 0.30947953487127} , { \"label\" : \"Group H\" ,\"value\" : 0} , { \"label\" : \"Group I\" ,\"value\" : 0 }]}]";
	
	
	public void onModuleLoad() {
		JSInjector.inject(JsResources.INSTANCE.d3barchartPlatoJS().getText());
	}
	
	
	/**
	 * 
	 * @param divID
	 * @param width if width=0 and height=0, then it is resizable 
	 * @param height
	 * @param showValues
	 * @param showTooltips
	 * @param showControls
	 * @param dataJSON
	 */
	public static native void createHorizontalBarChart(String divID, String width, String height, boolean showValues, boolean showTooltips, boolean showControls, String dataJSON) /*-{
		var divElem = $wnd.$("#"+divID);
		// clear all the previous content within the div (clear all html)
		divElem.empty();
		
		//clear also the styling
		divElem.removeAttr("style");
		
//		// set div size same as the visualisation's (in order for the library to work correctly)
//		if(width!=0){ //not case of popup panel
//			divElem.css("width", $wnd.$(".column.left").width());
//		}
		if(height!=0){ //not case of popup panel
			divElem.css("height", height);
		}
		
		
		//divElem.css("margin", "0 auto");
		//divElem.css("padding", "0px");
		
		// create an svg inside the div (required by the library)
		divElem.append("<svg></svg>");
		
		$wnd.barchart(divID, showValues, showTooltips, showControls, dataJSON);
		
	}-*/;

	public static native void updateBarChart (String divID, String dataJSON)/*-{
		var divElem = $wnd.$("#"+divID);
		$wnd.barchart_update(divID, dataJSON);
	}-*/;

	public static native void refreshBarChart (String divID)/*-{
		var divElem = $wnd.$("#"+divID);
		$wnd.barchart_refresh(divID);
	}-*/;
	
	
	
}