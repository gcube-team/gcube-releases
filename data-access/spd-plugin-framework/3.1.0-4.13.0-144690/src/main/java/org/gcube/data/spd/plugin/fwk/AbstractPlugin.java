package org.gcube.data.spd.plugin.fwk;

import java.util.Collections;
import java.util.Set;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.PropertySupport;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.UnfoldCapability;

public abstract class AbstractPlugin implements PropertySupport, Searchable<ResultItem>{
	
	private boolean initialized = false;
	
	private boolean useCache= false;
	
	public void initialize(ServiceEndpoint resource) throws Exception{
		initialized= true;
	}
	
	public void update(ServiceEndpoint resource) throws Exception{}
	
	public void shutdown() throws Exception{}
	
	public ClassificationCapability getClassificationInterface(){return null;};
	
	public MappingCapability getMappingInterface(){return null;};
	
	public ExpansionCapability getExpansionInterface(){return null;};
	
	public OccurrencesCapability getOccurrencesInterface(){return null;};
	
	public UnfoldCapability getUnfoldInterface(){return null;};
			
	public Set<Conditions> getSupportedProperties(){
		return Collections.emptySet();
	}
	
	public Set<Capabilities> getSupportedCapabilities() {
		return Collections.emptySet();
	}

	public abstract RepositoryInfo getRepositoryInfo();

	@Override
	public Class<ResultItem> getHandledClass() {
		return ResultItem.class;
	}

	/*
	@Override
	public abstract void searchByScientificName(String word,
			ObjectWriter<ResultItem> writer, Condition... properties) throws ExternalRepositoryException;
*/
	public abstract String getRepositoryName();
	
	public abstract String getDescription();

		
	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public boolean isRemote(){
		return false;
	}
	
	
	
	@Override
	public String toString() {
		return getRepositoryName()+"(use-cache="+isUseCache()+")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		AbstractPlugin ap = (AbstractPlugin) obj;
		if (ap.getRepositoryName()==null || ap.getRepositoryName().equals("")) return false;
		return ap.getRepositoryName().equals(this.getRepositoryName());
	}

	@Override
	public int hashCode() {
		return this.getRepositoryName().hashCode();
	}
	
	
	
}

