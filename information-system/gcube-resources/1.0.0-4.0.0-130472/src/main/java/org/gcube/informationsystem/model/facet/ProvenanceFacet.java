/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Provenance-Facet
 * Goal: to collect information related with resource lineage/provenance. 
 */
public interface ProvenanceFacet extends Facet {
	
	public static final String NAME = ProvenanceFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Collect information related with resource lineage/provenance";
	public static final String VERSION = "1.0.0";
	
	public enum Relationship {
		wasDerivedFrom, wasGeneratedBy /* .... */
	}
	
	@ISProperty
	public Relationship getRelationship();
	
	public void setRelationship(Relationship relationship);
	
	@ISProperty
	public String getReference();
	
	public void setReference(String reference);
	
	@ISProperty
	public String getProvenanceDocumentFormat();
	
	public void setProvenanceDocumentFormat(String provenanceDocumentFormat);

	@ISProperty
	public String getProvenanceDocument();
	
	public void setDocument(String provenanceDocument);
	
}
