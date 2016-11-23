/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import org.gcube.informationsystem.impl.entity.facet.CPUFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#CPU_Facet
 */
@JsonDeserialize(as=CPUFacetImpl.class)
public interface CPUFacet extends Facet {
	
	public static final String NAME = "CPUFacet"; //CPUFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Describes CPU information";
	public static final String VERSION = "1.0.0";
	
	@ISProperty(mandatory=true, nullable=false)
	public String getModel();
	
	public void setModel(String model);
	
	@ISProperty(mandatory=true, nullable=false)
	public String getVendor();
	
	public void setVendor(String vendor);
	
	@ISProperty(mandatory=true, nullable=false)
	public String getClockSpeed();
	
	public void setClockSpeed(String clockSpeed);
	
}
