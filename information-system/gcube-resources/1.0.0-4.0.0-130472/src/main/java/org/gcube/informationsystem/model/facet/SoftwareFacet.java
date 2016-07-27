/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Software-Facet
 * Goal: to capture SW related features 
 */
public interface SoftwareFacet extends Facet {
	
	public static final String NAME = SoftwareFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Capture SW related features";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public String getName();
	
	public void setName(String name);
	
	@ISProperty
	public String getGroup();
	
	public void setGroup(String group);
	
	@ISProperty
	public String getVersion();
	
	public void setVersion(String version);
	
	@ISProperty
	public String getDescription();
	
	public void setDescription(String description);
	
	@ISProperty
	public String getQualifier();
	
	public void setQualifier(String qualifier);
	
	@ISProperty
	public String getRole();
	
	public void setRole(String role);
	
	@ISProperty
	public boolean isOptional();
	
	public void setOptional(boolean optional);

}
