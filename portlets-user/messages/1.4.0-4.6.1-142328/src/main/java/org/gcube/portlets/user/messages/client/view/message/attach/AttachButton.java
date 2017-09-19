/**
 * 
 */
package org.gcube.portlets.user.messages.client.view.message.attach;


import org.gcube.portlets.user.messages.client.resources.Resources;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 2, 2014
 *
 */
public class AttachButton extends Composite {

	private final static String BASE_STYLE = "smartAttach";
	private String caption;
	private HorizontalPanel myPanel = new HorizontalPanel();
	private HTML text = new HTML();
	private Command myCommand = null;
	private Image delete = new Image(Resources.ICONS.close());
	private int clickClientX;
	private int clickClientY;

	public AttachButton(String caption, String tooltip, AbstractImagePrototype img) {
		super();
		this.caption = caption;
//		selected = false;
		super.setWidth(250);
		text.setWidth("100%");
		myPanel.setWidth("100%");

		text.setHTML("<div class=\"mysmartAttach\" style=\"width: 100%; height: 25px; line-height: 27px; text-align:left; padding-left: 15px;\">" +
				"<span style=\"display:inline-block; vertical-align:middle;\" >" + img.getHTML() + "</span>" +
				"<span title=\""+tooltip+"\" style=\"padding-left: 2px; padding-right: 10px;\">"+ caption+"</span></div>");
	
		myPanel.add(text);

		myPanel.setStyleName(BASE_STYLE);
		initComponent(myPanel);

		text.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				 commandExecute(event.getClientX(), event.getClientY());
			}
		});
		
		//RIGHT CLICK
		text.sinkEvents(Event.ONCONTEXTMENU);		
		text.addHandler(
	      new ContextMenuHandler() {
	        @Override
	        public void onContextMenu(ContextMenuEvent event) {
	          event.preventDefault();
	          event.stopPropagation();
			  commandExecute(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
	        }
	    }, ContextMenuEvent.getType());

	}
	
	private void commandExecute(int x, int y){		
		clickClientX = x;
		clickClientY = y;
		if (myCommand != null)
			myCommand.execute();
	}
	
	public int getClickClientX(){
		return clickClientX;
	}
	
	public int getClickClientY(){
		return clickClientY;
	}

	public AttachButton(String caption, String tooltip, AbstractImagePrototype img, boolean isDeletable) {
		this(caption, tooltip, img);

		delete.getElement().getStyle().setOpacity(0.6);
		delete.getElement().getStyle().setMarginTop(3, com.google.gwt.dom.client.Style.Unit.PX);
		myPanel.add(delete);
		delete.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {	
			}
		});
		
		delete.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				delete.getElement().getStyle().setOpacity(0.6);				
			}
		});
		
		delete.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				delete.getElement().getStyle().setOpacity(0.9);
			}
		});
	}
	
	public void setCommand(Command cmd) {
		myCommand = cmd;
	}

	public String getCaption() {
		return caption;
	}
	
	public void setEnabled(boolean bool){
		myPanel.setEnabled(bool);
	}
}