package gr.cite.geoanalytics.dataaccess.entities.user;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Index;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "\"User\"", indexes = { // quotes used to escape db reserved word
		@Index(name = "fki_UserCreator", columnList = "US_Creator"),
		@Index(name = "fki_UserCustomer", columnList = "US_Customer") })
public class UserOld implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {
	@Id
	@Type(type = "org.hibernate.type.PostgresUUIDType") // DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "US_ID", nullable = false)
	private UUID id = null;

	@Column(name = "US_SysName", nullable = false, length = 250)
	private String systemName = null;

	@Column(name = "US_FullName", nullable = false, length = 250)
	private String fullName = null;

	@Column(name = "US_Initials", nullable = false, length = 10)
	private String initials = null;

	@Column(name = "US_eMail", length = 250)
	private String eMail = null;

	@Lob
	@Type(type = "org.hibernate.type.TextType") // DEPWARN dependency to Hibernate and PostgreSQL (workaround for text~~bigint hibernate bug)
	@Column(name = "US_Credential")
	private String credential = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "US_Creation", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "US_ExpirationDate", nullable = false)
	private Date expirationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "US_LastUpdate", nullable = false)
	private Date lastUpdate = null;

	@Column(name = "US_IsActive", nullable = false)
	private short isActive = 1;

	@Type(type = "gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") // DEPWARN XML  Type:  Hibernate dependency, replace when JPA 2.1 annotation is available
	@Column(name = "US_Rights", columnDefinition = "xml") // DEPWARN possible db portability issue
	private String rights = null;

	@Column(name = "US_NotificationId", length = 250)
	private String notificationId = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "US_Creator", nullable = false)
	private UserOld creator = null;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "US_Customer")
	private Tenant tenant = null;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
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

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public boolean getIsActive() {
		return isActive == 0 ? false : true;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = (short) (isActive == true ? 1 : 0);
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public UserOld getCreator() {
		return creator;
	}

	public void setCreator(UserOld creator) {
		this.creator = creator;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public Tenant getCustomer() {
		return tenant;
	}

	public void setCustomer(Tenant tenant) {
		this.tenant = tenant;
	}

	@Override
	public String toString() {
		return "User(" + "id=" + getId() + " systemName=" + getSystemName() + " fullName=" + getFullName()
				+ " initials=" + getInitials() + " isActive=" + getIsActive() + " eMail=" + geteMail() + " credential="
				+ getCredential() + " rights=" + getRights() + " notificationId=" + getNotificationId() + " creation="
				+ getCreationDate() + " lastUpdate=" + getLastUpdate() + " expirationDate=" + getExpirationDate()
				+ " creator=" + (creator != null ? creator.getId() : null) + " customer="
				+ (tenant != null ? tenant.getId() : null);
	}
}
