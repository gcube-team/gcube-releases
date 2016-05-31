package org.gcube.portlets.user.joinvre.shared;

public class VRECustomAttributes {
	
	private boolean isUponRequest;
	private boolean isExternal;
	private String urlIfAny;
	
	public VRECustomAttributes() {
		super();
		this.isUponRequest = false;
		this.isExternal = false;
		this.urlIfAny = "";
	}

	public VRECustomAttributes(boolean isUponRequest, boolean isExternal,
			String urlIfAny) {
		super();
		this.isUponRequest = isUponRequest;
		this.isExternal = isExternal;
		this.urlIfAny = urlIfAny;
	}

	public boolean isUponRequest() {
		return isUponRequest;
	}

	public void setUponRequest(boolean isUponRequest) {
		this.isUponRequest = isUponRequest;
	}

	public boolean isExternal() {
		return isExternal;
	}

	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}

	public String getUrlIfAny() {
		return urlIfAny;
	}

	public void setUrlIfAny(String urlIfAny) {
		this.urlIfAny = urlIfAny;
	}

	@Override
	public String toString() {
		return "VRECustomAttributes [isUponRequest=" + isUponRequest
				+ ", isExternal=" + isExternal + ", urlIfAny=" + urlIfAny + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isExternal ? 1231 : 1237);
		result = prime * result + (isUponRequest ? 1231 : 1237);
		result = prime * result
				+ ((urlIfAny == null) ? 0 : urlIfAny.hashCode());
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
		VRECustomAttributes other = (VRECustomAttributes) obj;
		if (isExternal != other.isExternal)
			return false;
		if (isUponRequest != other.isUponRequest)
			return false;
		if (urlIfAny == null) {
			if (other.urlIfAny != null)
				return false;
		} else if (!urlIfAny.equals(other.urlIfAny))
			return false;
		return true;
	}
	
}
