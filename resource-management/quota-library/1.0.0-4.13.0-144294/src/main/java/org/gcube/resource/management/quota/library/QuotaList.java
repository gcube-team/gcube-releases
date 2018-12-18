package org.gcube.resource.management.quota.library;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.resource.management.quota.library.quotalist.Quota;

@XmlRootElement(name = "quote")
@XmlAccessorType (XmlAccessType.FIELD)

public class QuotaList
{

	@XmlElement(name = "quota")
	private List<Quota> quotaList;

	@SuppressWarnings("unused")
	private QuotaList(){}

	public QuotaList(List<Quota> quotaList) {
		super();
		this.quotaList = quotaList;
	}
	
	public List<Quota> getQuotaList() {
		return quotaList;
	}

}
