/**
 * 
 */
package org.gcube.portlets.user.td.expressionwidget.server;

import javax.servlet.http.HttpServletRequest;

import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.portlets.user.td.gwtservice.server.SessionConstants;
import org.gcube.portlets.user.td.gwtservice.server.SessionOp;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.FilterColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnByExpressionSession;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ExpressionSession {

	public static FilterColumnSession getColumnFilterSession(
			HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<FilterColumnSession> sessionOp = new SessionOp<>();
		FilterColumnSession columnFilterSession = sessionOp.get(httpRequest,
				serviceCredentials, SessionConstants.COLUMN_FILTER_SESSION,
				FilterColumnSession.class);
		return columnFilterSession;
	}

	public static void setColumnFilterSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials,
			FilterColumnSession columnFilterSession) {
		SessionOp<FilterColumnSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials,
				SessionConstants.COLUMN_FILTER_SESSION, columnFilterSession);
	}

	public static ReplaceColumnByExpressionSession getReplaceColumnByExpressionSession(
			HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ReplaceColumnByExpressionSession> sessionOp = new SessionOp<>();
		ReplaceColumnByExpressionSession replaceColumnByExpressionSession = sessionOp
				.get(httpRequest, serviceCredentials,
						SessionConstants.REPLACE_COLUMN_BY_EXPRESSION_SESSION,
						ReplaceColumnByExpressionSession.class);
		return replaceColumnByExpressionSession;
	}

	public static void setReplaceColumnByExpressionSession(
			HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials,
			ReplaceColumnByExpressionSession replaceColumnByExpressionSession) {
		SessionOp<ReplaceColumnByExpressionSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials,
				SessionConstants.REPLACE_COLUMN_BY_EXPRESSION_SESSION,
				replaceColumnByExpressionSession);

	}

	public static Task getColumnFilterTask(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<Task> sessionOp = new SessionOp<>();
		Task monitor = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.COLUMN_FILTER_SESSION_TASK);
		return monitor;
	}

	public static void setColumnFilterTask(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, Task task) {
		SessionOp<Task> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials,
				SessionConstants.COLUMN_FILTER_SESSION_TASK, task);
	}

}
