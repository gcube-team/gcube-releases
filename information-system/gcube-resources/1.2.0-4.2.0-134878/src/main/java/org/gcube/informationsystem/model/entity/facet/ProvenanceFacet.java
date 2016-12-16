/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import java.net.URI;
import java.util.UUID;

import org.gcube.informationsystem.impl.entity.facet.ProvenanceFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Provenance_Facet
 */
@JsonDeserialize(as=ProvenanceFacetImpl.class)
public interface ProvenanceFacet extends Facet {
	
	public static final String NAME = "ProvenanceFacet"; // ProvenanceFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Collect information related with resource lineage/provenance";
	public static final String VERSION = "1.0.0";
	
	public enum Relationship {
		wasDerivedFrom, wasGeneratedBy /* .... */
	}
	
	@ISProperty
	public Relationship getRelationship();
	
	public void setRelationship(Relationship relationship);
	
	@ISProperty
	public UUID getReference();
	
	public void setReference(UUID reference);
	
	@ISProperty
	public String getDocument();
	
	public void setDocument(String document);

	@ISProperty
	public URI getDocumentSchema();
	
	public void setDocumentSchema(URI documentSchema);
	
}
