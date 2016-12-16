/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public abstract class AbstractFld {
	
	protected Parameter parameter;
	public abstract String getValue();	
	public abstract Widget getWidget();
	
	
	/**
	 * 
	 */
	public AbstractFld(Parameter parameter) {
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
	 * 
	 * @return
	 */
	public boolean isValid() {
		return true;
	}
	
	
}
