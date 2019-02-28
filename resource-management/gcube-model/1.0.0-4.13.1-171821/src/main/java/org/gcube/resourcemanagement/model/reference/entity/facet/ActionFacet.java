package org.gcube.resourcemanagement.model.reference.entity.facet;

import org.gcube.informationsystem.model.reference.annotations.ISProperty;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.resourcemanagement.model.impl.entity.facet.ActionFacetImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * This facet is expected to capture information on how to instantiate a {@link SoftwareFacet}.
 * 
 * @author Manuele Simi (ISTI-CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Action_Facet

 */
@JsonDeserialize(as=ActionFacetImpl.class)
public interface ActionFacet extends Facet, NameProperty {

	public static final String NAME = "ActionFacet"; 
	public static final String DESCRIPTION = "This facet is expected to "
			+ "capture information on which action perform while a resource is added or removed from a context.";
	public static final String VERSION = "1.0.0";
		
	public enum TYPE {ANSIBLE, EXECUTABLE, REMOTE_SERVICE}

	public static final String TYPE_PROPERTY = "type";

	public static final String WHEN_PROPERTY = "when";

	/**
	 * Type of action.
	 * @return an instance of {@link TYPE}
	 */
	@ISProperty(name=TYPE_PROPERTY, mandatory=true, nullable=false)
	public TYPE getType();
	
	/**
	 * 
	 * @param type
	 */
	public void setType(TYPE type);
	
	/**
	 * From where to download the action.
	 * @return URL or Endpoint
	 */
	@ISProperty(mandatory=false, nullable=false)
	public String getSource();
	
	/**
	 * A remote source used by the command. Could be the endpoint of a service, the location of a 
	 * remote ansible playbook, etc.
	 * @param source
	 */
	public void setSource(String source);
	
	/**
	 * The options/params to use when executing the action.
	 * @return the command to execute to lauch the action
	 */
	@ISProperty(mandatory=true, nullable=false)
	public String getOptions();

	/**
	 * The options for the actions.
	 * @param options
	 */
	public void setOptions(String options);

	/**
	 * Sets the command to execute.
	 * @param command
	 */
	public void setCommand(String command);
	
	/**
	 * Gets the command to execute.
	 * @return the command
	 */
	public String getCommand();
}
