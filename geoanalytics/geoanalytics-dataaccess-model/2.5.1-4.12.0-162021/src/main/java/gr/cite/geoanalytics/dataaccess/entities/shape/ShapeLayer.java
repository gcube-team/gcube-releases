//package gr.cite.geoanalytics.dataaccess.entities.shape;
//
//import gr.cite.geoanalytics.dataaccess.entities.Stampable;
//import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
//
//import java.util.Date;
//import java.util.UUID;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.IdClass;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.OneToOne;
//import javax.persistence.Table;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//
//import org.hibernate.annotations.Type;
//
//@Entity
//@IdClass(ShapeLayerPK.class)
//@Table(name="\"ShapeLayer\"")
//public class ShapeLayer implements gr.cite.geoanalytics.dataaccess.entities.Entity, Stampable
//{
//	@Id
//	@OneToOne
//	@JoinColumn(name = "\"SHPT_Shape\"", nullable = false)
//	private Shape shape = null;
//	
//	@Id
//	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
//	@Column(name = "\"SHPT_LAYER_ID\"", nullable = false)
//	private UUID layerID = null;
//	
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name = "\"SHPT_CreationDate\"", nullable = false)
//	private Date creationDate = null;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name = "\"SHPT_LastUpdate\"", nullable = false)
//	private Date lastUpdate = null;
//	
//	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
//	@Column(name="\"SHPT_Creator\"", nullable = false)
//	private UUID creatorID = null;
//	
//
////	@AssociationOverrides({
////		@AssociationOverride(name="shape", 
////			joinColumns = @JoinColumn(name = "SHPT_Shape")),
////		@AssociationOverride(name="term", 
////			joinColumns = @JoinColumn(name = "SHPT_Term")) })
//	
//	public Shape getShape() {
//		return shape;
//	}
//
//	public void setShape(Shape shape) {
//		this.shape = shape;
//	}
//
//	public UUID getLayerID() {
//		return layerID;
//	}
//
//	public void setLayerID(UUID layerID) {
//		this.layerID = layerID;
//	}
//
//	public Date getCreationDate() {
//		return creationDate;
//	}
//
//	public void setCreationDate(Date creationDate) {
//		this.creationDate = creationDate;
//	}
//
//	public Date getLastUpdate() {
//		return lastUpdate;
//	}
//
//	public void setLastUpdate(Date lastUpdate) {
//		this.lastUpdate = lastUpdate;
//	}
//
//	public UUID getCreatorID() {
//		return creatorID;
//	}
//
//	public void setCreatorID(UUID creatorID) {
//		this.creatorID = creatorID;
//	}
//
//	@Override
//	public String toString() {
//		return "ShapeTerm(" + " shape=" + (shape != null ? shape : null) + " term=" + (layerID != null ? layerID : null)
//				+ " creation=" + getCreationDate() + " lastUpdate=" + getLastUpdate() + " creator="
//				+ (creatorID != null ? creatorID.toString() : null);
//	}
//}
