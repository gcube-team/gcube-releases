/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GCubeCkanDataCatalog;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.IFrameInstanciedEvent;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;


/**
 * The Class CkanFramePanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 9, 2016
 */
public class CkanFramePanel extends FlowPanel{

	private Frame frame;
	private HandlerManager eventBus;


	/**
	 * Instantiates a new ckan frame panel.
	 *
	 * @param eventBus the event bus
	 */
	public CkanFramePanel(HandlerManager eventBus) {
		this.eventBus = eventBus;

	}

	/**
	 * Prints the.
	 *
	 * @param msg the msg
	 */
	private static native void print(String msg)/*-{
	console.log(msg);
	}-*/;

	/**
	 * Instance frame.
	 *
	 * @param ckanUrlConnector the ckan url connector
	 * @return the frame
	 */
	public Frame instanceFrame(String ckanUrlConnector) {
		GWT.log("Instancing new IFRAME with uri: "+ckanUrlConnector);
		try{
			if(frame != null)
				remove(frame);
		}catch(Exception e){
			print("Error " + e);
		}

		frame = new Frame(ckanUrlConnector);
		frame.getElement().setId(GCubeCkanDataCatalog.GCUBE_CKAN_IFRAME);
		frame.setWidth("100%");
//		frame.setHeight("100%");
//		frame.getElement().getStyle().setOverflow(Overflow.HIDDEN);
//		frame.getElement().setAttribute("scrolling", "no");
		frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
		frame.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent arg0) {
			}
		});
		add(frame);
		frame.setVisible(true);
		eventBus.fireEvent(new IFrameInstanciedEvent());
		return frame;
	}

	/**
	 * Gets the frame.
	 *
	 * @return the frame
	 */
	public Frame getFrame() {
		return frame;
	}
}
