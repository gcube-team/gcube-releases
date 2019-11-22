package org.gcube.spatial.data.sdi.engine.impl.cluster;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.spatial.data.sdi.utils.ScopeUtils;
import org.gcube.spatial.data.sdi.NetUtils;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.is.CachedObject;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.model.service.GeoServiceDescriptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCluster<T extends GeoServiceDescriptor,E extends GeoServiceController<T>> {

	private long objectsTTL;
	private ConcurrentHashMap<String,CachedObject<ArrayList<E>>> scopedCache;
	private ISModule retriever;
	private String cacheName;
	
	
	public synchronized ArrayList<E> getActualCluster() throws ConfigurationNotFoundException{
		String key=ScopeUtils.getCurrentScope();
		log.info("Getting object from cache{} , key is {} ",cacheName,key);
		if((!scopedCache.containsKey(key))||(!scopedCache.get(key).isValid(objectsTTL)))
			scopedCache.put(key, new CachedObject<ArrayList<E>>(getLiveControllerCollection()));
		return scopedCache.get(key).getTheObject();
	}
	
	
	protected ArrayList<E> getLiveControllerCollection() throws ConfigurationNotFoundException{
		ArrayList<E> toReturn=new ArrayList<E>();
		for(ServiceEndpoint endpoint : retriever.getISInformation()) 
			try {
				toReturn.add(translate(endpoint));
			}catch(Throwable t) {
				log.warn("Unable to handle ServiceEndpoint [name {} , ID {}]",endpoint.profile().name(),endpoint.id(),t);
			}		
		Comparator<E> comp=getComparator();
		if(comp!=null)Collections.sort(toReturn, getComparator());		
		return toReturn;
	}
	
	protected abstract E translate(ServiceEndpoint e) throws InvalidServiceEndpointException;
	
	
	
	
	public void invalidate(){
		String key=ScopeUtils.getCurrentScope();
		log.info("Invalidating cache {} under scope {} ",cacheName,key);
		if(scopedCache.containsKey(key))scopedCache.get(key).invalidate();
	}
	
	public void invalidateAll(){
		for(CachedObject<?> obj:scopedCache.values())obj.invalidate();
	}
	
	
	public E getDefaultController() throws ConfigurationNotFoundException {		
		return getActualCluster().get(0);
	}

	protected abstract  Comparator<E> getComparator();
	
	
	public E getControllerByHostName(String hostname) throws ConfigurationNotFoundException {
		ArrayList<E> controllerCluster=getLiveControllerCollection();
		log.debug("Looking for {} inside cluster [size = {}]",hostname,controllerCluster.size());
		for(E toCheck:controllerCluster) {
			String toCheckHostname=NetUtils.getHostByURL(toCheck.getDescriptor().getBaseEndpoint());
			try {
				if(NetUtils.isSameHost(toCheckHostname, hostname))
						return toCheck;
			} catch (UnknownHostException e) {
				log.warn("Unable to check equality between {} and {} hosts.",toCheckHostname,hostname,e);
			}			
		}
		return null;
	}
	
	public AbstractCluster(long objectsTTL, ISModule retriever, String cacheName) {
		super();		
		this.objectsTTL = objectsTTL;
		this.retriever = retriever;
		this.cacheName=cacheName;
		scopedCache=new ConcurrentHashMap<>();		
	}
	
	
	
	
}
