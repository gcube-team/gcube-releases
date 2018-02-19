package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.notification.RuleEditDialogNotification;
import org.gcube.portlets.user.td.expressionwidget.client.notification.RuleEditDialogNotification.HasRuleEditDialogNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.client.notification.RuleEditDialogNotification.RuleEditDialogNotificationListener;
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
public class RuleEditDialog extends Window implements
		HasRuleEditDialogNotificationListener {
	private static final String WIDTH = "660px";
	private static final String HEIGHT = "200px";
	private RuleEditPanel ruleEditPanel;
	private EventBus eventBus;
	private ArrayList<RuleEditDialogNotificationListener> listeners;
	public RuleEditDialog(RuleDescriptionData ruleDescriptionData, EventBus eventBus) {
		listeners = new ArrayList<RuleEditDialogNotificationListener>();
		initWindow();
		this.eventBus = eventBus;
		ruleEditPanel = new RuleEditPanel(this,
				ruleDescriptionData, eventBus);
		add(ruleEditPanel);

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setModal(true);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("Edit Rule On Column");
		setClosable(true);
		forceLayoutOnResize = true;
		getHeader().setIcon(ExpressionResources.INSTANCE.ruleEdit());

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

	public void updateColumnRule(RuleDescriptionData ruleDescriptionData) {
		Log.debug("RuleDescriptionData: " + ruleDescriptionData);
		updateColumnRuleCall(ruleDescriptionData);
	
	}

	protected void updateColumnRuleCall(RuleDescriptionData ruleDescriptionData) {

		ExpressionServiceAsync.INSTANCE.updateColumnRule(ruleDescriptionData, 
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void v) {
						Log.debug("Updated Rule: ");
						UtilsGXT3.info("Update Rule", "The rule is updated!");
						fireNotification();

					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error updating column rule: "
									+ caught.getLocalizedMessage());
							caught.printStackTrace();
							UtilsGXT3.alert("Error updating column rule",
									caught.getLocalizedMessage());

							fireFailed(caught);
						}

					}
				});

	}



	@Override
	public void addRuleEditDialogNotificationListener(
			RuleEditDialogNotificationListener handler) {
		listeners.add(handler);
	}

	@Override
	public void removeRuleEditDialogNotificationListener(
			RuleEditDialogNotificationListener handler) {
		listeners.remove(handler);

	}

	private void fireNotification() {
		if (listeners != null) {
			for (RuleEditDialogNotificationListener listener : listeners) {
				listener.onNotification(new RuleEditDialogNotification());
			}
		}
		hide();
	}

	private void fireAborted() {
		if (listeners != null) {
			for (RuleEditDialogNotificationListener listener : listeners) {
				listener.aborted();
			}
		}
		hide();
	}

	private void fireFailed(Throwable caught) {
		if (listeners != null) {
			for (RuleEditDialogNotificationListener listener : listeners) {
				listener.failed(caught);
			}
		}
		hide();

	}

}
