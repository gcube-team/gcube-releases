package org.gcube.resource.management.quota.manager.persistence.entities;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.gcube.resource.management.quota.library.quotalist.CallerType;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;


/**
 * StorageQuotaEntity 
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
@DiscriminatorValue(EntityConstants.STORAGE_QUOTA)
public class StorageQuotaEntity extends QuotaEntity {


	protected StorageQuotaEntity() {
		super();
	}
		
	public StorageQuotaEntity(String context ,String identifier,CallerType callerType ,TimeInterval timeInterval,Double quotaValue) {
		super(context,EntityConstants.STORAGE_QUOTA,identifier,callerType,timeInterval,quotaValue);
		
	}

	@Override
	public String toString() {
		return "StorageQuotaEntity [id=" + id + ", context=" + context
				+ ", identifier=" + identifier + ", callerType=" + callerType
				+ ", timeInterval=" + timeInterval + ", quotaValue="
				+ quotaValue + ", creationTime=" + creationTime
				+ ", lastUpdateTime=" + lastUpdateTime + "]";
	}

	

	
}
