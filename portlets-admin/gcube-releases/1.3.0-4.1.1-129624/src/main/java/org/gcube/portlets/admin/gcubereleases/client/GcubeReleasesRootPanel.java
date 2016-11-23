/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The Class RootPanelGcubeReleases.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 25, 2015
 */
public class GcubeReleasesRootPanel extends FlowPanel{
	
	private BaloonPanelGoTop baloonOnTop;
	private GcubeReleasesRootPanel INSTANCE = this;
	
	/**
	 * Instantiates a new root panel gcube releases.
	 */
	public GcubeReleasesRootPanel() {
		
		baloonOnTop = new BaloonPanelGoTop("GoTop", false);
		baloonOnTop.setAnimationEnabled(true);
		
//		baloonOnTop.setVisible(false);
//		baloonOnTop.show();
		
		ScrollHandler scrollHandler = new Window.ScrollHandler() {
			
			@Override
			public void onWindowScroll(ScrollEvent event) {
				baloonOnTop.hide();
//				GWT.log("Scroll top: "+event.getScrollTop());
				int scroll = event.getScrollTop();
				int left = Window.getScrollLeft();
				int height = Window.getClientHeight();
				setNewPosition(scroll, left, height);
			}
		};
		
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				baloonOnTop.hide();
				int scroll = Window.getScrollTop();
				int left = Window.getScrollLeft();
				int height = Window.getClientHeight();
//				GWT.log("onResize height: "+Window.getClientHeight());
				setNewPosition(scroll, left, height);
			}
		});
		
		com.google.gwt.user.client.Window.addWindowScrollHandler(scrollHandler);
		
		this.addAttachHandler(new AttachEvent.Handler() {

	        /* (non-Javadoc)
        	 * @see com.google.gwt.event.logical.shared.AttachEvent.Handler#onAttachOrDetach(com.google.gwt.event.logical.shared.AttachEvent)
        	 */
        	@Override
	        public void onAttachOrDetach(AttachEvent event) {
//	        	GWT.log("<-- rendering complete -->");
				String zi = INSTANCE.getElement().getStyle().getZIndex();
//				GWT.log("zindex "+zi);
				int zIndex = 50;
				try{
					zIndex += Integer.parseInt(zi);
				}catch(NumberFormatException e){
					
				}
				baloonOnTop.getElement().getStyle().setZIndex(zIndex);
	        }
	    });
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
//			GWT.log("new position: "+newPosition);
			baloonOnTop.setVisible(false);
		    baloonOnTop.show();
		    baloonOnTop.setPopupPosition(left>0?left:1, newPosition-35);
		    baloonOnTop.setVisible(true);
		}
	}
	
}
