package org.gcube.spatial.data.gis.is;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public abstract class AbstractGeoServerDescriptor implements Comparable<AbstractGeoServerDescriptor>{


	private String url;
	private String user;
	private String password;
	
	public AbstractGeoServerDescriptor(String url, String user, String password) {
		super();
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	@Override
	public int compareTo(AbstractGeoServerDescriptor o) {
		Long localCount=0l;
		Long otherCount=0l;
		try {
			localCount=getHostedLayersCount();
			otherCount=o.getHostedLayersCount();			
		} catch (MalformedURLException e) {
			log.warn("Unable to evaluate count. This could lead to unbalanced layer amounts between instances",e);
		}
		return localCount.compareTo(otherCount);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		AbstractGeoServerDescriptor other = (AbstractGeoServerDescriptor) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	
	public abstract Long getHostedLayersCount() throws MalformedURLException;
	
	public abstract Set<String> getWorkspaces() throws MalformedURLException;
	
	public abstract Set<String> getStyles() throws MalformedURLException;
	
	public abstract Set<String> getDatastores(String workspace) throws MalformedURLException;
	
	
	public GeoServerRESTReader getReader() throws MalformedURLException{
		return getManager().getReader();
	}
	
	public GeoServerRESTStoreManager getDataStoreManager() throws IllegalArgumentException, MalformedURLException{
		return getManager().getStoreManager();
	}
	
	public GeoServerRESTPublisher getPublisher() throws IllegalArgumentException, MalformedURLException{
		return getManager().getPublisher();
	}
	
	protected GeoServerRESTManager getManager() throws IllegalArgumentException, MalformedURLException{
		return new GeoServerRESTManager(new URL(url), user, password);
	}
	
	
	
	public void onChangedStyles(){}
	public void onChangedWorkspaces(){}
	public void onChangedDataStores(){}
	public void onChangedLayers(){}

	@Override
	public String toString() {
		long layersCount=0l;
		try{
			layersCount=getHostedLayersCount();
		}catch(Exception e){
			log.warn("Unable to get layer count on {} ",url,e);
		}
		
		return "AbstractGeoServerDescriptor [url=" + url + ", user=" + user + ", password=" + password
				+ ", layerCount=" + layersCount + "]";
	}
}
