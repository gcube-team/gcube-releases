package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
//@XmlSeeAlso({SMComputationRequest.class,SMCreateTableRequest.class,SMGetFilesRequest.class,SMImportersRequest.class})
@XmlRootElement(namespace = TYPES_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)

public abstract class SMPagedRequest {

	@Override
	public String toString() {
		return "SMPagedRequest [user=" + theuser + ", page=" + page
				+ ", pageSize=" + pageSize + "]";
	}

	@XmlElement(namespace = TYPES_NAMESPACE, name="user")
	private String theuser;

	@XmlElement(namespace = TYPES_NAMESPACE)
	private int page;

	@XmlElement(namespace = TYPES_NAMESPACE)
	private int pageSize;

	  public SMPagedRequest() {
	    }

	    public SMPagedRequest(
	           int page,
	           int pageSize,
	           String user) {
	           this.theuser = user;
	           this.page = page;
	           this.pageSize = pageSize;
	    }


	public String user() {
		return theuser;

	}

	public void user(String user) {

		this.theuser = user;
	}

	public int page() {
		return page;

	}

	public void page(int page) {
		this.page = page;
	}
	
	public void pageSize(int pageSize)
	{
		this.pageSize=pageSize;
	}
	
	public int pageSize()
	{
		return pageSize;
	}
	
	
}