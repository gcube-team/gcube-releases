package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.regional.data.collection.dataaccess.entities.Properties;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataModelDto implements Dto {
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("version")
	private String version;
	
	@JsonProperty("uri")
	private String uri;
	
	@JsonProperty("definition")
	private String definition;
	
	@JsonProperty("domain")
	private DomainDto domain;
	
	@JsonProperty("properties")
	private Properties properties;
	
	@JsonProperty("activeDataCollectionPeriod")
	private boolean activeDataCollectionPeriod;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getDefinition() {
		return definition;
	}
	
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	public DomainDto getDomain() {
		return domain;
	}
	
	public void setDomain(DomainDto domain) {
		this.domain = domain;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public boolean isActiveDataCollectionPeriod() {
		return activeDataCollectionPeriod;
	}

	public void setActiveDataCollectionPeriod(boolean activeDataCollectionPeriod) {
		this.activeDataCollectionPeriod = activeDataCollectionPeriod;
	}

}
