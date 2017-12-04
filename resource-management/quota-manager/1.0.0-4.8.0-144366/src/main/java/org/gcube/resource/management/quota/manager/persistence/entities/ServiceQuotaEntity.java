package org.gcube.resource.management.quota.manager.persistence.entities;


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.gcube.resource.management.quota.library.quotalist.AccessType;
import org.gcube.resource.management.quota.library.quotalist.CallerType;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;


/**
 * ServiceQuotaEntity 
 * composed:
 * id: 				identifier quota
 * context: 		for specificy quota
 * identifier:		identifiy name of user/service/role 
 * callerType:  	caller type:USER(1),ROLE(2),SERVICE(3);   
 * managerType:		STORAGE,SERVICE,PORTLET     	
 * timeInterval:	DAILY,MONTHLY,YEARLY
 * quotaValue:  	value of quota
 * creationTime:	time of insert into db and creation Quota
 * lastUpdateTime:	time last update into db
 * 
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 */
@Entity
@DiscriminatorValue(EntityConstants.SERVICE_QUOTA)
public class ServiceQuotaEntity extends QuotaEntity {


	protected ServiceQuotaEntity() {
		super();
	}
	@Column(nullable=true)
	private AccessType  accessType;
	@Column(nullable=true)
	private String servicePackageId;
	
	public ServiceQuotaEntity(String context ,String identifier,CallerType callerType ,String servicePackageId, TimeInterval timeInterval,Double quotaValue,AccessType accessType) {
		super(context,EntityConstants.SERVICE_QUOTA,identifier,callerType,timeInterval,quotaValue);
		this.accessType=accessType;
		this.servicePackageId=servicePackageId;
	}

	public ServiceQuotaEntity(String context ,String identifier,CallerType callerType , TimeInterval timeInterval,Double quotaValue) {
		super(context,EntityConstants.SERVICE_QUOTA,identifier,callerType,timeInterval,quotaValue);
		
	}
	
	
	public AccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	public String getServicePackageId() {
		return servicePackageId;
	}

	public void setServicePackageId(String servicePackageId) {
		this.servicePackageId = servicePackageId;
	}

	@Override
	public String toString() {
		return "ServiceQuotaEntity [accessType=" + accessType
				+ ", servicePackageId=" + servicePackageId + ", id=" + id
				+ ", context=" + context + ", identifier=" + identifier
				+ ", callerType=" + callerType + ", timeInterval="
				+ timeInterval + ", quotaValue=" + quotaValue
				+ ", creationTime=" + creationTime + ", lastUpdateTime="
				+ lastUpdateTime + "]";
	}

	

	
}
