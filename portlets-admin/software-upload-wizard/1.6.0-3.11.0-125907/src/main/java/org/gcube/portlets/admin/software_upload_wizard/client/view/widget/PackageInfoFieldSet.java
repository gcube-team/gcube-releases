package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

public class PackageInfoFieldSet extends GenericComponentInfoFieldSet {

	public PackageInfoFieldSet() {
		this("Package");
	}

	public PackageInfoFieldSet(String componentName) {
		super(componentName);
		
//		initToolTip();

	}

	private void initToolTip() {
		ToolTipConfig fieldSetToolTip = new DefaultTooltipConfig();

		fieldSetToolTip.setTitle("Package related data");
		String toolTipText = "<p>In this section the user enters data related to the Package section of the Service Profile. All fields are mandatory if not otherwise declared.</p>"
				+ "<ul>"
				+ "<li><b>Package name</b>: <i>Name</i> assigned to the <i>Package</i> in the Service Profile. Only alphanumeric chars and '-' symbol are allowed.</li>"
				+ "<li><b>Package description</b> (optional): <i>Description</i> assigned to the <i>Package</i> in the Service Profile.</li>"
				+ "<li><b>Package version</b>: <i>Description</i> assigned to the <i>Package</i> in the Service Profile.</li>"
				+ "</ul>";

		fieldSetToolTip.setText(toolTipText);
		this.setToolTip(fieldSetToolTip);
	}

}
