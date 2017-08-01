package org.gcube.data.analysis.tabulardata.expression.evaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

import com.google.common.collect.Lists;

@Singleton
public class CubeManagerMock implements CubeManager{

	public TableCreator createTable(TableType type) {
		return null;
	}

	public TableMetaCreator modifyTableMeta(TableId tableId) throws NoSuchTableException {
		return null;
	}

	public Collection<Table> getTables() {
		return null;
	}

	public Collection<Table> getTables(TableType tableType) {
		return null;
	}

	public Table getTable(TableId id) throws NoSuchTableException {
		Table result = new Table(new CodelistTableType());
		Column column =  new Column(new ColumnLocalId("test"), new TextType(), new CodeNameColumnType());
		result.setColumns(Lists.newArrayList(column));
		List<LocalizedText> columnNames = new ArrayList<LocalizedText>();
		columnNames.add(new ImmutableLocalizedText("species"));
		NamesMetadata namesMetadata = new NamesMetadata(columnNames);
		column.setMetadata(namesMetadata);
		return result;
	}

	public void removeTable(TableId id) throws NoSuchTableException {

	}

	
	public Table getTimeTable(PeriodType periodType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table addValidations(TableId arg0, Column... arg1)
			throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table removeValidations(TableId id) throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table addValidations(TableId id,
			ValidationsMetadata tableValidationMetadata,
			Column... validationColumns) throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table removeColumn(TableId arg0, ColumnLocalId arg1)
			throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table exchangeColumnPosition(TableId tableId,
			ColumnLocalId columnId, int newPosition)
			throws NoSuchTableException {
		// TODO Auto-generated method stub
		return null;
	}
}
