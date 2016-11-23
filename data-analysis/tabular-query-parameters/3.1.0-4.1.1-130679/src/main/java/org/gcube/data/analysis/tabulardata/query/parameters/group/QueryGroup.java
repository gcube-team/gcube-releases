package org.gcube.data.analysis.tabulardata.query.parameters.group;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 877968964959436577L;
	
	private List<ColumnLocalId> columnLocalIds;
	
	@SuppressWarnings("unused")
	private QueryGroup(){}
	
	public QueryGroup(List<ColumnLocalId> groupColumnIds) {
		this.columnLocalIds = groupColumnIds;
	}

	/**
	 * @return the columns
	 */
	public List<ColumnLocalId> getColumns() {
		return Collections.unmodifiableList(columnLocalIds);
	}
	
	
}
