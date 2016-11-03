/**
 * 
 */
package org.gcube.vremanagement.executor.configuration.jsonbased;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.gcube.smartgears.ContextProvider;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.configuration.ScheduledTaskConfiguration;
import org.gcube.vremanagement.executor.exception.SchedulePersistenceException;
import org.gcube.vremanagement.executor.exception.ScopeNotMatchException;
import org.gcube.vremanagement.executor.utils.IOUtility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class FileScheduledTaskConfiguration implements ScheduledTaskConfiguration {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(FileScheduledTaskConfiguration.class);
	
	protected String configurationFileLocation;
	protected List<LaunchParameter> configuredTasks;
	
	
	public static final String CONFIG_TASK_FILENAME = "definedTasks.json";
	
	public FileScheduledTaskConfiguration() throws Exception {
		this(ContextProvider.get().persistence().location());
	}
	
	public FileScheduledTaskConfiguration(String location) throws IOException, JSONException {
		this.configurationFileLocation = location;
		this.configuredTasks = new ArrayList<LaunchParameter>();
		this.configuredTasks = retriveConfiguredTask();
	}
	
	protected Scheduling getScheduling(JSONObject jsonObject) 
			throws JSONException, ParseException, ScopeNotMatchException {
		return new JSONScheduling(jsonObject);
	}
	
	
	protected static String configurationFileName(String configurationFileLocation){
		return configurationFileLocation + "/" + CONFIG_TASK_FILENAME;
	}
	
	public List<LaunchParameter> retriveConfiguredTask() 
			throws IOException, JSONException {
		
		String configuredTasksDefinition = IOUtility.readFile(configurationFileName(configurationFileLocation)); 
		List<LaunchParameter> tasks = new ArrayList<LaunchParameter>();
		 
		JSONArray jsonArray = new JSONArray(configuredTasksDefinition);
		for(int i=0; i<jsonArray.length(); i++){
			try {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONLaunchParameter parameter = new JSONLaunchParameter(jsonObject);
				tasks.add(parameter);
			} catch (Exception e) {
				logger.error("Error launching configurad Task", e.getCause());
			}
			
		}
		return tasks;
	}
	
	protected void emptyConfigurationFile(String fileName) 
			throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(fileName);
		writer.print("");
		writer.close();
	}
	
	protected void writeOnConfigurationFile() throws JSONException, IOException, ParseException{
		String fileName = configurationFileName(configurationFileLocation);
		
		JSONArray jsonArray = new JSONArray();
		for(LaunchParameter launchParameter : configuredTasks){
			JSONLaunchParameter jsonLaunchParameter = new JSONLaunchParameter(launchParameter);
			jsonArray.put(jsonLaunchParameter.toJSON());
		}
		String jsonArrayString = jsonArray.toString();
		emptyConfigurationFile(fileName);
		FileUtils.writeStringToFile(new File(fileName), jsonArrayString);
	}
	
	@Override
	public synchronized void addScheduledTask(UUID uuid, String consumerID, LaunchParameter parameter) throws SchedulePersistenceException{
		try {
			addLaunch(new JSONLaunchParameter(parameter));
		} catch (ParseException e) {
			throw new SchedulePersistenceException(e.getCause());
		}
	}
	
	public synchronized void addLaunch(JSONLaunchParameter parameter) throws SchedulePersistenceException {
		configuredTasks.add(parameter);
		try {
			writeOnConfigurationFile();
		} catch (JSONException | IOException | ParseException e) {
			throw new SchedulePersistenceException();
		}
	}
	
	public void releaseLaunch(LaunchParameter parameter)
			throws SchedulePersistenceException {
		try {
			removeLaunch(new JSONLaunchParameter(parameter));
		} catch (JSONException | IOException | ParseException e) {
			throw new SchedulePersistenceException(e.getCause());
		}
	}
	
	protected synchronized void removeLaunch(JSONLaunchParameter parameter) 
			throws ParseException, JSONException, IOException {
		configuredTasks.remove(parameter);
		writeOnConfigurationFile();
	}

	
	/**
	 * @return the configuredTasks
	 */
	public List<LaunchParameter> getConfiguredTasks() {
		return configuredTasks;
	}

	/**
	 * @param configuredTasks the configuredTasks to set
	 */
	public void setConfiguredTasks(List<LaunchParameter> configuredTasks) {
		this.configuredTasks = configuredTasks;
	}

	/** {@inheritDoc} */
	@Override
	public List<LaunchParameter> getAvailableScheduledTasks()
			throws SchedulePersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void reserveScheduledTask(UUID uuid, String consumerID)
			throws SchedulePersistenceException {
		// TODO Auto-generated method stub
	}

	/** {@inheritDoc} */
	@Override
	public void removeScheduledTask(UUID uuid)
			throws SchedulePersistenceException {
		// TODO Auto-generated method stub
	}

	/** {@inheritDoc} */
	@Override
	public void releaseScheduledTask(UUID uuid)
			throws SchedulePersistenceException {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.gcube.vremanagement.executor.configuration.ScheduledTaskConfiguration#getScheduledTask(java.util.UUID)
	 */
	@Override
	public LaunchParameter getScheduledTask(UUID uuid)
			throws SchedulePersistenceException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
