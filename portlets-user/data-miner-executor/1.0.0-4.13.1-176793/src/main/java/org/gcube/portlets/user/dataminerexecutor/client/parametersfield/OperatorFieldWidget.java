/**
 * 
 */
package org.gcube.portlets.user.dataminerexecutor.client.parametersfield;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;


import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class OperatorFieldWidget {

	private Parameter parameter;
	private AbstractFld field;
	private FieldLabel parameterLabel;

	/**
	 * 
	 * @param parameter
	 *            parameter
	 */
	public OperatorFieldWidget(Parameter parameter) {
		super();
		this.parameter = parameter;

		try {
			field = new StringFld(parameter);
			
			if (parameter.getTypology() != null) {
				switch (parameter.getTypology()) {
				case COLUMN:
				case WKT:
				case DATE:
				case TIME:	
				case ENUM:
				case OBJECT:	
					field = new StringFld(parameter);
					break;
				case FILE:
					field = new FileFld(parameter);
					break;	
				case TABULAR:
					field = new TabularFld(parameter);
					break;
				case COLUMN_LIST:
					field = new ColumnListFld(parameter);
					break;
				case LIST:
					field = new ListStringFld(parameter);
					break;
				case TABULAR_LIST:
					field = new TabularListFld(parameter);
					break;
				default:
					break;

				}
			}

			
			
			if (field == null) {
				parameterLabel = new FieldLabel(null, parameter.getName());
				parameterLabel.setLabelWidth(200);
				parameterLabel.setLabelWordWrap(true);

			} else {
				parameterLabel = new FieldLabel(field.getWidget(), parameter.getName());
				parameterLabel.setLabelWidth(200);
				parameterLabel.setLabelWordWrap(true);
			}
		} catch (Throwable e) {
			Log.error("Error: " + e.getLocalizedMessage(),e);
		}

	}

	/**
	 * @return the parameter
	 */
	public Parameter getParameter() {
		return parameter;
	}

	public String getFieldValue() {
		return field.getValue();
	}

	public String getValue() {
		return getFieldValue();
	}

	public FieldLabel getParameterLabel() {
		return parameterLabel;
	}


	public AbstractFld getField() {
		return field;
	}

	public boolean isValid() {
		if (field != null)
			return field.isValid();
		else
			return false;
	}
}
