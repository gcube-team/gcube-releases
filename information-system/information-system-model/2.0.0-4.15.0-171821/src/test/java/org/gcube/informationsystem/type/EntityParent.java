package org.gcube.informationsystem.type;

import org.gcube.informationsystem.model.reference.annotations.ISProperty;

public interface EntityParent extends EntityParentParent {
	
	@ISProperty(nullable=false)
	public String getNullableP();
	
	@ISProperty(mandatory=false)
	public String getMandatoryP();
	
	@ISProperty(name="different", description="desc")
	public String getMynameP();
	
}
