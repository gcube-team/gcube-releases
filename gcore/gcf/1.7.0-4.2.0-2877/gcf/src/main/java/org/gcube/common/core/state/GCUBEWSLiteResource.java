package org.gcube.common.core.state;

import javax.xml.namespace.QName;

import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.impl.ReflectionResourceProperty;

/**
 * An abstract specialisation of {@link GCUBEWSResource GCUBEWSResources} for resource that act as views
 * over {@link GCUBELocalResource}s.
 *
 * 
 * @author Fabio Simeoni (University of Strathclyde).
 * @param <LOCAL> the type of the {@link GCUBELocalResource} which can be viewed over.
 *
 */
public abstract class GCUBEWSLiteResource<LOCAL extends GCUBELocalResource> extends GCUBEWSResource {
	
	/** The identifier of the {@link GCUBELocalResource} associated with the resource.*/
	private String localID;
	
	/**{@inheritDoc}*/
	protected void initialise(GCUBEWSResourceKey id, Object ... params) throws Exception {
		//first create/reuse local
		this.localID = this.getPorttypeContext().getLocalHome().create(id==null?null:id.getValue(), this.transformParams(params)).getID();
		super.initialise(id,params);//then proceed as usual
	}
	
	/**
	 * Invoked during the initialisation of the resource, it transforms the initialisation 
	 * parameters into those required by the associated {@link GCUBELocalResource}.
	 * <p>
	 * By default, it performs the identity transformation. Override if the initialisation of the resource
	 * must be decoupled from the initialisation of the {@link GCUBELocalResource}.
	 * 
	 * @param params the initialisation parameters of the view.
	 * @return the initialisation parameters of the local resource.
	 * @throws Exception if the transformation could not be performed.
	 */
	protected Object[] transformParams(Object ... params) throws Exception {return params;}

	/**
	 * Returns the identifier of the {@link GCUBELocalResource} associated with the resource.
	 * @return the identifier.
	 */
	public synchronized String getLocalID() {return localID;}

	/**
	 * Sets the identifier of the {@link GCUBELocalResource} associated with the resource.
	 * @param resourceID the identifier.
	 */
	public void setLocalID(String resourceID) {
		if (this.localID==null) this.localID = resourceID;
		else throw new RuntimeException("resource identifier is immutable");
	}
	
	/**{@inheritDoc}*/
	public void onRemove() throws ResourceException {
		this.getPorttypeContext().getLocalHome().remove(this.localID);//remove local resource first
		super.onRemove();//proceed as usual
	}
	
	/**
	 * Returns the names of fields to be transformed to into {@link ReflectionResourceProperty ReflectionResourceProperties}.
	 * With reflection, subclasses do not need to maintain their RPs in 
	 * sync with the state of the associated {@link GCUBELocalResource}. Rather, the RPs remain
	 * virtual, but requires getters and setters for each of the corresponding fields. Typically, getters and
	 * setters would dispatch to corresponding methods of the associated {@link GCUBELocalResource}.  
	 * @return the names.
	 */
	protected abstract String[] getPropertyNames();
	
	/**
	 * Returns the {@link GCUBELocalResource} associated with resource.
	 * @return the local resource.
	 */
	public LOCAL getLocalResource() throws ResourceException {
		return (LOCAL) this.getPorttypeContext().getLocalHome().find(this.localID);
	}

	/** {@inheritDoc}*/
	protected ResourceProperty getProperty(String name) throws Exception {
		return new ReflectionResourceProperty(new QName(this.getPorttypeContext().getNamespace(),name),name,this);
	}
	
}
