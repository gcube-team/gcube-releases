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

public abstract class GCUBETemplate1Text2Image extends TourStep {
	
	@UiField Image otherImage;
	
	private static GCUBETemplate1Text2ImageUiBinder uiBinder = GWT
			.create(GCUBETemplate1Text2ImageUiBinder.class);

	interface GCUBETemplate1Text2ImageUiBinder extends
			UiBinder<Widget, GCUBETemplate1Text2Image> {
	}

	public GCUBETemplate1Text2Image() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	public GCUBETemplate1Text2Image(boolean showTitle) {
		super(showTitle);
		initWidget(uiBinder.createAndBindUi(this));
	}

	public abstract String setStepTitle(); 
	
	public abstract String setStepBody(); 

	public abstract String setStepImage(); 
	
	public abstract String setStepOtherImage(); 
	
	@Override
	public void switchLanguage(TourLanguage language) {
		title.setHTML(setStepTitle());	
		textHtml.setHTML(setStepBody());	
    }
    
	@Override
	protected void setTheTitle() {
		title.setHTML(setStepTitle());				
	}
	@Override
	protected void setTheBody() {
		textHtml.setHTML(setStepBody());		
	}
	@Override
	protected void setTheImage() {
		topImage.setUrl((setStepImage().contains("//")) ? setStepImage() : GWT.getModuleBaseURL() + "../" + setStepImage());		
	}
	
	protected void setTheOtherImage() { otherImage.setUrl(setStepOtherImage());		
	}
	@Override
	public void commit() {
		super.commit();
		setTheOtherImage();
	}
}
