/**
 *
 */
package org.gcube.common.workspacetaskexecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.workspacetaskexecutor.dataminer.WorkspaceDataMinerTaskExecutor;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameter;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameterType;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.common.workspacetaskexecutor.util.Converter;
import org.gcube.common.workspacetaskexecutor.util.JsonUtil;
import org.gcube.common.workspacetaskexecutor.util.WsUtil;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ParameterType;
import org.json.JSONArray;
import org.json.JSONException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 26, 2018
 */
public class TestDataMinerTaskExecutor {


	public static String USERNAME = "francesco.mangiacrapa";
	public static String SCOPE = "/gcube";

	public static String WORKSPACE_FOLDER_ID = "682ff48e-0cc9-44df-884d-185fabe8909b";

	private static JsonUtil jUtil = new JsonUtil();

	//CREATE DUMMY CONFIGURATIONS
	private static List<TaskConfiguration> listDummyConf;


	/**
	 *
	 */
	private static void eraseAllTaskConfigurations(WorkspaceDataMinerTaskExecutor exec) {

		try {
			Boolean done = exec.eraseAllTaskConfigurations(WORKSPACE_FOLDER_ID);
			System.out.println("\n\nErase configurations done: "+done);
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private static void setDummyTaskConfigurations(WorkspaceDataMinerTaskExecutor exec){


		//SET TASK CONFIGURATION
		try {
			for (TaskConfiguration taskConfiguration : listDummyConf) {
				exec.setTaskConfiguration(taskConfiguration);
			}
			System.out.println("\n\nSet Task configurations done!");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void deleteConfiguration(WorkspaceDataMinerTaskExecutor exec, TaskConfiguration conf){
		try {
			Boolean done = exec.removeTaskConfiguration(conf);
			System.out.println("\n\nErase configuration done: "+done);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {

		//jsonCheck();

		ScopeProvider.instance.set(SCOPE);
		checkGubeProperties(null);
		listDummyConf = getDummyListOfConfigurations(3);
		checkGubeProperties(listDummyConf);
//		WorkspaceDataMinerTaskExecutor exec =  WorkspaceDataMinerTaskExecutor.getInstance();
//		exec.withOwner(USERNAME);
//		//jsonCheck();
//
//		//ERASE ALL CONFIGURATIONS
//		eraseAllTaskConfigurations(exec);


		//GET LIST CONFIGURATIONS
		//getConfigurations(exec);

		//ERASE ALL CONFIGURATIONS
		//eraseAllTaskConfigurations(exec);

		//SET TASK CONFIGURATION
		//setDummyTaskConfigurations(exec);
//
////		deleteConfiguration(exec, listDummyConf.get(1));
////
		//getConfigurations(exec);


//		listDummyConf.get(2).setTaskId("Updated task id");
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("Pippo", "Value Pippo");
//		map.put("Paperino", "Value Paperino");
//		listDummyConf.get(2).setMapParameters(map);
//
//		try {
//			exec.setTaskConfiguration(listDummyConf.get(2));
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		};
//
//		getConfigurations(exec);

//
//		//UPDATE TASK CONFIGURATION
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("Pippo", "Value Pippo");
//		map.put("Paperino", "Value Paperino");
//		c2.setMapParameters(map);
//
//		try {
//			exec.setTaskConfiguration(c2);
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}



//		try {
//			//exec.removeTaskConfiguration(taskConfiguration)
//			//exec.checkItemExecutable(WORKSPACE_FOLDER_ID);
//		}
//		catch (ItemNotExecutableException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//			System.out.println("The item is not executable...");
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	public static List<TaskConfiguration> getConfigurations(WorkspaceDataMinerTaskExecutor exec){

		try {
			List<TaskConfiguration> conf = exec.getListOfTaskConfigurations(WORKSPACE_FOLDER_ID);
			if(conf!=null){
				System.out.println("\n\nSaved configuration/s is/are: ");
				for (TaskConfiguration taskConfiguration : conf) {
					System.out.println(taskConfiguration);
				}
			}

			return conf;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static void checkGubeProperties(List<TaskConfiguration> listConfigurations){

		//SET
		try {
			WorkspaceItem workspaceItem = WsUtil.getItem(USERNAME, WORKSPACE_FOLDER_ID);

			if(listConfigurations!=null){
				JSONArray jsonArray = jUtil.toJSONArray(listConfigurations);
				System.out.println("Json array to save: "+jsonArray);
				WsUtil.setPropertyValue(workspaceItem, WorkspaceDataMinerTaskExecutor.WS_DM_TASK_TASK_CONF, jsonArray.toString());
			}

			//GET
			String jsonArrayConf = WsUtil.getPropertyValue(workspaceItem, WorkspaceDataMinerTaskExecutor.WS_DM_TASK_TASK_CONF);
			System.out.println("Json array read from "+WorkspaceDataMinerTaskExecutor.WS_DM_TASK_TASK_CONF+": "+jsonArrayConf);


			TypeReference<List<TaskConfiguration>> mapType = new TypeReference<List<TaskConfiguration>>() {};
			List<TaskConfiguration> listUnM = jUtil.readList(jsonArrayConf, mapType);
			System.out.println("Json array to listUnM...");
			for (TaskConfiguration taskConfiguration : listUnM) {
				System.out.println(taskConfiguration);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public static TaskConfiguration createDummyConfiguration(int index){
		List<ParameterType> availableTypes = Converter.getEnumList(ParameterType.class);
		List<TaskParameterType> types = new ArrayList<TaskParameterType>();
		for (ParameterType parameter : availableTypes) {
			types.add(new TaskParameterType(parameter.name()));
		}

		System.out.println(types.toString());

//		for (String availableType : tpt.getParametersTypes()) {
//			System.out.println(availableType);
//		}

		List<TaskParameter> listParameters = new ArrayList<TaskParameter>();

		TaskParameter tp = new TaskParameter();
		tp.setKey("publiclink");
		tp.setValue("this is the public link "+index);
		tp.setType(new TaskParameterType(ParameterType.FILE.toString()));


		listParameters.add(tp);

		TaskParameter tp2 = new TaskParameter();
		tp2.setKey("key"+index);
		tp2.setValue("value "+index);
		tp2.setType(types.get(new Random().nextInt(types.size())));
		tp2.setDefaultValues(Arrays.asList("value 1", "value2"));
		listParameters.add(tp2);


		return new TaskConfiguration(index+"", UUID.randomUUID().toString(), index+"name", null, USERNAME, SCOPE, "my token", WORKSPACE_FOLDER_ID, listParameters);
	}


	public static List<TaskConfiguration> getDummyListOfConfigurations(int total){
		List<TaskConfiguration> listConfigurations = new ArrayList<>(total);
		for (int i=0; i<total; i++) {
			TaskConfiguration c  = createDummyConfiguration(i);
			listConfigurations.add(c);
		}
		return listConfigurations;
	}

	public static void jsonCheck(){


		List<TaskConfiguration> listConfigurations = getDummyListOfConfigurations(3);
		try {
			JSONArray jsonArray = jUtil.toJSONArray(listConfigurations);
			System.out.println("Json array: "+jsonArray.toString(4));
			TypeReference<List<TaskConfiguration>> mapType = new TypeReference<List<TaskConfiguration>>() {};
			List<TaskConfiguration> listUnM = jUtil.readList(jsonArray.toString(), mapType);
			System.out.println("Json array to listUnM...");
			for (TaskConfiguration taskConfiguration : listUnM) {
				System.out.println(taskConfiguration);
			}

		}
		catch (JsonProcessingException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

