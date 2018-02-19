package gr.cite.regional.data.collection.dataaccess.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
@Table(name="\"DataSubmission\"")
@TypeDef(name = "XMLType", typeClass = XMLType.class)
public class DataSubmission implements Entity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="datasubmission_generator")
	@SequenceGenerator(name="datasubmission_generator", sequenceName="datasubmission_id_seq", allocationSize = 1)
	@Column(name="\"ID\"", updatable = false, nullable = false)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="\"Domain\"", nullable = false)
	private Domain domain;
	
	@Column(name="\"Status\"", nullable = false)
	private Integer status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"SubmissionTimestamp\"", nullable = false)
	private Date submissionTimestamp;
	
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "\"Comment\"", nullable = true)
	private String comment;
	
	@Type(type="XMLType")
	@Column(name="\"Attributes\"", columnDefinition = "xml")
	private String attributes;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="\"Owner\"", nullable = false)
	private UserReference owner;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"CompletionTimestamp\"", nullable = false)
	private Date completionTimestamp;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="\"DataCollection\"", nullable = false)
	private DataCollection dataCollection;
	
	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "dataSubmission", cascade=CascadeType.ALL)
	@Transient
	private List<Cdt> data = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getSubmissionTimestamp() {
		return submissionTimestamp;
	}

	public void setSubmissionTimestamp(Date submissionTimestamp) {
		this.submissionTimestamp = submissionTimestamp;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public UserReference getOwner() {
		return owner;
	}

	public void setOwner(UserReference owner) {
		this.owner = owner;
	}

	public Date getCompletionTimestamp() {
		return completionTimestamp;
	}

	public void setCompletionTimestamp(Date completionTimestamp) {
		this.completionTimestamp = completionTimestamp;
	}

	public DataCollection getDataCollection() {
		return dataCollection;
	}

	public void setDataCollection(DataCollection dataCollection) {
		this.dataCollection = dataCollection;
	}
	
	public List<Cdt> getData() {
		return data;
	}
	
	public void setData(List<Cdt> data) {
		this.data = data;
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
		DataSubmission other = (DataSubmission) obj;
		if (id != other.id)
			return false;
		return true;
	}
}