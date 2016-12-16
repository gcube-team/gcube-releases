/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GCubeCkanDataCatalog;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.IFrameInstanciedEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.resource.CkanPortletResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;


/**
 * The Class CkanFramePanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 9, 2016
 */
public class CkanFramePanel extends FlowPanel{

	private Frame frame;
	private HandlerManager eventBus;
	private Image loading = new Image(CkanPortletResources.ICONS.loading());


	/**
	 * Instantiates a new ckan frame panel.
	 *
	 * @param eventBus the event bus
	 */
	public CkanFramePanel(HandlerManager eventBus) {
		this.eventBus = eventBus;
		addLoading();
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
		
		addLoading();

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
				
				CkanFramePanel.this.remove(loading);
				
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
	
	/**
	 * add loading image
	 */
	private void addLoading(){
		this.add(loading);
		loading.getElement().getStyle().setProperty("margin", "auto");
		loading.getElement().getStyle().setDisplay(Display.BLOCK);
	}
}
