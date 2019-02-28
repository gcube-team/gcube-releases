package org.gcube.data.analysis.tabulardata.query;

import java.util.Calendar;
import java.util.Iterator;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.unprivileged.Unprivileged;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrderDirection;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class TabularQueryTest {

	TestUtils testUtils;

	@Inject
	TabularQueryFactory queryFactory;

	@Inject
	TabularQueryUtils queryUtils;

	@Inject
	CubeManager cm;

	@Inject
	@Unprivileged
	DatabaseConnectionProvider databaseConnectionProvider;

	Table speciesCodelist;

	Table sampleDataset;

	Table datasetView;

	Table emptyTable;
	
	@BeforeClass
	public static void beforeClass() {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Before
	public void before() {
		testUtils = new TestUtils(cm, databaseConnectionProvider);
		createSpeciesCodelist();
		createSampleDataset();
		emptyTable = testUtils.createEmptyTable();
		// createDatasetView();
	}

//	@Test
//	public void testViewCreationAndPagedQuery() {
//		System.err.println("View query test");
//		Assert.assertNotNull(datasetView);
//
//		TabularQuery tabularQuery = queryFactory.get(datasetView);
//		Iterator<Object[]> queryResult = tabularQuery.getPage(new Page(0, 100));
//		int linesNumber = writeQueryResultToSTDOUT(queryResult);
//		Assert.assertEquals(100, linesNumber);
//	}

	@Test
	public void testSimpleCodelistQuery() {
		System.err.println("Simple query test");
		TabularQuery tabularQuery = queryFactory.get(speciesCodelist);
		Iterator<Object[]> queryResult = tabularQuery.getPage(new QueryPage(0, 100));
		int linesNumber = writeQueryResultToSTDOUT(queryResult);
		Assert.assertEquals(100, linesNumber);
	}
	
	@Test
	public void testSimpleDataSetQuery() {
		System.err.println("Simple query test");
		TabularQuery tabularQuery = queryFactory.get(sampleDataset);
		Iterator<Object[]> queryResult = tabularQuery.getPage(new QueryPage(0, 100));
		
		while (queryResult.hasNext()){
			Object[] objs = queryResult.next();
			for (Object obj: objs)
				System.out.println(obj.getClass().getSimpleName());
			break;
		}
		//int linesNumber = writeQueryResultToSTDOUT(queryResult);
		//Assert.assertEquals(100, linesNumber);
	}
	
	@Test
	public void testFilteredDataSetQuery() {
		System.err.println("Filtered query test");
		TabularQuery tabularQuery = queryFactory.get(sampleDataset);
		
		Column column = null;
		for (Column col: sampleDataset.getColumnsExceptTypes(IdColumnType.class)){
			if (col.getDataType() instanceof IntegerType){
				column = col;
				break;
			}
			
		}
		
		System.out.println("selected column "+column.getName());
		
		tabularQuery.setFilter(new QueryFilter(
				new GreaterThan( new ColumnReference(sampleDataset.getId(), column.getLocalId()), new TDInteger(70))));
		tabularQuery.setOrdering(new QueryOrder(column.getLocalId(), QueryOrderDirection.ASCENDING));
		System.out.println(tabularQuery.toString());
		Iterator<Object[]> queryResult = tabularQuery.getPage(new QueryPage(0, 100));
		int linesNumber = writeQueryResultToSTDOUT(queryResult);
		Assert.assertEquals(100, linesNumber);
	}
	
	@Test
	public void testEmptyTableQuery() {
		System.err.println("Simple query test");
		TabularQuery tabularQuery = queryFactory.get(emptyTable);
		Iterator<Object[]> queryResult = tabularQuery.getPage(new QueryPage(0, 100));
		int linesNumber = writeQueryResultToSTDOUT(queryResult);
		Assert.assertEquals(0, linesNumber);
	}

	@Test
	public void testOrderedCodelistQuery() {
		System.err.println("Ordered query test");
		QueryPage page = new QueryPage(0, 100);
		QueryOrder ordering = new QueryOrder(speciesCodelist.getColumns().get(1).getLocalId(), QueryOrderDirection.DESCENDING);
		TabularQuery tabularQuery = queryFactory.get(speciesCodelist);
		tabularQuery.setOrdering(ordering);
		Iterator<Object[]> queryResult = tabularQuery.getPage(page);
		int linesNumber = writeQueryResultToSTDOUT(queryResult);
		Assert.assertEquals(100, linesNumber);
	}

	@Test
	public void testFilteredQuery() {
		System.err.println("Filtered/Paged query test");

		Column englishNameColumn = speciesCodelist.getColumnsByType(CodeNameColumnType.class).get(0);
		Column frenchNameColumn = speciesCodelist.getColumnsByType(CodeNameColumnType.class).get(1);
		
		
		Expression containsDory = new TextContains(speciesCodelist.getColumnReference(englishNameColumn),
				new TDText("dory"));
		Expression equalsSaintPierre = new Equals(speciesCodelist.getColumnReference(frenchNameColumn),
				new TDText("Saint Pierre"));
		QueryFilter filter = new QueryFilter(new And(containsDory, equalsSaintPierre));

		TabularQuery tabularQuery = queryFactory.get(speciesCodelist);
		tabularQuery.setFilter(filter);

		int totalTuples = tabularQuery.getTotalTuples();

		System.err.println("Total tuples: " + totalTuples);
		Assert.assertTrue(totalTuples > 0);
		QueryPage page = new QueryPage(0, totalTuples);
		Iterator<Object[]> queryResult = tabularQuery.getPage(page);
		int linesNumber = writeQueryResultToSTDOUT(queryResult);
		Assert.assertEquals(1, linesNumber);
	}

	@Test
	public void testGetAllOrderedFromCodelist() {
		System.err.println("Get All query test");

		TabularQuery tabularQuery = queryFactory.get(speciesCodelist);

		int totalTuples = tabularQuery.getTotalTuples();

		Assert.assertTrue(totalTuples > 0);
		
		Column orderingColumn = speciesCodelist.getColumns().get(1);

		tabularQuery.setOrdering(new QueryOrder(orderingColumn.getLocalId(), QueryOrderDirection.ASCENDING));
		Iterator<Object[]> queryResult = tabularQuery.getAll();
		int linesNumber = writeQueryResultToSTDOUT(queryResult);
		System.out.println("Tuples written: " + linesNumber);
	}

	@Test
	public void testGetAllFilteredFromCodelist() {
		System.err.println("Get All query test");
		
		Column englishNameColumn = speciesCodelist.getColumnsByType(CodeNameColumnType.class).get(0);

		TabularQuery tabularQuery = queryFactory.get(speciesCodelist);
		tabularQuery.setFilter(new QueryFilter(new TextMatchSQLRegexp(speciesCodelist.getColumnReference(englishNameColumn), new TDText("(%Bay%)|(%Beach%)"))));

		int totalTuples = tabularQuery.getTotalTuples();
		System.err.println("Total tuples: " + totalTuples);
		Assert.assertTrue(totalTuples > 0);
		Iterator<Object[]> queryResult = tabularQuery.getAll();
		int linesNumber = writeQueryResultToSTDOUT(queryResult);
	}

	// private void createDatasetView() {
	// datasetView = queryUtils.generateDatasetView(sampleDataset.getId());
	// }

	private void createSpeciesCodelist() {
		if (speciesCodelist != null)
			return;
		speciesCodelist = testUtils.createSpeciesCodelist();
	}

	private void createSampleDataset() {
		if (sampleDataset != null)
			return;
		sampleDataset = testUtils.createSampleDataset(speciesCodelist);
	}

	/**
	 * @param queryResult
	 * @return the number of lines written
	 */
	private int writeQueryResultToSTDOUT(Iterator<Object[]> queryResult) {
		Object[] line;
		int linesNumber = 0;
		while (queryResult.hasNext()) {
			line = queryResult.next();
			linesNumber++;
			for (Object cell : line) {
				if (cell == null)
					System.out.print("(null)\t");
				else
					System.out.print(cell.toString() + "\t");
			}
			System.out.println();
		}
		return linesNumber;
	}
}
