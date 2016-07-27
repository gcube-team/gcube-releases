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
public interface MemoryFacet extends Facet {
	
	public static final String NAME = MemoryFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Memory information";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public long getSize();
	
	public void setSize(long size);
	
	@ISProperty
	public long getUsed();
	
	public void setUsed(long used);
	
	@ISProperty
	public String getUnit();
	
	public void setUnit(String unit);
}
