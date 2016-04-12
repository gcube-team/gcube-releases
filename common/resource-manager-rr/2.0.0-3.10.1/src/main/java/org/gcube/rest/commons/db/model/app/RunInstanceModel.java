package org.gcube.rest.commons.db.model.app;

import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.gcube.rest.commons.db.dao.core.ConverterRecord;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance.Profile;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentMap;
import org.w3c.dom.Node;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Entity
//@SequenceGenerator(name = "SEQ_STORE", sequenceName = "run_instance_model_id_seq", allocationSize = 1)
@Table(name = "run_instance_model")
public class RunInstanceModel extends ConverterRecord<RunInstance> {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "resourceId")
	private String resourceId;
	
	@Column(name = "ghnId")
	private String ghnId;

	@Column(name = "version")
	private String version;

	@Column(name = "status")
	private String status;

	@Column(name = "activationTime")
	private Date activationTime;

	@Column(name = "endpoints")
	//@ElementCollection(targetClass=String.class)
	//@CollectionOfElements
	@ElementCollection
	private Map<String, URI> endpoints;

	@Column(name = "scopes")
    @ElementCollection
	private List<String> scopes;

	@Column(name = "specificData")
	private String specificData;
	
	@Column(name = "serviceClass")
	private String serviceClass;
	
	@Column(name = "serviceName")
	private String serviceName;
	
	
	public RunInstanceModel() {
		super();
	}
	
	public RunInstanceModel(RunInstance base){
		this.copyFrom(base);
	}
	
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getGhnId() {
		return ghnId;
	}

	public void setGhnId(String ghnId) {
		this.ghnId = ghnId;
	}

	public Map<String, URI> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(Map<String, URI> endpoints) {
		this.endpoints = endpoints;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	public String getSpecificData() {
		return specificData;
	}

	public void setSpecificData(String specificData) {
		this.specificData = specificData;
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
	public final void copyFrom(RunInstance resource){
		this.ghnId = resource.getProfile().ghn.ghnId;
		this.resourceId =  resource.getId();
		this.setDescription(resource.getProfile().description);
		this.specificData = XMLConverter.nodeToString(resource.getProfile().specificData.root);
		try {
			this.activationTime = RunInstance.DateFormatter.stringToDate(resource.getProfile().deploymentData.activationTime.value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.version = resource.getProfile().version;
		this.status = resource.getProfile().deploymentData.status.toString();
		
		this.serviceClass = resource.getProfile().serviceClass;
		this.serviceName = resource.getProfile().serviceName;
		
		if (resource.getScopes() != null)
			this.scopes = Lists.newArrayList(resource.getScopes());
		
		if (resource.getProfile().accessPoint.runningInstanceInterfaces != null)
			this.endpoints = Maps.newHashMap(resource.getProfile().accessPoint.runningInstanceInterfaces);
	}
	
	@Override
	public final RunInstance copyTo() throws IllegalStateException {
		
		
		Node node = XMLConverter.stringToNode(this.specificData);
		if (node == null && Strings.isNullOrEmpty(this.specificData) == false){
			throw new IllegalStateException("error while creating object from convertable");
		}
		
		boolean endpointsInit = false;
		boolean scopesInit = false;
		try {
			if (!((PersistentMap) endpoints).isEmpty())
				endpointsInit = true;
			if (!((PersistentBag) scopes).isEmpty())
				scopesInit = true;
		} catch (Exception e) {
		}
		HashMap<String, URI> endpointsMap = endpointsInit? Maps.newHashMap(this.endpoints) : new HashMap<String, URI>();
		Profile profile = new Profile(getDescription(), version, ghnId, this.resourceId, this.serviceName, this.serviceClass, activationTime, status, endpointsMap, node);
		
		RunInstance resource = new RunInstance(resourceId, scopesInit? Sets.newHashSet(this.scopes) : new HashSet<String>(), profile);
		
		return resource;
	}
	
}
