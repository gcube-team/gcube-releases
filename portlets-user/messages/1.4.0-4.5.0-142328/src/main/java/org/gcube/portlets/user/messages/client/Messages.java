package org.gcube.portlets.user.messages.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Messages implements EntryPoint {
	private MessagesApplicationController appController;
	 
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		appController = new MessagesApplicationController();
		appController.go(RootPanel.get(ConstantsPortletMessages.PORTLETDIV));
				
		 Window.addResizeHandler(new ResizeHandler() {
             @Override
             public void onResize(ResizeEvent event) {
                     System.out.println("onWindowResized width: "+event.getWidth()+" height: "+event.getHeight());
                     updateSize();
             }
		 });
		 
		 updateSize();

	}
	
	/**
	 * Update window size
	 */
    public void updateSize(){
    	
	    RootPanel workspace = RootPanel.get(ConstantsPortletMessages.PORTLETDIV);
	     
	    int topBorder = workspace.getAbsoluteTop();
	     
	    int leftBorder = workspace.getAbsoluteLeft();
	     
	     
	    int rootHeight = Window.getClientHeight() - topBorder - 4;// - ((footer == null)?0:(footer.getOffsetHeight()-15));
	     
	    int rootWidth = Window.getClientWidth() - 2* leftBorder; //- rightScrollBar;
	    
//	    System.out.println("New workspace dimension Height: "+rootHeight+" Width: "+rootWidth);
	    
	    if(rootHeight<ConstantsPortletMessages.DEFAULT_HEIGHT)
	    	appController.getMainPanel().updateHeight(rootHeight);
	    else
	    	appController.getMainPanel().updateHeight(ConstantsPortletMessages.DEFAULT_HEIGHT);
	    	
	    appController.getMainPanel().updateWidth(rootWidth);
    }
}
