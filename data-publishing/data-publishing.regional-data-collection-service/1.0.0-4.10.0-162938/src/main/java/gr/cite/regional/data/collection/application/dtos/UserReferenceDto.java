package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.regional.data.collection.dataaccess.entities.Annotation;

import java.util.Date;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserReferenceDto implements Dto {
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("uri")
	private String uri;

	@JsonProperty("attributes")
	private String attributes;

	@JsonProperty("fullName")
	private String fullName;

	@JsonProperty("email")
	private String email;

	@JsonProperty("registrationDate")
	private Date registrationDate;
	
	/*@JsonProperty("annotations")
	private Set<Annotation> annotations;*/
	
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

	public String getFullName() { return fullName; }

	public void setFullName(String fullName) { this.fullName = fullName; }

	public String getEmail() { return email; }

	public void setEmail(String email) { this.email = email; }

	public Date getRegistrationDate() { return registrationDate; }

	public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
	/*public Set<Annotation> getAnnotations() {
		return annotations;
	}
	
	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}*/
}
