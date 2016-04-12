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
public abstract class GCUBETemplate2Text2ImageML extends TourStep {	

	@UiField HTML otherText;
	@UiField Image otherImage;

	private static GCUBETemplate2Text2ImageMLUiBinder uiBinder = GWT
			.create(GCUBETemplate2Text2ImageMLUiBinder.class);

	interface GCUBETemplate2Text2ImageMLUiBinder extends
	UiBinder<Widget, GCUBETemplate2Text2ImageML> {
	}
	
	public GCUBETemplate2Text2ImageML() {
		initWidget(uiBinder.createAndBindUi(this));
	}	

	public GCUBETemplate2Text2ImageML(boolean showTitle)  {
		super(showTitle);
		initWidget(uiBinder.createAndBindUi(this));
	}	
	
	public abstract HashMap<TourLanguage, String> setStepTitle();  

	public abstract HashMap<TourLanguage, String> setStepBody(); 

	public abstract String setStepImage(); 

	public abstract HashMap<TourLanguage, String> setStepOtherBody(); 

	public abstract String setStepOtherImage(); 

	protected void setTheTitle() { 
		title.setHTML(setStepTitle().get(TourLanguage.EN));		
	}
	protected void setTheBody() { 
		textHtml.setHTML(setStepBody().get(TourLanguage.EN));		
	}
	protected void setTheImage() {
		topImage.setUrl((setStepImage().contains("//")) ? setStepImage() : GWT.getModuleBaseURL() + "../" + setStepImage());		
	}
	protected void setTheOtherBody() { 
		otherText.setHTML(setStepOtherBody().get(TourLanguage.EN));		
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
	
	public void switchLanguage(TourLanguage language) {
		title.setHTML(setStepTitle().get(language));
		textHtml.setHTML(setStepBody().get(language));		
		otherText.setHTML(setStepOtherBody().get(language));	
	}
}
