package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.exception.MultiColumnExpressionPanelException;
import org.gcube.portlets.user.td.expressionwidget.client.notification.RuleDialogNotification;
import org.gcube.portlets.user.td.expressionwidget.client.notification.RuleDialogNotification.HasRuleDialogNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.client.notification.RuleDialogNotification.RuleDialogNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.rpc.ExpressionServiceAsync;
import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class RuleOnTableCreateDialog extends Window implements HasRuleDialogNotificationListener {
	private static final String WIDTH = "940px";
	private static final String HEIGHT = "388px";
	private MultiColumnExpressionPanel multiColumnExpressionPanel;
	private EventBus eventBus;
	private ArrayList<RuleDialogNotificationListener> listeners;
	private Object initialRuleDescriptionData;

	/**
	 * 
	 * @param ruleDescriptionData
	 *            Rule description
	 * @param eventBus
	 *            Event bus
	 */
	public RuleOnTableCreateDialog(RuleDescriptionData ruleDescriptionData, EventBus eventBus) {
		listeners = new ArrayList<RuleDialogNotificationListener>();
		initWindow();
		this.eventBus = eventBus;
		this.initialRuleDescriptionData = ruleDescriptionData;
		try {
			multiColumnExpressionPanel = new MultiColumnExpressionPanel(this, ruleDescriptionData);
			add(multiColumnExpressionPanel);
		} catch (MultiColumnExpressionPanelException e) {
			UtilsGXT3.alert("Attention", e.getLocalizedMessage());
		}

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setModal(true);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("New Rule On Column");
		setClosable(true);
		forceLayoutOnResize = true;
		getHeader().setIcon(ExpressionResources.INSTANCE.ruleTableAdd());

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
		fireAborted();
	}

	public void addRule(RuleDescriptionData ruleDescriptionData) {
		Log.debug("RuleDescriptionData: " + ruleDescriptionData);
		if (initialRuleDescriptionData == null) {
			saveColumnRule(ruleDescriptionData);
		} else {
			updateColumnRule(ruleDescriptionData);
		}
	}

	public void saveColumnRule(RuleDescriptionData ruleDescriptionData) {

		ExpressionServiceAsync.INSTANCE.saveRule(ruleDescriptionData, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String ruleId) {
				Log.debug("Saved Rule: " + ruleId);
				UtilsGXT3.info("Save Rule", "The rule is saved!");
				fireNotification(ruleId);

			}

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					Log.error("Error saving column rule: " + caught.getLocalizedMessage());
					caught.printStackTrace();
					UtilsGXT3.alert("Error saving column rule", caught.getLocalizedMessage());

					fireFailed(caught);
				}

			}
		});

	}

	private void updateColumnRule(RuleDescriptionData ruleDescriptionData) {
		fireAborted();
	}

	@Override
	public void addRuleDialogNotificationListener(RuleDialogNotificationListener handler) {
		listeners.add(handler);
	}

	@Override
	public void removeRuleDialogNotificationListener(RuleDialogNotificationListener handler) {
		listeners.remove(handler);

	}

	private void fireNotification(String ruleId) {
		if (listeners != null) {
			for (RuleDialogNotificationListener listener : listeners) {
				listener.onNotification(new RuleDialogNotification(ruleId));
			}
		}
		hide();
	}

	private void fireAborted() {
		if (listeners != null) {
			for (RuleDialogNotificationListener listener : listeners) {
				listener.aborted();
			}
		}
		hide();
	}

	private void fireFailed(Throwable caught) {
		if (listeners != null) {
			for (RuleDialogNotificationListener listener : listeners) {
				listener.failed(caught);
			}
		}
		hide();

	}

}
