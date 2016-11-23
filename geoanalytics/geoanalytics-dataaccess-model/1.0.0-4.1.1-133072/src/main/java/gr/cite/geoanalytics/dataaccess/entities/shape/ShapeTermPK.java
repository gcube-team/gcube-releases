package gr.cite.geoanalytics.dataaccess.entities.shape;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.Type;

public class ShapeTermPK implements Serializable
{

	private static final long serialVersionUID = 7056173685306683281L;
	
	@Type(type="org.hibernate.type.PostgresUUIDType")
	private UUID shape;
	@Type(type="org.hibernate.type.PostgresUUIDType")
	private UUID term;
	
	@Override
	public boolean equals(Object other)
	{
		if (this == other)
	        return true;
	    if (!(other instanceof ShapeTermPK))
	        return false;
	    ShapeTermPK castOther = (ShapeTermPK) other;
	    return shape.equals(castOther.shape) && term.equals(castOther.term);
	}
	
	@Override
	public int hashCode() 
	{
	    final int prime = 31;
	    int hash = 17;
	    hash = hash * prime + this.shape.hashCode();
	    hash = hash * prime + this.term.hashCode();
	    return hash;
	}
}
