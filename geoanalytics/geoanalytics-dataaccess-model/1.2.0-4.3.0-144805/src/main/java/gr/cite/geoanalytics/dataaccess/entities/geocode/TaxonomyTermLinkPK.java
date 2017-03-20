package gr.cite.geoanalytics.dataaccess.entities.geocode;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.Type;

public class TaxonomyTermLinkPK implements Serializable {

	private static final long serialVersionUID = 7248177294975403198L;
	
	@Type(type="org.hibernate.type.PostgresUUIDType")
	private UUID sourceTerm = null;
	@Type(type="org.hibernate.type.PostgresUUIDType")
	private UUID destinationTerm = null;
	
	public TaxonomyTermLinkPK() { }
	
	public TaxonomyTermLinkPK(UUID sourceTerm, UUID destinationTerm)
	{
		this.sourceTerm = sourceTerm;
		this.destinationTerm = destinationTerm;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
	        return true;
	    if (!(other instanceof TaxonomyTermLinkPK))
	        return false;
	    TaxonomyTermLinkPK castOther = (TaxonomyTermLinkPK) other;
	    return sourceTerm.equals(castOther.sourceTerm) && destinationTerm.equals(castOther.destinationTerm);
	}
	
	@Override
	public int hashCode()  {
	    final int prime = 31;
	    int hash = 17;
	    hash = hash * prime + this.sourceTerm.hashCode();
	    hash = hash * prime + this.destinationTerm.hashCode();
	    return hash;
	}
}
