/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import java.net.URL;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Contact-Facet
 */
public interface ContactFacet extends Facet {
	
	public static final String NAME = ContactFacet.class.getSimpleName();
	public static final String DESCRIPTION = "This facet is expected to "
			+ "capture contact information";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public String getName();
	
	public void setName(String name);
	
	@ISProperty
	public String getEMail();
	
	public void setEMail(String eMail);
	
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
