package gr.uoa.di.madgik.visualisations.cummulativelines.client;

import gr.uoa.di.madgik.visualisations.client.injectors.JSInjector;
import gr.uoa.di.madgik.visualisations.client.injectors.JsResources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;

public class Multilines implements EntryPoint {

	
	public void onModuleLoad() {
		JSInjector.inject(JsResources.INSTANCE.d3multilinesJS().getText());
	}
	
	public static native void createMultiLineChart (String divID, String dataJSON)/*-{
		var divElem = $wnd.$("#"+divID);
		// clear all the previous content within the div (clear all html)
		divElem.empty();
		
		//clear also the styling
//		divElem.removeAttr("style");
		
//		divElem.css("width", 800);
//		divElem.css("height", 800);
		
		//divElem.css("margin", "0 auto");
		//divElem.css("padding", "0px");
		
		// create an svg inside the div (required by the library)
		divElem.append("<svg></svg>");
		
		$wnd.linechart(divID, dataJSON);
		
	}-*/;
	
	
	
	public static native void updateMultiLineChart (String divID, String dataJSON)/*-{
		var divElem = $wnd.$("#"+divID);
		$wnd.linechart_update(divID, dataJSON);
	}-*/;

	public static native void refreshMultiLineChart (String divID)/*-{
		var divElem = $wnd.$("#"+divID);
		$wnd.linechart_refresh(divID);
	}-*/;
	
}

