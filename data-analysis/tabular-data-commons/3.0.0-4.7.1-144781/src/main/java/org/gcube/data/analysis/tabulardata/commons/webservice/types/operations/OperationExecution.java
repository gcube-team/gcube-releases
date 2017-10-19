package org.gcube.data.analysis.tabulardata.commons.webservice.types.operations;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters.MapAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationExecution implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement	
	private String columnId;
	
	@XmlElement 
	private String id;
	
	private long operationId;
		
	@XmlJavaTypeAdapter(MapAdapter.class)
	private Map<String, Object> parameters;

	protected OperationExecution(){}
	
	public OperationExecution(String columnId,
			long operationId, Map<String, Object> parameters) {
		super();
		this.columnId = columnId;
		this.operationId = operationId;
		this.parameters = parameters;
	}

	public OperationExecution(long operationId,
			Map<String, Object> parameters) {
		super();
		this.operationId = operationId;
		this.parameters = parameters;
	}


	/**
	 * @return the columnId
	 */
	public String getColumnId() {
		return columnId;
	}

	/**
	 * @return the operationId
	 */
	public long getOperationId() {
		return operationId;
	}
	
	public Long getIdentifier() {
		return operationId;
	}

	
	
	/**
	 * @param columnId the columnId to set
	 */
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OperationExecution [columnId=" + columnId + ", operationId="
				+ operationId + ", parameters=" + parameters + "]";
	}
		
}
