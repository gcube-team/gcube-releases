package org.gcube.portlets.user.workspace.client.view.windows;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTExternalImage;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube.GWTImageDocument;

import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.ui.Image;

public class WindowImagePreview {
	
	Window window = new Window();
	
	
	public WindowImagePreview(String name, GWTImageDocument image, int positionX, int positionY) {
		initWindow(name, positionX, positionY);
		
		Image img = new Image(image.getThumbnailUrl());
		img.setWidth(String.valueOf(image.getThumbnailWidth()));
		img.setHeight(String.valueOf(image.getThumbnailHeight()));
		log("Thumbnail URL: "+image.getThumbnailUrl());
		window.add(new Image(image.getThumbnailUrl()));
		window.show();
	}
	
	public WindowImagePreview(String name, GWTExternalImage image, int positionX, int positionY){
		
		initWindow(name, positionX, positionY);

//		System.out.println("URL.................." + image.getThumbnailUrl());
//		System.out.println("W.................." + String.valueOf(image.getThumbnailWidth()));
//		System.out.println("H.................." + String.valueOf(image.getThumbnailHeight()));
//		System.out.println("postionX.................." + positionX);
//		System.out.println("positionY.................." + positionY);

		Image img = new Image(image.getThumbnailUrl());
		img.setWidth(String.valueOf(image.getThumbnailWidth()));
		img.setHeight(String.valueOf(image.getThumbnailHeight()));
		window.add(new Image(image.getThumbnailUrl()));
		window.show();
		
	}

	private void initWindow(String name, int positionX, int positionY) {

		window.setHeaderVisible(true);
		window.setHeading(ConstantsExplorer.PREVIEWOF + " " + name);  
		window.setMaximizable(false);  
		window.setResizable(false);  
		window.setAutoWidth(true);
		window.setAutoHeight(true);
		window.setPosition(positionX, positionY);
	}
	
	public static native void log(String msg) /*-{
	  console.log(msg);
	}-*/;

}
