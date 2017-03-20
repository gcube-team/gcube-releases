/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
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
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class CodelistMappingUrlSelectionCard extends WizardCard {
	private static CodelistMappingMessages msgs= GWT.create(CodelistMappingMessages.class);
	private CommonMessages msgsCommon;
	private CodelistMappingSession codelistMappingSession;
	private CodelistMappingUrlSelectionCard thisCard;

	private TextField urlField;
	
	public CodelistMappingUrlSelectionCard(
			final CodelistMappingSession codelistMappingSession) {
		super(msgs.codelistMappingUrlSelectionCardHead(), "");
		this.thisCard = this;
		this.codelistMappingSession = codelistMappingSession;
		initMessages();
		create();
	}

	
	protected void initMessages(){
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	protected void create(){
		FramedPanel form = new FramedPanel();
		form.setHeaderVisible(false);
		form.setBodyBorder(false);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		form.add(v);

		urlField = new TextField();
		urlField.setAllowBlank(false);
		urlField.setEmptyText(msgs.urlFieldEmptyText());
		urlField.setValue(codelistMappingSession.getUrl());
		urlField.setAllowBlank(false);
		v.add(new FieldLabel(urlField, msgs.urlFieldLabel()), new VerticalLayoutData(1, -1));

		setContent(form);
	}
	
	protected void checkUrl() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		AlertMessageBox d;
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(false);

			}
		};

		if (urlField.getValue() == null || urlField.getValue().isEmpty()
				|| !urlField.isValid()) {
			d = new AlertMessageBox(msgsCommon.attention(), msgs.attentionFillInAllFields());
			d.addHideHandler(hideHandler);
			d.show();
		} else {
			urlField.setReadOnly(true);
			goNext();
		}
	}

	protected void goNext() {
		try {
			String url = urlField.getCurrentValue();
			codelistMappingSession.setUrl(url);

			TabResourcesSelectionCard tabResourceSelectionCard = new TabResourcesSelectionCard(
					codelistMappingSession);
			getWizardWindow().addCard(tabResourceSelectionCard);
			Log.info("NextCard TabResourceSelectionCard");
			getWizardWindow().nextCard();

		} catch (Throwable e) {
			Log.error("goNext: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				checkUrl();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove CodelistMappingUrlSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
	
		setEnableNextButton(true);
		setNextButtonVisible(true);
		setEnableBackButton(true);
		setBackButtonVisible(true);
		
	}

}
