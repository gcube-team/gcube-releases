package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import java.util.Collection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Ghn {

	
	public static final String ID_FIELDNAME="id";
	
	
	@DatabaseField(id = true,width = 60, columnName=ID_FIELDNAME)
	private String id;
	
	
	@DatabaseField(canBeNull=false)
	private String host;
	
	@DatabaseField(canBeNull=false)
	private String location;
	
	@DatabaseField(canBeNull=false)
	private String country;
	
	@DatabaseField(canBeNull=false)
	private String domain;
	
	@DatabaseField(canBeNull=false)
	private long memoryAvailable;
	
	@DatabaseField(canBeNull=false)
	private long diskSpace;
	
	@DatabaseField(canBeNull=false)
	private boolean onCloud;
	
	@DatabaseField
	private boolean securityEnabled = false;
	
	Ghn(){}
	
	
	
	public Ghn(String id, String host, String location, String country,
			String domain, long memoryAvailable, long diskSpace, boolean onCloud) {
		super();
		this.id = id;
		this.host = host;
		this.location = location;
		this.country = country;
		this.domain = domain;
		this.memoryAvailable = memoryAvailable;
		this.diskSpace = diskSpace;
		this.onCloud = onCloud;
	}

	

	@ForeignCollectionField(eager = true, columnName="runningInstances")
	private Collection<RunningInstance> runningInstances;
	
	@ForeignCollectionField(eager = false)
	private Collection<VreGhnRelation> vreRelation;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public boolean isOnCloud() {
		return onCloud;
	}

	public void setOnCloud(boolean onCloud) {
		this.onCloud = onCloud;
	}

	public Collection<RunningInstance> getRunningInstances() {
		return runningInstances;
	}

	public void setRunningInstances(
			Collection<RunningInstance> runningInstances) {
		this.runningInstances = runningInstances;
	}

	

	public void setVreRelation(Collection<VreGhnRelation> vreRelation) {
		this.vreRelation = vreRelation;
	}



	public Collection<VreGhnRelation> getVreRelation() {
		return vreRelation;
	}

	
	public long getMemoryAvailable() {
		return memoryAvailable;
	}



	public void setMemoryAvailable(long memoryAvailable) {
		this.memoryAvailable = memoryAvailable;
	}



	public long getDiskSpace() {
		return diskSpace;
	}



	public void setDiskSpace(long diskSpace) {
		this.diskSpace = diskSpace;
	}

	

	public boolean isSecurityEnabled() {
		return securityEnabled;
	}



	public void setSecurityEnabled(boolean securityEnabled) {
		this.securityEnabled = securityEnabled;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Ghn other = (Ghn) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
