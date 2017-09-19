//package gr.cite.geoanalytics.dataaccess.entities.shape;
//
//import java.io.Serializable;
//import java.util.UUID;
//
//import org.hibernate.annotations.Type;
//
//public class ShapeLayerPK implements Serializable
//{
//
//	private static final long serialVersionUID = 7056173685306683281L;
//	
//	@Type(type="org.hibernate.type.PostgresUUIDType")
//	private UUID shape;
//	@Type(type="org.hibernate.type.PostgresUUIDType")
//	private UUID layerID;
//	
//	@Override
//	public boolean equals(Object other)
//	{
//		if (this == other)
//	        return true;
//	    if (!(other instanceof ShapeLayerPK))
//	        return false;
//	    ShapeLayerPK castOther = (ShapeLayerPK) other;
//	    return shape.equals(castOther.shape) && layerID.equals(castOther.layerID);
//	}
//	
//	@Override
//	public int hashCode() 
//	{
//	    final int prime = 31;
//	    int hash = 17;
//	    hash = hash * prime + this.shape.hashCode();
//	    hash = hash * prime + this.layerID.hashCode();
//	    return hash;
//	}
//}
