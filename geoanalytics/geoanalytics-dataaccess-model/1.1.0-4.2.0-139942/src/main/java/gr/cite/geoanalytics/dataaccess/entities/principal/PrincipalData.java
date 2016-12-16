package gr.cite.geoanalytics.dataaccess.entities.principal;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;

@Entity
@Table(name="\"PrincipalData\"")
public class PrincipalData implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {
	
	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"PRNCD_ID\"", nullable = false)
	private UUID id = null;
	
	@Lob
	@Type(type = "org.hibernate.type.TextType") //DEPWARN dependency to Hibernate and PostgreSQL (workaround for text~~bigint hibernate bug)
	@Column(name="\"PRNCD_Credential\"", nullable = true)
	private String credential = null;
	
	@Column(name="\"PRNCD_Email\"", nullable = true, length = 250)
	private String email = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PRNCD_ExpirationDate\"", nullable = false)
	private Date expirationDate = null;
	
	@Column(name="\"PRNCD_FullName\"", nullable = false, length = 250)
	private String fullName = null;
	
	@Column(name="\"PRNCD_Initials\"", nullable = false, length = 10)
	private String initials = null;
	
	@Column(name="\"PRNCD_IsActive\"", nullable = false)
	private short isActive = 0;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PRNCD_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PRNCD_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "principalData", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Principal> principals = new HashSet<Principal>(0);

	public Set<Principal> getPrincipals() {
		return principals;
	}
	
	public void setPrincipals(Set<Principal> principals) {
		this.principals = principals;
	}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credentials) {
		this.credential = credentials;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public short getIsActive() {
		return isActive;
	}

	public void setIsActive(short isActive) {
		this.isActive = isActive;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
