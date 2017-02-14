package org.gcube.data.analysis.tabulardata.query.parameters.select;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class QuerySelect implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2823540830337364876L;
	
	private List<QueryColumn> columns;
	
	@SuppressWarnings("unused")
	private QuerySelect(){}
	
	public QuerySelect(List<QueryColumn> columns){
		this.columns = columns;
	}

	/**
	 * @return the columns
	 */
	public List<QueryColumn> getColumns() {
		return Collections.unmodifiableList(columns);
	}
		
}
