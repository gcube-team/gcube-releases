package gr.uoa.di.madgik.visualisations.PieChartPlato.client;


import gr.uoa.di.madgik.visualisations.client.injectors.JsResources;
import gr.uoa.di.madgik.visualisations.client.injectors.JSInjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;


public class PieChartPlato extends AbsolutePanel implements EntryPoint{

	
//	private final static String sampleJSON = "[{\"label\":\"One\",\"value\":29.765957771107},{\"label\":\"Δύο\",\"value\":0},{\"label\":\"Three\",\"value\":32.807804682612},{\"label\":\"Four\",\"value\":196.45946739256},{\"label\":\"Five\",\"value\":0.19434030906893},{\"label\":\"Six\",\"value\":98.079782601442},{\"label\":\"Seven\",\"value\":13.925743130903},{\"label\":\"Eight\",\"value\":5.1387322875705}]";

	public void onModuleLoad() {
		
		JSInjector.inject(JsResources.INSTANCE.piePlatoJS().getText());
		
	}
	

	
	private void createdivelem(){
		HTML html = new HTML("<div id='piechart'></div>");
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(html);
	
	}
	
	/**
	 * 
	 * @param divID
	 * @param width
	 * @param height
	 * @param showLabels
	 * @param showLegend
	 * @param type if "simple" it returns a simple pie chart. Anything else, it returns a little different pie chart 
	 * @param dataJSON
	 */
	public static native void createPieChart(String divID, String width, String height, boolean showLabels, boolean showLegend, String type,  String dataJSON) /*-{
		var divElem = $wnd.$("#"+divID);
		// clear all the previous content within the div (clear all html)
		divElem.empty();
		
		//clear also the styling
		divElem.removeAttr("style");
		
//		// set div size same as the visualisation's (in order for the library to work correctly)
//		if(width!=0){ //not case of popup panel
//			divElem.css("width", $wnd.$(".column.left").width());
//		}
		if(height!=0) //not case of popup panel
			divElem.css("height", height);
			
			
		//divElem.css("margin", "0 auto");
		//divElem.css("padding", "0px");

		// create an svg inside the div (required by the library)
		divElem.append("<svg></svg>");
		$wnd.addPieChart(divID, dataJSON, showLabels, showLegend, type);
		
	}-*/;

	public static native void updatePieChart (String divID, String dataJSON)/*-{
		var divElem = $wnd.$("#"+divID);
		$wnd.piechart_update(divID, dataJSON);
	}-*/;

	public static native void refreshPieChart (String divID)/*-{
		var divElem = $wnd.$("#"+divID);
		$wnd.piechart_refresh(divID);
	}-*/;
	
	
	
}