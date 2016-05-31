package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;

public class SubmitCompletedCard extends WizardCard {

	private WizardWindow window = Util.getWindow();

	private Button closeButton = new Button("Close");

	public SubmitCompletedCard() {
		super("Software Submission ");

		buildUI();

		bind();
	}

	private void buildUI() {
		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE.stepInfo_SubmitCompleted()
				.getText());
		
		this.add(stepInfo);
		this.add(closeButton);
	}
	
	private void bind() {
		closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		});
		
	}

	@Override
	public void setup() {
		// Do nothing
	}

	@Override
	public void performNextStepLogic() {
		// There is no next step

	}

	@Override
	public void performBackStepLogic() {
		// There is no previous step

	}

	@Override
	public String getHelpContent() {
		return null;
	}

}
