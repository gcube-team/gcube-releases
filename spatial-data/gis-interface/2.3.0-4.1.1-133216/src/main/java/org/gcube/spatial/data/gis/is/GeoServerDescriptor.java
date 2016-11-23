package org.gcube.spatial.data.gis.is;

public class GeoServerDescriptor implements Comparable<GeoServerDescriptor>{


	private String url;
	private String user;
	private String password;
	private Long hostedLayersCount;
	
	
	public GeoServerDescriptor(String url, String user, String password,
			Long hostedLayersCount) {
		super();
		this.url = url;
		this.user = user;
		this.password = password;
		this.hostedLayersCount = hostedLayersCount;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GeoServerDescriptor o) {
		// TODO Auto-generated method stub
		return hostedLayersCount.compareTo(o.hostedLayersCount);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the hostedLayersCount
	 */
	public Long getHostedLayersCount() {
		return hostedLayersCount;
	}

	/**
	 * @param hostedLayersCount the hostedLayersCount to set
	 */
	public void setHostedLayersCount(Long hostedLayersCount) {
		this.hostedLayersCount = hostedLayersCount;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoServerDescriptor other = (GeoServerDescriptor) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoServerDescriptor [url=");
		builder.append(url);
		builder.append(", user=");
		builder.append(user);
		builder.append(", password=");
		builder.append(password);
		builder.append(", hostedLayersCount=");
		builder.append(hostedLayersCount);
		builder.append("]");
		return builder.toString();
	}


	
	
	
	
}
