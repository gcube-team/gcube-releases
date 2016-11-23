package org.gcube.portlets.user.templates.client.components;

import org.gcube.portlets.d4sreporting.common.client.uicomponents.ReportUIComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * <code> DroppingArea </code> class is a Widget that can be placed in the UI Component
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class DroppingArea extends ReportUIComponent {
	
	private String expectedContent = "";
	
	private TextBox textbox = new TextBox();
	
	private Presenter presenter;
	
	private final static String DEFAULT_TEXT = "<expected content here>"; 

	/**
	 * 
	 * @param controller controller instance
	 * @param left l
	 * @param top t
	 * @param width width
	 * @param height height
	 * @param isImage tell if il will be a image or a text when in the report portlet
	 */
	public DroppingArea(final Presenter controller, final int left, final int top, final int width, final int height, boolean isDoubled) {
		super(ComponentType.DYNA_IMAGE, left, top, width, height);	
		this.setStyleName("droppingArea-Image");
		this.presenter = controller;
		
		VerticalPanel myPanel = getResizablePanel();
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		myPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		addDefaultText(); //not used just for legacy	
	}
	
	/**
	 * 
	 * just add the default text in the textbox
	 */
	private void addDefaultText() {
		textbox.setText(DEFAULT_TEXT);
	}
	
		/**
	 * 
	 * @return expectedContent
	 */
	public String getExpectedContent() {
		return expectedContent;
	}

	/**
	 * 
	 * @param expectedContent .
	 */
	public void setExpectedContent(String expectedContent) {
		this.expectedContent = expectedContent;
		if (expectedContent.compareTo("") != 0  && !expectedContent.startsWith("http"))
			textbox.setText(expectedContent);
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
