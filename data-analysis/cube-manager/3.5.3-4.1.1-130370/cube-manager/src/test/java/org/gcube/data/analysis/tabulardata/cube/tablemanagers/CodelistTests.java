package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.AnnotationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.AttributeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeDescriptionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeNameColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.MeasureColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ImportMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class CodelistTests {
	
	@Inject
	TableManager tm;

	@Inject
	@Named("Codelist")
	Instance<TableCreator> codelistCreatorProvider;
	
	@Inject
	TableMetaCreatorProvider tableMetaCreatorProvider; 
	
	@Inject @Named("Dataset")
	Instance<TableCreator> datasetCreatorProvider;
	
	@Inject @Named("Hierarchical")
	Instance<TableCreator> hInstance;

	@BeforeClass
	public static void setUp() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Test
	public void newTableWithCustomMeta() throws Exception {
		System.err.println("newTableWithCustomMeta");

		// Create new Table with some columns
		TableCreator tc = codelistCreatorProvider.get();

		tc.addColumn(new CodeColumnFactory().createDefault());

		tc.addColumn(new CodeNameColumnFactory().create("en"));
		tc.addColumn(new CodeNameColumnFactory().create("it"));
		tc.addColumn(new CodeNameColumnFactory().create("fr"));

		tc.addColumn(new CodeDescriptionColumnFactory().create("it"));
		tc.addColumn(new CodeDescriptionColumnFactory().create("or"));

		tc.addColumn(new AnnotationColumnFactory().create(new ImmutableLocalizedText("my annotation")));
		
		Table table = tc.create();
		
		System.err.println("Created Table:\n" + table.toString());
		
		Assert.assertNotNull(table.getId());
		Assert.assertTrue(table.getId().getValue()>0);
		Assert.assertFalse(table.getName().isEmpty());
		Assert.assertTrue(table.getColumns().size()>0);
		Assert.assertTrue(table.getColumnsByType(new IdColumnType()).size()==1);
		Assert.assertTrue(table.getColumnsByType(new CodeColumnType()).size()==1);
		Assert.assertTrue(table.getColumnsByType(new CodeNameColumnType()).size()==3);
		Assert.assertTrue(table.getColumnsByType(new CodeDescriptionColumnType()).size()==2);
		Assert.assertTrue(table.getColumnsByType(new AnnotationColumnType()).size()==1);
		Assert.assertEquals(new CodelistTableType(), table.getTableType());
		
		// Add meta to columns and table
		TableMetaCreator tmc = tableMetaCreatorProvider.get(table.getId());
		
		tmc.setTableMetadata(new ImportMetadata("SDMX", 
				"http://localhost/FusionRegistry/codelist/ABBA/CODELIST/1.0",new Date()));
		
		Table newMetaTable = tmc.create();
				
		newMetaTable = tm.get(newMetaTable.getId());
		
		System.err.println("resultTable Table:\n" + newMetaTable.toString());
		
		Assert.assertNotEquals(table, newMetaTable);
		Assert.assertNotNull(newMetaTable.getMetadata(ImportMetadata.class));
		Assert.assertTrue(newMetaTable.getMetadata(ImportMetadata.class).getSourceType().equals("SDMX"));
	}

	@Test
	public void clonedTableWithStructuralChanges() throws Exception {
		System.err.println("clonedTableWithStructuralChanges");
		
		TableCreator tc = codelistCreatorProvider.get();
		
		// Create new table from existing
		Table startTable = createDataset();
		
		tc.like(startTable, true);
	
		// Modify the table type and remove annotation and attribute columns
		List<Column> columnsToRemove = startTable.getColumnsByType(new AttributeColumnType(), new AnnotationColumnType());
		tc.like(startTable, true, columnsToRemove);
		
		//Add column
		Column annotationColumn = new AnnotationColumnFactory().create(new ImmutableLocalizedText("My new annotation"));
		tc.addColumns(annotationColumn);
		
		//Create
		Table createdTable = tc.create();
		
		System.err.println("Created Table:\n"+createdTable);
		
		
		//Check
		Assert.assertNotNull(createdTable);
		Assert.assertEquals(3,createdTable.getColumns().size());
	}
	
	@Test
	public void cloneUnmodified() throws Exception{
		newTableWithCustomMeta();
		Table originalTable = tm.getAll().iterator().next();
		Table secondTable = tableMetaCreatorProvider.get(originalTable.getId()).create();
		Assert.assertNotEquals(originalTable, secondTable);
		System.err.println("First:\n"+originalTable);
		System.err.println("Second:\n"+secondTable);
	}
	
	private Table createDataset() throws TableCreationException{
		TableCreator tc = datasetCreatorProvider.get();
		
		tc.addColumn(new MeasureColumnFactory().create(new ImmutableLocalizedText("Quantity"), new IntegerType()));
		tc.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("Year"), new IntegerType()));
		tc.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("Name"), new IntegerType()));
		tc.addColumn(new AnnotationColumnFactory().create(new ImmutableLocalizedText("My Annotation","eng")));
		
		return tc.create();
	}

}
