package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.Version;

import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.NumberField;

public class VersionField extends MultiField<Integer> {

	private NumberField majorVersion = new NumberField();
	private NumberField minorVersion = new NumberField();
	private NumberField ageVersion = new NumberField();

	public VersionField() {
		this("Version*");
	}

	public VersionField(String label) {
		super(label);

		majorVersion.setMessageTarget("tooltip");
		majorVersion.setAllowBlank(false);
		majorVersion.setAllowDecimals(false);
		majorVersion.setAllowNegative(false);
		majorVersion.setPropertyEditorType(Integer.class);
		majorVersion.setWidth(30);

		minorVersion.setMessageTarget("tooltip");
		minorVersion.setAllowBlank(false);
		minorVersion.setAllowDecimals(false);
		minorVersion.setAllowNegative(false);
		minorVersion.setPropertyEditorType(Integer.class);
		minorVersion.setWidth(30);

		ageVersion.setMessageTarget("tooltip");
		ageVersion.setAllowBlank(false);
		ageVersion.setAllowDecimals(false);
		ageVersion.setAllowNegative(false);
		ageVersion.setPropertyEditorType(Integer.class);
		ageVersion.setWidth(30);

		add(majorVersion);
		add(minorVersion);
		add(ageVersion);
		setSpacing(5);
	}

	public VersionField(String label, Version defaultVersion) {
		this(label);

		// Set initial version values
		majorVersion.setValue(defaultVersion.getMajorVersion());
		minorVersion.setValue(defaultVersion.getMinorVersion());
		ageVersion.setValue(defaultVersion.getAgeVersion());
	}

	public Version getVersion() {
		int major;
		try {
			major = majorVersion.getValue().intValue();
		} catch (Exception e) {
			major = 0;
		}
		int age;
		try {
			age = minorVersion.getValue().intValue();
		} catch (Exception e) {
			age = 0;
		}
		int minor;
		try {
			minor = ageVersion.getValue().intValue();
		} catch (Exception e) {
			minor = 0;
		}
		return new Version(major, age, minor);
	}

	public void setVersion(Version version) {
		majorVersion.setRawValue(String.valueOf(version.getMajorVersion()));
		minorVersion.setRawValue(String.valueOf(version.getMinorVersion()));
		ageVersion.setRawValue(String.valueOf(version.getAgeVersion()));
	}

}
