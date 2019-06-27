package org.gcube.portlets.widgets.wstaskexecutor.client.view.binder;

import java.util.List;

import org.gcube.common.workspacetaskexecutor.shared.TaskParameterType;
import org.gcube.portlets.widgets.wstaskexecutor.client.WsTaskExecutorWidget;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.DeleteCustomFieldEvent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class CustomFieldEntry.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 8, 2018
 */
public class CustomFieldEntry extends Composite {

	private static CustomFieldEntryUiBinder uiBinder = GWT
			.create(CustomFieldEntryUiBinder.class);

	/**
	 * The Interface CustomFieldEntryUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * May 8, 2018
	 */
	interface CustomFieldEntryUiBinder extends
	UiBinder<Widget, CustomFieldEntry> {
	}

	@UiField InputAddOn keyFieldPrepend;
	@UiField InputAddOn valueFieldPrepend;
	@UiField InputAddOn valuesSelectPrepend;
	@UiField Button removeCustomField;
	@UiField ListBox field_select_parameter;
	@UiField ControlGroup cg_parameter;
	@UiField TextBox field_input_value;
	//@UiField ListBox field_select_value;
	@UiField Controls control_select_value;
	@UiField Controls control_input_value;

	private List<TaskParameterType> parameterTypes;

	private List<String> values;
	private String key;
	//private boolean isCustomCreatedByUser;

	// event bus
	private HandlerManager eventBus;
	private String parameterType;


	/**
	 * Instantiates a new custom field entry.
	 *
	 * @param eventBus the event bus
	 * @param key the key
	 * @param value the value
	 * @param parameterType the parameter type
	 */
	public CustomFieldEntry(HandlerManager eventBus, String key, List<String> values, final String parameterType, boolean removable) {
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().getStyle().setMarginTop(10, Unit.PX);
		this.getElement().getStyle().setMarginBottom(20, Unit.PX);
		cg_parameter.getElement().setId(Random.nextInt()+Random.nextInt()+"");

		keyFieldPrepend.setTitle("This is the key of the parameter");
		valueFieldPrepend.setTitle("This is the value of the parameter");

		control_select_value.setVisible(false);

		// save information
		this.eventBus = eventBus;
		this.key = key;
		this.values = values;
		this.parameterType = parameterType;


		if(key!=null && !key.isEmpty()){
			((TextBox)this.keyFieldPrepend.getWidget(1)).setText(key);
		}

		if(values!=null && !values.isEmpty()){
			//one default value is added
			if(values.size()==1){
				control_input_value.setVisible(true);
				((TextBox)this.valueFieldPrepend.getWidget(1)).setText(values.get(0));
			}
			else{
				//Many default value existing.. creating combo box to them
				control_input_value.setVisible(false);
				control_select_value.setVisible(true);
				ListBox selectValues = (ListBox) this.valuesSelectPrepend.getWidget(1);
				for (String value : values) {
					selectValues.addItem(value, value);
				}
			}

		}

		WsTaskExecutorWidget.wsTaskService.getAvailableParameterTypes(new AsyncCallback<List<TaskParameterType>>() {


			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				field_select_parameter.setEnabled(false);

			}

			@Override
			public void onSuccess(List<TaskParameterType> result) {

				parameterTypes = result;

				for (TaskParameterType taskParameterType : result) {
					field_select_parameter.addItem(taskParameterType.getType(), taskParameterType.getType());
				}

				field_select_parameter.setEnabled(true);

				if(parameterType!=null && !parameterType.isEmpty()){
					field_select_parameter.setSelectedValue(parameterType);
					field_select_parameter.setEnabled(false);
				}

			}
		});

		field_select_parameter.setEnabled(false);

		removeCustomField.setVisible(removable);

	}

	/**
	 * Sets the enable value.
	 *
	 * @param enabled the new enable value
	 */
	public void setEnableValue(boolean enabled){
		((TextBox)this.valueFieldPrepend.getWidget(1)).setEnabled(enabled);
	}


	/**
	 * Gets the control group.
	 *
	 * @return the cg_parameter
	 */
	public ControlGroup getControlGroup() {

		return cg_parameter;
	}
	/**
	 * Retrieve the key value.
	 *
	 * @return the key
	 */
	public String getKey(){

		return ((TextBox)this.keyFieldPrepend.getWidget(1)).getText();
	}

	/**
	 * Retrieve the value.
	 *
	 * @return the value
	 */
	public String getValue(){

		if(values!=null && values.size()>1)
			return ((ListBox) this.valuesSelectPrepend.getWidget(1)).getSelectedValue();

		return ((TextBox)this.valueFieldPrepend.getWidget(1)).getText();

	}

	/**
	 * On remove custom field.
	 *
	 * @param e the e
	 */
	@UiHandler("removeCustomField")
	void onRemoveCustomField(ClickEvent e){

		// fire event
		eventBus.fireEvent(new DeleteCustomFieldEvent(this));

	}

	/**
	 * Remove delete button.
	 */
	public void freeze() {

		removeCustomField.setEnabled(false);

	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType(){
		return field_select_parameter.getSelectedValue();
	}

}
