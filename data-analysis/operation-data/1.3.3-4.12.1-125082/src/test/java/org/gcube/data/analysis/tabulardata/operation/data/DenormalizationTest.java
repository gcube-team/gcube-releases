package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.DenormalizationFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.CopyHandler;
import org.gcube.data.analysis.tabulardata.operation.test.util.DatasetHelper;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class DenormalizationTest extends OperationTester<DenormalizationFactory> {

	@Inject
	private DenormalizationFactory factory;
	
	@Inject
	private CopyHandler copyHandler;
	
	@Inject
	private CodelistHelper clHelper;
	
	@Inject 
	private DatasetHelper dsHelper;
	
	@Inject
	public CubeManager cm;
	
	private Table table;
	
	private Column measure;
	private Column attr;
	
	
	@Before
	public void initTable(){
		TableCreator tc=cm.createTable(new GenericTableType());
		Column country=BaseColumnFactory.getFactory(new AttributeColumnType()).create(new ImmutableLocalizedText("Country"), new TextType(10));
		measure=BaseColumnFactory.getFactory(new MeasureColumnType()).create(new ImmutableLocalizedText("Quantity"), new NumericType());
		attr=BaseColumnFactory.getFactory(new AttributeColumnType()).create(new ImmutableLocalizedText("Year"), new TextType(10));
		
		table=tc.addColumn(country).addColumn(attr).addColumn(measure).create();
		copyHandler.copy("normalizedDataset.csv", table);
		
		
//		Table cl=clHelper.createSpeciesCodelist();
//		table=dsHelper.createSampleDataset(cl);
//		measure=table.getColumnsByType(MeasureColumnType.class).get(0);
//		attr=table.getColumnsByType(DimensionColumnType.class).get(0);
//		
		
		
	}
	
	
	@Override
	protected DenormalizationFactory getFactory() {
		return factory;
	}

	@Override
	protected Map getParameterInstances() {		
		HashMap<String,Object> toReturn=new HashMap<>();
		toReturn.put(DenormalizationFactory.ATTRIBUTE_COLUMN.getIdentifier(), table.getColumnReference(attr));
		toReturn.put(DenormalizationFactory.VALUE_COLUMN.getIdentifier(), table.getColumnReference(measure));
		return toReturn;
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
