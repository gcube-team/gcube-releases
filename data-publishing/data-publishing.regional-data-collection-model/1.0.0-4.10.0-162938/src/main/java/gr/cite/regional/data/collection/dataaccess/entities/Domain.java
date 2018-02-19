package gr.cite.regional.data.collection.dataaccess.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import gr.cite.regional.data.collection.dataaccess.types.XMLType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * @author vfloros
 *
 */
@javax.persistence.Entity
@Table(name="\"Domain\"")
@TypeDef(name = "XMLType", typeClass = XMLType.class)
public class Domain implements Entity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="domain_generator")
	@SequenceGenerator(name = "domain_generator", sequenceName = "domain_id_seq", allocationSize = 1)
	@Column(name="\"ID\"", updatable = false, nullable = false)
	private Integer id;
	
	@Column(name = "\"Label\"", nullable = false, length = 100)
	private String label;
	
	@Column(name = "\"URI\"", nullable = true, length = 500)
	private String uri;
	
	@Type(type="XMLType")
	@Column(name="\"Attributes\"", columnDefinition = "xml")
	private String attributes;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "domain", cascade=CascadeType.ALL)
	private Set<Annotation> annotations = new HashSet<>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "domain", cascade=CascadeType.ALL)
	private Set<DataCollection> dataCollections = new HashSet<>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "domain", cascade=CascadeType.ALL)
	private Set<DataModel> dataModels = new HashSet<>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "domain", cascade=CascadeType.ALL)
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

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}

	public Set<DataCollection> getDataCollections() {
		return dataCollections;
	}

	public void setDataCollections(Set<DataCollection> dataCollections) {
		this.dataCollections = dataCollections;
	}

	public Set<DataModel> getDataModels() {
		return dataModels;
	}

	public void setDataModels(Set<DataModel> dataModels) {
		this.dataModels = dataModels;
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
		Domain other = (Domain) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}