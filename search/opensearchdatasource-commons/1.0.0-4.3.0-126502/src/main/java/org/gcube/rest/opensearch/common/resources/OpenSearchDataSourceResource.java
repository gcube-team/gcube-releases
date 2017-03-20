package org.gcube.rest.opensearch.common.resources;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants.SupportedRelations;
import org.gcube.opensearch.opensearchoperator.resource.ISOpenSearchResource;
import org.gcube.opensearch.opensearchoperator.resource.ISOpenSearchResourceCache;
import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;



/**
 * A class containing stateful information regarding an OpenSearch adaptor.
 *
 * @author gerasimos.farantatos, NKUA
 *
 */

@XmlRootElement
public class OpenSearchDataSourceResource extends StatefulResource {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	transient private EnvHintCollection envHints = new EnvHintCollection();
	
	@JsonIgnore
	transient private  static final Logger logger = LoggerFactory.getLogger(OpenSearchDataSourceResource.class);
	
	/* cached generic OpenSearch resource */
	@JsonIgnore
	transient public ISOpenSearchResource[] openSearchGenericResources = null;
	
	/* cache including OpenSearch generic resource and description document XML representations and XSLT generic resources */
	@JsonIgnore
	transient public ISOpenSearchResourceCache cache = new ISOpenSearchResourceCache();
	
	
	public List<String> searchableFields = new ArrayList<String>();
	public List<String> presentableFields = new ArrayList<String>();
	public List<String> allPresentableNames = new ArrayList<String>();
	
    private String adaptorID;
    
    private List<String> collections;
    
    private Set<String> supportedRelations;
    
    private List<String> fields;
    
    private List<String> descriptionDocumentURI;
    
    private List<FixedParam> fixedParameters;
    
    private List<String> openSearchResource;
    
    private String scope;
    
    private List<String> openSearchResourceXML;
    
    private String hostname;
    
    @XmlRootElement
    public static class FixedParam {
    	
    	private List<String> params;

    	@XmlElement
		public List<String> getParams() {
			return params;
		}

		public void setParams(List<String> params) {
			this.params = params;
		}
    	
    	
    }
    
    public static void main(String[] args) throws JAXBException {
    	List<FixedParam> fixedParameters = Lists.newArrayList();
    	
    	FixedParam fp = new FixedParam();
    	fp.params = Lists.newArrayList("this is a fixed param", "this is another fixed param");
    	
    	FixedParam fp2 = new FixedParam();
    	fp2.params = Lists.newArrayList("a", "b");
    	
    	fixedParameters.add(fp);
    	fixedParameters.add(fp2);
    	
		OpenSearchDataSourceResource resource = new OpenSearchDataSourceResource();
		resource.setFixedParameters(fixedParameters);
		resource.setFields(Lists.newArrayList("field1"));
		
		System.out.println(resource.toJSON());
		System.out.println(resource.toXML());
	}
    
    @XmlElement
    public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@XmlElement
    public List<FixedParam> getFixedParameters() {
		return fixedParameters;
	}

    @XmlElement
	public List<String> getCollections() {
		return collections;
	}

	public void setCollections(List<String> collections) {
		this.collections = collections;
	}

	public void setFixedParameters(List<FixedParam> fixedParameters) {
		this.fixedParameters = fixedParameters;
	}

	@XmlElement
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@XmlElement
    public String getAdaptorID() {
        return this.adaptorID;
    }

    public void setAdaptorID(String adaptorID) { 
    	this.adaptorID  = adaptorID;	
    }
    
    @XmlElement
    public List<String> getFields() {
    	return this.fields;
    }
    
    public void setFields(List<String> fields) {
		this.fields = fields;
	}
    
    @XmlElement
    public List<String> getCollectionID() {
    	return this.collections;
    }
    
  
    public void setCollectionID(List<String> collectionIDs) {
    	this.collections = collectionIDs;
    }

    public void setDescriptionDocumentURI(String descriptionDocumentURI) throws Exception {
    	this.descriptionDocumentURI = Lists.newArrayList(descriptionDocumentURI);
    }

    @XmlElement
    public List<String> getDescriptionDocumentURI() {
    	return this.descriptionDocumentURI;
    }
    
    public void setDescriptionDocumentURI(List<String> descriptionDocumentURIs) {
        this.descriptionDocumentURI = descriptionDocumentURIs;
    }
    
    @XmlElement
    public List<String> getOpenSearchResource() {
    	return this.openSearchResource;
    } 
    
    public void setOpenSearchResource(List<String> openSearchResource) {
    	this.openSearchResource = openSearchResource;
    }
    
//    public Document getDescriptionDocument(int i) {
//    	synchronized(cache) {
//    		return openSearchGenericResources[i].getDescriptionDocument();
//    	}
//    }
//    
//    public OpenSearchResource getCachedOpenSearchResource(String ddURL) {
//    	synchronized(cache) {
//    		return cache.resources.get(ddURL);
//    	}
//    }
//
//    public List<OpenSearchResource> getCachedOpenSearchResources() {
//    	synchronized(cache) {
//    		return new ArrayList<OpenSearchResource>(cache.resources.values());
//    	}
//    }
//    
//    public void addCachedOpenSearchResource(String ddURL, String resource) throws Exception {
//    	synchronized(cache) {
//    		cache.resources.put(ddURL, new ISOpenSearchResource(resource, getEnvHints()));
//    	}
//    }
//    
//    public void addCachedOpenSearchResources(Map<String, String> resources) throws Exception {
//    	synchronized(cache) {
//	    	for(Map.Entry<String, String> e : resources.entrySet()) {
//	    		cache.resources.put(e.getKey(), new ISOpenSearchResource(e.getValue(), getEnvHints()));
//	    	}
//    	}
//    }
//
	public EnvHintCollection getISEnvHints() {
		return getEnvHints();
	}

    @XmlElement
	public List<String> getOpenSearchResourceXML() {
		return openSearchResourceXML;
	}

	public void setOpenSearchResourceXML(List<String> openSearchResourceXML) {
		this.openSearchResourceXML = openSearchResourceXML;
	}
	
	//@XmlElement
	public EnvHintCollection getEnvHints() {
		return envHints;
	}

	public void setEnvHints(EnvHintCollection envHints) {
		this.envHints = envHints;
	}
    
	
	@XmlElement
	public Set<String> getSupportedRelations() {
		return supportedRelations;
	}

	public void setSupportedRelations(Set<String> supportedRelations) {
		this.supportedRelations = supportedRelations;
	}
	
	public static Set<String> getSupportedRelationsSet(){
		Set<String> relations = Sets.newHashSet();
		
		for (SupportedRelations relation : OpenSearchConstants.SupportedRelations.values()){
			String relStr = null;
			if (relation.equals(OpenSearchConstants.SupportedRelations.eq))
				relStr = "=";
			else if (relation.equals(OpenSearchConstants.SupportedRelations.exact))
				relStr = "==";
			else if (relation.equals(OpenSearchConstants.SupportedRelations.any))
				relStr = "any";
			else if (relation.equals(OpenSearchConstants.SupportedRelations.all))
				relStr = "all";
			
			relations.add(relStr);
		}
		
		return relations;
	}
	
	@Override
	public void onLoad() throws StatefulResourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose() throws StatefulResourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() throws StatefulResourceException {
		// TODO Auto-generated method stub
		
	}
}
