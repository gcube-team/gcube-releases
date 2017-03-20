package org.gcube.data.analysis.tabulardata.cube.metadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.factories.AnnotationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeNameColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ImportMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class CubeMetadataWranglerTest {

	@Inject
	private CubeMetadataWrangler cmw;

	private static Table startingTable;

	@BeforeClass
	public static void beforeClass() {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@BeforeClass
	public static void setUp() {
		List<Column> columns = new ArrayList<Column>();

		Column idColumn = new Column(new ColumnLocalId("test"), new IntegerType(), new IdColumnType());
		idColumn.setName(UUID.randomUUID().toString());
		columns.add(idColumn);

		Column codeColumn = new CodeColumnFactory().createDefault();
		codeColumn.setName(UUID.randomUUID().toString());
		columns.add(codeColumn);

		Column codeNameColumn = new CodeNameColumnFactory().create("en");
		codeNameColumn.setName(UUID.randomUUID().toString());
		columns.add(codeNameColumn);

		Column annotationColumn = new AnnotationColumnFactory().create(new ImmutableLocalizedText("La mia annotazione", "it"));
		annotationColumn.setName(UUID.randomUUID().toString());
		columns.add(annotationColumn);

		startingTable = new Table(new GenericTableType());
		startingTable.setName(UUID.randomUUID().toString());
		startingTable.setColumns(columns);

		startingTable.setMetadata(new ImportMetadata("SMDX", "http://localhost:8080/myservice", new Date()));
	}

	private Table saveTable(Table table) {
		
		return cmw.save(table, false);
	}

	@Test
	public void testSave() {
		System.out.println("Save Test");
		Table savedTable = saveTable(startingTable);
		System.out.println("Starting Table:\n" + startingTable);
		System.out.println("Saved Table:\n" + savedTable);
		Assert.assertNotNull(savedTable);
		Assert.assertTrue(startingTable.sameStructureAs(savedTable));
		//Assert.assertFalse(startingTable.equals(savedTable));
		//Assert.assertFalse(savedTable.equals(startingTable));
	}

	@Test
	public void testGet() throws Exception {
		System.out.println("Get test (cmw is null? "+(cmw==null)+" )");
		Table savedTable = saveTable(startingTable);
		Table loadedTable1 = cmw.get(savedTable.getId());
		Table loadedTable2 = cmw.get(savedTable.getId());

		System.out.println("Saved Table:\n" + savedTable);
		System.out.println("First loaded Table:\n" + loadedTable1);
		System.out.println("Second loaded Table:\n" + loadedTable2);

		Assert.assertEquals(savedTable, loadedTable1);
		Assert.assertEquals(loadedTable1, loadedTable2);
		Assert.assertEquals(savedTable, loadedTable2);
		Assert.assertTrue(savedTable.sameStructureAs(loadedTable1));
		Assert.assertTrue(savedTable.sameStructureAs(loadedTable2));
	}

	@Test
	public void testGetAll() {
		Table created = saveTable(startingTable);
		List<Table> tables = cmw.getAll();
		System.out.println("All tables: " + tables);
		Assert.assertTrue(tables.contains(created));
	}

	@Test
	public void testGetAllByType() {
		Table created = saveTable(startingTable);
		List<Table> tables = cmw.getAll(created.getTableType());
		System.out.println("All tables of type " + startingTable.getTableType() + ": " + tables);
		Assert.assertTrue(tables.contains(created));
	}

	@Test
	public void testSaveTwice() {
		System.out.println("Save-Twice test");
		Table savedTable1 = saveTable(startingTable);
		Table savedTable2 = saveTable(savedTable1);
		System.out.println("First saved table:\n" + savedTable1);
		System.out.println("Second saved table:\n" + savedTable2);
		Assert.assertTrue(savedTable1.sameStructureAs(savedTable2));
		Assert.assertFalse(savedTable1.equals(savedTable2));
		Assert.assertTrue(savedTable2.getId().getValue() > savedTable1.getId().getValue());
	}
	
}
