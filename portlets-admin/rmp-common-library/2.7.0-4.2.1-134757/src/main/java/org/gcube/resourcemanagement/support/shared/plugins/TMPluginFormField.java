package org.gcube.resourcemanagement.support.shared.plugins;

import java.io.Serializable;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 1.0 Oct 2012
 *
 */
@SuppressWarnings("serial")
public class TMPluginFormField implements Serializable {

	private String label;
	private String defaultValue;
	private boolean required;
	private boolean repeatable;

	public TMPluginFormField() {
		super();
	}

	public TMPluginFormField(String label, String defaultValue,
			boolean required, boolean repeatable) {
		super();
		this.label = label;
		this.defaultValue = defaultValue;
		this.required = required;
		this.repeatable = repeatable;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isRepeatable() {
		return repeatable;
	}

	public void setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
	}	
	
	
}
