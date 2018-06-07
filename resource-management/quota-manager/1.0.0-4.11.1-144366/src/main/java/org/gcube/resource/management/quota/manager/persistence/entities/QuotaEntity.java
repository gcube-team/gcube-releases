package org.gcube.resource.management.quota.manager.persistence.entities;


import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gcube.resource.management.quota.library.quotalist.CallerType;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;


/**
 * QuotaEntity 
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
@Inheritance
@DiscriminatorColumn(name="QuotaType")
@Table(name="Quota")
@NamedQueries({
	@NamedQuery(name="Quota.all", query="SELECT quota FROM QuotaEntity quota WHERE  "
			+ " quota.context=:context"),
		@NamedQuery(name="Quota.getByIdentifier", query="SELECT quota FROM QuotaEntity quota WHERE  "
					+ " quota.identifier=:identifier"),
					@NamedQuery(name="Quota.getSpecified", query="SELECT quota FROM QuotaEntity quota WHERE  "
							+ "quota.identifier=:identifier and "
							+ "quota.context=:context and "
							+ "quota.quotaType=:quotaType and "
							+ "quota.timeInterval=:timeInterval"
							)
})
public abstract class QuotaEntity {

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;

	@Column(nullable=true)
	protected String context;

	@Column(name="QuotaType")
	private String quotaType;
	
	@Column(nullable=true)
	protected String identifier;

	@Column(nullable=true)
	protected CallerType callerType;

	@Column(nullable=true)
	protected TimeInterval timeInterval;

	@Column(nullable= true)
	protected Double quotaValue;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable= true)
	protected Calendar creationTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable= true)
	protected Calendar lastUpdateTime;

	/*
	@Column(nullable= true)
	protected Double quotaUsageValue;
	*/
	
	
	protected QuotaEntity() {}

	public QuotaEntity(String context ,String quotaType, String identifier,CallerType callerType ,TimeInterval timeInterval,Double quotaValue) {
		super();
		this.quotaType=quotaType;
		this.context=context;
		this.callerType=callerType;
		this.identifier=identifier;
		//this.managerType=managerType;
		this.timeInterval=timeInterval;
		this.quotaValue=quotaValue;
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

	public String getIdentifier() {
		return identifier;
	}

	public CallerType getCallerType() {
		return callerType;
	}

	public TimeInterval getTimeInterval() {
		return timeInterval;
	}
	
	public Double getQuotaValue() {
		return quotaValue;
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
	public String getQuotaType() {
		return quotaType;
	}
	
	/*
	public Double getQuotaUsageValue(){
		return quotaUsageValue;
	}
	public void setQuotaUsageValue(Double usageValue){
		this.quotaUsageValue=usageValue;
	}
	*/
}
