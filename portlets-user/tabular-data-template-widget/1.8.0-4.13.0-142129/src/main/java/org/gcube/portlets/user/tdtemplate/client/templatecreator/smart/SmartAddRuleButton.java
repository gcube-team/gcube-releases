package org.gcube.portlets.user.tdtemplate.client.templatecreator.smart;


import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 11, 2014
 *
 */
public class SmartAddRuleButton extends Composite {

	private final static String BASE_STYLE = "smartButton";

	private boolean selected;
	
	private String caption;

	private HorizontalPanel myPanel = new HorizontalPanel();

	private HTML text = new HTML();
	
	private Command myCommand = null;

//	private SmartFolderPanel caller;
	
//	private Image delete = new Image(TdTemplateAbstractImage.INSTANCE.getRemoveColumn());
	
	/**
	 * 
	 */
	public SmartAddRuleButton() {
		super();
		
		myPanel.setStyleName(BASE_STYLE);
		
		text.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (myCommand != null)
					myCommand.execute();
			}
		});
		
		myPanel.add(text);
		
		initComponent(myPanel);
	}
	
	public void setCommand(Command cmd) {
		myCommand = cmd;
	}

	public void update(String caption, AbstractImagePrototype img) {
		this.caption = caption;

		text.setHTML("<div style=\"width: 97%; text-align:center; padding: 5px;\">" +
				"<span style=\"display:inline-block; vertical-align:middle;\" >" + img.getHTML() + "</span>" +
				"<span style=\"padding-left: 5px; vertical-align:middle;\">"+ caption+"</span></div>");
	
		myPanel.layout();
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

	public String getCaption() {
		return caption;
	}

	public HTML getText() {
		return text;
	}
	
	public void panelMask(boolean bool){
		if(bool)
			myPanel.mask("Loading");
		else
			myPanel.unmask();
	}
	
	public void setVisible(boolean b){
		myPanel.setVisible(b);
	}
}

