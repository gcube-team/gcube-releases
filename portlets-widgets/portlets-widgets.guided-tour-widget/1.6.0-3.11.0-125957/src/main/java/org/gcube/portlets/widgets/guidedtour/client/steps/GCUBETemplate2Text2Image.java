package org.gcube.portlets.widgets.guidedtour.client.steps;


import java.util.HashMap;

import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.1 FEB 7th 2012
 */
public abstract class GCUBETemplate2Text2Image extends TourStep {	

	@UiField HTML otherText;
	@UiField Image otherImage;

	private static GCUBETemplate2Text2ImageUiBinder uiBinder = GWT
			.create(GCUBETemplate2Text2ImageUiBinder.class);

	interface GCUBETemplate2Text2ImageUiBinder extends
	UiBinder<Widget, GCUBETemplate2Text2Image> {
	}
	
	public GCUBETemplate2Text2Image() {
		initWidget(uiBinder.createAndBindUi(this));
	}	

	public GCUBETemplate2Text2Image(boolean showTitle)  {
		super(showTitle);
		initWidget(uiBinder.createAndBindUi(this));
	}	
	
	public abstract String setStepTitle();  

	public abstract String setStepBody(); 

	public abstract String setStepImage(); 

	public abstract String setStepOtherBody(); 

	public abstract String setStepOtherImage(); 

	protected void setTheTitle() { 
		title.setHTML(setStepTitle());		
	}
	protected void setTheBody() { 
		textHtml.setHTML(setStepBody());		
	}
	protected void setTheImage() {
		topImage.setUrl((setStepImage().contains("//")) ? setStepImage() : GWT.getModuleBaseURL() + "../" + setStepImage());		
	}
	protected void setTheOtherBody() { 
		otherText.setHTML(setStepOtherBody());		
	}
	protected void setTheOtherImage() { 
		otherImage.setUrl((setStepOtherImage().contains("//")) ? setStepOtherImage() : GWT.getModuleBaseURL() + "../" + setStepOtherImage());		
	}

	@Override
	public void commit() {
		super.commit();
		setTheOtherBody();
		setTheOtherImage();
	}	
}
