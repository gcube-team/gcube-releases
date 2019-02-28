/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.facet;

import java.net.URL;

import org.gcube.informationsystem.model.reference.annotations.ISProperty;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.resourcemanagement.model.impl.entity.facet.ContactReferenceFacetImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Contact_Reference_Facet
 */
@JsonDeserialize(as=ContactReferenceFacetImpl.class)
public interface ContactReferenceFacet extends Facet {
	
	public static final String NAME = "ContactReferenceFacet"; // ContactReferenceFacet.class.getSimpleName();
	public static final String DESCRIPTION = "This facet is expected to "
			+ "capture contact information";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public URL getWebsite();
	
	public void setWebsite(URL website);
	
	@ISProperty
	public String getAddress();
	
	public void setAddress(String address);
	
	@ISProperty
	public String getPhoneNumber();
	
	public void setPhoneNumber(String phoneNumber);

}
