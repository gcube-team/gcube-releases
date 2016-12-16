package org.gcube.portlets.user.td.rulewidget.client;

import org.gcube.portlets.user.td.expressionwidget.client.RuleEditDialog;
import org.gcube.portlets.user.td.expressionwidget.client.notification.RuleEditDialogNotification;
import org.gcube.portlets.user.td.expressionwidget.client.notification.RuleEditDialogNotification.RuleEditDialogNotificationListener;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.rulewidget.client.resources.ResourceBundle;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RuleOpenDialog extends Window implements
		RuleEditDialogNotificationListener {
	private static final String WIDTH = "770px";
	private static final String HEIGHT = "530px";
	private RuleOpenPanel rulesOpenPanel;
	private EventBus eventBus;
	private RuleOpenMessages msgs;

	public RuleOpenDialog(EventBus eventBus) {
		this.eventBus = eventBus;
		initMessages();
		initWindow();
		rulesOpenPanel = new RuleOpenPanel(this, eventBus);
		add(rulesOpenPanel);
	}

	protected void initMessages() {
		msgs = GWT.create(RuleOpenMessages.class);
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogRuleOpenHead());
		setClosable(true);
		setModal(true);
		forceLayoutOnResize = true;
		getHeader().setIcon(ResourceBundle.INSTANCE.ruleOpen());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	protected void close() {
		hide();
	}

	public void ruleEdit(RuleDescriptionData ruleDescriptionData) {
		openRuleDialog(ruleDescriptionData);

	}

	protected void openRuleDialog(RuleDescriptionData ruleDescriptionData) {
		Log.debug("Request Open New Rule Dialog");
		RuleEditDialog reDialog = new RuleEditDialog(ruleDescriptionData,
				eventBus);
		reDialog.addRuleEditDialogNotificationListener(this);
		reDialog.show();

	}

	@Override
	public void onNotification(
			RuleEditDialogNotification ruleEditDialogNotification) {
		rulesOpenPanel.gridReload();

	}

	@Override
	public void aborted() {

	}

	@Override
	public void failed(Throwable throwable) {
		Log.debug("Error in edit rule: " + throwable.getLocalizedMessage());

	}

}
