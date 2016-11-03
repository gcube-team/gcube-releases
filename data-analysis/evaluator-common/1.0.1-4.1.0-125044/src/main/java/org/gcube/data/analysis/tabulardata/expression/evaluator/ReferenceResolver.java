package org.gcube.data.analysis.tabulardata.expression.evaluator;



import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public interface ReferenceResolver {

	public Column getColumn(ColumnReference columnRef);

	public Table getTable(ColumnReference columnRef);

}