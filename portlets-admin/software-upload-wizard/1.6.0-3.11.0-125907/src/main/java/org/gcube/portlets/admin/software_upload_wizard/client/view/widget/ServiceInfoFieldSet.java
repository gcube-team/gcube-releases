package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class ServiceInfoFieldSet extends GenericComponentInfoFieldSet {

	ServiceClassField classField = new ServiceClassField();

	// LayoutContainer classFieldContainer = new LayoutContainer();

	public ServiceInfoFieldSet(String componentName) {
		super(componentName);

		buildUI();
		
		//Disable version field as the service version should always be 1.0.0 
		versionField.setEnabled(false);
	}

	public ServiceInfoFieldSet() {
		this("Service");
	}

	private void buildUI() {

		FormLayout formLayout = new FormLayout();
		formLayout.setLabelWidth(((FormLayout) this.getLayout())
				.getLabelWidth());
		// classFieldContainer.setLayout(formLayout);
		//
		// classFieldContainer.add(classField);
		// this.add(classFieldContainer);
		this.add(classField);
	}

	public String getClazz() {
		return classField.getValue();
	}

	public void setClassName(String value) {
		classField.setRawValue(value);
	}

	public void setEnableClassField(boolean value) {
		classField.setEnabled(value);
//		classField.setReadOnly(value);
//		if (value) classField.unmask(); else classField.mask();
	}
}
