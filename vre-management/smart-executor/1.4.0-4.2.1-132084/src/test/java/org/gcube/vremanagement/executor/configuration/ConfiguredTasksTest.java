/**
 * 
 */
package org.gcube.vremanagement.executor.configuration;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.HelloWorldPlugin;
import org.acme.HelloWorldPluginDeclaration;
import org.gcube.vremanagement.executor.TokenBasedTests;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.configuration.jsonbased.FileScheduledTaskConfiguration;
import org.gcube.vremanagement.executor.configuration.jsonbased.JSONLaunchParameter;
import org.gcube.vremanagement.executor.exception.SchedulePersistenceException;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class ConfiguredTasksTest extends TokenBasedTests {

	private static Logger logger = LoggerFactory.getLogger(ConfiguredTasksTest.class);
	
	public static final String TEST = "test";
	
	
	public void checkOriginal(FileScheduledTaskConfiguration parser, int size){
		List<LaunchParameter> configuredTasks = parser.getConfiguredTasks();
		Assert.assertEquals(size, configuredTasks.size());
		
		JSONLaunchParameter parameter = (JSONLaunchParameter) configuredTasks.get(0);
		Assert.assertEquals(HelloWorldPluginDeclaration.NAME, parameter.getPluginName());
		Map<String, Object> inputs = parameter.getInputs();
		Assert.assertEquals(1000, inputs.get(HelloWorldPlugin.SLEEP_TIME));
		Assert.assertEquals(1, inputs.get(TEST));
		Assert.assertEquals(null, parameter.getScheduling());
		
		parameter = (JSONLaunchParameter) configuredTasks.get(1);
		Assert.assertEquals(parameter.getPluginName(), HelloWorldPluginDeclaration.NAME);
		inputs = parameter.getInputs();
		Assert.assertEquals(1000, inputs.get(HelloWorldPlugin.SLEEP_TIME));
		Assert.assertEquals(2, inputs.get(TEST));
		Scheduling scheduling = parameter.getScheduling();
		Assert.assertEquals(null, scheduling.getCronExpression());
		Assert.assertEquals(new Integer(2000), scheduling.getDelay());
		Assert.assertEquals(2, scheduling.getSchedulingTimes());
		Assert.assertEquals(null, scheduling.getFirtStartTime());
		Assert.assertEquals(null, scheduling.getEndTime());
		Assert.assertEquals(false, scheduling.mustPreviousExecutionsCompleted());
		Assert.assertEquals(true, scheduling.getGlobal());
		
		parameter = (JSONLaunchParameter) configuredTasks.get(2);
		Assert.assertEquals(parameter.getPluginName(), HelloWorldPluginDeclaration.NAME);
		inputs = parameter.getInputs();
		Assert.assertEquals(1000, inputs.get(HelloWorldPlugin.SLEEP_TIME));
		Assert.assertEquals(3, inputs.get(TEST));
		Assert.assertEquals(null, parameter.getScheduling());
		Assert.assertEquals(true, scheduling.getGlobal());
	}
	
	public static final String TASK_FILE_PATH = "/src/test/resources/";
	
	@Test
	public void testLaunchConfiguredTask() throws SchedulePersistenceException, IOException, JSONException, ParseException {
		File file = new File(".", TASK_FILE_PATH);
		String location = file.getAbsolutePath();
		logger.trace("File location : {}", location);
		FileScheduledTaskConfiguration parser = new FileScheduledTaskConfiguration(location);
		
		checkOriginal(parser, 3);
		
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put(HelloWorldPlugin.SLEEP_TIME, 1000);
		inputs.put(TEST, 4);
		JSONLaunchParameter added = new JSONLaunchParameter(HelloWorldPluginDeclaration.NAME, inputs);
		parser.addLaunch(added);
		
		parser = new FileScheduledTaskConfiguration(location);
		checkOriginal(parser, 4);
		
		List<LaunchParameter> configuredTasks = parser.getConfiguredTasks();
		JSONLaunchParameter parameter = (JSONLaunchParameter) configuredTasks.get(3);
		Assert.assertEquals(parameter.getPluginName(), HelloWorldPluginDeclaration.NAME);
		inputs = parameter.getInputs();
		Assert.assertEquals(1000, inputs.get(HelloWorldPlugin.SLEEP_TIME));
		Assert.assertEquals(4, inputs.get(TEST));
		Assert.assertEquals(null, parameter.getScheduling());
		
		parser.releaseLaunch(parameter);
		
		parser = new FileScheduledTaskConfiguration(location);
		checkOriginal(parser, 3);
		
	}
}
