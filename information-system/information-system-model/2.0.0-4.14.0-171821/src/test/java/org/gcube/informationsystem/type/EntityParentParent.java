package org.gcube.informationsystem.type;

import org.gcube.informationsystem.model.reference.annotations.ISProperty;
import org.gcube.informationsystem.model.reference.entity.Facet;

public interface EntityParentParent extends Facet {

	@ISProperty(name="asd", description="desc")
	public String getAs();
	
	
	
}
