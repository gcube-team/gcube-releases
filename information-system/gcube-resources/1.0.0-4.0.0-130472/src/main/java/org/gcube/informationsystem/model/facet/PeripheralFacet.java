/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public interface PeripheralFacet extends Facet {
	
	public static final String NAME = PeripheralFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Peripheral information";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public String getModel();
	
	public void setModel(String model);
	
	@ISProperty
	public String getVendor();
	
	public void setVendor(String vendor);
	
}
