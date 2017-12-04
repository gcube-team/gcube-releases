package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.AnnotationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.AttributeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.MeasureColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class GenericTableTests {

	@Inject
	@Named("GenericTable")
	Instance<TableCreator> genericTableCreatorProvider;

	@Inject
	TableMetaCreatorProvider tableMetaCreatorProvider;

	@BeforeClass
	public static void setUp() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	private TableCreator getGenericTableCreator() {
		return genericTableCreatorProvider.get();
	}

	private Table createGenericTable() throws TableCreationException {
		TableCreator tc = getGenericTableCreator();

		System.err.println("Generic Table creation");

		tc.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("Attribute1"), new TextType()));
		tc.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("Attribute2"), new TextType()));
		tc.addColumn(new MeasureColumnFactory().create(new ImmutableLocalizedText("MyMeasureTest"), new IntegerType()));
		tc.addColumn(new AnnotationColumnFactory().create(new ImmutableLocalizedText("My first annot")));
		return tc.create();
	}

	@Test
	public void newTableWithCustomMeta() throws Exception {
		Table createdTable = createGenericTable();

		// Checks
		System.err.println("Created Table:\n" + createdTable);
		Assert.assertNotNull(createdTable);
		Assert.assertNotNull(createdTable.getId());
		Assert.assertTrue(createdTable.getId().getValue() > 0);
		Assert.assertFalse(createdTable.getName().isEmpty());
		Assert.assertTrue(createdTable.getColumns().size() > 0);
		Assert.assertEquals(1, createdTable.getColumnsByType(new IdColumnType()).size());
		Assert.assertEquals(2, createdTable.getColumnsByType(new AttributeColumnType()).size());
		Assert.assertEquals(1, createdTable.getColumnsByType(new MeasureColumnType()).size());
		Assert.assertEquals(new GenericTableType(), createdTable.getTableType());

		// Change meta

		// Checks

	}

	@Test
	public void cloneTableWithStructuralChanges() throws Exception {
		System.err.println("clonedTableWithStructuralChanges");

		Table startTable = createGenericTable();

		// Create new table from existing and remove annotation and attribute
		// columns
		TableCreator tc = genericTableCreatorProvider.get();

		List<Column> columnsToRemove = startTable.getColumnsByType(new AttributeColumnType(), new AnnotationColumnType());
		System.err.println(columnsToRemove);
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
	}

}
