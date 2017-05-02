/**
 * 
 */
package org.gcube.vremanagement.executor.plugin;

import org.gcube.vremanagement.executor.api.types.Scheduling;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property=Scheduling.CLASS_PROPERTY)
@JsonTypeName(value="RunOn")
public class RunOn {
	
	@JsonProperty
	protected Ref hostingNode;
	
	@JsonProperty
	protected Ref eService;
	
	public RunOn(){
		
	}
	
	public RunOn(Ref hostingNode, Ref eService){
		this.hostingNode = hostingNode;
		this.eService = hostingNode;
	}
	
	/**
	 * @return the hostingNodeID
	 */
	public Ref getHostingNode() {
		return hostingNode;
	}

	/**
	 * @param hostingNodeID the hostingNodeID to set
	 */
	public void setHostingNode(Ref hostingNode) {
		this.hostingNode = hostingNode;
	}

	/**
	 * @return the eServiceID
	 */
	public Ref getEService() {
		return eService;
	}

	/**
	 * @param eServiceID the eServiceID to set
	 */
	public void setEService(Ref eService) {
		this.eService = eService;
	}

}
