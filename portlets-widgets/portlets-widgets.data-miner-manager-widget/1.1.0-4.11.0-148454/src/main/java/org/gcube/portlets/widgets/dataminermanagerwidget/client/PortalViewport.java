/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.Viewport.ViewportAppearance;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class PortalViewport extends SimpleContainer {

	protected int rightScrollBarSize = 17;

	protected boolean enableScroll;
	

	/**
	 * Creates a viewport layout container with the default appearance.
	 */
	public PortalViewport() {
		this(GWT.<ViewportAppearance> create(ViewportAppearance.class));
	}

	/**
	 * Creates a viewport layout container with the specified appearance.
	 * 
	 * @param appearance
	 *            the appearance of the viewport layout container
	 */
	public PortalViewport(ViewportAppearance appearance) {
		super(true);
		try {

			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			appearance.render(sb);
			XElement element=XDOM.create(sb.toSafeHtml());
			setElement((Element)element);
			monitorWindowResize = true;
			forceLayoutOnResize = true;
			getFocusSupport().setIgnore(false);
			resize();
		} catch (Exception e) {
			Log.error("PortalViewport: constructor error "
					+ e.getLocalizedMessage());
		}
	}

	/**
	 * Returns true if window scrolling is enabled.
	 * 
	 * @return true if window scrolling is enabled
	 */
	public boolean isEnableScroll() {
		return enableScroll;
	}

	/**
	 * Sets whether window scrolling is enabled.
	 * 
	 * @param enableScroll
	 *            true to enable window scrolling
	 */
	public void setEnableScroll(boolean enableScroll) {
		this.enableScroll = enableScroll;
		Window.enableScrolling(enableScroll);
	}

	/**
	 * @return the rightScrollBarSize
	 */
	public int getRightScrollBarSize() {
		return rightScrollBarSize;
	}

	/**
	 * @param rightScrollBarSize
	 *            the rightScrollBarSize to set
	 */
	public void setRightScrollBarSize(int rightScrollBarSize) {
		this.rightScrollBarSize = rightScrollBarSize;
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		setEnableScroll(enableScroll);
		resize();

	}

	protected void resize() {
		int viewWidth;
		if(enableScroll){
			viewWidth= calculateWidth() - rightScrollBarSize;
		} else {
			viewWidth = calculateWidth();
		}
		
		int viewHeight = calculateHeight();
		Log.info("AM resize viewWidth: " + viewWidth + " viewHeight: "
				+ viewHeight + " clientWidth: " + Window.getClientWidth()
				+ " clientHeight: " + Window.getClientHeight());
		try {
			setPixelSize(viewWidth, viewHeight);
		} catch (Exception e) {
			Log.error("PortalViewport: error in resize() at setPixelSize "
					+ e.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onWindowResize(int width, int height) {
		int viewWidth = calculateWidth();
		int viewHeight = calculateHeight();
		Log.trace("AM onWindowResize viewWidth: " + viewWidth
				+ " viewHeight: " + viewHeight + " clientWidth: "
				+ Window.getClientWidth() + " clientHeight: "
				+ Window.getClientHeight());
		setPixelSize(viewWidth, viewHeight);
	}

	

	protected int calculateWidth() {
		int leftBorder = getAbsoluteLeft();
		Log.info("AM width: "
				+ String.valueOf(Window.getClientWidth() - 2 * leftBorder));
		return Window.getClientWidth() - 2 * leftBorder;
	}

	protected int calculateHeight() {
		int topBorder = getAbsoluteTop();
		Log.info("AM height: "
				+ String.valueOf(Window.getClientHeight() - topBorder - 34));
		return Window.getClientHeight() - topBorder - 34;
	}

}
