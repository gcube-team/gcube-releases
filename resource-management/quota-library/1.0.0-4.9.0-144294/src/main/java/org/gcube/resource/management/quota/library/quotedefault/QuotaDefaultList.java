package org.gcube.resource.management.quota.library.quotedefault;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "QuotaList")
@XmlAccessorType (XmlAccessType.FIELD)
public class QuotaDefaultList
{
	@XmlElementRefs({
	        @XmlElementRef(type = StorageQuotaDefault.class),
	        @XmlElementRef(type = ServiceQuotaDefault.class)
	    })
	private List<QuotaDefault> quotaList= null;

	public List<QuotaDefault> getQuotaDefaultList() {
		return quotaList;
	}
	public void setQuotaDefaultList(List<QuotaDefault> quotaList) {
		this.quotaList = quotaList;
	}
	
}
