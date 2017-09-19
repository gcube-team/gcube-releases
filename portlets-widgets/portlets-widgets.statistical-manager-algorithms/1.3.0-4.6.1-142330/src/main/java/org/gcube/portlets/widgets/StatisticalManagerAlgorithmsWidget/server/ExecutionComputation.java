/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server;



import javax.servlet.http.HttpSession;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.Operator;



public interface ExecutionComputation {
	

	public String getId();
	
	

	public String startComputation(HttpSession session,Operator operator, String computationTitle,
			String computationDescription) throws Exception;



}
