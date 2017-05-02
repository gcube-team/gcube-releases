package org.gcube.common.authorization.library.policies;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User2ServicePolicy extends Policy {

	@XmlElementRefs({
		@XmlElementRef(type = User.class),
		@XmlElementRef(type = Role.class),
	})
	private UserEntity entity;
	private String context;
	private ServiceAccess serviceAccess;
		
	private Calendar lastUpdateTime;
	private Calendar creationTime;
	
	private Action mode = Action.ALL;
	
	protected User2ServicePolicy(){}
	
	public User2ServicePolicy(String context, ServiceAccess serviceAccess, UserEntity entity) {
		this.context = context;
		this.serviceAccess = serviceAccess;
		this.entity = entity;
	}

	public User2ServicePolicy(String context, ServiceAccess serviceAccess, UserEntity entity, Action mode) {
		this(context, serviceAccess, entity);
		this.mode = mode;
	}
	
	public UserEntity getEntity() {
		return entity;
	}
	
	public ServiceAccess getServiceAccess() {
		return serviceAccess;
	}

	@Override
	public PolicyType getPolicyType() {
		return PolicyType.USER;
	}

	@Override
	public String getPolicyAsString() {
		return this.context+","+serviceAccess.getAsString()+","+entity.getAsString()+","+mode.toString();
	}

	public String getContext() {
		return context;
	}
	
	public Calendar getLastUpdateTime() {
		return lastUpdateTime;
	}

	public Calendar getCreationTime() {
		return creationTime;
	}

	
	
	public void setLastUpdateTime(Calendar lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public void setCreationTime(Calendar creationTime) {
		this.creationTime = creationTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
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
		User2ServicePolicy other = (User2ServicePolicy) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
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
		return "User2ServicePolicy [entity=" + entity + ", context=" + context
				+ ", serviceAccess=" + serviceAccess + ", lastUpdateTime="
				+ lastUpdateTime + ", creationTime=" + creationTime + ", mode="
				+ mode + "]";
	}

	@Override
	public Action getMode() {
		return this.mode;
	}
	
}
