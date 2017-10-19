package gr.cite.geoanalytics.dataaccess.entities.tenant;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

import java.util.Date;
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
@Table(name="\"TenantActivation\"")
public class TenantActivation implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {
	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"TENA_ID\"", nullable = false)
	private UUID id = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"TENA_Start\"", nullable = true)               //start=end=null for non-expiring activations
	private Date start = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"TENA_End\"", nullable = true)			
	private Date end = null;

	@Column(name="\"TENA_IsActive\"", nullable = false)
	private short isActive = 1;
	
	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Column(name="\"TENA_ActivationConfig\"", columnDefinition = "xml") //DEPWARN possible db portability issue
	private String activationConfig = null; //XML
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"TENA_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"TENA_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="\"TENA_Creator\"", nullable = false)
	private Principal creator = null;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="\"TENA_Tenant\"", nullable = false)
	private Tenant tenant = null;
	
//	@ManyToOne(fetch = FetchType.LAZY, optional = true) //nullable, customers can be active for all shapes
//	@JoinColumn(name="\"TENA_Shape\"")
	@Column(name="\"TENA_Shape\"", nullable = false)
	private UUID shapeID = null;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean getIsActive() {
		return isActive == 0 ? false : true;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = (short) (isActive == true ? 1 : 0);
	}

	public String getActivationConfig() {
		return activationConfig;
	}

	public void setActivationConfig(String activationConfig) {
		this.activationConfig = activationConfig;
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

	public UUID getShapeID() {
		return shapeID;
	}

	public void setShapeID(UUID shapeID) {
		this.shapeID = shapeID;
	}

	@Override
	public String toString() {
		return "CustomerActivation(" + "id=" + getId() + " start=" + getStart() + " end=" + getEnd() + " isActive="
				+ getIsActive() + " activationConfig=" + getActivationConfig() + " creation=" + getCreationDate()
				+ "lastUpdate=" + getLastUpdate() + " creator=" + (creator != null ? creator.getId() : null)
				+ " customer=" + (tenant != null ? tenant.getId() : null) + " shape="
				+ (shapeID != null ? shapeID.toString() : null);
	}

}
