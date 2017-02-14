/**
 * 
 */
package org.gcube.portlets.user.td.expressionwidget.server;

import javax.servlet.http.HttpSession;

import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.FilterColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnByExpressionSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ExpressionSession {

	
	protected static final String COLUMN_FILTER_SESSION = "COLUMN_FILTER_SESSION";
	protected static final String COLUMN_FILTER_SESSION_TASK = "COLUMN_FILTER_SESSION_TASK";
	
	protected static final String REPLACE_COLUMN_BY_EXPRESSION_SESSION = "REPLACE_COLUMN_BY_EXPRESSION_SESSION";
	
	protected static Logger logger = LoggerFactory.getLogger(ExpressionSession.class);


	public static FilterColumnSession getColumnFilterSession(
			HttpSession httpSession) {
		FilterColumnSession columnFilterSession = (FilterColumnSession) httpSession
				.getAttribute(COLUMN_FILTER_SESSION);
		if (columnFilterSession != null) {
			return columnFilterSession;
		} else {
			columnFilterSession = new FilterColumnSession();
			httpSession.setAttribute(COLUMN_FILTER_SESSION,
					columnFilterSession);
			return columnFilterSession;
		}
	}

	public static void setColumnFilterSession(HttpSession httpSession,
			FilterColumnSession columnFilterSession) {
		FilterColumnSession cf = (FilterColumnSession) httpSession
				.getAttribute(COLUMN_FILTER_SESSION);
		if (cf != null) {
			httpSession.removeAttribute(COLUMN_FILTER_SESSION);
		}
		httpSession.setAttribute(COLUMN_FILTER_SESSION,
				columnFilterSession);

	}
	
	public static ReplaceColumnByExpressionSession getReplaceColumnByExpressionSession(
			HttpSession httpSession) {
		ReplaceColumnByExpressionSession replaceColumnByExpressionSession = (ReplaceColumnByExpressionSession) httpSession
				.getAttribute(REPLACE_COLUMN_BY_EXPRESSION_SESSION);
		if (replaceColumnByExpressionSession != null) {
			return replaceColumnByExpressionSession;
		} else {
			replaceColumnByExpressionSession = new ReplaceColumnByExpressionSession();
			httpSession.setAttribute(REPLACE_COLUMN_BY_EXPRESSION_SESSION,
					replaceColumnByExpressionSession);
			return replaceColumnByExpressionSession;
		}
	}

	public static void setReplaceColumnByExpressionSession(HttpSession httpSession,
			ReplaceColumnByExpressionSession replaceColumnByExpressionSession) {
		ReplaceColumnByExpressionSession rce = (ReplaceColumnByExpressionSession) httpSession
				.getAttribute(REPLACE_COLUMN_BY_EXPRESSION_SESSION);
		if (rce != null) {
			httpSession.removeAttribute(REPLACE_COLUMN_BY_EXPRESSION_SESSION);
		}
		httpSession.setAttribute(REPLACE_COLUMN_BY_EXPRESSION_SESSION,
				replaceColumnByExpressionSession);

	}
	

	

	public static Task getColumnFilterTask(HttpSession httpSession) {
		Task monitor = (Task) httpSession.getAttribute(COLUMN_FILTER_SESSION_TASK);
		if (monitor == null) {
			logger.error("CHANGE_THE_COLUMN_LABEL_TASK was not acquired");
		}
		return monitor;
	}

	public static void setColumnFilterTask(HttpSession httpSession,
			Task task) {
		Task monitor = (Task) httpSession.getAttribute(COLUMN_FILTER_SESSION_TASK);
		if (monitor != null)
			httpSession.removeAttribute(COLUMN_FILTER_SESSION_TASK);
		httpSession.setAttribute(COLUMN_FILTER_SESSION_TASK, task);
	}
	
	

}
