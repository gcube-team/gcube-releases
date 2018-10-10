package org.gcube.informationsystem.type;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

public interface EntityParentParent extends Facet {

	@ISProperty(name="asd", description="desc")
	public String getAs();
	
	
	
}
