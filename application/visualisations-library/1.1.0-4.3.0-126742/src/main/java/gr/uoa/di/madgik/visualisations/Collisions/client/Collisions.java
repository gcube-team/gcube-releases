package gr.uoa.di.madgik.visualisations.Collisions.client;

import gr.uoa.di.madgik.visualisations.client.injectors.CssResources;
import gr.uoa.di.madgik.visualisations.client.injectors.JSInjector;
import gr.uoa.di.madgik.visualisations.client.injectors.JsResources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.ui.AbsolutePanel;


public class Collisions extends AbsolutePanel implements EntryPoint{



	public void onModuleLoad() {
//		alert("Running Collisions component");
//		collisions("body", "500", "500");
		
		JSInjector.inject(JsResources.INSTANCE.d3geomJS().getText());
		JSInjector.inject(JsResources.INSTANCE.d3col_layoutJS().getText());
		JSInjector.inject(JsResources.INSTANCE.collisionJS().getText());
		
		StyleInjector.inject(CssResources.INSTANCE.collisionCSS().getText());	
	}
	
	
	public static native void alert(String msg) /*-{
	  $wnd.alert(msg);
	}-*/;
	
	public static native void collisions(String divID, String width, String height) /*-{
	  $wnd.collision(divID,width,height);
	}-*/;



}
