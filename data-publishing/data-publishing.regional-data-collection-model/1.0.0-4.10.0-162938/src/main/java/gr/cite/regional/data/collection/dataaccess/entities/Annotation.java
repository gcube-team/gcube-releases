package gr.cite.regional.data.collection.dataaccess.entities;


import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Type;

@javax.persistence.Entity
@Table(name = "\"Annotation\"")
public class Annotation implements Entity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="annotation_generator")
	@SequenceGenerator(name="annotation_generator", sequenceName="annotation_id_seq", allocationSize = 1)
	@Column(name="\"ID\"", updatable = false, nullable = false)
	private Integer id;
	
	@Column(name="\"Thread\"", nullable = false)
	private Integer thread;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"Timestamp\"", nullable = false)
	private Date timestamp;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="\"Author\"", nullable = false)
	private UserReference author;
	
	@Column(name = "\"Status\"", nullable = true)
	private Integer status;
	
	@Column(name = "\"Subject\"", nullable = true, length = 500)
	private String subject;
	
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "\"Body\"", nullable = true)
	private String body;
	
	@Column(name = "\"IsPublic\"", nullable = true)
	private Integer isPublic;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="\"Domain\"", nullable = false)
	private Domain domain;
	
	@Column(name = "\"TargetClass\"", nullable = false)
	private Integer targetClass;
	
	@Column(name = "\"TargetIdentifier\"", nullable = false, length = 500)
	private String targetIdentifier;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getThread() {
		return thread;
	}

	public void setThread(Integer thread) {
		this.thread = thread;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public UserReference getAuthor() {
		return author;
	}

	public void setAuthor(UserReference author) {
		this.author = author;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Integer getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Integer isPublic) {
		this.isPublic = isPublic;
	}

	public Integer getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Integer targetClass) {
		this.targetClass = targetClass;
	}

	public String getTargetIdentifier() {
		return targetIdentifier;
	}

	public void setTargetIdentifier(String targetIdentifier) {
		this.targetIdentifier = targetIdentifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		Integer result = 1;
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
		Annotation other = (Annotation) obj;
		if (id != other.id)
			return false;
		return true;
	}
}