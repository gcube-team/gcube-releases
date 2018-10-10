package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.ColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.DenormalizationFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CopyHandler;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class DonrmalizationTestCase1 extends OperationTester<DenormalizationFactory> {

	@Inject
	private DenormalizationFactory factory;
	
	@Inject
	private CopyHandler copyHandler;
	
	@Inject
	public CubeManager cm;
	
	private Table table;
	
	private Column measure;
	private Column attr;
	
	
	
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

	@Before
	public void createDatasetWithSpecies(){
		ColumnFactory factory=BaseColumnFactory.getFactory(new AttributeColumnType());
		TableCreator tc = cm.createTable(new GenericTableType());

		// Create table structure
		try {
			tc.addColumn(factory.create(new ImmutableLocalizedText("English name"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Spanish name"),new TextType()));
			attr=factory.create(new ImmutableLocalizedText("Species code"),new TextType());
			tc.addColumn(attr);
			measure=factory.create(new ImmutableLocalizedText("A measure"),new NumericType());
			tc.addColumn(measure);
			table = tc.create();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// Fill with data
		copyHandler.copy("normal.csv", table);
	}
}
