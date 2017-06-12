package org.gcube.data.analysis.tabulardata.service.impl.tabular;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tabularResource;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TabularResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.service.tabular.HistoryStep;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabularResourceObject implements TabularResource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(TabularResourceObject.class);

	private volatile TabularResourceManagerProxy trManager = tabularResource().build();
	private org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource remoteTabularResource;
		
	public TabularResourceObject(org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource remoteTabularResource){
		this.remoteTabularResource = remoteTabularResource;
	}

	@SuppressWarnings("unchecked")
	public <C extends TabularResourceMetadata<?>> C getMetadata(
			Class<C> metadataType) {
				
		if (metadataType.equals(NameMetadata.class))
			return (C) new NameMetadata(remoteTabularResource.getName());
								
		if (!getProperties().containsKey(metadataType.getName()))
			throw new NoSuchMetadataException(metadataType);
		
		try {
			return (C)retrieveInternalMetadata(metadataType.getName(), getProperties().get(metadataType.getName()));
		} catch (Exception e) {
			throw new RuntimeException("error instantiating metadata "+metadataType.getName()+" it must implement a void constructor");
		}
		
	}

	public boolean contains(
			Class<? extends TabularResourceMetadata<?>> metadataType) {
		if (metadataType.equals(NameMetadata.class)) return true;
		if (getProperties().containsKey(metadataType.getName()))
			return true;
		return false;
	}
	
	public void removeMetadata(
			Class<? extends TabularResourceMetadata<?>> metadataType) {
		getProperties().remove(metadataType.getName());
		flush();
	}

	public void setMetadata(TabularResourceMetadata<?> metadata) {
		if (metadata.getClass().equals(NameMetadata.class))
			remoteTabularResource.setName((String)metadata.getValue());	
		else 
			if (metadata.getValue()!=null) getProperties().put(metadata.getClass().getName(), (Serializable)metadata.getValue());
		flush();
	}

	@SuppressWarnings("unchecked")
	private <T> TabularResourceMetadata<T> retrieveInternalMetadata(String metadataClassName, T value) throws Exception{
		Constructor<TabularResourceMetadata<T>> constructor = (Constructor<TabularResourceMetadata<T>>)Class.forName(metadataClassName).getDeclaredConstructor();
		constructor.setAccessible(true);
		TabularResourceMetadata<T> trm = constructor.newInstance();
		trm.setValue(value);
		return trm;
	}
	
	public Collection<TabularResourceMetadata<?>> getAllMetadata() {
		List<TabularResourceMetadata<?>> metadata = new ArrayList<TabularResourceMetadata<?>>();
		if (getProperties()!=null && getProperties().size()>0)
			for (Entry<String, Serializable> entry : getProperties().entrySet()){
				try {
					TabularResourceMetadata<?> trm = retrieveInternalMetadata(entry.getKey(), entry.getValue());
					metadata.add(trm);
				} catch (Exception e) {
					throw new RuntimeException("error instantiating metadata "+entry.getKey()+" it must implement a void constructor");
				}
			}
		metadata.add(new NameMetadata(remoteTabularResource.getName()));
		return metadata;
	}

	public void setAllMetadata(Collection<TabularResourceMetadata<?>> metadata) {
		for (TabularResourceMetadata<?> singleMetadata: metadata ){
			if (singleMetadata.getClass().equals(NameMetadata.class))
				remoteTabularResource.setName((String)singleMetadata.getValue());	
			else getProperties().put(singleMetadata.getClass().getName(), (Serializable)singleMetadata.getValue());
		}
				
		
		flush();
	}

	public void removeAllMetadata() {
		remoteTabularResource.setProperties(new HashMap<String, Serializable>());
		flush();
	}

	public TabularResourceId getId() {
		return new TabularResourceId(remoteTabularResource.getId());
	}

	public List<HistoryStep> getHistory() {
		try{
			List<HistoryStep> historySteps = new ArrayList<HistoryStep>();
			List<HistoryData> steps = remoteTabularResource.getHistory();
			for (HistoryData step: steps)
				historySteps.add(new HistoryStepImpl(step));
			return historySteps;
		}catch (Exception e) {
			logger.error("error retrievin history for tabular resource id {}",remoteTabularResource.getId());
			throw new RuntimeException(e);
		}
	}

	private Map<String, Serializable> getProperties(){
		return remoteTabularResource.getProperties();
	}
	
	public Calendar getCreationDate() {
		return remoteTabularResource.getCreationDate();
	}

	private void flush(){
		try{
			trManager.updateTabularResource(remoteTabularResource);
		}catch (Exception e) {
			new RuntimeException("error updating item properties on server",e);
		}
	}

	public String getTableType() {
		return remoteTabularResource.getTableType();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResource#getOwner()
	 */
	public String getOwner() {
		return remoteTabularResource.getOwner();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResource#getSharedWithUsers()
	 */
	public List<String> getSharedWithUsers() {
		return remoteTabularResource.getSharedWithUsers();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResource#getSharedWithGroup()
	 */
	public List<String> getSharedWithGroups() {
		return remoteTabularResource.getSharedWithGroups();
	}

	@Override
	public TabularResourceType getTabularResourceType(){
		return remoteTabularResource.getTabularResourceType();
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResource#isValid()
	 */
	public boolean isValid() {
		return remoteTabularResource.isValid();
	}
	
	@Override
	public boolean isLocked() {
		return remoteTabularResource.isLocked();
	}

	@Override
	public boolean isFinalized() {
		return remoteTabularResource.isFinalized();
	}

	@Override
	public void finalize() {
		remoteTabularResource.finalize(true);
		flush();
	}

	@Override
	public String toString() {
		return remoteTabularResource.toString();
	}
	
	@Override
	public org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource getRemoteTabularResource(){
		return remoteTabularResource;
	}
	
}
