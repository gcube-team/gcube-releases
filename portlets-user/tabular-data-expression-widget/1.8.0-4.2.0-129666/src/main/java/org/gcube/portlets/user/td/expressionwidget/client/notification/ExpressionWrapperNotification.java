package org.gcube.portlets.user.td.expressionwidget.client.notification;

import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ExpressionWrapperNotification {

	private ExpressionWrapper expressionWrapper;

	public interface ExpressionWrapperNotificationListener {
		void onExpression(ExpressionWrapperNotification expressionWrapperNotification);

		void aborted();

		void failed(Throwable throwable);
	}

	public interface HasExpressionWrapperNotificationListener {
		public void addExpressionWrapperNotificationListener(ExpressionWrapperNotificationListener handler);

		public void removeExpressionWrapperNotificationListener(
				ExpressionWrapperNotificationListener handler);
		
	}

	
	
	public ExpressionWrapperNotification(ExpressionWrapper expressionWrapper) {
		this.expressionWrapper = expressionWrapper;
	}

	
	public ExpressionWrapper getExpressionWrapper() {
		return expressionWrapper;
	}

	public void setExpressionWrapper(ExpressionWrapper expressionWrapper) {
		this.expressionWrapper = expressionWrapper;
	}

	@Override
	public String toString() {
		return "ExpressionWrapperNotification [expressionWrapper="
				+ expressionWrapper + "]";
	}
	
}