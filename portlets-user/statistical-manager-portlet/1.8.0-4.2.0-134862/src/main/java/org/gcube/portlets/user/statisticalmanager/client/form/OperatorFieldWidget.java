/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.form;

import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ListParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ObjectParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.Parameter;

import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.layout.TableData;

/**
 * @author ceras
 *
 */
public class OperatorFieldWidget extends HorizontalPanel {

	private Parameter parameter;

	private AbstractField field;
//	private Field generalParameterField;
//	private SimpleComboBox<String> inputDataParameterField;
//	private TextField<String> outputDataParameterField;

	/**
	 * 
	 */
	public OperatorFieldWidget(Parameter p) {
		super();
		this.setStyleAttribute("margin", "10px");
		this.parameter = p;

		Label label = new Label(p.getName());
		this.add(label, new TableData("200px", "30px"));

		try {
			if (p.isObject())
				field = createObjectField((ObjectParameter)p);
			else if (p.isEnum())
				field = new EnumField(p);
			else if (p.isTabular())
				field = new TabularField(p);
			else if (p.isTabularList())
				field = new TabularListField(p);
			else if (p.isColumn())
				field = new ColumnField(p);
			else if (p.isColumnList())
				field = new ColumnListField(p);
			else if (p.isList())
				field = createListField((ListParameter)p);
			else if (p.isFile())
				field = new FileField(p);
			else if (p.isBoundingBox())
				field = new BoundingBoxField(p);

			this.add(field.getWidget());
		
			if (p.getDescription() != null) {
				Html descr = new Html(p.getDescription());
				descr.addStyleName("workflow-fieldDescription");
				this.add(descr);
			}
		} catch (Exception e) {
			this.add(new Html("Error in field retrieving."));
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

	/**
	 * @param p
	 * @return
	 */
	private AbstractField createObjectField(ObjectParameter p) {
		String type = p.getType();
		if (type.contentEquals(Integer.class.getName())) {
			return new IntField(p);
		} else if (type.contentEquals(String.class.getName())) {
			return new StringField(p);
		} else if (type.contentEquals(Boolean.class.getName())) {
			return new BooleanField(p);
		} else if (type.contentEquals(Double.class.getName())) {
			return new DoubleField(p);
		} else if (type.contentEquals(Float.class.getName())) {
			return new FloatField(p);
		} else
			return null;
	}

	/**
	 * 
	 */
	private AbstractField createListField(ListParameter p) {
		String type = p.getType();

		if (type.contentEquals(String.class.getName()) || type.contentEquals("STRING")) { // TODO REMOVE "STRING"
			return new ListStringField(p);
		} else if (type.contentEquals(Integer.class.getName()) || type.contentEquals("NUMBER")) {
			return new ListIntField(p);
		}
//		} else if (type.contentEquals(Boolean.class.getName())) {
//			return new ListBooleanField(p);
//		} else if (type.contentEquals(Double.class.getName())) {
//			return new ListDoubleField(p);
//		} else if (type.contentEquals(Float.class.getName())) {
//			return new ListFloatField(p);
//		} 
		else
			return null;
	}


	/**
	 * 
	 */
	public void updateOperatorParameterValue() {
		this.parameter.setValue(this.getFieldValue());
	}
	
	public AbstractField getField() {
		return field;
	}
	
	public boolean isValid() {
		if (field!=null)
			return field.isValid();
		else
			return false;
	}
}
