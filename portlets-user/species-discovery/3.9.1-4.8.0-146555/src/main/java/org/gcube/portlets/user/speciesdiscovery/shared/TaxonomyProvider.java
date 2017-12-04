package org.gcube.portlets.user.speciesdiscovery.shared;

import java.util.List;

public interface TaxonomyProvider {

	public List<? extends TaxonomyInterface> getParents();

	public String getBaseTaxonValue();

	public void setClassID(String value);

	public void setFamilyID(String value);

	public void setGenusID(String value);

	public void setKingdomID(String value);

	public void setOrderID(String value);

	public void setPhylumID(String value);

	public void setSpeciesID(String value);

}