package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
@XmlSeeAlso({SMImport.class,SMComputation.class,SMSystemImport.class,})
public abstract class SMOperation {

	@XmlElement(namespace = TYPES_NAMESPACE)
	private int operationType;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private long operationId;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String portalLogin;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private Calendar submissionDate;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private Calendar completedDate;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private int operationStatus;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String description;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private SMAbstractResource abstractResource;

	public SMOperation() {
	}

	public SMOperation(SMAbstractResource abstractResource,
			Calendar completedDate, String description, long operationId,
			int operationStatus, int operationType, String portalLogin,
			Calendar submissionDate) {
		this.operationType = operationType;
		this.operationId = operationId;
		this.portalLogin = portalLogin;
		this.submissionDate = submissionDate;
		this.completedDate = completedDate;
		this.operationStatus = operationStatus;
		this.description = description;
		this.abstractResource = abstractResource;
	}

	public int operationType() {
		return operationType;
	}

	public void operationType(int operationType) {
		this.operationType = operationType;
	}

	public long operationId() {
		return operationId;
	}

	public void operationId(long operationId) {
		this.operationId = operationId;
	}

	public String portalLogin() {
		return portalLogin;
	}

	public void portalLogin(String portalLogin) {
		this.portalLogin = portalLogin;
	}

	public Calendar submissionDate() {
		return submissionDate;
	}

	public void submissionDate(Calendar submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Calendar completedDate() {
		return completedDate;
	}

	public void completedDate(Calendar completedDate) {
		this.completedDate = completedDate;
	}

	public int operationStatus() {
		return operationStatus;
	}

	public void oerationStatus(int operationStatus) {
		this.operationStatus = operationStatus;
	}

	public String description() {
		return description;
	}

	public void description(String description) {
		this.description = description;
	}

	public SMAbstractResource abstractResource() {
		return abstractResource;
	}

	public void astractResource(SMAbstractResource abstractResource) {
		this.abstractResource = abstractResource;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMOperation [operationType=");
		builder.append(operationType);
		builder.append(", operationId=");
		builder.append(operationId);
		builder.append(", portalLogin=");
		builder.append(portalLogin);
		builder.append(", submissionDate=");
		builder.append(submissionDate);
		builder.append(", completedDate=");
		builder.append(completedDate);
		builder.append(", operationStatus=");
		builder.append(operationStatus);
		builder.append(", description=");
		builder.append(description);
		builder.append(", abstractResource=");
		builder.append(abstractResource);
		builder.append("]");
		return builder.toString();
	}

	
	
}
