package org.gcube.rest.commons.db.model.app;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.gcube.rest.commons.db.dao.core.ConverterRecord;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.publisher.resourceregistry.PublisherRRimpl;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Entity
//@SequenceGenerator(name = "SEQ_STORE", sequenceName = "resource_model_id_seq", allocationSize = 1)
@Table(name = "resource_model")
public class ResourceModel extends ConverterRecord<Resource> {

	private static final long serialVersionUID = 1L;
	private static transient final Logger logger = LoggerFactory.getLogger(ResourceModel.class);

	@Column(name = "resourceID")
	private String resourceID;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	private String type;

	@Column(name = "scopes")
	@ElementCollection(targetClass = String.class)
	private List<String> scopes;

	@Column(name = "body")
	private String body;

	public ResourceModel() {
		super();
	}

	public ResourceModel(Resource base) {
		this.copyFrom(base);
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	// /
	@Override
	public final void copyFrom(Resource resource) {
		this.resourceID = resource.getResourceID();
		this.name = resource.getName();
		this.type = resource.getType();
		this.setDescription(resource.getDescription());
		try {
			this.body = resource.getBodyAsString();
		} catch (Exception e) {
			logger.warn("error in resource body marshalling", e);
		}
		if (resource.getScopes() != null)
			this.scopes = Lists.newArrayList(resource.getScopes());
	}

	@Override
	public final Resource copyTo() throws IllegalStateException {
		Node node = XMLConverter.stringToNode(this.body);
		if (node == null && Strings.isNullOrEmpty(this.body) == false) {
			throw new IllegalStateException(
					"error while creating object from convertable");
		}

		Resource resource = new Resource();

		resource.setResourceID(this.resourceID);
		resource.setName(this.name);
		resource.setType(this.type);
		resource.setDescription(this.getDescription());
		resource.setScopes(Lists.newArrayList(this.scopes));
		resource.setBody(node);

		return resource;
	}

}
