package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.BulkStatus;

@XmlRootElement
public class GetBulkUpdatesStatusResponseType {

	@XmlElement
	private BulkStatus status;
	@XmlElement
	private String rsLocator;
	
	public GetBulkUpdatesStatusResponseType() {
		// TODO Auto-generated constructor stub
	}

	public GetBulkUpdatesStatusResponseType(BulkStatus status, String rsLocator) {
		super();
		this.status = status;
		this.rsLocator = rsLocator;
	}

	/**
	 * @return the status
	 */
	public BulkStatus status() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void status(BulkStatus status) {
		this.status = status;
	}

	/**
	 * @return the rsLocator
	 */
	public String rsLocator() {
		return rsLocator;
	}

	/**
	 * @param rsLocator the rsLocator to set
	 */
	public void rsLocator(String rsLocator) {
		this.rsLocator = rsLocator;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GetBulkUpdatesResponse [status=");
		builder.append(status);
		builder.append(", rsLocator=");
		builder.append(rsLocator);
		builder.append("]");
		return builder.toString();
	}
	
	
}
