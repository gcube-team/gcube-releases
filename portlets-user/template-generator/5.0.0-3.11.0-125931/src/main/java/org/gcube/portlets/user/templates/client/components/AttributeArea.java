package org.gcube.portlets.user.templates.client.components;

import org.gcube.portlets.d4sreporting.common.client.uicomponents.ReportUIComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.user.templates.client.model.TemplateComponent;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * <code> AttributeArea </code> class 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class AttributeArea extends ReportUIComponent {
	private HTML htmlToDisplay = new HTML();
	
	private VerticalPanel myPanel;
	private Presenter presenter;
	private ComponentType type;
	private TemplateComponent myCompoenent;
	final HTML editImage = new HTML();
	/**
	 * 
	 * @param presenter .
	 * @param left .
	 * @param top .
	 * @param width .
	 * @param height .
	 * @param attrName .
	 * @param values .
	 */
	public AttributeArea(final Presenter presenter, int left, int top, int width,  final int height, String attrName, String[] values, ComponentType type) {
		super(type, left, top, width, height);
		this.type = type;
		GWT.log("AttributeArea() Type? "+type);
		
		myPanel = getResizablePanel();
		this.presenter = presenter;
		String toDisplay = getHtmlToDisplay(attrName, values);
		htmlToDisplay.setText(toDisplay);
		htmlToDisplay.setPixelSize(width, height);

		myPanel.add(htmlToDisplay);
		myPanel.setTitle(toDisplay);
		
		//repositionMyPanel(0, 15);
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		myPanel.setPixelSize(width, height);
		htmlToDisplay.setStyleName("attribute");
		htmlToDisplay.addStyleName(type.toString().toLowerCase());
		setStyleName("d4sFrame");
		
		/* edit attr part */
		editImage.setHeight("15px");
		editImage.setStyleName("editImage");
		editImage.setTitle("Click to edit this attribute area");
		getTopPanel().insert(editImage, 0);
		editImage.addClickHandler(editImageHandler);
	}
	
	/**
	 * used by the system
	 * 
	 * @param presenter
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param textToDisplay
	 * @param type
	 */
	public AttributeArea(final Presenter presenter, int left, int top, int width,  final int height, String textToDisplay, final ComponentType type) {
		super(type, left, top, width, height);
		this.presenter = presenter;
		this.type = type;
		myPanel = getResizablePanel();
		
		htmlToDisplay.setPixelSize(width, height);
	
		myPanel.add(htmlToDisplay);
		myPanel.setTitle(textToDisplay);
		htmlToDisplay.setText(textToDisplay);
		//repositionMyPanel(0, 15);
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		myPanel.setPixelSize(width, height);
		htmlToDisplay.setStyleName("attribute");
		htmlToDisplay.addStyleName(type.toString().toLowerCase());
		setStyleName("d4sFrame");
		
		/* edit attr part */
		editImage.setHeight("15px");
		editImage.setStyleName("editImage");
		editImage.setTitle("Click to edit this attribute area");
		getTopPanel().insert(editImage, 0);
		editImage.addClickHandler(editImageHandler);
	}
	
	ClickHandler editImageHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			 openDialog();
		}
	};
	
	private void openDialog() {
		AttributeDialog dlg = new AttributeDialog(presenter, myCompoenent, this);
		dlg.show();	
	}

	public void setHtmlToDisplay(String attrName, String[] values) {
		String toDisplay = getHtmlToDisplay(attrName, values);
		this.htmlToDisplay.setText(toDisplay);
		myPanel.setTitle(toDisplay);
	}
	
	private String getHtmlToDisplay(String attrName, String[] values) {
		String toDisplay = attrName+": ";
		for (int i = 0; i < values.length; i++) {
			toDisplay += values[i];
			if (i != values.length-1)
				toDisplay += " | ";
		}
		return toDisplay;
	}

	
	public void setComponent(TemplateComponent toSet) {
		this.myCompoenent = toSet;
	}
	/**
	 * 
	 * @return .
	 */
	public String getText() {
		return htmlToDisplay.getText();
	}

	@Override
	public void lockComponent(ReportUIComponent toLock, boolean locked) {
		presenter.lockComponent(this, locked);
	}

	@Override
	public void removeTemplateComponent(ReportUIComponent toRemove) {
		presenter.removeTemplateComponent(this);		
	}
	
	
}
