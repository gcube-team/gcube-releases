package gr.cite.geoanalytics.dataaccess.test;

import java.util.UUID;

public class TestEntity 
{
	private String id = UUID.randomUUID().toString();
	private String name = null;
	
	public TestEntity() { }
	
	public TestEntity(String id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public String getId() 
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
