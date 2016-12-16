package org.gcube.data.analysis.tabulardata.commons.webservice.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement

@XmlAccessorType(XmlAccessType.FIELD)
public class WorkerResult implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	List<TableId> collateralTables = new ArrayList<>(); 
	
	@XmlElement
	public Table resultTable;

	public WorkerResult(Table resultTable) {
		this.resultTable = resultTable;
	}
	
	public WorkerResult(Table resultTable, List<TableId> collateralTables) {
		this(resultTable);
		this.collateralTables = collateralTables;
	}


	protected WorkerResult() {
		super();
	}



	/**
	 * @return the resultTable
	 */
	public Table getResultTable() {
		return resultTable;
	}


	public List<TableId> getCollateralTables() {
		return collateralTables;
	}
	
	

}
