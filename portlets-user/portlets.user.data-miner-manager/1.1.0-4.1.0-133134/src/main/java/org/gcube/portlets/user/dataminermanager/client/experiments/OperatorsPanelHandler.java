/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.experiments;

import org.gcube.portlets.user.dataminermanager.shared.process.Operator;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface OperatorsPanelHandler {

	/**
	 * @param operatorPanel 
	 * @param operator
	 */
	void addOperator(OperatorPanel operatorPanel, Operator operator);

}
