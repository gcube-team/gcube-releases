package org.gcube.rest.commons.db.model.app;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.gcube.rest.commons.db.dao.core.ConverterRecord;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.resourceawareservice.resources.SerInstance;
import org.w3c.dom.Node;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Entity
//@SequenceGenerator(name = "SEQ_STORE", sequenceName = "ser_instance_model_id_seq", allocationSize = 1)
@Table(name = "ser_instance_model")
public class SerInstanceModel extends ConverterRecord<SerInstance> {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "endpoint")
	private String endpoint;
	
	@Column(name = "service_key")
	private String serviceKey;

	@Column(name = "nodeId")
	private String nodeId;

	@Column(name = "scopes")
    @ElementCollection
	private List<String> scopes;

	@Column(name = "customProperties")
	private String customProperties;

	@Column(name = "serviceClass")
	private String serviceClass;
	
	@Column(name = "serviceName")
	private String serviceName;
	
	
	public SerInstanceModel() {
		super();
	}
	
	public SerInstanceModel(SerInstance base){
		this.copyFrom(base);
	}
	
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	public String getCustomProperties() {
		return customProperties;
	}

	public void setCustomProperties(String customProperties) {
		this.customProperties = customProperties;
	}
	
	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	///
	@Override
	public final void copyFrom(SerInstance resource){
		if (resource.getEndpoint() != null)
			this.endpoint = resource.getEndpoint().toASCIIString();
		
		this.serviceKey = resource.getKey();
		this.serviceClass = resource.getServiceClass();
		this.serviceName = resource.getServiceName();
		
		if (resource.getProperties() != null) {
			this.nodeId = resource.getProperties().getNodeId();
			this.scopes = Lists.newArrayList(resource.getProperties().getScopes());
			this.customProperties = XMLConverter.nodeToString(resource.getProperties().getCustomProperties());
		}
		
	}
	
	@Override
	public final SerInstance copyTo() throws IllegalStateException {
		URI endpoint = null;
		
		if (this.endpoint != null)
			try {
				endpoint = new URI(this.endpoint);
			} catch (URISyntaxException e) {
				throw new IllegalStateException("error while creating object from convertable", e);
			}
		
		
		Node customProperties = XMLConverter.stringToNode(this.customProperties);
		if (customProperties == null && Strings.isNullOrEmpty(this.customProperties) == false){
			throw new IllegalStateException("error while creating object from convertable");
		}
		
		SerInstance.NodeProperties properties = new SerInstance.NodeProperties(this.nodeId, Lists.newArrayList(this.scopes), customProperties); 
		
		SerInstance resource = new SerInstance(endpoint, this.serviceKey, this.serviceName, this.serviceClass, properties);
		
		return resource;
	}
	
}
