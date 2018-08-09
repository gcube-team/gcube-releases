/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *         
 * 
 */
public class CodelistMappingTableDetailCard extends WizardCard {
	private static CodelistMappingMessages msgs = GWT.create(CodelistMappingMessages.class);
	private CommonMessages msgsCommon;
	private CodelistMappingSession codelistMappingSession;
	private CodelistMappingTableDetailCard thisCard;

	private VerticalLayoutContainer p;

	private TextField fieldName;
	private TextArea fieldDescription;

	private ResourceTDDescriptor resourceDetails = new ResourceTDDescriptor();
	

	public CodelistMappingTableDetailCard(
			final CodelistMappingSession codelistMappingSession) {
		super(msgs.codelistMappingDetailCardHead(), "");
		this.codelistMappingSession = codelistMappingSession;
		thisCard = this;
		
		initMessages();
		
		FramedPanel form = new FramedPanel();
		form.setHeaderVisible(false);

		p = new VerticalLayoutContainer();
		form.add(p);

		fieldName = new TextField();
		fieldName.setAllowBlank(false);
		fieldName.setEmptyText(msgs.fieldNameEmptyText());
		if (codelistMappingSession.getLocalFileName() != null
				&& !codelistMappingSession.getLocalFileName().isEmpty()) {
			fieldName.setValue(codelistMappingSession.getLocalFileName());
		} else {

		}
		p.add(new FieldLabel(fieldName, msgs.fieldNameLabel()), new VerticalLayoutData(1, -1));

		fieldDescription = new TextArea();
		fieldDescription.setAllowBlank(false);
		fieldDescription.setEmptyText(msgs.fieldDescriptionEmptyText());
		fieldDescription.setValue(msgs.fieldDescriptionDefaultValue());
		p.add(new FieldLabel(fieldDescription, msgs.fieldDescriptionLabel()),
				new VerticalLayoutData(1, -1));

		setContent(form);

	}
	
	protected void initMessages(){
		msgsCommon = GWT.create(CommonMessages.class);
	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				checkData();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove CodelistMappingTableDetailCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setEnableNextButton(true);

	}

	protected void checkData() {
		Log.debug("checkData()");
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		AlertMessageBox d;
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(true);

			}
		};

		if (fieldName.getValue() == null || fieldName.getValue().isEmpty()
				|| !fieldName.isValid() || fieldDescription.getValue() == null
				|| fieldDescription.getValue().isEmpty() || !fieldDescription.isValid()) {
			d = new AlertMessageBox(msgsCommon.attention(), msgs.attentionFillInAllFields());
			d.addHideHandler(hideHandler);
			d.show();
		} else {
			fieldName.setReadOnly(true);
			fieldDescription.setReadOnly(true);
			goNext();
		}
	}

	protected void goNext() {
		Log.debug("goNext()");
		try {
			resourceDetails.setName(fieldName.getCurrentValue());
			resourceDetails.setDescription(fieldDescription.getCurrentValue());

			codelistMappingSession.setResourceTDDescriptor(resourceDetails);

			CodelistMappingOperationInProgressCard codelistMappingOperationInProgressCard = new CodelistMappingOperationInProgressCard(
					codelistMappingSession);
			getWizardWindow().addCard(codelistMappingOperationInProgressCard);
			Log.info("NextCard CodelistMappingOperationInProgressCard");
			getWizardWindow().nextCard();

			

		} catch (Throwable e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}

	

}
