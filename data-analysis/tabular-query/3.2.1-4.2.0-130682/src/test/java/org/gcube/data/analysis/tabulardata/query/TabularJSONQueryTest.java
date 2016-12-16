package org.gcube.data.analysis.tabulardata.query;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.unprivileged.Unprivileged;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.query.json.TabularJSONQuery;
import org.gcube.data.analysis.tabulardata.query.json.TabularJSONQueryFactory;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrderDirection;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QueryColumn;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QueryColumn.Function;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;
import org.gcube.data.analysis.tabulardata.query.utils.TestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class TabularJSONQueryTest {

	@Inject
	TabularJSONQueryFactory queryFactory;

	@Inject
	CubeManager cm;

	@Inject
	@Unprivileged
	DatabaseConnectionProvider databaseConnectionProvider;

	private static TestUtils testUtils;

	private Table table;
	
	Table speciesCodelist;

	Table sampleDataset;
	
	private static QueryPage defaultPage = new QueryPage(200, 300);

	@BeforeClass
	public static void beforeAllTests() {
		
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Before
	public void beforeEachTest() {
		testUtils = new TestUtils(cm, databaseConnectionProvider);
		table = testUtils.createEmptyTable();
		createSpeciesCodelist();
		createSampleDataset();
	}

	@Test
	public void testJsonOrderedQuery() {
		System.err.println("Test ordered query as JSON");
		TabularJSONQuery jsonQuery = queryFactory.get(table.getId());
		QueryOrder ordering = new QueryOrder(table.getColumns().get(0).getLocalId(), QueryOrderDirection.ASCENDING);
		jsonQuery.setOrdering(ordering);
		String json = jsonQuery.getPage(defaultPage);
		System.err.println("JSON:");
		System.err.println(json);
		Assert.assertNotNull(json);
	}
	
	@Test
	public void testJsonGroupQuery() {
		System.err.println("Test grouped query as JSON");
		TabularJSONQuery jsonQuery = queryFactory.get(table.getId());
		QuerySelect select = new  QuerySelect(Arrays.asList(new QueryColumn(table.getColumns().get(0).getLocalId(), Function.COUNT), new QueryColumn(table.getColumns().get(0).getLocalId())));
		jsonQuery.setSelection(select);
		QueryGroup group = new QueryGroup(Arrays.asList(table.getColumns().get(0).getLocalId()));
		jsonQuery.setGrouping(group);
		String json = jsonQuery.getPage(defaultPage);
		System.err.println("JSON:");
		System.err.println(json);
		Assert.assertNotNull(json);
	}

	@Test
	public void testJsonSelectQuery() {
		System.err.println("Test grouped query as JSON");
		TabularJSONQuery jsonQuery = queryFactory.get(table.getId());
		QuerySelect select = new  QuerySelect(Arrays.asList(new QueryColumn(table.getColumns().get(0).getLocalId())));
		jsonQuery.setSelection(select);
		String json = jsonQuery.getPage(defaultPage);
		System.err.println("JSON:");
		System.err.println(json);
		Assert.assertNotNull(json);
	}
	
	@Test
	public void testJsonSelectDatasetQuery() throws JSONException {
		System.err.println("Test grouped query as JSON");
		TabularJSONQuery jsonQuery = queryFactory.get(sampleDataset.getId());
		String json = jsonQuery.getPage(defaultPage);
		
		org.json.JSONObject obj = new org.json.JSONObject(json);
		org.json.JSONArray rows = obj.getJSONArray("rows");

		long totalRows = rows.length();
		for (int i = 0; i < totalRows; i++) {
			JSONArray currentRow = rows.getJSONArray(i);
			for (int k=0; k< currentRow.length(); k++){
				System.out.println(currentRow.get(k).getClass());
				System.out.println(currentRow.get(k));
			}
			break;	
		}
		
		Assert.assertNotNull(json);
	}
	
	@Test
	public void testJsonSimpleQuery() throws JSONException {
		System.err.println("Test simple query as JSON");
		TabularJSONQuery jsonQuery = queryFactory.get(table.getId());
		String json = jsonQuery.getPage(defaultPage);
		System.err.println("JSON:");
		System.err.println(json);
		Assert.assertNotNull(json);
	}
	
	
	
	@Test
	public void prepareMockupResources() throws Exception {
		// Write table to file for mockup
		FileOutputStream fos = new FileOutputStream("/tmp/TestCodelistTable.obj");
		ObjectOutputStream ous = new ObjectOutputStream(fos);
		ous.writeObject(table);
		ous.close();

		//Write content
		TabularJSONQuery jsonQuery = queryFactory.get(table.getId());
		String json = jsonQuery.getPage(new QueryPage(0, jsonQuery.getTotalTuples()));
		FileWriter fw = new FileWriter("/tmp/TestCodelistContent.json");
		fw.write(json);
		fw.close();
	}
	
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

}
