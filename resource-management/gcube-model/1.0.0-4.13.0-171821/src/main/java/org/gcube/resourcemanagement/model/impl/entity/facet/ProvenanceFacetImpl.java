/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.facet;

import java.net.URI;
import java.util.UUID;

import org.gcube.informationsystem.model.impl.entity.FacetImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.ProvenanceFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=ProvenanceFacet.NAME)
public class ProvenanceFacetImpl extends FacetImpl implements ProvenanceFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 1014553736569877775L;
	
	protected Relationship relationship;
	protected UUID reference;
	protected String document;
	protected URI documentSchema;
	
	/**
	 * @return the relationship
	 */
	@Override
	public Relationship getRelationship() {
		return relationship;
	}
	
	/**
	 * @param relationship the relationship to set
	 */
	@Override
	public void setRelationship(Relationship relationship) {
		this.relationship = relationship;
	}
	
	/**
	 * @return the reference
	 */
	@Override
	public UUID getReference() {
		return reference;
	}
	
	/**
	 * @param reference the reference to set
	 */
	@Override
	public void setReference(UUID reference) {
		this.reference = reference;
	}
	/**
	 * @return the document
	 */
	@Override
	public String getDocument() {
		return document;
	}
	
	/**
	 * @param document the document to set
	 */
	@Override
	public void setDocument(String document) {
		this.document = document;
	}
	
	/**
	 * @return the documentSchema
	 */
	@Override
	public URI getDocumentSchema() {
		return documentSchema;
	}
	
	/**
	 * @param documentSchema the documentSchema to set
	 */
	@Override
	public void setDocumentSchema(URI documentSchema) {
		this.documentSchema = documentSchema;
	}
	
}
