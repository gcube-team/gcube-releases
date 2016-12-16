package org.gcube.portlets.user.workspace.client.view.windows;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTExternalImage;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube.GWTImageDocument;

import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;


/**
 * The Class WindowImagePreview.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 30, 2016
 */
public class WindowImagePreview {

	Window window = new Window();

	Image loader = Resources.getIconLoading().createImage();

	/**
	 * Instantiates a new window image preview.
	 *
	 * @param name the name
	 * @param image the image
	 * @param positionX the position x
	 * @param positionY the position y
	 */
	public WindowImagePreview(String name, GWTImageDocument image, int positionX, int positionY) {
		initWindow(name, positionX, positionY);
		window.add(loader);
		Image img = new Image(image.getThumbnailUrl());
		img.setWidth(String.valueOf(image.getThumbnailWidth()));
		img.setHeight(String.valueOf(image.getThumbnailHeight()));
		log("Thumbnail URL: "+image.getThumbnailUrl());
		Image loadImg = new Image(image.getThumbnailUrl());

		loadImg.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				GWT.log("Image Load event fired");
				window.remove(loader);
			}
		});
		window.add(loadImg);
		window.show();
	}

	/**
	 * Instantiates a new window image preview.
	 *
	 * @param name the name
	 * @param image the image
	 * @param positionX the position x
	 * @param positionY the position y
	 */
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

	/**
	 * Inits the window.
	 *
	 * @param name the name
	 * @param positionX the position x
	 * @param positionY the position y
	 */
	private void initWindow(String name, int positionX, int positionY) {

		window.setHeaderVisible(true);
		window.setHeading(ConstantsExplorer.PREVIEWOF + " " + name);
		window.setMaximizable(false);
		window.setResizable(false);
		window.setAutoWidth(true);
		window.setAutoHeight(true);
		window.setPosition(positionX, positionY);
	}

	/**
	 * Log.
	 *
	 * @param msg the msg
	 */
	public static native void log(String msg) /*-{
	  console.log(msg);
	}-*/;

}
