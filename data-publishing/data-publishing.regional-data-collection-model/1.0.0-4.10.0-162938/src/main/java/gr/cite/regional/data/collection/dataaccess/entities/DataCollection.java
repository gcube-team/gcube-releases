package gr.cite.regional.data.collection.dataaccess.entities;

import java.util.Date;
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
@Table(name="\"DataCollection\"")
@TypeDef(name = "XMLType", typeClass = XMLType.class)
public class DataCollection implements Entity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="datacollection_generator")
	@SequenceGenerator(name="datacollection_generator", sequenceName="datacollection_id_seq", allocationSize = 1)
	@Column(name="\"ID\"", updatable = false, nullable = false)
	private Integer id;
	
	@Column(name = "\"Label\"", nullable = true, length = 100)
	private String label;
	
	@Column(name="\"Status\"", nullable = false)
	private Integer status;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "\"StartDate\"", nullable = true)
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "\"EndDate\"", nullable = true)
	private Date endDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="\"Domain\"", nullable = false)
	private Domain domain;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="\"DataModel\"", nullable = false)
	private DataModel dataModel;
	
	@Type(type="XMLType")
	@Column(name="\"Attributes\"", columnDefinition = "xml")
	private String attributes;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dataCollection", cascade=CascadeType.ALL)
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
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public DataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
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
		DataCollection other = (DataCollection) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
