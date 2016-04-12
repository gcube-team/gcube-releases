package org.gcube.rest.index.common.resources;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class IndexResource extends StatefulResource {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(IndexResource.class);
    
    public enum SupportedRelations { adj, fuzzy, proximity, within , lt, le, gt, ge , eq}
    public static final String EQUALS = "=";
    public static final String BROWSE_EQUALS = "==";

    
    private String clusterID;
    
    private List<String> collections;
    
    private List<String> fields;
    
    private Set<String> supportedRelations;
    
    private String scope;
    
    private String indexID;
    
    private String esTransportAddress;
    
    private String hostname;
    
    @XmlElement
	public String getEsTransportAddress() {
		return this.esTransportAddress;
	}

	public void setEsTransportAddress(String esTransportAddress) {
		this.esTransportAddress = esTransportAddress;
	}


	@XmlElement
	public String getClusterID() {
		return this.clusterID;
	}

	public void setClusterID(String clusterID) {
		this.clusterID = clusterID;
	}

	@XmlElement
	public List<String> getCollections() {
		return this.collections;
	}

	public void setCollections(List<String> collections) {
		this.collections = collections;
	}

	@XmlElement
	public List<String> getFields() {
		return this.fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	@XmlElement
	public Set<String> getSupportedRelations() {
		return this.supportedRelations;
	}

	public void setSupportedRelations(Set<String> supportedRelations) {
		this.supportedRelations = supportedRelations;
	}

	@XmlElement
	public String getScope() {
		return this.scope;
	}


	public void setScope(String scope) {
		this.scope = scope;
	}


	@XmlElement
	public String getIndexID() {
		return this.indexID;
	}


	public void setIndexID(String indexID) {
		this.indexID = indexID;
	}
	
	@XmlElement
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	

	public static Set<String> getSupportedRelationsSet(){
		Set<String> supportedRelations = new HashSet<String>();
		for(IndexResource.SupportedRelations relation : IndexResource.SupportedRelations.values())
		{
			String relStr = null;
			if (relation.equals(IndexResource.SupportedRelations.eq))
					relStr = "=";
			else if (relation.equals(IndexResource.SupportedRelations.ge))
					relStr = ">=";
			else if (relation.equals(IndexResource.SupportedRelations.gt))
				relStr = ">";
			else if (relation.equals(IndexResource.SupportedRelations.le))
				relStr = "<=";
			else if (relation.equals(IndexResource.SupportedRelations.lt))
				relStr = "<";
			else 
				relStr = relation.name();
					
			supportedRelations.add(relStr);
		}
		supportedRelations.add(IndexResource.EQUALS);
		supportedRelations.add(IndexResource.BROWSE_EQUALS);
		
		return supportedRelations;
    }

	@Override
	public void onLoad() throws StatefulResourceException {
	}

	@Override
	public void onClose() throws StatefulResourceException {
	}

	@Override
	public void onDestroy() throws StatefulResourceException {
	}
}
