package gr.uoa.di.madgik.visualisations.RandomTree.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.AbsolutePanel;


public class RandomTree extends AbsolutePanel implements EntryPoint{

	

	public void onModuleLoad() {
		this.setSize("1000", "1000");
		alert("Running randomtree component");
//		collisions("body", "500", "500");
		
	}
	
	
	public static native void alert(String msg) /*-{
	  $wnd.alert(msg);
	}-*/;
	
//	public static native void collisions(String divID, String width, String height) /*-{
//	  $wnd.collision(divID,width,height);
//	}-*/;



}