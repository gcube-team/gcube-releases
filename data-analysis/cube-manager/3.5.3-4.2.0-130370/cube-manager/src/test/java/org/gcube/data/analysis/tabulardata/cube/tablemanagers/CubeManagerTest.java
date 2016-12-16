package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.factories.AnnotationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeNameColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.DimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ViewColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetViewTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class CubeManagerTest {

	@Inject
	CubeManager cm;
	
	@Test
	public void addValidationColumns(){
		Table table = cm.createTable(new GenericTableType()).create();
		
		ValidationsMetadata tvm = new ValidationsMetadata(Collections.singletonList(new Validation("testValid", true,0)));
		
		Column validationColumn = new Column(new ColumnLocalId("testId"), new BooleanType(), new ValidationColumnType());
				
		validationColumn.setMetadata(tvm);
		
		System.out.println(table.getId());
				
		table = cm.addValidations(table.getId(), tvm, validationColumn);
		
		System.out.println("--- first---------");
		System.out.println(table);
		System.out.println("------------");
						
		table = cm.getTable(table.getId());
		System.out.println(table.getId());
		System.out.println("-----reloaded table-------");
		System.out.println(table);
		System.out.println("------------");
			
		ValidationsMetadata tvm2 = new ValidationsMetadata(Collections.singletonList(new Validation("testValid2", true,0)));
			
		table = cm.modifyTableMeta(table.getId()).setColumnMetadata(new ColumnLocalId("testId"), tvm2).create();
		
		System.out.println("-----last-------");
		System.out.println(table);
		System.out.println("------------");
		
		table = cm.getTable(table.getId());
		System.out.println(table.getId());
		System.out.println("-----reloaded table-------");
		System.out.println(table);
		System.out.println("------------");
	}
	
	@Test
	public void createDatasetView(){
		TableCreator originCreator = cm.createTable(new CodelistTableType());

		
		originCreator.addColumn(new CodeColumnFactory().createDefault());
		originCreator.addColumn(new CodeNameColumnFactory().create("en"));
		originCreator.addColumn(new CodeNameColumnFactory().create("it"));
		originCreator.addColumn(new AnnotationColumnFactory().create(new ImmutableLocalizedText("My Annotation on species", "en")));
		
		Table codelist = originCreator.create();
		
		TableCreator datasetCreator = cm.createTable(new DatasetTableType());
		
		datasetCreator.addColumn(new DimensionColumnFactory().create(new ImmutableColumnRelationship(codelist)));
		
		Table dataset = datasetCreator.create();
		
		
		TableCreator tableCreator = cm.createTable(new DatasetViewTableType());
		tableCreator.like(dataset, false);
		Collection<Column> dimensionColumns = dataset.getColumnsByType(new DimensionColumnType(),
				new TimeDimensionColumnType());
		for (Column dimensionColumn : dimensionColumns) {
			Column afterColumn = dimensionColumn;
			Table dimensionTable = cm.getTable(dimensionColumn.getRelationship().getTargetTableId());
			for (Column column : dimensionTable.getColumnsByType(CodeColumnType.class, CodeNameColumnType.class,
					CodeDescriptionColumnType.class)) {
				column.setMetadata(new ViewColumnMetadata(dimensionTable.getId(), column.getLocalId(),
						dimensionColumn.getLocalId()));
				tableCreator.addColumnAfter(column, afterColumn);
				afterColumn = column;
			}
		}
		Table viewTable = tableCreator.create();
		
		cm.removeTable(viewTable.getId());
		cm.removeTable(codelist.getId());
		cm.removeTable(dataset.getId());
	}
	
	@Test
	public void excangeColumns(){
		TableCreator originCreator = cm.createTable(new CodelistTableType());

		Column col = new CodeNameColumnFactory().create("en");
		originCreator.addColumn(new CodeColumnFactory().createDefault());
		originCreator.addColumn(col);
		originCreator.addColumn(new CodeNameColumnFactory().create("it"));
		originCreator.addColumn(new AnnotationColumnFactory().create(new ImmutableLocalizedText("My Annotation on species", "en")));
		
		Table t =originCreator.create();
		System.out.println(t);
		
		Table newTable = cm.exchangeColumnPosition(t.getId(), col.getLocalId(), 0);
		System.out.println(newTable);
		
	}
	
	
	
}
