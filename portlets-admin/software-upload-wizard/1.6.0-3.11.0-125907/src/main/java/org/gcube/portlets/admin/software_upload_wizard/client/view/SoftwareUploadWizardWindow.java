package org.gcube.portlets.admin.software_upload_wizard.client.view;

import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.event.shared.HandlerManager;

public class SoftwareUploadWizardWindow extends WizardWindow {
	private HandlerManager eventBus = Util.getEventBus();

	public SoftwareUploadWizardWindow() {
		super("Software Upload Wizard");
		
		bind();
	}

	private void bind() {
		this.setNextButtonListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				getCurrentCard().performNextStepLogic();
			}
		});
		
		this.setBackButtonListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				getCurrentCard().performBackStepLogic();
			}
		});
		
	}
	
	

}
