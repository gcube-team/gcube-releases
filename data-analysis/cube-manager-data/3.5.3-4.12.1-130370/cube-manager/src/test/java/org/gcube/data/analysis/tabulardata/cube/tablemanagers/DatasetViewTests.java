package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import java.util.Collection;

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
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.geometry.GeometryShape;
import org.gcube.data.analysis.tabulardata.model.exceptions.NoSuchColumnException;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ViewColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetViewTableType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class DatasetViewTests {

	@Inject
	@Named("Codelist")
	Instance<TableCreator> codelistCreatorProvider;

	@Inject
	@Named("Dataset")
	Instance<TableCreator> datasetCreatorProvider;

	@Inject
	@Named("ViewTable")
	Instance<TableCreator> viewCreatorProvider;

	@Inject
	TableManager tm;

	@BeforeClass
	public static void setup() {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	private Table createCodelist() throws TableCreationException {
		TableCreator cc = codelistCreatorProvider.get();

		cc.addColumn(new CodeColumnFactory().createDefault());
		cc.addColumn(new CodeNameColumnFactory().create("eng"));
		cc.addColumn(new CodeNameColumnFactory().create("ita"));
		cc.addColumn(new AnnotationColumnFactory().create(new ImmutableLocalizedText("My Annotation on species", "eng")));
		return cc.create();
	}

	private Table createDataset(Table codelist) throws TableCreationException {
		// Create dataset
		TableCreator tc = datasetCreatorProvider.get();

		tc.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("Catcher", "eng"), new TextType(30)));

		tc.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("Area", "eng"), new GeometryType(
				GeometryShape.MULTIPOLYGON, 2)));
		tc.addColumn(new MeasureColumnFactory().create(new ImmutableLocalizedText("Quantity", "eng"), new IntegerType()));
		try {
			tc.addColumn(new DimensionColumnFactory().create(new ImmutableLocalizedText("Species"),
					new ImmutableColumnRelationship(codelist.getId(), codelist.getColumnByName("id").getLocalId())));
		} catch (NoSuchColumnException e) {
			Assert.fail();
		}
		tc.addColumn(new AnnotationColumnFactory().create(new ImmutableLocalizedText("My Annotation on data")));

		return tc.create();
	}

	@Test
	public void newTableWithCustomMeta() throws Exception {
		System.err.println("newTableWithCustomMeta");
		Table codelist = createCodelist();
		Table dataset = createDataset(codelist);

		TableCreator tc = viewCreatorProvider.get();

		tc.like(dataset, false);
		Collection<Column> datasetDimensionColumns = dataset.getColumnsByType(DimensionColumnType.class);
		for (Column datasetDimensionColumn : datasetDimensionColumns) {
			Table refCodelist = tm.get(datasetDimensionColumn.getRelationship().getTargetTableId());
			System.err.println("RefTable: " + refCodelist);

			for (Column codelistColumn : refCodelist.getColumns()) {
				if (codelistColumn.getColumnType().equals(new IdColumnType()))
					continue;
				codelistColumn.setMetadata(new ViewColumnMetadata(refCodelist.getId(), codelistColumn.getLocalId(),
						datasetDimensionColumn.getLocalId()));
				tc.addColumn(codelistColumn);
			}
		}
		Table createdTable = tc.create();
		Assert.assertNotNull(createdTable);
		System.err.println("Created view: " + createdTable);

		Assert.assertNotNull(createdTable);
		Assert.assertTrue(createdTable.getId().getValue() > 0);
		Assert.assertFalse(createdTable.getName().isEmpty());
		Assert.assertTrue(createdTable.getColumns().size() > 0);
		Assert.assertTrue(createdTable.getColumnsByType(IdColumnType.class).size() == 1);
		Assert.assertTrue(createdTable.getColumnsByType(AttributeColumnType.class).size() == 2);
		Assert.assertTrue(createdTable.getColumnsByType(DimensionColumnType.class).size() == 1);
		Assert.assertTrue(createdTable.getColumnsByType(MeasureColumnType.class).size() == 1);
		Assert.assertTrue(createdTable.getColumnsByType(AnnotationColumnType.class).size() == 2);
		Assert.assertEquals(new DatasetViewTableType(), createdTable.getTableType());
	}
}
