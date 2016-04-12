package org.gcube.portlets.widgets.guidedtour.client.steps;

import org.gcube.portlets.widgets.guidedtour.client.types.VerticalAlignment;
import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * @version 1.1 FEB 7th 2012
 * 
 * This is the basic step
 */
public abstract class TourStep extends Composite {

	protected @UiField HTML title;
	protected @UiField HTML textHtml;
	protected @UiField Image topImage;
	
	boolean showTitle = true;
	
	public TourStep() {
	}
	
	public TourStep(boolean showTitle) {
		this.showTitle = showTitle;
	}
	
	protected abstract void setTheTitle();
    
    protected abstract void setTheBody();
    
    protected abstract void setTheImage();  
    
    public void switchLanguage(TourLanguage language) {}
	
    @Override
    public String getTitle() {
    	return title.getText();
    }
       
	public void commit() {
		setTheTitle();
		if (! showTitle)
			title.setVisible(false);
		setTheBody();
		setTheImage();
	}
	/**
	 * Specifies that the text's contents should be aligned to align value
	 * @param align ALIGN_TOP, ALIGN_MIDDLE, ALIGN_BOTTOM
	 */
	public void setTextVerticalAlignment(VerticalAlignment align) {
		switch (align) {
		case ALIGN_TOP:
			title.getElement().getParentElement().getParentElement().setAttribute("valign", "top");
			break;
		case ALIGN_MIDDLE:
			title.getElement().getParentElement().getParentElement().setAttribute("valign", "middle");
			break;
		case ALIGN_BOTTOM:
			title.getElement().getParentElement().getParentElement().setAttribute("valign", "bottom");
			break;
		default:
			break;
		}
	}

	/**
	 * @return the showTitle
	 */
	public boolean isShowTitle() {
		return showTitle;
	}
	
	public HTML getTitleElement()
	{
		return title;
	}
}
