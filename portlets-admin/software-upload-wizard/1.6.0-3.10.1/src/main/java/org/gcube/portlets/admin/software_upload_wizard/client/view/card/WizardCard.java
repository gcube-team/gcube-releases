package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import org.gcube.portlets.admin.software_upload_wizard.client.wizard.IWizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

public abstract class WizardCard extends FormPanel implements IWizardCard {
	
	public WizardCard(String wizardStepHeader) {
		
		this.setHeading(wizardStepHeader);
		this.setHeaderVisible(false);
		this.setBodyStyleName("wizardCardBody");
		this.setLabelWidth(150);
		this.setPadding(10);
		this.setScrollMode(Scroll.AUTO);
		
	}

	/**
	 * Encapsulates actions that setup controls when the panel is shown
	 */
	public abstract void setup();
	
	public abstract void performNextStepLogic();
	
	public abstract void performBackStepLogic();
	
	public abstract String getHelpContent();
	
	public void handleError(String errorTitle, String errorMsg, Throwable caught ){
		Log.error(errorMsg, caught);
		MessageBox.alert(errorTitle, errorMsg, null);
	}

	@Override
	public El mask() {
		return super.mask("Loading...", "loading");
	}

}
