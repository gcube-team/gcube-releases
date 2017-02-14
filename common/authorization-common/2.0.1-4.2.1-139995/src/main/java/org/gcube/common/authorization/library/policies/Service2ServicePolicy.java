package org.gcube.common.authorization.library.policies;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Service2ServicePolicy extends Policy{
	
	private ServiceEntity client;
	private String context;
	private Calendar lastUpdateTime;
	private Calendar creationTime;
	private ServiceAccess serviceAccess;
	private Action mode = Action.ALL;
	
	protected Service2ServicePolicy(){}
	
	public Service2ServicePolicy(String context, ServiceAccess serviceAccess,
			ServiceEntity client) {
		this.context = context;
		this.serviceAccess = serviceAccess;
		this.client = client;
	}
	
	public Service2ServicePolicy(String context, ServiceAccess serviceAccess,
			ServiceEntity client, Action mode) {
		this(context, serviceAccess, client);
		this.mode = mode;
	}
	
	@Override
	public PolicyType getPolicyType() {
		return PolicyType.SERVICE;
	}

	@Override
	public String getPolicyAsString() {
		return this.context+","+serviceAccess.getAsString()+","+client.getAsString()+","+mode.toString();
	}

	public ServiceEntity getClient() {
		return client;
	}

	public String getContext() {
		return context;
	}

	public ServiceAccess getServiceAccess() {
		return serviceAccess;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result
				+ ((serviceAccess == null) ? 0 : serviceAccess.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Service2ServicePolicy other = (Service2ServicePolicy) obj;
		if (client == null) {
			if (other.client != null)
				return false;
		} else if (!client.equals(other.client))
			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (mode != other.mode)
			return false;
		if (serviceAccess == null) {
			if (other.serviceAccess != null)
				return false;
		} else if (!serviceAccess.equals(other.serviceAccess))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Service2ServicePolicy [id="+getId()+" client=" + client + ", context="
				+ context + ", serviceAccess=" + serviceAccess + ", mode="
				+ mode + "]";
	}

	@Override
	public Action getMode() {
		return this.mode;
	}

	@Override
	public Calendar getCreationTime() {
		return creationTime;
	}

	@Override
	public Calendar getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Calendar lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public void setCreationTime(Calendar creationTime) {
		this.creationTime = creationTime;
	}

}
