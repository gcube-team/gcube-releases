package org.gcube.portlets.widgets.guidedtour.client.steps;

import java.util.HashMap;

import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public abstract class GCUBETemplate1Text2ImageML extends TourStep {
	
	@UiField Image otherImage;
	
	private static GCUBETemplate1Text2ImageMLUiBinder uiBinder = GWT
			.create(GCUBETemplate1Text2ImageMLUiBinder.class);

	interface GCUBETemplate1Text2ImageMLUiBinder extends
			UiBinder<Widget, GCUBETemplate1Text2ImageML> {
	}

	public GCUBETemplate1Text2ImageML() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	public GCUBETemplate1Text2ImageML(boolean showTitle) {
		super(showTitle);
		initWidget(uiBinder.createAndBindUi(this));
	}

	public abstract HashMap<TourLanguage, String> setStepTitle(); 
	
	public abstract HashMap<TourLanguage, String> setStepBody(); 

	public abstract String setStepImage(); 
	
	public abstract String setStepOtherImage(); 
	
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
	
	protected void setTheOtherImage() {
		otherImage.setUrl((setStepOtherImage().contains("//")) ? setStepOtherImage() : GWT.getModuleBaseURL() + "../" + setStepOtherImage());	
	}
	@Override
	public void commit() {
		super.commit();
		setTheOtherImage();
	}
}
