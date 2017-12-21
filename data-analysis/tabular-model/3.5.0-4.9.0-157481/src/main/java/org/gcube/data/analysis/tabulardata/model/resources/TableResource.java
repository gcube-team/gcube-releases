package org.gcube.data.analysis.tabulardata.model.resources;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TableResource extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6092223539069205128L;

	private TableId tableId;
		
	public TableResource(){}
	
	public TableResource(TableId tableId) {
		super();
		this.tableId = tableId;
	}

	@Override
	public String getStringValue() {
		return tableId.toString();
	}
	
	public TableId getTableId() {
		return tableId;
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return this.getClass();
	}
	
}
