package org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationDescriptor implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3508407873723992386L;
	
	private String description;
	
	private String title;
	
	private boolean valid;
	
	@XmlElement
	private String validationColumn = null;
	
	@SuppressWarnings("unused")
	private ValidationDescriptor(){}
	
	private int conditionCode;
	
	public ValidationDescriptor(String description, boolean valid, int conditionCode) {
		super();
		this.description = description;
		this.valid = valid;
		this.conditionCode = conditionCode;
	}

	public ValidationDescriptor(String title, String description, boolean valid, int conditionCode) {
		this(description, valid, conditionCode);
		this.title = title;
	}
	
	public ValidationDescriptor(String title, String description, boolean valid, int conditionCode, String validationColumn ) {
		this(title, description, valid, conditionCode);
		this.validationColumn = validationColumn;
	}
	
	public ValidationDescriptor(String description, boolean valid, int conditionCode, String validationColumn ) {
		this(description, valid, conditionCode);
		this.validationColumn = validationColumn;
	}
	
	public String getDescription() {
		return description;
	}

	public boolean isValid() {
		return valid;
	}
	
	public String getValidationColumn() {
		return validationColumn;
	}

	public int getConditionCode() {
		return conditionCode;
	}
	
	public String getTitle() {
		return title;
	}

	public void setValidationColumn(String validationColumn) {
		this.validationColumn = validationColumn;
	}

	@Override
	public String toString() {
		return "ValidationDescriptor [description=" + description + ", valid="
				+ valid + "]";
	}
		
}
