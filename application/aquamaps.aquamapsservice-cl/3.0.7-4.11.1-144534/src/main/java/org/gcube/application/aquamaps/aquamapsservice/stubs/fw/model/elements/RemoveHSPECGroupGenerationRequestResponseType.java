package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RemoveHSPECGroupGenerationRequestResponseType {
	
	@XmlElement
	private String requestId;
	@XmlElement
	private boolean removeTables;
	@XmlElement
	private boolean removeJobs;
	
	public RemoveHSPECGroupGenerationRequestResponseType() {
		// TODO Auto-generated constructor stub
	}

	
	
	public RemoveHSPECGroupGenerationRequestResponseType(String requestId, boolean removeTables,
			boolean removeJobs) {
		super();
		this.requestId = requestId;
		this.removeTables = removeTables;
		this.removeJobs = removeJobs;
	}



	/**
	 * @return the requestId
	 */
	public String requestId() {
		return requestId;
	}

	/**
	 * @param requestId the requestId to set
	 */
	public void requestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the removeTables
	 */
	public boolean removeTables() {
		return removeTables;
	}

	/**
	 * @param removeTables the removeTables to set
	 */
	public void removeTables(boolean removeTables) {
		this.removeTables = removeTables;
	}

	/**
	 * @return the removeJobs
	 */
	public boolean removeJobs() {
		return removeJobs;
	}

	/**
	 * @param removeJobs the removeJobs to set
	 */
	public void removeJobs(boolean removeJobs) {
		this.removeJobs = removeJobs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RemoveHSPECGroupMessage [requestId=");
		builder.append(requestId);
		builder.append(", removeTables=");
		builder.append(removeTables);
		builder.append(", removeJobs=");
		builder.append(removeJobs);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
