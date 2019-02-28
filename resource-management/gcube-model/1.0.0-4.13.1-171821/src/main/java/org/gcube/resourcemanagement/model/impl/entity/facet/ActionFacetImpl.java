package org.gcube.resourcemanagement.model.impl.entity.facet;

import org.gcube.informationsystem.model.impl.entity.FacetImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.ActionFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Implementation of {@link ActionFacet}.
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
@JsonTypeName(value=ActionFacet.NAME)
public class ActionFacetImpl extends FacetImpl implements ActionFacet {

	private static final long serialVersionUID = -1749157426900635075L;

	protected String name;
	
	/**
	 * Type of action.
	 */
	protected TYPE type;
	
	/**
	 * A remote source used by the command. Could be the endpoint of a service, the location of a 
	 * remote ansible playbook, etc.
	 */
	protected String source;
	
	/**
	 * Options and parameters for the command.
	 */
	protected String options;
	
	/**
	 * The command to execute
	 */
	protected String command;

	/* (non-Javadoc)
	 * @see org.gcube.informationsystem.model.entity.facet.ActionFacet#getType()
	 */
	@Override
	public TYPE getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see org.gcube.informationsystem.model.entity.facet.ActionFacet#getSource()
	 */
	@Override
	public String getSource() {
		return this.source;
	}

	/* (non-Javadoc)
	 * @see org.gcube.informationsystem.model.entity.facet.NamedFacet#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see org.gcube.informationsystem.model.entity.facet.NamedFacet#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;		
	}

	/* (non-Javadoc)
	 * @see org.gcube.informationsystem.model.entity.facet.ActionFacet#setType(org.gcube.informationsystem.model.entity.facet.ActionFacet.TYPE)
	 */
	@Override
	public void setType(TYPE type) {
		this.type = type;		
	}

	/* (non-Javadoc)
	 * @see org.gcube.informationsystem.model.entity.facet.ActionFacet#setSource(java.lang.String)
	 */
	@Override
	public void setSource(String source) {
		this.source = source;		
	}

	/* (non-Javadoc)
	 * @see org.gcube.informationsystem.model.entity.facet.ActionFacet#getOptions()
	 */
	@Override
	public String getOptions() {
		return this.options;
	}

	/* (non-Javadoc)
	 * @see org.gcube.informationsystem.model.entity.facet.ActionFacet#setOptions(java.lang.String)
	 */
	@Override
	public void setOptions(String options) {
		this.options = options;		
	}

	/* (non-Javadoc)
	 * @see org.gcube.informationsystem.model.entity.facet.ActionFacet#setCommand(java.lang.String)
	 */
	@Override
	public void setCommand(String command) {
		this.command = command;
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.informationsystem.model.entity.facet.ActionFacet#getCommand()
	 */
	@Override
	public String getCommand() {
		return this.command;
	}


}
