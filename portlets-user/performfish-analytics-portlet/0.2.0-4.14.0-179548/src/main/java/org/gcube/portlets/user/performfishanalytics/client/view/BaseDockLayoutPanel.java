/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.view;

import org.gcube.portlets.user.performfishanalytics.client.controllers.PerformFishAnalyticsViewController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * The Class RootPanel.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 16, 2019
 */
public class BaseDockLayoutPanel extends DockLayoutPanel{
	/**
	 * Instantiates a new root panel.
	 *
	 * @param unit the unit
	 */
	public BaseDockLayoutPanel(Unit unit) {
		super(unit);
		instanceHandlers();
	}

	/**
	 * Instance handlers.
	 */
	private void instanceHandlers() {

		ScrollHandler scrollHandler = new Window.ScrollHandler() {

			@Override
			public void onWindowScroll(ScrollEvent event) {
//				GWT.log("Scroll top: "+event.getScrollTop());
				int scroll = event.getScrollTop();
				int left = Window.getScrollLeft();
				int height = Window.getClientHeight();
				setNewPosition(scroll, left, height);
			}
		};

//		Window.addResizeHandler(new ResizeHandler() {
//
//			@Override
//			public void onResize(ResizeEvent event) {
//				int scroll = Window.getScrollTop();
//				int left = Window.getScrollLeft();
//				int height = Window.getClientHeight();
////				GWT.log("onResize height: "+Window.getClientHeight());
//				setNewPosition(scroll, left, height);
//			}
//		});


		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				GWT.log("onWindowResized width: "+event.getWidth()+" height: "+event.getHeight());
				updateSize();
			}
		});

		updateSize();
	}

	/**
	 * Sets the new position.
	 *
	 * @param scrollTop the scroll top
	 * @param left the left
	 * @param height the height
	 */
	private void setNewPosition(int scrollTop, int left, int height){

		if(scrollTop>400){
//			GWT.log("height: "+height +" scrollTop: "+scrollTop);
			scrollTop= scrollTop>0?scrollTop:1;
			height = height>0?height:1;
			int newPosition = scrollTop+height;
		}
	}

	/**
	 * Update window size
	 */
	public void updateSize(){

		RootPanel rootPanelArea = RootPanel.get(PerformFishAnalyticsViewController.PERFORM_FISH_ANALYTICS_DIV);
		int topBorder = rootPanelArea.getAbsoluteTop();
		int leftBorder = rootPanelArea.getAbsoluteLeft();
		int footer = 85; //footer is bottombar + sponsor

		int rootHeight = Window.getClientHeight() - topBorder - 4 - footer;// - ((footer == null)?0:(footer.getOffsetHeight()-15));
//		if (rootHeight < 550)
//			rootHeight = 550;

		rootHeight+= 2000;
		int rootWidth = Window.getClientWidth() - 2* leftBorder; //- rightScrollBar;
		GWT.log("New workspace dimension Height: "+rootHeight+" Width: "+rootWidth);
		setHeight(rootHeight+"px");
		setWidth(rootWidth+"px");

	}

}
