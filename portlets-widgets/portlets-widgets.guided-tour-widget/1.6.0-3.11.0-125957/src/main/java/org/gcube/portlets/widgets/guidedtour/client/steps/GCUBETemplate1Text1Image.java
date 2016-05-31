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
public abstract class GCUBETemplate1Text1Image extends TourStep  {
	private static GCUBETemplate1Text1ImageUiBinder uiBinder = GWT
			.create(GCUBETemplate1Text1ImageUiBinder.class);

	interface GCUBETemplate1Text1ImageUiBinder extends UiBinder<Widget, GCUBETemplate1Text1Image> {}

	public GCUBETemplate1Text1Image() {
		initWidget(uiBinder.createAndBindUi(this));
	}	
	public GCUBETemplate1Text1Image(boolean showTitle)  {
		super(showTitle);
		initWidget(uiBinder.createAndBindUi(this));
	}	
	
	public abstract String setStepTitle(); 
	
	public abstract String setStepBody(); 

	public abstract String setStepImage(); 
	
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
}
