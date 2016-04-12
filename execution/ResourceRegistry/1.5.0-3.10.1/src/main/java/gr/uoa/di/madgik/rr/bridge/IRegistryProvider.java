package gr.uoa.di.madgik.rr.bridge;

import gr.uoa.di.madgik.rr.RRContext.ReadPolicy;
import gr.uoa.di.madgik.rr.RRContext.WritePolicy;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.access.InMemoryStore;

import java.util.Properties;
import java.util.Set;

public interface IRegistryProvider
{
	/**
	 * Reads provider-specific configuration
	 * 
	 * @param config The configuration resource
	 * @throws ResourceRegistryException
	 */
	public void readConfiguration(Properties config) throws ResourceRegistryException;
	/**
	 * Used to specify which data item types are to be prefetched in main memory
	 * 
	 * @param itemTypes The set of item types to be kept in main memory
	 * @throws ResourceRegistryException
	 */
	public void setInMemoryTargets(Set<Class<?>> itemTypes) throws ResourceRegistryException;
	/**
	 * Determines if a read policy is supported by the registry provider
	 * 
	 * @param policy The read policy to be checked for support
	 * @return true if the read policy is supported, false otherwise
	 * @throws ResourceRegistryException
	 */
	public boolean isReadPolicySupported(ReadPolicy policy) throws ResourceRegistryException;
	/**
	 * Determines if a write policy is supported by the registry provider
	 * 
	 * @param policy The write policy to be checked for support
	 * @return true if the write policy is supported, false otherwise
	 * @throws ResourceRegistryException
	 */
	public boolean isWritePolicySupported(WritePolicy policy) throws ResourceRegistryException;
	/**
	 * Prefetches all data items which are marked as in-memory from the local data store and keeps them in an {@link InMemoryStore}
	 * 
	 * @throws ResourceRegistryException
	 */
	public void prefetchInMemoryItems() throws ResourceRegistryException;
	/**
	 * Stores all data items to the remote repository. Intended to be called on a periodic basis as a bridging iteration step, implementing {@link WritePolicy#WRITE_BEHIND}
	 * 
	 * @param items The set of items to be stored in the repository
	 * @throws ResourceRegistryException
	 */
	public void persist(Set<Class<?>> items, Set<String> nonUpdateVOScopes) throws ResourceRegistryException;
	/**
	 * Retrieves all data items from the remote repository. Intended to be called on a periodic basis as a bridging iteration step, implementing {@link ReadPolicy#REFRESH_AHEAD}
	 * 
	 * @param items The set of items to be stored in the repository
	 * @throws ResourceRegistryException
	 */
	public void retrieve(Set<Class<?>> items) throws ResourceRegistryException;
	
	/**
	 * Implemented by providers whose remote repository is not expressed through a Datastore type and must support {@link WritePolicy#WRITE_THROUGH} and {@link WritePolicy#WRITE_LOCAL}
	 * Stores a specific data item instance residing on the local data store directly to the remote repository.
	 * 
	 * @param item The item type
	 * @param id The item instance id
	 * @throws ResourceRegistryException
	 */
	public void persistDirect(Class<?> item, String id) throws ResourceRegistryException;
	/**
	 * Implemented by providers whose remote repository is not expressed through a Datastore type and must support {@link WritePolicy#WRITE_THROUGH} and {@link WritePolicy#WRITE_LOCAL}
	 * Stores all data item instances of a specific type residing on the local data store directly to the remote repository.
	 * 
	 * @param item The item type
	 * @throws ResourceRegistryException
	 */
	public void persistDirect(Class<?> item) throws ResourceRegistryException;
	/**
	 * Implemented by providers whose remote repository is not expressed through a Datastore type and must support {@link ReadPolicy#READ_THROUGH} and {@link ReadPolicy#READ_LOCAL}
	 * Retrieves a specific data item instance from the remote repository and stores it in the local data store.
	 * 
	 * @param item The item type
	 * @param id The item instance id
	 * @throws ResourceRegistryException
	 */
	public void retrieveDirect(Class<?> item, String id) throws ResourceRegistryException;
	/**
	 **Implemented by providers whose remote repository is not expressed through a Datastore type and must support {@link ReadPolicy#READ_THROUGH} and {@link ReadPolicy#READ_LOCAL}
	 * Retrieves all data item instances of a specific type from the remote repository and stores it in the local data store. 
	 * 
	 * @param item The item type
	 * @throws ResourceRegistryException
	 */
	public void retrieveDirect(Class<?> item) throws ResourceRegistryException;
}
