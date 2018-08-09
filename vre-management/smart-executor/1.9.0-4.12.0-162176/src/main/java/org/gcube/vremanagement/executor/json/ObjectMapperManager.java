/**
 * 
 */
package org.gcube.vremanagement.executor.json;

import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.ExternalServiceInfo;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTask;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ObjectMapperManager {
	
	static{
		
		SEMapper.getObjectMapper().registerSubtypes(ScheduledTask.class);
		
		SEMapper.getObjectMapper().addMixIn(ClientInfo.class, ClientInfoMixIn.class);
		SEMapper.getObjectMapper().registerSubtypes(UserInfo.class);
		SEMapper.getObjectMapper().registerSubtypes(ServiceInfo.class);
		SEMapper.getObjectMapper().registerSubtypes(ExternalServiceInfo.class);
		SEMapper.getObjectMapper().registerSubtypes(ContainerInfo.class);
		
	}

	public static ObjectMapper getObjectMapper() {
		return SEMapper.getObjectMapper();
	}
	
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property=SEMapper.CLASS_PROPERTY)
	class ClientInfoMixIn {
		
	}

}
