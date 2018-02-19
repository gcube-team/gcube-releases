package org.gcube.datatransfer.scheduler.db.model;


import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;




@PersistenceCapable(table="AGENT")
public class Agent implements java.io.Serializable {


	private static final long serialVersionUID = -94229392796085517L;

	@PrimaryKey
	private String agentId;
	
	private String agentIdOfIS;
	private String host;
	private int port;
	private String agentEpr;
	
	public Agent(){
		this.host=null;
		this.agentEpr=null;
	}

	//@Id
	//@Column(name = "AGENT_ID")
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getAgentEpr() {
		return agentEpr;
	}
	public void setAgentEpr(String agentEpr) {
		this.agentEpr = agentEpr;
	}
	
	public String getAgentIdOfIS() {
		return agentIdOfIS;
	}

	public void setAgentIdOfIS(String agentIdOfIS) {
		this.agentIdOfIS = agentIdOfIS;
	}

}
