package org.gcube.data.analysis.tabulardata.metadata.tabularresource;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class EditEntry implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;
	
	@Column(name = "usr")
	private String user;
	
	@OneToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="tr_id")
	private StorableTabularResource tabularResource;
	
	protected EditEntry(){};
	
	public EditEntry(String user, StorableTabularResource tabularResource) {
		super();
		this.user = user;
		this.tabularResource = tabularResource;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the tabularResource
	 */
	public StorableTabularResource getTabularResource() {
		return tabularResource;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EditEntry [id=" + id + ", user=" + user + "]";
	}
		
}
