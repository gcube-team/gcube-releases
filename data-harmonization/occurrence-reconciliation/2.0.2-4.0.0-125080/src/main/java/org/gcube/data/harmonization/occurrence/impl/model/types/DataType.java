package org.gcube.data.harmonization.occurrence.impl.model.types;

import java.util.List;

public class DataType{

	public static enum Type{
		TABULAR,FILE,PRIMITIVE,LIST,ENUM,TABULAR_LIST,COLUMN_LIST,COLUMN
	}

	private List<String> value;
	private Type type;
	public DataType(List<String> value, Type type) {
		super();
		this.value = value;
		this.type = type;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataType [value=");
		builder.append(value);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
	/**
	 * @return the value
	 */
	public List<String> getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(List<String> value) {
		this.value = value;
	}
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}
	
	
	
}