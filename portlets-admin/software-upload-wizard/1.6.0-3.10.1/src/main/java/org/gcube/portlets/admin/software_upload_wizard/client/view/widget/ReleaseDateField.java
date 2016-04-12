package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.form.DateField;

public class ReleaseDateField extends DateField {
	public ReleaseDateField() {
		this.setFieldLabel("Release date*");
		this.setAllowBlank(false);
	}
}
