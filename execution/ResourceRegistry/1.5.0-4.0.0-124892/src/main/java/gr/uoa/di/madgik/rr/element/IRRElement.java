package gr.uoa.di.madgik.rr.element;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.RRContext.ReadPolicy;
import gr.uoa.di.madgik.rr.RRContext.WritePolicy;

public interface IRRElement
{
	/**
	 * Returns the id of the item.
	 * 
	 * @return the id
	 */
	String getID();
	/**
	 * Sets the item id. Useful mainly for subsequent load operations.
	 * 
	 * @param id The id to be set
	 */
	void setID(String id);
	
	/**
	 * Marks the item dirty. This will have the effect that store operations will take place regardless of equality.
	 */
	void setDirty();
	
	/**
	 * Checks if an {@link RRElement} exists using the default {@link ReadPolicy}.
	 * The read policy used is selected as follows:
	 * 
	 * @return
	 * @throws ResourceRegistryException
	 */
	boolean exists() throws ResourceRegistryException;
	/**
	 * 
	 * @param readPolicy
	 * @return
	 * @throws ResourceRegistryException
	 */
	boolean exists(RRContext.ReadPolicy readPolicy) throws ResourceRegistryException;
	/**
	 * 
	 * @param persistencyType
	 * @return
	 * @throws ResourceRegistryException
	 */
	boolean exists(RRContext.DatastoreType persistencyType) throws ResourceRegistryException;
	
	/**
	 * Loads an {@link IRRElement} using the default {@link ReadPolicy}.
	 * The read policy used is selected as follows:
	 * 1.The item is retrieved from the local datastore if {@link ReadPolicy#REFRESH_AHEAD} is available.
	 * 2.If {@link ReadPolicy#READ_THROUGH} is available, an attempt to load the item from the local datastore is made first
	 * by using {@link ReadPolicy#READ_LOCAL}.
	 * If the latter fails, the item is retrieved from the remote datastore using {@link ReadPolicy#READ_THROUGH}
	 * 
	 * @param loadDetails true if the entire object graph should be loaded, false otherwise
	 * @return true if item was successfully loaded, false if load failed because item does not exist
	 * @throws ResourceRegistryException An error has occurred
	 */
	boolean load(boolean loadDetails) throws ResourceRegistryException;
	/**
	 * Loads an {@link IRRElement} using a specific {@link ReadPolicy}
	 * 
	 * @param loadDetails true if the entire object graph should be loaded, false otherwise
	 * @param policy the {@link ReadPolicy} to be used
	 * @return true if item was successfully loaded, false if load failed because item does not exist
	 * @throws ResourceRegistryException An error has occurred
	 */
	boolean load(boolean loadDetails, RRContext.ReadPolicy policy) throws ResourceRegistryException;
	/**
	 * Loads an {@link IRRElement} from a specific {@link DatastoreType}
	 * 
	 * @param loadDetails true if the entire object graph should be loaded, false otherwise
	 * @param persistencyType the {@link DatastoreType} to be used
	 * @return true if item was successfully loaded, false if load failed because item does not exist
	 * @throws ResourceRegistryException An error has occurred
	 */
	boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException;
	
	/**
	 * Stores an {@link IRRElement} using the default {@link WritePolicy}.
	 * The write policy used is selected as follows:
	 * 1.If {@link WritePolicy#WRITE_BEHIND} is available, the object is stored to the local datastore.
	 * 2.If {@link WritePolicy#WRITE_THROUGH} is available, the object is stored directly to the remote datastore, through the 
	 * local datastore. 
	 * Note that this method does not provide the option to use the local datastore explicitly as a cache instead of the remote one.
	 * If such kind of optimization ism
	 * @param storeDetails true if the entire object graph should be stored, false otherwise
	 * @throws ResourceRegistryException if no {@link WritePolicy} could be found
	 */
	void store(boolean storeDetails) throws ResourceRegistryException;
	/**
	 * 
	 * @param storeDetails
	 * @param policy
	 * @throws ResourceRegistryException
	 */
	public void store(boolean storeDetails, WritePolicy policy) throws ResourceRegistryException;
	/**
	 * 
	 * @param storeDetails
	 * @param persistencyType
	 * @throws ResourceRegistryException
	 */
	void store(boolean storeDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException;
	
	/**
	 * 
	 * @param loadDetails
	 * @throws ResourceRegistryException
	 */
	void delete(boolean loadDetails) throws ResourceRegistryException;
	/**
	 * 
	 * @param deleteDetails
	 * @param policy
	 * @throws ResourceRegistryException
	 */
	public void delete(boolean deleteDetails, WritePolicy policy) throws ResourceRegistryException;
	/**
	 * 
	 * @param loadDetails
	 * @param persistencyType
	 * @throws ResourceRegistryException
	 */
	void delete(boolean loadDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException;
	/**
	 * 
	 * @param f
	 * @param includeDetails
	 * @return
	 * @throws ResourceRegistryException
	 */
	boolean isEqual(IRRElement f, boolean includeDetails) throws ResourceRegistryException;

	/**
	 * Returns the underlying DAO
	 * 
	 * @return the underlying {@link IDaoElement}
	 */
	IDaoElement getItem();
	/**
	 * Returns the context associated with {@link ResourceRegistry}
	 * 
	 * @return the context
	 */
	RRContext getISContext();

}
