package gr.uoa.di.madgik.rr.element;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.RRContext.ReadPolicy;
import gr.uoa.di.madgik.rr.RRContext.WritePolicy;

public abstract class RRElement implements IRRElement
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exists() throws ResourceRegistryException
	{
		if(ResourceRegistry.isReadPolicySupported(RRContext.ReadPolicy.REFRESH_AHEAD) || ResourceRegistry.isReadPolicySupported(RRContext.ReadPolicy.READ_LOCAL))  
			return this.exists(DatastoreType.LOCAL);
		else if(ResourceRegistry.isReadPolicySupported(RRContext.ReadPolicy.READ_THROUGH))
		{
			if(this.exists(ReadPolicy.READ_LOCAL)) return true;
			return this.exists(ReadPolicy.READ_THROUGH);
		}
		
		throw new ResourceRegistryException("Failed to find supported read policy");	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exists(ReadPolicy policy) throws ResourceRegistryException
	{
		if(!ResourceRegistry.isReadPolicySupported(policy)) throw new ResourceRegistryException("Read policy not supported");
		if(policy == ReadPolicy.READ_LOCAL || policy == ReadPolicy.REFRESH_AHEAD)
			return this.exists(DatastoreType.LOCAL);
		else if(policy == ReadPolicy.READ_THROUGH)
		{
			if(ResourceRegistry.getContext().isDatastoreSupportedForRead(DatastoreType.REMOTE))
			{
				return this.exists(DatastoreType.REMOTE);	
			}
			else 
			{
				ResourceRegistry.retrieveDirect(this.getClass(), this.getID());
				return this.exists(DatastoreType.LOCAL);
			}
		}
		throw new ResourceRegistryException("Unsupported read policy");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean load(boolean loadDetails) throws ResourceRegistryException
	{
		if(ResourceRegistry.isReadPolicySupported(RRContext.ReadPolicy.REFRESH_AHEAD))  
			return this.load(loadDetails, DatastoreType.LOCAL);
		else if(ResourceRegistry.isReadPolicySupported(RRContext.ReadPolicy.READ_THROUGH) && ResourceRegistry.isReadPolicySupported(ReadPolicy.READ_LOCAL))
		{
			if(this.load(loadDetails, ReadPolicy.READ_LOCAL)) return true;
			return this.load(loadDetails, ReadPolicy.READ_THROUGH);
		}
		
		throw new ResourceRegistryException("Failed to find supported read policy");	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean load(boolean loadDetails, RRContext.ReadPolicy policy) throws ResourceRegistryException
	{
		if(!ResourceRegistry.isReadPolicySupported(policy)) throw new ResourceRegistryException("Read policy not supported");
		if(policy == ReadPolicy.READ_LOCAL || policy == ReadPolicy.REFRESH_AHEAD)
			return this.load(loadDetails, DatastoreType.LOCAL);
		else if(policy == ReadPolicy.READ_THROUGH)
		{
			boolean result = false;
			if(ResourceRegistry.getContext().isDatastoreSupportedForRead(DatastoreType.REMOTE))
			{
				result = this.load(loadDetails, DatastoreType.REMOTE);
				if(result) this.store(loadDetails, DatastoreType.LOCAL);
				return result;
			}
			else 
			{
				ResourceRegistry.retrieveDirect(this.getClass(), this.getID());
				return this.load(loadDetails, DatastoreType.LOCAL);
			}
		}
		else throw new ResourceRegistryException("Unsupported read policy");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(boolean storeDetails) throws ResourceRegistryException
	{
		if(ResourceRegistry.isWritePolicySupported(RRContext.WritePolicy.WRITE_BEHIND))
			this.store(storeDetails, DatastoreType.LOCAL);
		else if(ResourceRegistry.isWritePolicySupported(RRContext.WritePolicy.WRITE_THROUGH))
			this.store(storeDetails, WritePolicy.WRITE_THROUGH);
		else throw new ResourceRegistryException("Failed to find supported write policy");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(boolean storeDetails, WritePolicy policy) throws ResourceRegistryException
	{
		if(!ResourceRegistry.isWritePolicySupported(policy)) throw new ResourceRegistryException("Write policy not supported");
		if(policy == WritePolicy.WRITE_LOCAL || policy == WritePolicy.WRITE_BEHIND)
			this.store(storeDetails, DatastoreType.LOCAL);
		else if(policy == WritePolicy.WRITE_THROUGH)
		{
			if(ResourceRegistry.getContext().isDatastoreSupportedForWrite(DatastoreType.REMOTE))
			{
				this.store(storeDetails, DatastoreType.REMOTE);
				this.store(storeDetails, DatastoreType.LOCAL);
			}
			else 
			{
				throw new ResourceRegistryException("This element does not support storing");
			}
		}
		else throw new ResourceRegistryException("Unsupported read policy");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(boolean deleteDetails) throws ResourceRegistryException
	{
		if(ResourceRegistry.isWritePolicySupported(RRContext.WritePolicy.WRITE_BEHIND))  
			this.delete(deleteDetails, DatastoreType.LOCAL);
		else if(ResourceRegistry.isWritePolicySupported(RRContext.WritePolicy.WRITE_THROUGH))
			this.delete(deleteDetails, WritePolicy.WRITE_THROUGH);
		else
			throw new ResourceRegistryException("Failed to find supported write policy");	
	}
	
	@Override
	public void delete(boolean deleteDetails, WritePolicy policy) throws ResourceRegistryException
	{
		if(!ResourceRegistry.isWritePolicySupported(policy)) throw new ResourceRegistryException("Write policy not supported");
		if(policy == WritePolicy.WRITE_LOCAL || policy == WritePolicy.WRITE_BEHIND)
			this.delete(deleteDetails, DatastoreType.LOCAL);
		else if(policy == WritePolicy.WRITE_THROUGH)
		{
			if(ResourceRegistry.getContext().isDatastoreSupportedForWrite(DatastoreType.REMOTE))
			{
				this.delete(deleteDetails, DatastoreType.REMOTE);
				this.delete(deleteDetails, DatastoreType.LOCAL);
			}
			else 
			{
				throw new ResourceRegistryException("This element does not support deletion");
			}
		}
		else throw new ResourceRegistryException("Unsupported write policy");
	}

}
