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
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;

public class WindowMetadataView extends LayoutContainer {

	private Window window = new Window();
	private Frame frame;

	public WindowMetadataView(String heading, LayerItem lastLayerItem, final String servletUrl) {
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
		frame = new Frame();
		frame.setStyleName("whiteBackground");

		final Image imgLoading = Images.iconLoading().createImage();

		window.add(imgLoading);

		GeoExplorer.service.isSessionExpired(new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {

				if(!result)
					showFrame(servletUrl, imgLoading);
				else
					CheckSession.getInstance().showLogoutDialog();
			}

			@Override
			public void onFailure(Throwable caught) {

				CheckSession.getInstance().showLogoutDialog();
			}
		});

		window.add(frame);

		add(window);

		window.show();

	}

	private void showFrame(String requestURL, final Image imgLoading){

		frame.setUrl(requestURL);

		frame.addDomHandler(new LoadHandler() {

	        @Override
	        public void onLoad(LoadEvent event) {
	        	window.setLayout(new FitLayout());
	        	window.remove(imgLoading);
	        	window.layout(true);
	        }

	    }, LoadEvent.getType());
	}


	private native Document getIFrameDocument(IFrameElement iframe) /*-{
	    return iframe.contentDocument;
	}-*/;

}
