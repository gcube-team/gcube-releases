/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.experimentArea;

import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;

/**
 * @author ceras
 *
 */
public interface OperatorsPanelHandler {

	/**
	 * @param operatorPanel 
	 * @param operator
	 */
	void addOperator(OperatorPanel operatorPanel, Operator operator);

}
