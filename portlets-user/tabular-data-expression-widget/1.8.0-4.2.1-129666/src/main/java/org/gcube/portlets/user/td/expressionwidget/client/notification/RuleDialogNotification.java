package org.gcube.portlets.user.td.expressionwidget.client.notification;


/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RuleDialogNotification {

	private String ruleId;

	public interface RuleDialogNotificationListener {
		void onNotification(RuleDialogNotification ruleDialogNotification);

		void aborted();

		void failed(Throwable throwable);
	}

	public interface HasRuleDialogNotificationListener {
		public void addRuleDialogNotificationListener(
				RuleDialogNotificationListener handler);

		public void removeRuleDialogNotificationListener(
				RuleDialogNotificationListener handler);

	}

	public RuleDialogNotification(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	@Override
	public String toString() {
		return "RuleDialogNotification [ruleId=" + ruleId + "]";
	}

}