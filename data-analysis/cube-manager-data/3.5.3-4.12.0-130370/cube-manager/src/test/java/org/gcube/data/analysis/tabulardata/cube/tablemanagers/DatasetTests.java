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
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeNameColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.DimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.MeasureColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.geometry.GeometryShape;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ImportMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class DatasetTests {

	@Inject
	@Named("Codelist")
	Instance<TableCreator> codelistCreatorProvider;

	@Inject
	@Named("Dataset")
	Instance<TableCreator> datasetCreatorProvider;

	@Inject
	TableMetaCreatorProvider tableMetaCreatorProvider;

	@BeforeClass
	public static void setUp() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	private Table createCodelist() throws TableCreationException {
		TableCreator cc = codelistCreatorProvider.get();

		// Create preliminary codelist
		Table codelist = null;

		cc.addColumn(new CodeColumnFactory().createDefault());
		cc.addColumn(new CodeNameColumnFactory().create("eng"));
		cc.addColumn(new CodeNameColumnFactory().create("ita"));
		cc.addColumn(new AnnotationColumnFactory().create(new ImmutableLocalizedText("My Annotation on species", "eng")));
		codelist = cc.create();
//		System.err.println("Created codelist:\n"+codelist);
		Assert.assertNotNull(codelist);
		return codelist;
	}

	@Test
	public void newTableWithCustomMeta() throws Exception {
		System.err.println("newTableWithCustomMeta");

		// Create preliminary codelist
		Table codelist = createCodelist();

		// Create dataset
		TableCreator tc = datasetCreatorProvider.get();
		Table dataset = null;

		tc.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("Catcher", "eng"), new TextType(30)));

		tc.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("Area", "eng"), new GeometryType(
				GeometryShape.MULTIPOLYGON, 2)));
		tc.addColumn(new MeasureColumnFactory().create(new ImmutableLocalizedText("Quantity", "eng"), new IntegerType()));
		tc.addColumn(new DimensionColumnFactory().create(new ImmutableLocalizedText("Species"), new ImmutableColumnRelationship(codelist)));
		tc.addColumn(new AnnotationColumnFactory().create(new ImmutableLocalizedText("My Annotation on data")));

		Table createdDataset = tc.create();
		System.err.println("Created dataset:\n" + dataset);

		// Checks
		Assert.assertNotNull(createdDataset);
		Assert.assertTrue(createdDataset.getId().getValue() > 0);
		Assert.assertFalse(createdDataset.getName().isEmpty());
		Assert.assertTrue(createdDataset.getColumns().size() > 0);
		Assert.assertTrue(createdDataset.getColumnsByType(new IdColumnType()).size() == 1);
		Assert.assertTrue(createdDataset.getColumnsByType(new AttributeColumnType()).size() == 2);
		Assert.assertTrue(createdDataset.getColumnsByType(new DimensionColumnType()).size() == 1);
		Assert.assertTrue(createdDataset.getColumnsByType(new MeasureColumnType()).size() == 1);
		Assert.assertTrue(createdDataset.getColumnsByType(AnnotationColumnType.class).size() == 1);
		Assert.assertEquals(new DatasetTableType(), createdDataset.getTableType());

		// Add custom meta
		TableMetaCreator tmc = tableMetaCreatorProvider.get(createdDataset.getId());

		tmc.setTableMetadata(new ImportMetadata("SDMX", 
				"http://localhost/FusionRegistry/codelist/ABBA/CODELIST/1.0",new Date()));

		Table newMetaTable = tmc.create();

		// Checks
		Assert.assertNotEquals(createdDataset, newMetaTable);
		Assert.assertNotNull(newMetaTable.getMetadata(ImportMetadata.class));
		Assert.assertTrue(newMetaTable.getMetadata(ImportMetadata.class).getSourceType().equals("SDMX"));

	}

	@Test
	public void clonedTableWithStructuralChanges() throws Exception {
		System.err.println("clonedTableWithStructuralChanges");

		TableCreator tc = datasetCreatorProvider.get();

		// Create new table from existing
		Table startTable = createCodelist();

		tc.like(startTable, true);

		// Modify the table type and remove annotation and attribute columns
		List<Column> columnsToRemove = startTable.getColumnsByType(CodeNameColumnType.class, AnnotationColumnType.class);
		tc.like(startTable, true, columnsToRemove);
		
		// Add column
		Column annotationColumn = new AnnotationColumnFactory().create(new ImmutableLocalizedText("My new annotation"));
		tc.addColumns(annotationColumn);

		// Create
		Table createdTable = tc.create();

		System.err.println("Created Table:\n" + createdTable);

		// Check
		Assert.assertNotNull(createdTable);
		Assert.assertEquals(3, createdTable.getColumns().size());
		Assert.assertEquals(new DatasetTableType(), createdTable.getTableType());
	}

}
