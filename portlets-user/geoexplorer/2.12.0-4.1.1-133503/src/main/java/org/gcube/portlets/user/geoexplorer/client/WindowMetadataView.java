/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 26, 2013
 *
 */

import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.client.resources.Images;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;

public class WindowMetadataView extends LayoutContainer {

	private Window window = new Window();
	private Frame frame;
	
	public WindowMetadataView(String heading, LayerItem lastLayerItem, String servletUrl) {
//		setLayout(new FitLayout());
		
		window = new Window();
		window.setSize(800, 600);
		window.setResizable(true);
		window.setMaximizable(true);
//		window.setPlain(true);
//		window.setModal(true);
		window.setAnimCollapse(true);
		window.setCollapsible(true);
//		window.setBlinkModal(true);
		window.setHeading(heading);
		window.setLayout(new FitLayout());
		frame = new Frame(servletUrl);
		frame.setStyleName("whiteBackground");

		
		final Image imgLoading = Images.iconLoading().createImage();
		window.add(imgLoading);
		
		frame.addDomHandler(new LoadHandler() {

	        @Override
	        public void onLoad(LoadEvent event) {
	        	window.setLayout(new FitLayout());
//	            IFrameElement iframe = IFrameElement.as(frame.getElement());
	            
//	            Document frameDocument = getIFrameDocument(iframe);
//	            if (frameDocument != null) {
//	            	com.google.gwt.user.client.Window.alert(frameDocument.getDomain());
//	            }
//	            else {
//	            	com.google.gwt.user.client.Window.alert("the document is empty, nothing to display!");
//	            }
	            
	        	window.remove(imgLoading);
	        	
	        	window.layout(true);

	        }
	    }, LoadEvent.getType());
		
		window.add(frame);
		
		add(window);
		
		window.show();
	}
	



	private native Document getIFrameDocument(IFrameElement iframe) /*-{
	    return iframe.contentDocument;
	}-*/;

}
