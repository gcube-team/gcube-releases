package gr.cite.geoanalytics.dataaccess.entities.auditing;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name="\"Auditing\"")
public class Auditing implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable
{
	public enum AuditingType {
		LastDataUpdate((short)0), LastUserAction((short)1), LayerIllegalAccessAttempt((short)2),
		LayerZoomIllegalAccessAttempt((short)3), IllegalRequestAttempt((short)4),
		LastUserLogin((short)5), LastUnsuccessfulUserLogin((short)6), LastUserPasswordRequest((short)7), 
		DOSAttack((short)8);
		
		private final short typeCode;
		
		private static final Map<Short,AuditingType> lookup  = new HashMap<Short,AuditingType>();
		 
		static {
		      for(AuditingType t : EnumSet.allOf(AuditingType.class))
		           lookup.put(t.typeCode(), t);
		 }
		
		AuditingType(short typeCode) {
			this.typeCode = typeCode;
		}
		
		public short typeCode() { return typeCode; }
	
		public static AuditingType fromTypeCode(short typeCode) {
			return lookup.get(typeCode);
		}
	};
	
	
	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"AUD_ID\"", nullable = false)
	private UUID id = null;// = UUIDGenerator.randomUUID();
	
	@Column(name="\"AUD_Type\"", nullable = false)
	private short type = 0;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"AUD_Date\"", nullable=false)
	private Date date = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"AUD_CreationDate\"", nullable=false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"AUD_LastUpdate\"", nullable=false)
	private Date lastUpdate = null;
	
	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Column(name="\"AUD_Data\"", columnDefinition = "xml" ) //DEPWARN possible db portability issue
	private String data = null;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="\"AUD_Creator\"", nullable = false)
	private Principal creator = null;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	@JoinColumn(name="\"AUD_Tenant\"")
	private Tenant tenant = null;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="\"AUD_Principal\"", nullable=false)
	private Principal principal = null;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public AuditingType getType() {
		return AuditingType.fromTypeCode(this.type);
	}

	public void setType(AuditingType type) {
		this.type = type.typeCode();
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Auditing(" + "id=" + getId() + " type=" + getType() + " date=" + getDate() + " creation="
				+ getCreationDate() + "lastUpdate=" + getLastUpdate() + "data=" + getData() + " creator="
				+ (creator != null ? creator.getId() : null) + " customer=" + (tenant != null ? tenant.getId() : null)
				+ " user=" + (principal != null ? principal.getId() : null);
	}
}
