/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public abstract class AbstractFld {

	protected Parameter parameter;

	public abstract String getValue();

	public abstract Widget getWidget();

	/**
	 * 
	 * @param parameter parameter
	 *            parameter
	 */
	public AbstractFld(Parameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * 
	 * @return Parameter
	 */
	public Parameter getParameter() {
		return parameter;
	}

	/**
	 * 
	 * @param parameter parameter
	 *            parameter
	 */
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * 
	 * @return true if is valid
	 */
	public boolean isValid() {
		return true;
	}

}
