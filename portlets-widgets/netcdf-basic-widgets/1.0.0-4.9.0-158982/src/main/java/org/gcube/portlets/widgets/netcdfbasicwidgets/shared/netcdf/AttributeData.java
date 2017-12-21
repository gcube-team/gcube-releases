package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf;

import java.io.Serializable;

import com.google.gwt.view.client.ProvidesKey;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AttributeData implements Serializable, Comparable<AttributeData> {

	private static final long serialVersionUID = -273113960995381444L;

	/**
	 * The key provider that provides the unique ID of a variable.
	 */
	public static final ProvidesKey<AttributeData> KEY_PROVIDER = new ProvidesKey<AttributeData>() {
		@Override
		public Object getKey(AttributeData attributeData) {
			return attributeData == null ? null : attributeData.getId();
		}
	};

	private int id;
	private String fullName;
	private String dataType;
	private String values;

	public AttributeData() {
		super();
	}

	public AttributeData(int id, String fullName, String dataType, String values) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.dataType = dataType;
		this.values = values;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	@Override
	public int compareTo(AttributeData attributeData) {
		return (id < attributeData.id) ? -1 : ((id == attributeData.id) ? 0 : 1);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AttributeData) {
			return id == ((AttributeData) o).id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "AttributeData [id=" + id + ", fullName=" + fullName + ", dataType=" + dataType + ", values=" + values
				+ "]";
	}

}
