package org.gcube.portlets.user.td.gwtservice.shared.tr;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TableData implements Serializable {

	private static final long serialVersionUID = -6248251038277538555L;

	protected String id; // For insert in table only
	protected String name;
	protected String typeName;
	protected String typeCode;
	protected String metaData; // Metadata Description

	protected ArrayList<ColumnData> listColumnData;
	protected TRId trId;

	public TableData() {

	}

	/**
	 * 
	 * @param id
	 *            Only for insert in grid
	 * @param name
	 *            Name
	 * @param typeName
	 *            For example: Dataset View
	 * @param typeCode
	 *            For example: DATASETVIEW
	 * @param metaData
	 *            A description
	 * @param listColumnData
	 *            List of Column Data
	 * @param trId
	 *            TR id
	 */
	public TableData(String id, String name, String typeName, String typeCode, String metaData,
			ArrayList<ColumnData> listColumnData, TRId trId) {
		this.id = id;
		this.name = name;
		this.typeName = typeName;
		this.typeCode = typeCode;
		this.metaData = metaData;
		this.listColumnData = listColumnData;
		this.trId = trId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public ArrayList<ColumnData> getListColumnData() {
		return listColumnData;
	}

	public void setListColumnData(ArrayList<ColumnData> listColumnData) {
		this.listColumnData = listColumnData;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "TableData [id=" + id + ", name=" + name + ", typeName=" + typeName + ", typeCode=" + typeCode
				+ ", metaData=" + metaData + ", listColumnData=" + listColumnData + ", trId=" + trId + "]";
	}

}
