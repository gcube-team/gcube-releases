package org.gcube.data.analysis.tabulardata.utils;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalInvocation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Logger log = LoggerFactory.getLogger(InternalInvocation.class); 
		
	private Map<String, Object> parameters;

	private transient WorkerFactory<?> workerFactory;

	private final String invocationId = UUID.randomUUID().toString();
	
	private long operationId;
	private ColumnLocalId columnId;		
	private Table diffTable;

	private Map<String, ColumnLocalId> mappingColumnsPlaceholder = null;

	@SuppressWarnings("unused")
	private InternalInvocation(){}

	public InternalInvocation(Map<String, Object> parameters,
			WorkerFactory<?> workerFactory) {
		super();
		this.parameters = parameters;
		this.workerFactory = workerFactory;
		if(workerFactory!=null){
			this.operationId = workerFactory.getOperationDescriptor().getOperationId().getValue();
			log.debug("saved operationID is "+this.operationId);
		}
		log.debug("is workerFacotry null ?  "+(workerFactory==null));
	}

	public InternalInvocation(Map<String, Object> parameters,
			WorkerFactory<?> workerFactory, Table diffTable) {
		this(parameters, workerFactory);
		this.diffTable = diffTable;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the columnId
	 */
	public ColumnLocalId getColumnId() {
		return columnId;
	}
	
	public String getInvocationId() {
		return invocationId;
	}

	public WorkerFactory<?> getWorkerFactory() {
		if (workerFactory==null){
			log.debug("searching for workerFactory with ID "+this.operationId);
			log.debug("the factory map contains the ID? "+CDIProducer.getFactoryMap().containsKey(new OperationId(this.operationId)));
			this.workerFactory = CDIProducer.getFactoryMap().get(new OperationId(this.operationId));
		}
		return workerFactory;
	}
	
	public Map<String, ColumnLocalId> getMappingColumnsPlaceholder() {
		return mappingColumnsPlaceholder;
	}
	
	public void setMappingColumnsPlaceholder(
			Map<String, ColumnLocalId> mappingColumnsPlaceholder) {
		this.mappingColumnsPlaceholder = mappingColumnsPlaceholder;
	}

	/**
	 * @param columnId the columnId to set
	 */
	public void setColumnId(ColumnLocalId columnId) {
		this.columnId = columnId;
	}

	public Table getDiffTable() {
		return diffTable;
	}

	public boolean isNop(){
		return false;
	}
			
	
	@Override
	public String toString() {
		return "InternalInvocation [parameters=" + parameters + ", operationId="
				+ operationId + ", columnId=" + columnId + ", diffTableId="
				+ (diffTable!=null?diffTable.getId():"not provided") + "]";
	}

	
	public static String getDinamicallyCreatedColumnId(String invocationId, int index){
		return invocationId+":"+index;
	}


	public static class NOPInvocation extends InternalInvocation{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1731547452104615926L;

		public NOPInvocation(Table table){
			super(null, null, table);
		}

		public boolean isNop(){
			return true;
		}

	}
}