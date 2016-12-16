package org.gcube.portlets.user.dataminermanager.shared.data.computations;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationId implements Serializable {

	private static final long serialVersionUID = 7898676192287822723L;
	private String id;
	private String urlId;
	private String operatorId;
	private String operatorName;
	private String equivalentRequest;

	public ComputationId() {
		super();
	}

	public ComputationId(String id, String urlId, String operatorId,
			String operatorName, String equivalentRequest) {
		super();
		this.id = id;
		this.urlId = urlId;
		this.operatorId = operatorId;
		this.operatorName = operatorName;
		this.equivalentRequest = equivalentRequest;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrlId() {
		return urlId;
	}

	public void setUrlId(String urlId) {
		this.urlId = urlId;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getEquivalentRequest() {
		return equivalentRequest;
	}

	public void setEquivalentRequest(String equivalentRequest) {
		this.equivalentRequest = equivalentRequest;
	}

	@Override
	public String toString() {
		return "ComputationId [id=" + id + ", urlId=" + urlId + ", operatorId="
				+ operatorId + ", operatorName=" + operatorName
				+ ", equivalentRequest=" + equivalentRequest + "]";
	}

}
