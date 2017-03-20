package org.gcube.common.authorizationservice.persistence.entities;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.gcube.common.authorization.library.policies.Action;
import org.gcube.common.authorization.library.policies.ServiceAccess;

@Entity
@Inheritance
@DiscriminatorColumn(name="PolicyType")
@Table(name="Policies")
@NamedQuery(name="Policy.allPolicies", query="SELECT policy FROM PolicyEntity policy WHERE policy.context = :context")
public abstract class PolicyEntity {

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;
	
	@Column(nullable=false)
	private String context;
	
	@Enumerated(EnumType.ORDINAL)
    protected ExcludeType excludeType =ExcludeType.NOTEXCLUDE ;
	
	@Column(name="PolicyType")
	private String policyType;
	
	@Transient
	private ServiceAccess serviceAccess;
	
	@Column(nullable=true)
	private String accessServiceClass;
	
	@Column(nullable=true)
	private String accessServiceName;
	
	@Column(nullable=true)
	private String accessServiceIdentifier;
		
	@Column(nullable= false)
	private Action action;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable= false)
	private Calendar creationTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable= false)
	private Calendar lastUpdateTime;
	
	protected PolicyEntity() {}

	public PolicyEntity(String context, ServiceAccess serviceAccess, String policyType, Action action) {
		super();
		this.context = context;
		this.serviceAccess = serviceAccess;
		
		if (serviceAccess.getServiceClass()!=null){
			this.accessServiceClass= serviceAccess.getServiceClass();
			if (serviceAccess.getName()!=null){
				this.accessServiceName = serviceAccess.getName();
				if (serviceAccess.getServiceId()!=null)
					this.accessServiceIdentifier = serviceAccess.getServiceId();
			}
		}
		
		this.policyType = policyType;
		this.action = action;
		Calendar now = Calendar.getInstance();
		this.creationTime = now;
		this.lastUpdateTime = now;
	}

	public long getId() {
		return id;
	}

	public String getContext() {
		return context;
	}

	public ServiceAccess getServiceAccess() {
		if (serviceAccess==null)
			serviceAccess = new ServiceAccess(accessServiceName, accessServiceClass, accessServiceIdentifier);
		return serviceAccess;
	}

	public String getPolicyType() {
		return policyType;
	}

	public Action getAction() {
		return action;
	}
	
	public Calendar getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Calendar lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Calendar getCreationTime() {
		return creationTime;
	}

	public abstract boolean isRewritable();

	@Override
	public String toString() {
		return "PolicyEntity [id=" + id + ", context=" + context
				+ ", policyType=" + policyType + ", serviceAccess="
				+ serviceAccess + ", accessServiceClass=" + accessServiceClass
				+ ", accessServiceName=" + accessServiceName
				+ ", accessServiceIdentifier=" + accessServiceIdentifier
				+ ", action=" + action + "]";
	}
	
	
}
