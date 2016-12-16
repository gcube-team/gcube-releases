package org.gcube.data.analysis.tabulardata.query.parameters.select;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryColumn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5060391194097859955L;

	public enum Function {
		NONE, 
		MAX,
		MIN,
		COUNT,
		AVERAGE
	}
	
	private ColumnLocalId columnLocalId;
	
	private Function function;
	
	@SuppressWarnings("unused")
	private QueryColumn(){}
	
	public QueryColumn(ColumnLocalId columnLocalId){
		this.columnLocalId = columnLocalId;
		this.function = Function.NONE;
	}
	
	public QueryColumn(ColumnLocalId columnLocalId, Function function){
		this.columnLocalId = columnLocalId;
		this.function = function;
	}
	
	/**
	 * @return the column
	 */
	public ColumnLocalId getColumnLocalId() {
		return columnLocalId;
	}
	
	public Function getFunction(){
		return function;
	}
}
