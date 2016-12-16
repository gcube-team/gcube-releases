package gr.uoa.di.madgik.visualisations.GraphEditor.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.AbsolutePanel;


public class GraphEditor extends AbsolutePanel implements EntryPoint{

	

	public void onModuleLoad() {
		this.setSize("500", "500");
		alert("Running GraphEditor component");
//		collisions("body", "500", "500");
		
	}
	
	
	public static native void alert(String msg) /*-{
	  $wnd.alert(msg);
	}-*/;
	
//	public static native void collisions(String divID, String width, String height) /*-{
//	  $wnd.collision(divID,width,height);
//	}-*/;



}