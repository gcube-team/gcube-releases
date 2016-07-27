/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#License-Facet
 */
public interface LicenseFacet extends ValueSchema, Facet {

	public static final String NAME = LicenseFacet.class.getSimpleName();
	public static final String DESCRIPTION = "License information";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public ValueSchema getLicense();
	
	public void setLicense(ValueSchema license);
	
	
}
