package org.gcube.portlets.user.td.gwtservice.shared.template;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

/**
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TemplateColumnData implements Serializable {

	private static final long serialVersionUID = -4747143287880727022L;

	private String id; // For insert in table only
	private String columnId; // Id of column on server
	private ColumnTypeCode typeCode;
	private ColumnDataType dataTypeName;
	private String label;

	public TemplateColumnData() {
		super();
	}

	public TemplateColumnData(String id, String columnId,
			ColumnTypeCode typeCode, ColumnDataType dataTypeName, String label) {
		super();
		this.id = id;
		this.columnId = columnId;
		this.typeCode = typeCode;
		this.dataTypeName = dataTypeName;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public ColumnTypeCode getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(ColumnTypeCode typeCode) {
		this.typeCode = typeCode;
	}

	public ColumnDataType getDataTypeName() {
		return dataTypeName;
	}

	public void setDataTypeName(ColumnDataType dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "TemplateColumnData [id=" + id + ", columnId=" + columnId
				+ ", typeCode=" + typeCode + ", dataTypeName=" + dataTypeName
				+ ", label=" + label + "]";
	}

}
