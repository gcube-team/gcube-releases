/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.experiments;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public interface OperatorsPanelHandler {

	/**
	 * @param operatorPanel
	 *            operator panel
	 * @param operator
	 *            operator
	 */
	void addOperator(OperatorPanel operatorPanel, Operator operator);

}
