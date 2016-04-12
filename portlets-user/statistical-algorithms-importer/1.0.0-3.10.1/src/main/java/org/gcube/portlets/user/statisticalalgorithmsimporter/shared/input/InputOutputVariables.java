package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input;

import java.io.Serializable;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class InputOutputVariables implements Serializable {

	private static final long serialVersionUID = -2519686355634242523L;
	private int id;
	private String name;
	private String description;
	private String defaultValue;
	private DataType dataType;
	private IOType ioType;
	private String sourceSelection;

	public InputOutputVariables() {
		super();
	}
	
	public InputOutputVariables(String name, String description,
			String defaultValue, DataType dataType, IOType ioType,
			String sourceSelection){
		super();
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.dataType = dataType;
		this.ioType = ioType;
		this.sourceSelection = sourceSelection;
	}
	

	public InputOutputVariables(int id, String name, String description,
			String defaultValue, DataType dataType, IOType ioType,
			String sourceSelection) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.dataType = dataType;
		this.ioType = ioType;
		this.sourceSelection = sourceSelection;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public IOType getIoType() {
		return ioType;
	}

	public void setIoType(IOType ioType) {
		this.ioType = ioType;
	}

	public String getSourceSelection() {
		return sourceSelection;
	}

	public void setSourceSelection(String sourceSelection) {
		this.sourceSelection = sourceSelection;
	}

	@Override
	public String toString() {
		return "InputOutputVariables [id=" + id + ", name=" + name
				+ ", description=" + description + ", defaultValue="
				+ defaultValue + ", dataType=" + dataType + ", ioType="
				+ ioType + ", sourceSelection=" + sourceSelection + "]";
	}

	
	
	
}
