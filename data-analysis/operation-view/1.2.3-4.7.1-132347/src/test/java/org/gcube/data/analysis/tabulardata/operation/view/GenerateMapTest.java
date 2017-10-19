package org.gcube.data.analysis.tabulardata.operation.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.geometry.GeometryShape;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CopyHandler;
import org.gcube.data.analysis.tabulardata.operation.view.maps.GenerateMapFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class GenerateMapTest extends OperationTester<GenerateMapFactory> {

	private Table testTable;
	private Column feature;
	
	
	@Inject
	private GenerateMapFactory factory;
	
	@Inject
	private CubeManager cm;
	
	@Inject
	private CopyHandler copyHandler;
	
	
	@Before
	public void prepareTable(){
		TableCreator tc=cm.createTable(new GenericTableType());
		Column geom=BaseColumnFactory.getFactory(new AttributeColumnType()).create(new GeometryType(0,GeometryShape.POLYGON,2));
		feature=BaseColumnFactory.getFactory(new AttributeColumnType()).create(new TextType(),Collections.singletonList((LocalizedText)new ImmutableLocalizedText("My Feature")));
		
		testTable=tc.addColumn(geom).addColumn(feature).create();
		copyHandler.copy("Geom.csv", testTable);
		
		
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
	}
	
	
	
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map getParameterInstances() {
		Map<String,Object> toReturn=new HashMap<>();
		toReturn.put(GenerateMapFactory.user.getIdentifier(), "fabio.sinibaldi");
		toReturn.put(GenerateMapFactory.toCreateFeatureTypes.getIdentifier(),testTable.getColumnReference(feature));
		toReturn.put(GenerateMapFactory.mapName.getIdentifier(), "MapTest");
		toReturn.put(GenerateMapFactory.metaAbstract.getIdentifier(), "This layer is something");
		toReturn.put(GenerateMapFactory.metaCredits.getIdentifier(), "Me & Myself");
		toReturn.put(GenerateMapFactory.metaPurpose.getIdentifier(), "Just for fun");		
		toReturn.put(GenerateMapFactory.useView.getIdentifier(), false);
		return toReturn;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}

}
