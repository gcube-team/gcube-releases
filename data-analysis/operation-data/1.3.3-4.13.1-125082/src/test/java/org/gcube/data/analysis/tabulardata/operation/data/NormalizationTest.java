package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.NormalizationFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CopyHandler;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class NormalizationTest extends OperationTester<NormalizationFactory> {

	@Inject
	private NormalizationFactory factory;
	
	@Inject
	private CopyHandler copyHandler;
	
	@Inject
	public CubeManager cm;
	
	private Table table;
	
	@Before
	public void initTable(){
		TableCreator tc=cm.createTable(new GenericTableType());
		tc.addColumn(BaseColumnFactory.getFactory(new AttributeColumnType()).create(new ImmutableLocalizedText("Country"), new TextType(10)));
		tc.addColumn(BaseColumnFactory.getFactory(new MeasureColumnType()).create(new ImmutableLocalizedText("1998"), new IntegerType()));
		tc.addColumn(BaseColumnFactory.getFactory(new MeasureColumnType()).create(new ImmutableLocalizedText("1999"), new IntegerType()));
		tc.addColumn(BaseColumnFactory.getFactory(new MeasureColumnType()).create(new ImmutableLocalizedText("2000"), new IntegerType()));
		table=tc.create();
		copyHandler.copy("denormalizedDataset.csv", table);
	}
	
	@Override
	protected NormalizationFactory getFactory() {
		return factory;
	}

	@Override
	protected Map getParameterInstances() {
		ArrayList<ColumnReference> refs=new ArrayList<>();
		for(Column col:table.getColumnsByType(MeasureColumnType.class))
			refs.add(table.getColumnReference(col));
		return Collections.singletonMap(NormalizationFactory.TO_NORMALIZE_COLUMNS.getIdentifier(),(Object)refs );
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return table.getId();
	}

}
