package org.gcube.search.sru.consumer.common.resources;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;

@XmlRootElement
public class SruConsumerResource extends StatefulResource {

	private static final long serialVersionUID = 1L;
	
	public enum SupportedRelations { /*adj, fuzzy, proximity, within ,*/ lt, le, gt, ge , eq}
    public static final String EQUALS = "=";

	private List<String> presentables;
	private List<String> searchables;
	private String recordIDField;

	private String collectionID;
	
	private List<String> collections;
	private List<String> fields;
	
	private Boolean isCustomMapped;
	private Map<String, String> mapping;
	private DescriptionDocument descriptionDocument;
	
	private String scope;
	
	private Set<String> supportedRelations = getSupportedRelationsSet();
	
	private String hostname;
	
	private String snippetField;
	
	
	@XmlElement
    public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	@XmlElement
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	@XmlElement
	public Set<String> getSupportedRelations() {
		return this.supportedRelations;
	}

	public void setSupportedRelations(Set<String> supportedRelations) {
		this.supportedRelations = supportedRelations;
	}
	
	
	@XmlElement
	public String getCollectionID() {
		return collectionID;
	}

	public void setCollectionID(String collectionID) {
		this.collectionID = collectionID;
	}

	@XmlElement
	public List<String> getPresentables() {
		return presentables;
	}

	public void setPresentables(List<String> presentables) {
		this.presentables = presentables;
	}

	@XmlElement
	public List<String> getSearchables() {
		return searchables;
	}

	public void setSearchables(List<String> searchables) {
		this.searchables = searchables;
	}

	@XmlElement
	public String getRecordIDField() {
		return recordIDField;
	}

	public void setRecordIDField(String recordIDField) {
		this.recordIDField = recordIDField;
	}

	@XmlElement
	public Boolean getIsCustomMapped() {
		return isCustomMapped;
	}

	public void setIsCustomMapped(Boolean isCustomMapped) {
		this.isCustomMapped = isCustomMapped;
	}

	@XmlElement
	public Map<String, String> getMapping() {
		return mapping;
	}

	public void setMapping(Map<String, String> mapping) {
		this.mapping = mapping;
	}

	@XmlElement
	public DescriptionDocument getDescriptionDocument() {
		return descriptionDocument;
	}

	public void setDescriptionDocument(DescriptionDocument descriptionDocument) {
		this.descriptionDocument = descriptionDocument;
	}
	
	@XmlElement
	public List<String> getCollections() {
		return collections;
	}

	public void setCollections(List<String> collections) {
		this.collections = collections;
	}

	@XmlElement
	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	@XmlElement
	public String getSnippetField() {
		return snippetField;
	}

	public void setSnippetField(String snippetField) {
		this.snippetField = snippetField;
	}

	@XmlRootElement
	public static class DescriptionDocument implements Serializable {

		private static final long serialVersionUID = 1L;
		private String schema;
		private String host;
		private Integer port;
		private String servlet;
		private String version;
		private Long maxRecords;
		private String defaultRecordSchema;

		@XmlElement
		public String getSchema() {
			return schema;
		}

		public void setSchema(String schema) {
			this.schema = schema;
		}

		@XmlElement
		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		@XmlElement
		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		@XmlElement
		public String getServlet() {
			return servlet;
		}

		public void setServlet(String servlet) {
			this.servlet = servlet;
		}

		@XmlElement
		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		@XmlElement
		public Long getMaxRecords() {
			return maxRecords;
		}

		public void setMaxRecords(Long maxRecords) {
			this.maxRecords = maxRecords;
		}

		@XmlElement
		public String getDefaultRecordSchema() {
			return defaultRecordSchema;
		}

		public void setDefaultRecordSchema(String defaultRecordSchema) {
			this.defaultRecordSchema = defaultRecordSchema;
		}

	}
	
	
	public static Set<String> getSupportedRelationsSet(){
		Set<String> supportedRelations = new HashSet<String>();
		for(SupportedRelations relation : SupportedRelations.values())
		{
			String relStr = null;
			if (relation.equals(SupportedRelations.eq))
					relStr = "=";
			else if (relation.equals(SupportedRelations.ge))
					relStr = ">=";
			else if (relation.equals(SupportedRelations.gt))
				relStr = ">";
			else if (relation.equals(SupportedRelations.le))
				relStr = "<=";
			else if (relation.equals(SupportedRelations.lt))
				relStr = "<";
			else 
				relStr = relation.name();
					
			supportedRelations.add(relStr);
		}
		supportedRelations.add(EQUALS);
		
		return supportedRelations;
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
