package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
@XmlRootElement(namespace = TYPES_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({SMFile.class,SMObject.class, SMError.class, SMTable.class})
public abstract class SMResource {

	@XmlElement(namespace = TYPES_NAMESPACE)
	private String resourceId;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private int resourceType;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String portalLogin;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private long operationId;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String description;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String name;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private int provenance;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private Calendar creationDate;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String algorithm;

	public SMResource() {
//		super();
	}

	public SMResource(String algorithm, Calendar creationDate,
			String description, String name, long operationId,
			String portalLogin, int provenance, String resourceId,
			int resourceType) {


		this.resourceId = resourceId;
		this.resourceType = resourceType;
		this.portalLogin = portalLogin;
		this.operationId = operationId;
		this.description = description;
		this.name = name;
		this.provenance = provenance;
		this.creationDate = creationDate;
		this.algorithm = algorithm;
	}

	public void resourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String resourceId() {
		return resourceId;
	}

	public void resourceType(int resourceType) {
		this.resourceType = resourceType;
	}

	public int resourceType() {
		return resourceType;
	}

	public void portalLogin(String portalLogin) {
		this.portalLogin = portalLogin;
	}

	public String portalLogin() {
		return portalLogin;
	}

	public void operationId(long operationId) {
		this.operationId = operationId;
	}

	public long operationId() {
		return operationId;
	}

	public void description(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public void name(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	public void provenance(int provenance) {
		this.provenance = provenance;
	}

	public int provenance() {
		return provenance;
	}

	public void creationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Calendar creationDate() {
		return creationDate;
	}

	public void algorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String algorithm() {
		return algorithm;
	}

	@Override
	public String toString() {
		return "SMResource [resourceId=" + resourceId + ", resourceType="
				+ resourceType + ", portalLogin=" + portalLogin
				+ ", operationId=" + operationId + ", description="
				+ description + ", name=" + name + ", provenance=" + provenance
				+ ", creationDate=" + creationDate + ", algorithm=" + algorithm
				+ "]";
	}
   
	@Deprecated
	public boolean __hashCodeCalc = false;

	
}
