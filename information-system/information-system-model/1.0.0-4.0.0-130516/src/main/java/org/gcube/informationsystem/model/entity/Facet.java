package org.gcube.informationsystem.model.entity;

import org.gcube.informationsystem.model.annotations.Abstract;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets
 */
@Abstract
public abstract interface Facet extends Entity {
	
	public static final String NAME = Facet.class.getSimpleName();
	public static final String DESCRIPTION = "This is the base class for Facet";
	public static final String VERSION = "1.0.0";
	
}
