/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://redmine.d4science.org/projects/bluebridge/wiki/Facets#Functionality-Related-Facet
 */
public interface FunctionalityRelatedFacet extends Facet {

	public static final String NAME = FunctionalityRelatedFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Capture what are the facilities supported challenging because of the different audiences";
	public static final String VERSION = "1.0.0";
	
	@ISProperty
	public String getName();
	
	public void setName(String name);
	
	@ISProperty
	public String getDescription();
	
	public void setDescription(String description);
	
	@ISProperty
	public String getInput();
	
	public void setInput(String input);
	
	@ISProperty
	public String getOutput();
	
	public void setOutput(String output);
	
}
