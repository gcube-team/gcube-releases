package org.gcube.data.analysis.tabulardata.operation.validation;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.harmonization.HarmonizationRule;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class ValidateDimensionTest extends OperationTester<ValidateDimensionColumnFactory>{
	
	@Inject
	CodelistHelper codelistHelper;

	@Inject
	GenericHelper genericHelper;
	
	@Inject
	ValidateDimensionColumnFactory factory;
	
	Table targetTable;
	Table codelistTable;
	
	
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Before
	public void createTestTable() {
		codelistTable = codelistHelper.createSpeciesCodelist();
		targetTable = genericHelper.createSpeciesGenericTable();
	}
	
	@Override
	protected ColumnLocalId getTargetColumnId() {
		return targetTable.getColumnsExceptTypes(IdColumnType.class).get(0).getLocalId();
	}
	
	@Override
	protected TableId getTargetTableId() {
		return targetTable.getId();
	}
	
	@Override
	protected Map<String, Object> getParameterInstances() {
		HashMap<String,Object> params=new HashMap<>();
		Column col=codelistTable.getColumnsByType(CodeColumnType.class).get(0);
		
		params.put(ValidateDimensionColumnFactory.TARGET_COLUMN_PARAMETER.getIdentifier(), codelistTable.getColumnReference(col));
		return params;
	}
	
	
}
