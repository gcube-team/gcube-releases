package gr.uoa.di.madgik.workflow.environment;

public interface ICacheEntry
{
	public enum CacheType
	{
		Plot
	}

	public CacheType GetCacheEntryType();
}
