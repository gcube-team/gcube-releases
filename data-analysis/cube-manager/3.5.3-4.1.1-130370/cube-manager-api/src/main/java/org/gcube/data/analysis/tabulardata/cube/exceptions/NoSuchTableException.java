package org.gcube.data.analysis.tabulardata.cube.exceptions;

import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;

public class NoSuchTableException extends CubeManagerException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3218208012859148967L;

	TableId tableId;
	
	TableType type;
	
	public NoSuchTableException(TableId id){
		super(String.format("Unable to retrieve table with id: '%1$s'.",id));
		this.tableId = id;
	}

	public NoSuchTableException(TableId id, TableType type) {
		super("Unable to retrieve table of type " + type + " and id=" + id);
		this.tableId = id;
		this.type = type;
	}

	public TableId getId() {
		return tableId;
	}

	public TableType getType() {
		return type;
	}

}
