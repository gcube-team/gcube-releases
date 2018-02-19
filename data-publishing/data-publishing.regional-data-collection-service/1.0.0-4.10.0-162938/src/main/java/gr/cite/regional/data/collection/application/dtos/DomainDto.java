package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DomainDto implements Dto {
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("uri")
	private String uri;
	
	@JsonProperty("attributes")
	private String attributes;
	
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
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getAttributes() {
		return attributes;
	}
	
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	
	/*public static DomainDto from(Domain domainEntity) {
		DomainDto domainDto = new DomainDto();
		domainDto.setId(domainEntity.getId());
		domainDto.setLabel(domainEntity.getLabel());
		domainDto.setUri(domainEntity.getUri());
		domainDto.setAttributes(domainEntity.getAttributes());
		
		return domainDto;
	}
	
	public static Domain toDomainEntity(DomainDto domainDto) {
		Domain domainEntity = new Domain();
		domainEntity.setLabel(domainDto.getLabel());
		domainEntity.setUri(domainDto.getUri());
		domainEntity.setAttributes(domainDto.getAttributes());
		
		return domainEntity;
	}
	
	public Domain toDomainEntity() {
		Domain domainEntity = new Domain();
		domainEntity.setLabel(this.label);
		domainEntity.setUri(this.uri);
		domainEntity.setAttributes(this.attributes);
		
		return domainEntity;
	}*/
}
