/**
 * 
 */
package org.gcube.vremanagement.executor;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.acme.HelloWorldPluginDeclaration;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.exception.InvalidPluginStateEvolutionException;
import org.gcube.vremanagement.executor.json.SEMapper;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.plugin.Ref;
import org.gcube.vremanagement.executor.plugin.RunOn;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTask;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class SerializationTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(SerializationTest.class);
	
	@Test
	public void testScheduling() throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Hello", "World");
		long sleepTime = 10000;
		inputs.put("sleepTime", sleepTime);
		
		Scheduling scheduling = new Scheduling(20);
		scheduling.setGlobal(true);
		
		LaunchParameter launchParameter = new LaunchParameter("HelloWorld", inputs, scheduling);
		logger.debug("{} to be Marshalled : {}", launchParameter.getClass().getSimpleName(), launchParameter);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String launchParameterJSONString = objectMapper.writeValueAsString(launchParameter);
		logger.debug("Marshalled : {}", launchParameterJSONString);
		
		LaunchParameter launchParameterUnmarshalled = objectMapper.readValue(launchParameterJSONString, LaunchParameter.class);
        logger.debug("UnMarshalled : {}", launchParameterUnmarshalled);
	}
	
	@Test
	public void testScheduledTask() throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Hello", "World");
		long sleepTime = 10000;
		inputs.put("sleepTime", sleepTime);
		
		Scheduling scheduling = new Scheduling(20);
		scheduling.setGlobal(true);
		
		LaunchParameter launchParameter = new LaunchParameter("HelloWorld", inputs, scheduling);
		UUID uuid = UUID.randomUUID();
		Ref hostingNode = new Ref(UUID.randomUUID().toString(), "localhost");
		Ref eService = new Ref(UUID.randomUUID().toString(), "localhost");
		RunOn runOn = new RunOn(hostingNode, eService);
		ScheduledTask scheduledTask = new ScheduledTask(uuid, launchParameter, runOn);
		logger.debug("{} to be Marshalled : {}", scheduledTask.getClass().getSimpleName(), launchParameter);
		
		
		ObjectMapper mapper = SEMapper.getObjectMapper();
		String scheduledTaskJSONString = mapper.writeValueAsString(scheduledTask);
		logger.debug("Marshalled : {}", scheduledTaskJSONString);
		
		ScheduledTask scheduledTaskUnmarshalled = mapper.readValue(scheduledTaskJSONString, ScheduledTask.class);
        logger.debug("UnMarshalled : {}", scheduledTaskUnmarshalled);
        
        
        
        
	}
	
	@Test
	public void testPluginEvolutionState() throws JsonGenerationException, JsonMappingException, IOException, InvalidPluginStateEvolutionException {
		
		PluginStateEvolution pes = new PluginStateEvolution(UUID.randomUUID(), 1, Calendar.getInstance().getTimeInMillis(), new HelloWorldPluginDeclaration(), PluginState.RUNNING, 10);
		logger.debug("{} to be Marshalled : {}", pes.getClass().getSimpleName(), pes);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String scheduledTaskJSONString = objectMapper.writeValueAsString(pes);
		logger.debug("Marshalled : {}", scheduledTaskJSONString);
		
		PluginStateEvolution pesUnmarshalled = objectMapper.readValue(scheduledTaskJSONString, PluginStateEvolution.class);
        logger.debug("UnMarshalled : {}", pesUnmarshalled);
	}
		
}
