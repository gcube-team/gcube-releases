package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class TableManagerTest {

	@Inject
	private TableManager tm;

	@Inject
	@Named("Codelist")
	private TableCreator tc;

	@BeforeClass
	public static void setup() {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Test(expected=NoSuchTableException.class)
	public void testTableRemoval() throws Exception {
		System.err.println("Codelist creation");
		
		Table codelist = tc.create();
		System.err.println("Created empty table:\n" + codelist);

		tm.remove(codelist.getId());
		Table removedTable = tm.get(codelist.getId());
		
	}

	@Test
	public void testValidationRemoval() throws Exception {
		System.err.println("Codelist creation");
		
		Table codelist = tc.addColumn(new Column(new ColumnLocalId("columnTest"), new IntegerType(), new ValidationColumnType())).create();
		System.err.println("Created table:\n" + codelist);
		
		System.out.println("column size is "+codelist.getColumns().size());
		
		Assert.assertTrue(codelist.getColumns().size()==2);
		
		codelist = tm.removeValidationColumns(codelist.getId());
			
		
		System.out.println("table is "+codelist);
		
		Assert.assertTrue(codelist.getColumns().size()==1);
		
		tm.remove(codelist.getId());
	}
	
	@Test
	public void testValidationAdd() throws Exception {
		System.err.println("Codelist creation");
		
		Table codelist = tc.create();
		System.err.println("Created table:\n" + codelist);
		
		System.out.println("column size is "+codelist.getColumns().size());
		
		Assert.assertTrue(codelist.getColumns().size()==1);
		
		codelist = tm.addValidationColumns(codelist.getId(), new Column(new ColumnLocalId("columnTest"), new IntegerType(), new ValidationColumnType()));
					
		System.out.println("table is "+codelist);
		
		Assert.assertTrue(codelist.getColumns().size()==2);
		
		tm.remove(codelist.getId());
	}
	
	@Test
	public void addColumnAfterTest() throws Exception {
		System.err.println("Codelist creation");
		
		Column col =new Column(new ColumnLocalId("columnTest"), new TextType(), new CodeColumnType());
		Column colAfter =new Column(new ColumnLocalId("columnAfter"), new IntegerType(), new CodeColumnType());
		Column colBefore =new Column(new ColumnLocalId("columnbefore"), new IntegerType(), new CodeColumnType());
		
		Table codelist = tc.addColumn(col)
				.addColumnAfter(colAfter, col)
				.addColumnBefore(colBefore, col)
				.create();
		
		System.out.println(codelist.getColumns());
		Assert.assertTrue(codelist.getColumns().size()==4);
		
		System.err.println("Created table:\n" + codelist);
	
		List<Column> expectedCols = Arrays.asList(colBefore, col, colAfter); 
		
		Assert.assertEquals(expectedCols ,codelist.getColumnsExceptTypes(IdColumnType.class));
		
		System.out.println(codelist.getColumnById(col.getLocalId()).getDataType().toString());
		
		tm.remove(codelist.getId());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void addColumnAfterToLikeTest() throws Exception {
		System.err.println("Codelist creation");
		
		Column col =new Column(new ColumnLocalId("columnTest"), new IntegerType(), new CodeColumnType());
		Column colAfter =new Column(new ColumnLocalId("columnAfter"), new IntegerType(), new CodeColumnType());
		Column colBefore =new Column(new ColumnLocalId("columnbefore"), new IntegerType(), new CodeColumnType());
		
		Table codelist = tc.addColumn(col)
				.addColumnAfter(colAfter, col)
				.addColumnBefore(colBefore, col)
				.create();
		
		Column newCol =new Column(new ColumnLocalId("columnNew"), new IntegerType(), new CodeColumnType());
		
		Table tbLike = tc.like(codelist, true, Lists.newArrayList(col)).addColumnAfter(newCol, colBefore).create();
		
		
		
		System.out.println(codelist.getColumns());
		Assert.assertTrue(tbLike.getColumns().size()==4);
		
		List<Column> notExpectedCols = Arrays.asList(colBefore, col, colAfter); 
		
		Assert.assertNotEquals(notExpectedCols ,tbLike.getColumnsExceptTypes(IdColumnType.class));
		
		List<Column> expectedCols = Arrays.asList(colBefore, newCol, colAfter); 
		
		Assert.assertEquals(expectedCols ,tbLike.getColumnsExceptTypes(IdColumnType.class));
		
		System.err.println("Created table:\n" + tbLike);
		
		System.out.println("Old table:\n" + codelist);
		
		tm.remove(codelist.getId());
		tm.remove(tbLike.getId());
	}
}
