package org.gcube.data.analysis.tabulardata.operation.empty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.importer.empty.EmptyTableCreatorFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class EmptyTableCreatorTest extends OperationTester<EmptyTableCreatorFactory> {

	Table codelistTable;

	Table datasetTable;

	@Inject
	GenericHelper genericHelper;

	@Inject
	CodelistHelper codelistHelper;

	@Inject
	EmptyTableCreatorFactory factory;


	@Before
	public void setupTestTables() throws InvalidInvocationException {
		codelistTable = codelistHelper.createSpeciesCodelist();
	}

	@Override
	protected EmptyTableCreatorFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		List<Map<String, Object>> columnMapping = new ArrayList<>();

		Map<String, Object> first = new HashMap<>();
		first.put(EmptyTableCreatorFactory.DATA_TYPE.getIdentifier(), new IntegerType());
		first.put(EmptyTableCreatorFactory.COLUMN_TYPE.getIdentifier(), new AttributeColumnType());
		first.put(EmptyTableCreatorFactory.LABEL.getIdentifier(), new ImmutableLocalizedText("tempLabel"));
		columnMapping.add(first);

		Map<String, Object> second = new HashMap<>();
		second.put(EmptyTableCreatorFactory.DATA_TYPE.getIdentifier(), new IntegerType());
		second.put(EmptyTableCreatorFactory.COLUMN_TYPE.getIdentifier(), new DimensionColumnType());
		second.put(EmptyTableCreatorFactory.RELATIONSHIP.getIdentifier(), new ColumnReference(codelistTable.getId(), codelistTable.getColumnsByType(CodeColumnType.class).get(0).getLocalId()));
		second.put(EmptyTableCreatorFactory.LABEL.getIdentifier(), new ImmutableLocalizedText("dimCol"));
		columnMapping.add(second);	

		Map<String, Object> third = new HashMap<>();
		third.put(EmptyTableCreatorFactory.DATA_TYPE.getIdentifier(), new IntegerType());
		third.put(EmptyTableCreatorFactory.COLUMN_TYPE.getIdentifier(), new TimeDimensionColumnType());
		third.put(EmptyTableCreatorFactory.PERIOD_TYPE.getIdentifier(), PeriodType.YEAR.name());
		third.put(EmptyTableCreatorFactory.LABEL.getIdentifier(), new ImmutableLocalizedText("dimCol"));
		columnMapping.add(third);
		
		Map<String, Object> parameters = Collections.singletonMap(EmptyTableCreatorFactory.COMPOSITE.getIdentifier(), (Object)columnMapping );

		return parameters;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return null;
	}

}