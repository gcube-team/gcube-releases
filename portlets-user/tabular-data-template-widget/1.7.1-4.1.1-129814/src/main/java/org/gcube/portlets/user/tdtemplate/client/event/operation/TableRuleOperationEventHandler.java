/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event.operation;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface TableRuleOperationEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 25, 2015
 */
public interface TableRuleOperationEventHandler extends EventHandler{
	

	/**
	 * On add table rule.
	 *
	 * @param tableRuleOperationEvent the table rule operation event
	 */
	void onAddTableRule(TableRuleOperationEvent tableRuleOperationEvent);

}
