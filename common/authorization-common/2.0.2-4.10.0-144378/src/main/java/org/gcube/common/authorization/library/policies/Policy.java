package org.gcube.common.authorization.library.policies;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({User2ServicePolicy.class, Service2ServicePolicy.class})
public abstract class Policy {
	
	protected long id;
	
	protected Policy() {}
		
	public abstract PolicyType getPolicyType();
	
	public abstract String getPolicyAsString();
	
	public abstract ServiceAccess getServiceAccess();
	
	public abstract Calendar getCreationTime();
	
	public abstract Calendar getLastUpdateTime();
	
	public abstract void setCreationTime(Calendar creationTime);
	
	public abstract void setLastUpdateTime(Calendar lastUpdateTime);
	
	public abstract String getContext();
	
	public abstract Action getMode();
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
