package org.gcube.portlets.user.tdtemplate.client.templatecreator.smart;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateConstants;
import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.UserActionInterface;
import org.gcube.portlets.user.tdtemplate.shared.util.CutStringUtil;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 21, 2014
 *
 */
public class SmartButtonDescription extends Composite {

	private final static String BASE_STYLE = "smartButton";

	private boolean selected;
	
	private String captionField;

	private HorizontalPanel myPanel = new HorizontalPanel();

	private HTML text = new HTML();

	private Command myCommand = null;

	private Image delete = new Image(TdTemplateAbstractResources.INSTANCE.delete());
	
	private Image edit = new Image(TdTemplateAbstractResources.INSTANCE.pencil10());

	@SuppressWarnings("unused")
	private UserActionInterface caller;

	private String title;

	private int index;

	public SmartButtonDescription(int index, final String title, String caption, UserActionInterface caller) {
		super();
		selected = false;
		this.captionField = caption;
		this.caller = caller;
		this.title = title;
		this.index = index;
//		super.setWidth(300);
		text.setWidth((TdTemplateConstants.WIDTHWIDGETCOLUMN-20)+"px");
		myPanel.setWidth("98%");
		
		
		text.setHTML("<div style=\"width: 98%; text-align:left; padding: 2px;\">" +
				"<span style=\"width: inherit; display:inline-block; vertical-align:middle; font-weight: bold;\" >" + title + "</span>" +
				"<br/>" +
				"<span style=\"width: inherit; padding-left: 3px;\">"+ CutStringUtil.cutString(caption, 12)+"</span>" +
				"</div>");
	
		myPanel.add(text);

		myPanel.setStyleName(BASE_STYLE);
		initComponent(myPanel);

		text.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				MessageBox.info(title, captionField, null);
				if (myCommand != null)
					myCommand.execute();
			}
		});

	}
	
	public SmartButtonDescription(int index, String title, String caption, final UserActionInterface caller, boolean isDeletable, boolean isEditable) {
		this(index, title, caption, caller);

		VerticalPanel vp = new VerticalPanel();
		vp.setHorizontalAlign(HorizontalAlignment.RIGHT);
		vp.setVerticalAlign(VerticalAlignment.MIDDLE);
		vp.setStyleAttribute("margin-left", "9px");
		if(isDeletable){
			delete.getElement().getStyle().setOpacity(0.4);
			delete.getElement().getStyle().setMarginTop(3, com.google.gwt.dom.client.Style.Unit.PX);
			vp.add(delete);
			
//			myPanel.add(delete);
			
			delete.addClickHandler(new ClickHandler() {			
				@Override
				public void onClick(ClickEvent event) {
//					caller.resetRule();		
//					caller.resetDescriptionCell();
					caller.deleteClicked(getIndex());
				}
			});
			
			delete.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					delete.getElement().getStyle().setOpacity(0.4);				
				}
			});
			
			delete.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					delete.getElement().getStyle().setOpacity(0.9);
				}
			});
		}
		
		
		if(isEditable){
			edit.getElement().getStyle().setOpacity(0.4);
			edit.getElement().getStyle().setMarginTop(3, com.google.gwt.dom.client.Style.Unit.PX);
			vp.add(edit);

			edit.addClickHandler(new ClickHandler() {			
				@Override
				public void onClick(ClickEvent event) {
					caller.editClicked(getIndex());
				}
			});
			
			edit.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					edit.getElement().getStyle().setOpacity(0.4);				
				}
			});
			
			edit.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					edit.getElement().getStyle().setOpacity(0.9);
				}
			});
		}
		
		myPanel.add(vp);
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

	public int getIndex() {
		return index;
	}
}

