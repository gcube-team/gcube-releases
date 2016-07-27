/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Coverage-Facet
 */
public interface CoverageFacet extends Facet {
	
	public static final String NAME = CoverageFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Collect any \"extent\"-related information.";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public ValueSchema getSpatial();
	
	public void setSpatial(ValueSchema spatial);
	
	@ISProperty
	public ValueSchema getTemporal();
	
	public void setTemporal(ValueSchema temporal);

}
