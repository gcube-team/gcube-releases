
package org.gcube.data.analysis.tabulardata.commons.webservice.types;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HistoryData {

	@XmlElement
	long id;
	
	@XmlElement
	private String operationDescription;
	
	@XmlElement
	private TableId resultTable;

	@XmlElement	
	private Calendar date;
	
	protected HistoryData(){}
	
	public HistoryData(long id, String operationDescription, TableId resultTable, Calendar date) {
		super();
		this.id = id;
		this.operationDescription = operationDescription;
		this.resultTable = resultTable;
		this.date = date;
	}

	/**
	 * @return the invocation
	 */
	public String getOperationDescription() {
		return operationDescription;
	}
		
	/**
	 * @return the resultTable
	 */
	public TableId getResultTableId() {
		return resultTable;
	}
	
	
	public long getId() {
		return id;
	}

	/**
	 * @return the date
	 */
	public Calendar getDate() {
		return date;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HistoryData [id= "+id+", description=" + operationDescription + ", resultTable="
				+ resultTable + ", date=" + date.getTime() + "]";
	}


	

	
	
}
