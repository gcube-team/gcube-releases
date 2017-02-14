package org.gcube.opensearch.opensearchdatasource.processor;

public class OpenSearchParameter {
	public String name;
	public String value;
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other == null) return false;
		if(other == this) return true;
		if(!(other instanceof OpenSearchParameter)) return false;
		return name.equals(((OpenSearchParameter)other).name);
	}
}
