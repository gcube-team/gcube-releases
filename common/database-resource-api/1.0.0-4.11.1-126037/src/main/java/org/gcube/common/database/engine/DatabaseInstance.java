package org.gcube.common.database.engine;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.database.endpoint.DatabaseEndpoint;

public class DatabaseInstance {
	
	private String id;
	
	private Map<String, DatabaseEndpoint> endpoints = new HashMap<String, DatabaseEndpoint>();
	
	private Platform platform;
	
	private HostingNode node;
	
	public DatabaseInstance() {
	}
	
	public DatabaseInstance(String id, Map<String, DatabaseEndpoint> endpoints, Platform platform,
			HostingNode node) {
		super();
		this.id = id;
		this.endpoints = endpoints;
		this.platform = platform;
		this.node = node;
	}

	/** Main getter/setter methods **/
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	

	public Map<String, DatabaseEndpoint> getEndpoints() {
		return endpoints;
	}

	public Platform getPlatform() {
		return platform;
	}

	public HostingNode getNode() {
		return node;
	}

	public void setEndpoints(Map<String, DatabaseEndpoint> endpoints) {
		this.endpoints = endpoints;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public void setNode(HostingNode node) {
		this.node = node;
	}

	/** Delegate methods **/
	public String getName() {
		return platform.getName();
	}

	public Short getVersion() {
		return platform.getVersion();
	}

	public Short getMinorVersion() {
		return platform.getMinorVersion();
	}

	public Short getRevisionVersion() {
		return platform.getRevisionVersion();
	}

	public Short getBuildVersion() {
		return platform.getBuildVersion();
	}

	public String getHostingURL() {
		return node.getHostingURL();
	}

	public String getGhnUniqueId() {
		return node.getGhnUniqueId();
	}

	public String getStatus() {
		return node.getStatus();
	}
	
	/** Custom methods **/
	
	public DatabaseEndpoint getEndpoint(String endpointId){
		return endpoints.get(endpointId);
	}
	
	public void addEndpoint(DatabaseEndpoint endpoint){
		endpoints.put(endpoint.getId(),endpoint);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DatabaseInstance [id=");
		builder.append(id);
		builder.append(", endpoints=");
		builder.append(endpoints);
		builder.append(", platform=");
		builder.append(platform);
		builder.append(", node=");
		builder.append(node);
		builder.append("]");
		return builder.toString();
	}

}
