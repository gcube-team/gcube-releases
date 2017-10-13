/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import org.gcube.portlets.user.dataminermanager.shared.parameters.ListParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.ObjectParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;

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
			if (parameter.getTypology() != null) {
				switch (parameter.getTypology()) {
				case COLUMN:
					field = new ColumnFld(parameter);
					break;
				case COLUMN_LIST:
					field = new ColumnListFld(parameter);
					break;
				case ENUM:
					field = new EnumFld(parameter);
					break;
				case FILE:
					field = new FileFld(parameter);
					break;
				case LIST:
					field = createListField(parameter);
					break;
				case OBJECT:
					field = createObjectField(parameter);
					break;
				case TABULAR:
					field = new TabularFld(parameter);
					break;
				case TABULAR_LIST:
					field = new TabularListFld(parameter);
					break;
				case WKT:
					field = new WKTFld(parameter);
					break;
				case DATE:
					field = new DateFld(parameter);
					break;
				case TIME:
					field = new TimeFld(parameter);
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
			Log.error("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
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

	/**
	 * @param parameter
	 *            parameter
	 * @return abstract field
	 */
	private AbstractFld createObjectField(Parameter parameter) {
		ObjectParameter objectParameter = (ObjectParameter) parameter;
		String type = objectParameter.getType();
		if (type.contentEquals(Integer.class.getName())) {
			return new IntFld(objectParameter);
		} else if (type.contentEquals(String.class.getName())) {
			return new StringFld(objectParameter);
		} else if (type.contentEquals(Boolean.class.getName())) {
			return new BooleanFld(objectParameter);
		} else if (type.contentEquals(Double.class.getName())) {
			return new DoubleFld(objectParameter);
		} else if (type.contentEquals(Float.class.getName())) {
			return new FloatFld(objectParameter);
		} else
			return null;
	}

	private AbstractFld createListField(Parameter p) {
		ListParameter listParameter = (ListParameter) p;
		String type = listParameter.getType();

		if (type.contentEquals(String.class.getName()) || type.contentEquals("STRING")) { // TODO
																							// REMOVE
																							// "STRING"
			return new ListStringFld(listParameter);
		} else if (type.contentEquals(Integer.class.getName()) || type.contentEquals("NUMBER")) {
			return new ListIntFld(listParameter);
		}
		// } else if (type.contentEquals(Boolean.class.getName())) {
		// return new ListBooleanField(p);
		// } else if (type.contentEquals(Double.class.getName())) {
		// return new ListDoubleField(p);
		// } else if (type.contentEquals(Float.class.getName())) {
		// return new ListFloatField(p);
		// }
		else
			return null;
	}

	public void updateOperatorParameterValue() {
		this.parameter.setValue(this.getFieldValue());
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
