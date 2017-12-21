/**
 * 
 */
package org.gcube.vremanagement.executor.json;

import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.ExternalServiceInfo;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.plugin.Ref;
import org.gcube.vremanagement.executor.plugin.RunOn;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTask;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ObjectMapperManager {
	
	protected static final ObjectMapper mapper;
	
	static{
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		mapper.registerSubtypes(ScheduledTask.class);
		mapper.registerSubtypes(RunOn.class);
		mapper.registerSubtypes(Ref.class);
		
		mapper.registerSubtypes(PluginDeclaration.class);
		mapper.registerSubtypes(PluginStateEvolution.class);
		
		mapper.addMixIn(ClientInfo.class, ClientInfoMixIn.class);
		//mapper.registerSubtypes(ClientInfo.class);
		mapper.registerSubtypes(UserInfo.class);
		mapper.registerSubtypes(ServiceInfo.class);
		mapper.registerSubtypes(ExternalServiceInfo.class);
		mapper.registerSubtypes(ContainerInfo.class);
		
		
		
	}

	public static ObjectMapper getObjectMapper() {
		return mapper;
	}
	
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property=Scheduling.CLASS_PROPERTY)
	class ClientInfoMixIn {
		
	}

}
