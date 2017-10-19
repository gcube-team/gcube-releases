/**
 * 
 */
package org.gcube.vremanagement.executor.scheduledtask;

import java.util.UUID;

import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.smartgears.Constants;
import org.gcube.smartgears.ContextProvider;
import org.gcube.vremanagement.executor.SmartExecutorInitializator;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.plugin.Ref;
import org.gcube.vremanagement.executor.plugin.RunOn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property=Scheduling.CLASS_PROPERTY)
public class ScheduledTask {
	
	public static final String LAUNCH_PARAMETER = "launchParameter";
	
	protected UUID uuid;
	@JsonProperty(value=LAUNCH_PARAMETER)
	protected LaunchParameter launchParameter;
	
	protected String scope;
	protected String token;
	protected ClientInfo clientInfo;
	
	protected RunOn runOn;
		
	protected ScheduledTask(){}
	
	public ScheduledTask(UUID uuid, LaunchParameter launchParameter) {
		this(uuid, launchParameter, generateRunOn());
	}
	
	public ScheduledTask(UUID uuid, LaunchParameter launchParameter, RunOn runOn) {
		this.uuid = uuid;
		this.launchParameter = launchParameter;
		this.token = SecurityTokenProvider.instance.get();
		this.scope = SmartExecutorInitializator.getCurrentScope();
		this.clientInfo = SmartExecutorInitializator.getClientInfo();
		this.runOn = runOn;
	}
	
	/**
	 * @return the uuid
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}
	
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @return the clientInfo
	 */
	public ClientInfo getClientInfo() {
		return clientInfo;
	}
	
	/**
	 * @return the runOn
	 */
	public RunOn getRunOn() {
		return runOn;
	}
	
	
	public static final String LOCALHOST = "localhost";
	
	/**
	 * @param runOn the runOn to set
	 */
	public static RunOn generateRunOn() {
		Ref hostingNodeRef = null;
		try {
			HostingNode hostingNode = ContextProvider.get().container().profile(HostingNode.class);
			hostingNodeRef = new Ref(hostingNode.id(), hostingNode.profile().description().name());
		}catch (Exception e) {
			// 
			hostingNodeRef = new Ref(LOCALHOST, LOCALHOST);
		}
		
		Ref eServiceRef = null;
		try {
			GCoreEndpoint gCoreEndpoint = ContextProvider.get().profile(GCoreEndpoint.class);
			String address = "";
			Group<Endpoint> endpoints = gCoreEndpoint.profile().endpoints();
			for(Endpoint endpoint : endpoints){
				if(endpoint.name().contains(Constants.remote_management)){
					continue;
				}else{
					address = endpoint.uri().toString();
					break;
				}
			}
			
			eServiceRef = new Ref(gCoreEndpoint.id(), address);
		}catch (Exception e) {
			eServiceRef = new Ref(LOCALHOST, LOCALHOST);
		}
		
		RunOn runOn = new RunOn(hostingNodeRef, eServiceRef);
		
		return runOn;
	}

	/**
	 * @return the launchParameter
	 */
	public LaunchParameter getLaunchParameter(){
		return launchParameter;
	}
	
}
