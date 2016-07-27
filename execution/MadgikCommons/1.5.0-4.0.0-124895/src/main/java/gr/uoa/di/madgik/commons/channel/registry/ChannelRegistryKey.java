package gr.uoa.di.madgik.commons.channel.registry;

import java.io.Serializable;

/**
 * This class is the one kept by the registry identifying a {@link ChannelRegistryEntry}
 * 
 * @author gpapanikos
 */
public class ChannelRegistryKey implements Serializable
{
	private static final long serialVersionUID = 1L;
	/** The Unique id. */
	private String UniqueID = null;

	/**
	 * Creates a new instance of the class
	 * 
	 * @param UniqueID The Registry UUID
	 */
	public ChannelRegistryKey(String UniqueID)
	{
		this.UniqueID = UniqueID;
	}

	/**
	 * Retrieves the registry key unique id
	 * 
	 * @return the registry UUID
	 */
	public String GetUniqueID()
	{
		return this.UniqueID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.UniqueID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ChannelRegistryKey))
		{
			return false;
		}
		if (!((ChannelRegistryKey) o).UniqueID.equals(this.UniqueID))
		{
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 97 * hash + (this.UniqueID != null ? this.UniqueID.hashCode() : 0);
		return hash;
	}
}
