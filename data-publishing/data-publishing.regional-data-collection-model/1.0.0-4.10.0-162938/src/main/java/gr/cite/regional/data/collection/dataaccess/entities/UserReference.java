package gr.cite.regional.data.collection.dataaccess.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import gr.cite.regional.data.collection.dataaccess.types.XMLType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@javax.persistence.Entity
@Table(name="\"UserReference\"")
@TypeDef(name = "XMLType", typeClass = XMLType.class)
public class UserReference implements Entity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="userreference_generator")
	@SequenceGenerator(name="userreference_generator", sequenceName="userreference_id_seq", allocationSize = 1)
	@Column(name="\"ID\"", updatable = false, nullable = false)
	private Integer id;

	@Column(name = "\"Label\"", nullable = false, length = 100)
	private String label;

	@Column(name = "\"FullName\"", nullable = false, length = 100)
	private String fullName;

	@Column(name = "\"Email\"", nullable = false, length = 100)
	private String email;
	
	@Column(name = "\"URI\"", nullable = false, length = 500)
	private String uri;
	
	@Type(type="XMLType")
	@Column(name="\"Attributes\"", columnDefinition = "xml")
	private String attributes;

	@Temporal(TemporalType.DATE)
	@Column(name = "\"RegistrationDate\"", nullable = false)
	private Date registrationDate;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "author", cascade=CascadeType.ALL)
	private Set<Annotation> annotations = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade=CascadeType.ALL)
	private Set<DataSubmission> dataSubmissions = new HashSet<>();

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

	public String getFullName() { return fullName; }

	public void setFullName(String fullName) { this.fullName = fullName; }

	public String getEmail() { return email; }

	public void setEmail(String email) { this.email = email; }

	public Date getRegistrationDate() { return registrationDate; }

	public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}

	public Set<DataSubmission> getDataSubmissions() {
		return dataSubmissions;
	}

	public void setDataSubmissions(Set<DataSubmission> dataSubmissions) {
		this.dataSubmissions = dataSubmissions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserReference other = (UserReference) obj;
		if (id != other.id)
			return false;
		return true;
	}
}