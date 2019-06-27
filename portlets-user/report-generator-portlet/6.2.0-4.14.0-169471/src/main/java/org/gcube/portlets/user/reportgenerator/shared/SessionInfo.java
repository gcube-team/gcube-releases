package org.gcube.portlets.user.reportgenerator.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SessionInfo implements Serializable {

	private UserBean user;
	private String scope;
	private Boolean isWorkflowDocument;
	private Boolean isEditable;
	private Boolean isVME;
	private String rsgEndpoint;
	
	public SessionInfo() {	}

	public SessionInfo(UserBean user, String scope, Boolean isWorkflowDocument,
			Boolean isEditable, Boolean isVME, String rsgEndpoint) {
		super();
		this.user = user;
		this.scope = scope;
		this.isWorkflowDocument = isWorkflowDocument;
		this.isEditable = isEditable;
		this.isVME = isVME;
		this.rsgEndpoint = rsgEndpoint;
	}

	public UserBean getUserName() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Boolean isWorkflowDocument() {
		return isWorkflowDocument;
	}

	public void setWorkflowDocument(Boolean isWorkflowDocument) {
		this.isWorkflowDocument = isWorkflowDocument;
	}

	public Boolean isEditable() {
		return isEditable;
	}

	public void setEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

	public Boolean isVME() {
		return isVME;
	}

	public void setIsVME(Boolean isVME) {
		this.isVME = isVME;
	}

	public String getRsgEndpoint() {
		return rsgEndpoint;
	}

	public void setRsgEndpoint(String rsgEndpoint) {
		this.rsgEndpoint = rsgEndpoint;
	}

	@Override
	public String toString() {
		return "SessionInfo [user=" + user + ", scope=" + scope
				+ ", isWorkflowDocument=" + isWorkflowDocument
				+ ", isEditable=" + isEditable + ", isVME=" + isVME
				+ ", rsgEndpoint=" + rsgEndpoint + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((isEditable == null) ? 0 : isEditable.hashCode());
		result = prime * result + ((isVME == null) ? 0 : isVME.hashCode());
		result = prime
				* result
				+ ((isWorkflowDocument == null) ? 0 : isWorkflowDocument
						.hashCode());
		result = prime * result
				+ ((rsgEndpoint == null) ? 0 : rsgEndpoint.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SessionInfo other = (SessionInfo) obj;
		if (isEditable == null) {
			if (other.isEditable != null)
				return false;
		} else if (!isEditable.equals(other.isEditable))
			return false;
		if (isVME == null) {
			if (other.isVME != null)
				return false;
		} else if (!isVME.equals(other.isVME))
			return false;
		if (isWorkflowDocument == null) {
			if (other.isWorkflowDocument != null)
				return false;
		} else if (!isWorkflowDocument.equals(other.isWorkflowDocument))
			return false;
		if (rsgEndpoint == null) {
			if (other.rsgEndpoint != null)
				return false;
		} else if (!rsgEndpoint.equals(other.rsgEndpoint))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	
}
