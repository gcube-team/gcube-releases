/**
 *
 */
package org.gcube.common.workspacetaskexecutor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
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

	public static String USERNAME;
	public static String SCOPE;
	public static String TOKEN;
	public static String WORKSPACE_FOLDER_ID;

	private static JsonUtil jUtil = new JsonUtil();

	//CREATE DUMMY CONFIGURATIONS
	private static List<TaskConfiguration> listDummyConf;
	
	private static WsUtil wsUtil = new WsUtil();
	
	private static String TEST_CONF_PROPERTIES = "test-conf.properties";
	
	
	private static void loadTestConfig() {

		try (InputStream input = TestDataMinerTaskExecutor.class.getClassLoader()
				.getResourceAsStream(TEST_CONF_PROPERTIES)) {

			Properties prop = new Properties();

			if (input == null) {
				System.out.println("Sorry, unable to find " + TEST_CONF_PROPERTIES);
				return;
			}

			prop.load(input);
			/*
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				System.out.println("Key : " + key + ", Value : " + value);
			}
			*/

			System.out.println("Loaded Configurations:");
			USERNAME = prop.getProperty("USERNAME").trim();
			System.out.println("USERNAME: " + USERNAME);
			SCOPE = prop.getProperty("SCOPE").trim();
			System.out.println("SCOPE: " + SCOPE);
			TOKEN = prop.getProperty("TOKEN").trim();
			System.out.println("TOKEN: " + TOKEN);
			WORKSPACE_FOLDER_ID = prop.getProperty("WORKSPACE_FOLDER_ID").trim();
			System.out.println("WORKSPACE_FOLDER_ID: " + WORKSPACE_FOLDER_ID);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

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

		System.out.println("\n\nSet Task configurations called...");
		
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
		System.out.println("\n\nDelete configuration called...");
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
		
		loadTestConfig();
		//jsonCheck();
		ScopeProvider.instance.set(SCOPE);
		SecurityTokenProvider.instance.set(TOKEN);
//		checkGubeProperties(null);
		listDummyConf = getDummyListOfConfigurations(3);
//		checkGubeProperties(listDummyConf);
		WorkspaceDataMinerTaskExecutor exec =  WorkspaceDataMinerTaskExecutor.getInstance();
	
//		exec.withOwner(USERNAME);
//		//jsonCheck();
		
		//TESTING SAVE CONFIGURATIONS
		setDummyTaskConfigurations(exec);
		
		//GET LIST TASK CONFIGURATIONS
		List<TaskConfiguration> listTaskConfigs = getConfigurations(exec);
		
		//TESTING DELETE ONE CONFIGURATIOn
		deleteConfiguration(exec, listTaskConfigs.get(1));
//
//		//ERASE ALL CONFIGURATIONS
	//	eraseAllTaskConfigurations(exec);

		//GET LIST CONFIGURATIONS
		getConfigurations(exec);

	}

	public static List<TaskConfiguration> getConfigurations(WorkspaceDataMinerTaskExecutor exec){
		System.out.println("\n\nGet configuration/s called...");
		try {
			List<TaskConfiguration> conf = exec.getListOfTaskConfigurations(WORKSPACE_FOLDER_ID);
			if(conf!=null){
				System.out.println("\n\nSaved configuration/s is/are: ");
				for (TaskConfiguration taskConfiguration : conf) {
					System.out.println(taskConfiguration);
				}
			}else {
				System.out.println("\n\n No configurations found");
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


		try {
			Workspace workspace = wsUtil.getWorkspace();
			WorkspaceItem workspaceItem = workspace.getItem(WORKSPACE_FOLDER_ID);

			//SET IF list of Configurations is not null
			if(listConfigurations!=null){
				JSONArray jsonArray = jUtil.toJSONArray(listConfigurations);
				System.out.println("Json array to save: "+jsonArray);
				wsUtil.setPropertyValue(workspaceItem, WorkspaceDataMinerTaskExecutor.WS_DM_TASK_TASK_CONF, jsonArray.toString());
			}

			//GET
			String jsonArrayConf = wsUtil.getPropertyValue(workspaceItem,WorkspaceDataMinerTaskExecutor.WS_DM_TASK_TASK_CONF);
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

