package gr.cite.geoanalytics.dataaccess.entities.accounting;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "\"Accounting\"")
public class Accounting implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {
	public enum AccountingType {
		Payment((short) 0);

		private final short typeCode;

		private static final Map<Short, AccountingType> lookup = new HashMap<Short, AccountingType>();

		static {
			for (AccountingType t : EnumSet.allOf(AccountingType.class))
				lookup.put(t.typeCode(), t);
		}

		AccountingType(short typeCode) {
			this.typeCode = typeCode;
		}

		public short typeCode() {
			return typeCode;
		}

		public static AccountingType fromTypeCode(short typeCode) {
			return lookup.get(typeCode);
		}
	};

	@Id
	@Type(type = "org.hibernate.type.PostgresUUIDType") // DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"ACC_ID\"", nullable = false)
	private UUID id = null;// = UUIDGenerator.randomUUID();

	@Column(name = "\"ACC_Type\"", nullable = false)
	private short type = 0;

	@Column(name = "\"ACC_Units\"", nullable = false)
	private float units = 0.0f; // currency (could be of money type in db)

	@Type(type = "gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") // DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Column(name = "\"ACC_ReferenceData\"", columnDefinition = "xml") // DEPWARN possible db portability issue
	private String referenceData = null; // xml

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"ACC_Date\"", nullable = false)
	private Date date = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"ACC_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"ACC_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"ACC_Creator\"", nullable = false)
	private Principal creator = null;

	/**
	 * One accounting entry per customer per user. Note that this also allows
	 * multiple entries with no user, but nothing should change in the
	 * processing of such entries (e.g. aggregation)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"ACC_Tenant\"", nullable = false)
	private Tenant tenant = null;

	/**
	 * One accounting entry per user, since users are defined by strictly one
	 * customer.
	 */
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "\"ACC_Principal\"")
	private Principal principal = null;

	@Column(name = "\"ACC_IsValid\"")
	private Short isValid = 1;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public AccountingType getType() {
		return AccountingType.fromTypeCode(this.type);
	}

	public void setType(AccountingType type) {
		this.type = type.typeCode();
	}

	public float getUnits() {
		return units;
	}

	public void setUnits(float units) {
		this.units = units;
	}

	public String getReferenceData() {
		return referenceData;
	}

	public void setReferenceData(String referenceData) {
		this.referenceData = referenceData;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public boolean getIsValid() {
		if (isValid == null)
			return true;
		return isValid == 0 ? false : true;
	}

	public void setIsValid(boolean isValid) {
		this.isValid = (short) (isValid == true ? 1 : 0);
	}

	@Override
	public String toString() {
		return "Accounting(" + "id=" + getId() + " type=" + getType() + " units=" + getUnits() + " referenceData="
				+ getReferenceData() + " date=" + getDate() + " creation=" + getCreationDate() + "lastUpdate="
				+ getLastUpdate() + " creator=" + (creator != null ? creator.getId() : null) + " customer="
				+ (tenant != null ? tenant.getId() : null) + " user=" + (principal != null ? principal.getId() : null);
	}
}
