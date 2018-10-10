package org.gcube.portlets.user.td.rulewidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.rpc.ExpressionServiceAsync;
import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.rulewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class RuleDeleteDialog extends Window {
	private static final String WIDTH = "770px";
	private static final String HEIGHT = "530px";
	private EventBus eventBus;
	private RuleDeletePanel ruleDeletePanel;
	private RuleDeleteMessages msgs;

	public RuleDeleteDialog(EventBus eventBus) {
		this.eventBus = eventBus;
		initMessages();
		initWindow();
		
		ruleDeletePanel = new RuleDeletePanel(this, eventBus);
		add(ruleDeletePanel);
	}
	
	protected void initMessages(){
		msgs = GWT.create(RuleDeleteMessages.class);
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogRuleDeleteHead());
		setClosable(true);
		setModal(true);
		forceLayoutOnResize = true;
		getHeader().setIcon(ResourceBundle.INSTANCE.ruleDelete());

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

	protected void removeRule(ArrayList<RuleDescriptionData> rules) {

		ExpressionServiceAsync.INSTANCE.removeRulesById(rules,
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void v) {
						Log.debug("Rule is deleted!");
						UtilsGXT3.info(msgs.ruleIsDeletedHead(), msgs.ruleIsDeleted());
						ruleDeletePanel.gridReload();
					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error deleting column rule: "
									+ caught.getLocalizedMessage());
							caught.printStackTrace();
							UtilsGXT3.alert(msgs.errorDeletingRuleOnColumnHead(),
									caught.getLocalizedMessage());
						}
						ruleDeletePanel.gridReload();

					}
				});
	}

}
