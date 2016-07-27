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
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class OperatorFieldWidget {

	private Parameter parameter;
	private AbstractFld field;
	private FieldLabel parameterLabel;

	/**
	 */
	public OperatorFieldWidget(Parameter p) {
		super();
		this.parameter = p;

		try {
			if (p.isObject())
				field = createObjectField(p);
			else if (p.isEnum())
				field = new EnumFld(p);
			else if (p.isTabular())
				field = new TabularFld(p);
			else if (p.isTabularList())
				field = new TabularListFld(p);
			else if (p.isColumn())
				field = new ColumnFld(p);
			else if (p.isColumnList())
				field = new ColumnListFld(p);
			else if (p.isList())
				field = createListField(p);
			else if (p.isFile())
				field = new FileFld(p);

			if (field == null) {
				parameterLabel = new FieldLabel(null, p.getName());
				parameterLabel.setLabelWidth(200);
				parameterLabel.setLabelWordWrap(true);

			} else {
				parameterLabel = new FieldLabel(field.getWidget(), p.getName());
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
	 * @param p
	 * @return
	 */
	private AbstractFld createObjectField(Parameter p) {
		ObjectParameter objectParameter = (ObjectParameter) p;
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

	/**
	 * 
	 */
	private AbstractFld createListField(Parameter p) {
		ListParameter listParameter = (ListParameter) p;
		String type = listParameter.getType();

		if (type.contentEquals(String.class.getName())
				|| type.contentEquals("STRING")) { // TODO REMOVE "STRING"
			return new ListStringFld(listParameter);
		} else if (type.contentEquals(Integer.class.getName())
				|| type.contentEquals("NUMBER")) {
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

	/**
	 * 
	 */
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
