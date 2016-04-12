package gr.uoa.di.madgik.visualisations.PieChart.client;

import gr.uoa.di.madgik.visualisations.client.injectors.JsResources;
import gr.uoa.di.madgik.visualisations.client.injectors.JSInjector;

import java.util.Properties;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;


public class PieChart extends AbsolutePanel implements EntryPoint{

	
	private final static String sampleJSON = "[{\"label\":\"One\",\"value\":29.765957771107},{\"label\":\"Δύο\",\"value\":0},{\"label\":\"Three\",\"value\":32.807804682612},{\"label\":\"Four\",\"value\":196.45946739256},{\"label\":\"Five\",\"value\":0.19434030906893},{\"label\":\"Six\",\"value\":98.079782601442},{\"label\":\"Seven\",\"value\":13.925743130903},{\"label\":\"Eight\",\"value\":5.1387322875705}]";

	public void onModuleLoad() {
		
//		AllResources bundle = GWT.create(AllResources.class);
		JSInjector.inject(JsResources.INSTANCE.pieJS().getText());
		
//		Window.alert("Running PieChart component's onModuleLoad");
		
//		createdivelem();
//		createPieChart("piechart", "500", "500", false, sampleJSON);
	}
	

	
	private void createdivelem(){
		HTML html = new HTML("<div id='piechart'></div>");
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(html);
	
	}
	
	
	public static native void createPieChart(String divID, String width, String height, boolean showLabels, String dataJSON) /*-{

		var divElem = $wnd.$("#"+divID);
		// clear all the previous content within the div (clear all html)
		divElem.empty();
		divElem.css("width", width);
		divElem.css("height", height);
		divElem.css("margin", "0 auto");
		
		// create an svg inside the div (required by the library)
		divElem.append("<svg></svg>");
		$wnd.addPieChart(divID,dataJSON, showLabels);
		
	}-*/;

}