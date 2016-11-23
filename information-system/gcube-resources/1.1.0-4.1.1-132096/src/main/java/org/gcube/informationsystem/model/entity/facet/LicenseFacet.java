/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import java.net.URL;

import org.gcube.informationsystem.impl.entity.facet.LicenseFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#License_Facet
 */
@JsonDeserialize(as=LicenseFacetImpl.class)
public interface LicenseFacet extends Facet {

	public static final String NAME = "LicenseFacet"; // LicenseFacet.class.getSimpleName();
	public static final String DESCRIPTION = "License information";
	public static final String VERSION = "1.0.0";
	
	public static final String TEXT_URL_PROPERTY = "textURL";
	
	@ISProperty(mandatory=true, nullable=false)
	public String getName();
	
	public void setName(String name);

	@ISProperty(name=TEXT_URL_PROPERTY, mandatory=true, nullable=false)
	public URL getTextURL();
	
	public void setTextURL(URL textURL);
	
}
