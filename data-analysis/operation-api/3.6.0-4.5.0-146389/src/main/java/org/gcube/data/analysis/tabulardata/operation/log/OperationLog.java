package org.gcube.data.analysis.tabulardata.operation.log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OperationLog {

	private Map<Long, RowOperation> rowOperations = new HashMap<Long, RowOperation>();
	
	private Map<String, ColumnOperation> columnOperations = new HashMap<String, ColumnOperation>();
	
	public enum Modification { ALL, PART };
	
	private Modification tableModification = Modification.ALL;
	
	void row(long id, RowOperation op){
		rowOperations.put(id, op);
		tableModification = Modification.PART;
	}

	void column(String localId, ColumnOperation op ){
		columnOperations.put(localId, op);
		tableModification = Modification.PART;
	}
		
	public Map<Long, RowOperation> rows(){
		return Collections.unmodifiableMap(rowOperations);
	}
	
	public Map<String, ColumnOperation> columns(){
		return Collections.unmodifiableMap(columnOperations);
	}

	public Modification getTableModification() {
		return tableModification;
	}

}
