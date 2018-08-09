package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.ColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.csquare.DownScaleCsquareFactory;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.csquare.Resolution;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CopyHandler;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class DownScaleCsquareTest extends OperationTester<DownScaleCsquareFactory>{

	@Inject 
	private DownScaleCsquareFactory factory;
	
	@Inject
	private CopyHandler copyHandler;
	
	@Inject 
	private CubeManager cm;
	
	private Table table;
	
	@Before
	public void initTable(){
		ColumnFactory factory=BaseColumnFactory.getFactory(new AttributeColumnType());
		TableCreator tc = cm.createTable(new GenericTableType());

		// Create table structure
		try {
			tc.addColumn(factory.create(new ImmutableLocalizedText("csquares"),new TextType()));			
			table = tc.create();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// Fill with data
		copyHandler.copy("csquares.csv", table);
	}
	
	@Override
	protected WorkerFactory<?> getFactory() {
		return factory;
	}
	
	@Override
	protected ColumnLocalId getTargetColumnId() {
		return table.getColumnsExceptTypes(IdColumnType.class).get(0).getLocalId();
	}
	
	@Override
	protected Map<String, Object> getParameterInstances() {
		return Collections.singletonMap(DownScaleCsquareFactory.RESOLUTION_PARAM.getIdentifier(), (Object)Resolution.TEN.getLabel());
	}
	
	@Override
	protected TableId getTargetTableId() {
		return table.getId();
	}
	
	
	@Test
	public void testRegexp(){
		String code="1000:100:1";
		Pattern.matches(DownScaleCsquareFactory.CSQUARE_REGEXP, code);
	}
}
