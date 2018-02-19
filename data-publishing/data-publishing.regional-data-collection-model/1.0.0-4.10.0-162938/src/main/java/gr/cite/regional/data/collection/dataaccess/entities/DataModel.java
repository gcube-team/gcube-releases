/**
 * 
 */
package gr.cite.regional.data.collection.dataaccess.entities;

import java.util.HashSet;
import java.util.Set;

import gr.cite.regional.data.collection.dataaccess.types.JsonUserType;
import gr.cite.regional.data.collection.dataaccess.types.XMLType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

@javax.persistence.Entity
@Table(name="\"DataModel\"")
@TypeDefs({
	@TypeDef(name = "XMLType", typeClass = XMLType.class),
	@TypeDef(name = "JsonUserType", typeClass = JsonUserType.class)
})
public class DataModel implements Entity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="DataModel_generator")
	@SequenceGenerator(name="DataModel_generator", sequenceName="datamodel_id_seq", allocationSize = 1)
	@Column(name="\"ID\"", updatable = false, nullable = false)
	private Integer id;
	
	@Column(name = "\"Label\"", nullable = true, length = 500)
	private String label;
	
	@Column(name = "\"Version\"", nullable = true, length = 20)
	private String version;
	
	@Type(type="XMLType")
	@Column(name="\"Definition\"", columnDefinition = "xml")
	private String definition;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="\"Domain\"", nullable = false)
	private Domain domain;
	
	@Column(name="\"Previous\"", nullable = true)
	private Integer previous;
	
	@Column(name = "\"URI\"", nullable = true, length = 1000)
	private String uri;
	
	@Type(type = "JsonUserType", parameters = { @Parameter(name = "classType", value = "gr.cite.regional.data.collection.dataaccess.entities.Properties")} )
	@Column(name="\"Properties\"", nullable = true, columnDefinition = "json")
	private Properties properties;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dataModel", cascade=CascadeType.ALL)
	private Set<DataCollection> dataCollections = new HashSet<>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dataModel", cascade=CascadeType.ALL)
	private Set<Constraint> constraints = new HashSet<>();
	
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

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	public Integer getPrevious() {
		return previous;
	}

	public void setPrevious(Integer previous) {
		this.previous = previous;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public Set<DataCollection> getDataCollections() {
		return dataCollections;
	}

	public void setDataCollections(Set<DataCollection> dataCollections) {
		this.dataCollections = dataCollections;
	}

	public Set<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(Set<Constraint> constraints) {
		this.constraints = constraints;
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
		DataModel other = (DataModel) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
