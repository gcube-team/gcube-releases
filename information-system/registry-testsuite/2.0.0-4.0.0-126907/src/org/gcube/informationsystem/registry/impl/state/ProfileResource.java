package org.gcube.informationsystem.registry.impl.state;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.resources.GCUBERegisteredResource;

import org.globus.wsrf.ResourceException;
import org.w3c.dom.Document;

/**
 * Profile Stateful resource
 * 
 * @author Andrea Manzi, Lucio Lelii, Manuele Simi (ISTI-CNR)
 * 
 */
public class ProfileResource extends GCUBEWSResource {

	private static GCUBELog logger = new GCUBELog(ProfileResource.class.getName());

	protected GCUBEResource gCubeResource;

	protected static final String ProfileRP = "Profile";

	/**
	 * Constructor 
	 *             
	 */
	public ProfileResource() {};
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String[] getPropertyNames() {
		return new String[] { ProfileRP };
	}

	/**
	 * Initializes the resource
	 * 
	 * @param params
	 *            Object
	 * @throws ResourceException
	 *             if resource is missing
	 */
	@Override
	public void initialise(Object... params) throws ResourceException {
		GCUBERegisteredResource resource;
		try {
			resource = new GCUBERegisteredResource((GCUBEResource) params[0]);
		} catch (Exception e) {
			throw new ResourceException("Invalid GCUBEResource");
		}
		logger.debug("initializing resource " + resource.getID());
		
		this.setProfile(resource.getAsDOM());
		// this.setNotificationProfile(dom);
		this.setGCubeResource(resource.getSource());

		if (resource.isTemporary())
			this.setTerminationTime(null);		
	}

	/**
	 * Sets Profile
	 * 
	 * @param profile the profile
	 */
	public void setProfile(Document profile) {
		this.getResourcePropertySet().get(ProfileRP).clear();
		this.getResourcePropertySet().get(ProfileRP).add(profile);
	}

	/**
	 * Gets Profile
	 * 
	 * @return the profile
	 */
	public Document getProfile() {
		return (Document) this.getResourcePropertySet().get(ProfileRP).get(0);
	}

	/**
	 * Returns the source {@link GCUBEResource}
	 *  
	 * @return the resource
	 */
	public GCUBEResource getGCubeResource() {
		return this.gCubeResource;
	}

	/**
	 * Sets the source {@link GCUBEResource}
	 * 
	 * @param resource the resource
	 */
	public void setGCubeResource(GCUBEResource resource) {
		this.gCubeResource = resource;
	}

	/**
	 * Updates the resource
	 * 
	 * @param resource
	 *            the resource to update
	 * @throws Exception
	 *             if something goes wrong
	 */
	public void updateResource(GCUBEResource resource) throws Exception {
		GCUBERegisteredResource regResource = new GCUBERegisteredResource(resource);				
		this.setProfile(regResource.getAsDOM());
		this.setGCubeResource(resource);
		this.store();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.core.state.GCUBEWSResource#getPublisher()
	 */
	@Override
	protected ISPublisher getPublisher() throws Exception {
		return  GHNContext.getImplementation(ISPublisher.class);
	}

}
