package org.gcube.data.spd.caching;
import java.io.Serializable;






public class CacheKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String searchName;
	private Class<?> clazz;
	private String propsAsString;
	//TODO: properties
	
	public CacheKey(String searchName, String propsAsString,
			Class<?> clazz) {
		super();
		this.searchName = searchName;
		this.propsAsString = propsAsString;
		this.clazz = clazz;
	}

	
	
	
	public String getSearchName() {
		return searchName;
	}




	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}


	public Class<?> getClazz() {
		return clazz;
	}

	public String getPropsAsString() {
		return propsAsString;
	}

	public void setPropsAsString(String propsAsString) {
		this.propsAsString = propsAsString;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.getName().hashCode());
		result = prime * result
				+ ((propsAsString == null) ? 0 : propsAsString.hashCode());
		result = prime * result
				+ ((searchName == null) ? 0 : searchName.hashCode());
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
		CacheKey other = (CacheKey) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.getName().equals(other.clazz.getName()))
			return false;
		if (propsAsString == null) {
			if (other.propsAsString != null)
				return false;
		} else if (!propsAsString.equals(other.propsAsString))
			return false;
		if (searchName == null) {
			if (other.searchName != null)
				return false;
		} else if (!searchName.equals(other.searchName))
			return false;
		return true;
	}




	
	
	
	
	
}
