package org.gcube.rest.commons.db.dao.core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@MappedSuperclass
public abstract class BaseRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@Type(type = "java.lang.Long")
	@GenericGenerator(name="SEQ_STORE" , strategy="increment")
	@GeneratedValue(generator = "SEQ_STORE")
	private long id;

	@Column(name = "description")
	private String description = null;

	public BaseRecord() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
