package org.gcube.resource.management.quota.library.quotalist;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;


@XmlRootElement(name = "quota")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ServiceQuota.class, StorageQuota.class})

public abstract class Quota {

protected long id;
	
	protected Quota() {}
		
	public abstract QuotaType getQuotaType();
	
	public abstract String getQuotaAsString();
	
	public abstract String getIdentifier();
	
	public abstract String getContext();

	public abstract CallerType getCallerType();
		
	public abstract TimeInterval getTimeInterval();
	
	public abstract Double getQuotaValue();
	
	public abstract Calendar getCreationTime();
	
	public abstract Calendar getLastUpdateTime();
	
	public abstract void setCreationTime(Calendar creationTime);
	
	public abstract void setLastUpdateTime(Calendar lastUpdateTime);
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	
	
}
