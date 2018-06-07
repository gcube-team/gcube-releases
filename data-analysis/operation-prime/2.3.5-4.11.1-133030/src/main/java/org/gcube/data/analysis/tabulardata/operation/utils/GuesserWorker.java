package org.gcube.data.analysis.tabulardata.operation.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.common.database.DatabaseEndpointIdentifier;
import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.resources.StringResource;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableStringResource;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.Category;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.dataanalysis.lexicalmatcher.analysis.run.CategoryGuesser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuesserWorker extends ResourceCreatorWorker{

	private static Logger logger = LoggerFactory.getLogger(GuesserWorker.class);
	
	DatabaseProvider database;
	CubeManager cubeManager;
		
	public GuesserWorker(OperationInvocation sourceInvocation, DatabaseProvider database, CubeManager cubeManager) {
		super(sourceInvocation);
		this.database = database;
		this.cubeManager = cubeManager;
	}

	@Override
	protected ResourcesResult execute() throws WorkerException {
		
		LexicalEngineConfiguration configurator = new LexicalEngineConfiguration();
		
		DatabaseEndpoint dbInstance = database.get(new DatabaseEndpointIdentifier("TabularData Database","Data-User"));
		
		configurator.setDatabaseUserName(dbInstance.getCredentials().getUsername());
		configurator.setDatabasePassword(dbInstance.getCredentials().getPassword());
		configurator.setDatabaseDriver("org.postgresql.Driver");
		configurator.setDatabaseURL(dbInstance.getConnectionString());
		configurator.setDatabaseDialect("org.hibernate.dialect.PostgreSQLDialect");
		
		ArrayList<Category> categories = retrieveCategories();
		configurator.setCategories(categories);
		CategoryGuesser guesser = new CategoryGuesser();
		Table targetTable= cubeManager.getTable(getSourceInvocation().getTargetTableId());
		try {
			guesser.runGuesser(targetTable.getName(), targetTable.getColumnById(getSourceInvocation().getTargetColumnId()).getName(), configurator);
		} catch (Exception e) {
			logger.error("error executing guesser",e);
			throw new WorkerException("error executing guesser",e);
		}
		ArrayList<SingleResult> results = guesser.getLastResults();
		
		logger.trace("guesser results are  "+results.size());
		
		String jsonResult = generateJSONFromData(results);
		
		logger.trace("guesser result: "+jsonResult);
						
		return new ResourcesResult(new ImmutableStringResource(new StringResource(jsonResult),"guesser", "guesser", ResourceType.GUESSER));
	}

	private String generateJSONFromData(ArrayList<SingleResult> results) {
		JSONObject json = new JSONObject();
		JSONArray jsonRows = new JSONArray();
		for (SingleResult result : results) {
			long codelistId = Long.parseLong(result.getFamilyID());
			Table codelist = cubeManager.getTable(new TableId(codelistId));
			String localId = codelist.getColumnByName(result.getColumn()).getLocalId().getValue();
			jsonRows.put(Arrays.asList(codelistId, localId, result.getScore()));
		}
		try {
			json.put("rows", jsonRows);
		} catch (JSONException e) {
			throw new RuntimeException("Error occured with serialization of table content. Check server log.");
		}
		return json.toString();
	}


	@SuppressWarnings("unchecked")
	private ArrayList<Category> retrieveCategories() {
		ArrayList<Category> categories = new ArrayList<Category>();
				
		List<Integer> codelistIds = (List<Integer>) getSourceInvocation().getParameterInstances().get(GuesserWorkerFactory.CODELISTS_PARAMETER.getIdentifier());
		for (Integer id :codelistIds ){
			Table table = cubeManager.getTable(new TableId(id));
			if (table.contains(TableDescriptorMetadata.class)){
				TableDescriptorMetadata tdm = table.getMetadata(TableDescriptorMetadata.class);
				categories.add(new Category(tdm.getName(), table.getId().getValue()+"", table.getName(), tdm.getName()));
			} else 
				categories.add(new Category("undefined", table.getId().getValue()+"", table.getName(), "undefined"));
		}
		return categories;
	}
	
	
}
