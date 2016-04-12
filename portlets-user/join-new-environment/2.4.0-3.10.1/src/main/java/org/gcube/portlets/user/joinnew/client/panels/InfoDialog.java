package org.gcube.portlets.user.joinnew.client.panels;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * 
 * @author massi
 *
 */
public class InfoDialog extends GCubeDialog {
	private  ScrollPanel scroller = new ScrollPanel();
	private VerticalPanel main_panel = null;
	
    public InfoDialog(String title, String content) {
    	
      // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
      // If this is set, the panel closes itself automatically when the user
      // clicks outside of it.
      super(true);
      super.setText(title);
      main_panel = new VerticalPanel();
      main_panel.addStyleName("bgBlank p8 font_family font_12");
 
      if (content == null || content.equals(""))
    	  content = "We're sorry, there is no available description yet";
      
      scroller.add(new HTML(content));
     
      // PopupPanel is a SimplePanel, so you have to set it's widget property to
      // whatever you want its contents to be.
      Button close = new Button("Close");
      close.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
			hide();					
		}    	  
      });
      main_panel.add(scroller);
      main_panel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
      main_panel.add(close);
      scroller.setPixelSize(550, 300);
      main_panel.setPixelSize(550, 350);
      setWidget(main_panel);
    }
    
    public void show() {
    	super.show();
        center();
//        int left = (Window.getClientWidth() - getOffsetWidth()) / 2 +  getBodyScrollLeft();
//        int top = (Window.getClientHeight() - getOffsetHeight()) / 2 +  getBodyScrollTop();
//        setPopupPosition(left, top);
      }

      private native int getBodyScrollLeft() /*-{
        return $doc.body.scrollLeft;
      }-*/;

      private native int getBodyScrollTop() /*-{
        return $doc.body.scrollTop;
      }-*/; 
}