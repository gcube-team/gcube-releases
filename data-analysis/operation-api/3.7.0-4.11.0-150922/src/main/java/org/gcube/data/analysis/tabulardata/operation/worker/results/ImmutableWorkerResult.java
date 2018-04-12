package org.gcube.data.analysis.tabulardata.operation.worker.results;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.table.Table;

public class ImmutableWorkerResult implements WorkerResult {

	private Table resultTable;

	private Table diffTable;
	
	private List<Table> collateralTables = new ArrayList<Table>();
	
	@SuppressWarnings("unused")
	private ImmutableWorkerResult() {}

	public ImmutableWorkerResult(Table table) {
		this.resultTable = table;
	}

	public ImmutableWorkerResult(Table table, List<Table> collateralTables) {
		this.resultTable = table;
		this.collateralTables = collateralTables;
	}

	public ImmutableWorkerResult(Table resultTable, Table difftable, List<Table> collateralTables) {
		this.resultTable = resultTable;
		this.collateralTables = collateralTables;
		this.diffTable = difftable;
	}
	
	public ImmutableWorkerResult(Table resultTable, Table difftable) {
		this.resultTable = resultTable;
		this.diffTable = difftable;
	}
	
	public Table getResultTable() {
		return resultTable;
	}

	public Table getDiffTable() {
		return diffTable;
	}
	
	public List<Table> getCollateralTables() {
		return collateralTables;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImmutableWorkerResult [table=");
		builder.append(resultTable);
		builder.append(", diffTable=");
		builder.append(diffTable);
		builder.append(", collateralTables=");
		builder.append(collateralTables);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((collateralTables == null) ? 0 : collateralTables.hashCode());
		result = prime * result
				+ ((diffTable == null) ? 0 : diffTable.hashCode());
		result = prime * result
				+ ((resultTable == null) ? 0 : resultTable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImmutableWorkerResult other = (ImmutableWorkerResult) obj;
		if (collateralTables == null) {
			if (other.collateralTables != null)
				return false;
		} else if (!collateralTables.equals(other.collateralTables))
			return false;
		if (diffTable == null) {
			if (other.diffTable != null)
				return false;
		} else if (!diffTable.equals(other.diffTable))
			return false;
		if (resultTable == null) {
			if (other.resultTable != null)
				return false;
		} else if (!resultTable.equals(other.resultTable))
			return false;
		return true;
	}

}
