package org.gcube.portlets.widgets.guidedtour.client.steps;


import java.util.HashMap;

import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.1 FEB 7th 2012
 */

public abstract class GCUBETemplate1Text1ImageML extends TourStep  {
	private static GCUBETemplate1Text1ImageMLUiBinder uiBinder = GWT
			.create(GCUBETemplate1Text1ImageMLUiBinder.class);

	interface GCUBETemplate1Text1ImageMLUiBinder extends UiBinder<Widget, GCUBETemplate1Text1ImageML> {}

	public GCUBETemplate1Text1ImageML() {
		initWidget(uiBinder.createAndBindUi(this));
	}	
	public GCUBETemplate1Text1ImageML(boolean showTitle)  {
		super(showTitle);
		initWidget(uiBinder.createAndBindUi(this));
	}	
	
	public abstract HashMap<TourLanguage, String> setStepTitle(); 
	
	public abstract HashMap<TourLanguage, String> setStepBody(); 

	public abstract String setStepImage(); 
	
	@Override
	public void switchLanguage(TourLanguage language) {
		title.setHTML(setStepTitle().get(language));	
		textHtml.setHTML(setStepBody().get(language));	
    }
    
	@Override
	protected void setTheTitle() {
		title.setHTML(setStepTitle().get(TourLanguage.EN));				
	}
	@Override
	protected void setTheBody() {
		textHtml.setHTML(setStepBody().get(TourLanguage.EN));		
	}
	@Override
	protected void setTheImage() {
		topImage.setUrl((setStepImage().contains("//")) ? setStepImage() : GWT.getModuleBaseURL() + "../" + setStepImage());			
	}	
}
