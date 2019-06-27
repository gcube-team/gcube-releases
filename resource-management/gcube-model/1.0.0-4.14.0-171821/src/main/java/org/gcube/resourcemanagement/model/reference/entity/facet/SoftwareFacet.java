/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.facet;

import org.gcube.informationsystem.model.reference.annotations.ISProperty;
import org.gcube.informationsystem.model.reference.annotations.Key;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.resourcemanagement.model.impl.entity.facet.SoftwareFacetImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Software_Facet
 */
@Key(fields={NameProperty.NAME_PROPERTY, SoftwareFacet.GROUP_PROPERTY, SoftwareFacet.VERSION_PROPERTY})
@JsonDeserialize(as=SoftwareFacetImpl.class)
public interface SoftwareFacet extends Facet, NameProperty {
	
	public static final String NAME = "SoftwareFacet"; // SoftwareFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Capture SW related features";
	public static final String VERSION = "1.0.0";
	
	public static final String GROUP_PROPERTY = "group";
	public static final String VERSION_PROPERTY = "version";
	
	@ISProperty(name=GROUP_PROPERTY, mandatory=true, nullable=false)
	public String getGroup();
	
	public void setGroup(String group);
	
	@ISProperty(name=VERSION_PROPERTY, mandatory=true, nullable=false)
	public String getVersion();
	
	public void setVersion(String version);
	
	@ISProperty
	public String getDescription();
	
	public void setDescription(String description);
	
	@ISProperty
	public String getQualifier();
	
	public void setQualifier(String qualifier);
	
	@ISProperty
	public boolean isOptional();
	
	public void setOptional(boolean optional);

}
