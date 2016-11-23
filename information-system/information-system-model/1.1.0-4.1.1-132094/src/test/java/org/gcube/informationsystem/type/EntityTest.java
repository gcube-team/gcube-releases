package org.gcube.informationsystem.type;

import org.gcube.informationsystem.model.annotations.ISProperty;


public interface EntityTest extends EntityParent {

	@ISProperty(nullable=false)
	public String getNotnullable();
	
	
	@ISProperty(mandatory=true)
	public String getMandatory();
	
	@ISProperty(name="different", description="desc")
	public String getMyname();

}
