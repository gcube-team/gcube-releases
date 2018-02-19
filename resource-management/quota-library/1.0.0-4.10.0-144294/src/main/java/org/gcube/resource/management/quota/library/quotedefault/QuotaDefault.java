package org.gcube.resource.management.quota.library.quotedefault;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.resource.management.quota.library.quotalist.CallerType;
import org.gcube.resource.management.quota.library.quotalist.QuotaType;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;

/**
 *
 * @author Alessandro Pieve at ISTI-CNR 
 * (alessandro.pieve@isti.cnr.it)
 * ex: 
 *<StorageQuotaDefault>
 *	<callerType>USER</callerType>
 *   <quotaValue>1.5</quotaValue>
 *   <timeInterval>DAILY</timeInterval>
 *</StorageQuotaDefault>
 */

@XmlRootElement(name = "QuotaDefault")
@XmlSeeAlso({ServiceQuotaDefault.class, StorageQuotaDefault.class})
public abstract class QuotaDefault {
	
	protected QuotaDefault() {}
		
	public abstract QuotaType getQuotaType();
	
	public abstract String getQuotaAsString();
	
	public abstract CallerType getCallerType();
		
	public abstract TimeInterval getTimeInterval();
	
	public abstract Double getQuotaValue();

}
