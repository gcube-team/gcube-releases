/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;

import com.google.gwt.user.client.ui.Widget;





/**
 * @author ceras
 *
 */
public abstract class AbstractField {	
	
	Parameter parameter;
	public abstract String getValue();	
	public abstract Widget getWidget();
	
	/**
	 * 
	 */
	public AbstractField(Parameter parameter) {
		this.parameter = parameter;
	}
	
	/**
	 * @return the operator
	 */
	public Parameter getParameter() {
		return parameter;
	}
	
	/**
	 * @param operator the operator to set
	 */
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	
	/**
	 * @return
	 */
	public boolean isValid() {
		// is valid by dafault
		// (for string, int, float, double and boolean) the  built-in validation is enough
		return true;
	}
	/**
	 * @param tableItem
	 */
	public void fireEvent(Object message) {		
	}	
}
