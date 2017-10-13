package org.gcube.portlets.user.workspace.client.view.smartfolder;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.event.DeleteSmartFolderEvent;
import org.gcube.portlets.user.workspace.client.resources.Resources;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.0 May 14th 2012
 */
public class SmartButton extends Composite {

	private final static String BASE_STYLE = "smartButton";

	private boolean selected;
	
	private String caption;

	private HorizontalPanel myPanel = new HorizontalPanel();

	private HTML text = new HTML();

	private Command myCommand = null;

	private SmartFolderPanel caller;
	
	private Image delete = new Image(Resources.ICONS.close());

	public SmartButton(String caption, AbstractImagePrototype img, SmartFolderPanel caller) {
		super();
		selected = false;
		this.caption = caption;
		this.caller = caller;
		super.setWidth(300);
		text.setPixelSize(300, 30);
		myPanel.setWidth("100%");

		text.setHTML("<div style=\"width: 100%; height: 30px; line-height: 32px; text-align:left; padding-left: 20px;\">" +
				"<span style=\"display:inline-block; vertical-align:middle;\" >" + img.getHTML() + "</span>" +
				"<span style=\"padding-left: 20px;\">"+ caption+"</span></div>");
	
		myPanel.add(text);

		myPanel.setStyleName(BASE_STYLE);
		initComponent(myPanel);

		text.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selected = selected? false : true;
				if (selected) {
					myPanel.addStyleName(BASE_STYLE + "-selected");
				} else {
					myPanel.removeStyleName(BASE_STYLE + "-selected");
				}
				deselectOthers();
				if (myCommand != null)
					myCommand.execute();
			}
		});

	}

	public SmartButton(String caption, AbstractImagePrototype img, SmartFolderPanel caller, boolean isDeletable) {
		this(caption, img, caller);

		delete.getElement().getStyle().setOpacity(0.6);
		delete.getElement().getStyle().setMarginTop(3, com.google.gwt.dom.client.Style.Unit.PX);
		myPanel.add(delete);
		delete.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				deleteFolder();				
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
	
	private void deleteFolder() {
		AppControllerExplorer.getEventBus().fireEvent(new DeleteSmartFolderEvent(this.getId(), caption));
	}

	private void deselectOthers() {
		caller.toggleOthers(this);
	}

	public void setCommand(Command cmd) {
		myCommand = cmd;
	}

	/**
	 * Returns true if the button is pressed.
	 * 
	 * @return the pressed state
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the current selected state.
	 * 
	 * @param state true to set selected state
	 */
	protected void toggle(boolean state) {
		this.selected = state;
		if (selected) {
			myPanel.addStyleName(BASE_STYLE + "-selected");
		} else {
			myPanel.removeStyleName(BASE_STYLE + "-selected");
		}
	}	
}

