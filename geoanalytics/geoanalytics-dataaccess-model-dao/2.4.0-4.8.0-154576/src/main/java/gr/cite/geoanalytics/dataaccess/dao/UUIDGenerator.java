package gr.cite.geoanalytics.dataaccess.dao;

import java.util.UUID;

public class UUIDGenerator
{
	public static UUID randomUUID()
	{
		UUID uuid = null;
		do 
		{
			uuid = UUID.randomUUID();
		}while(!UUIDGenerator.testReservedCode(uuid));
		
		return uuid;
	}
	
	public static UUID systemUserUUID()
	{
		return UUID.fromString("00000000-0000-0000-0000-000000000001");
	}
	
	public static UUID dummyUserUUID()
	{
		return UUID.fromString("00000000-0000-0000-0000-000000000002");
	}
	
	private static boolean testReservedCode(UUID uuid)
	{
		if(uuid.equals(UUIDGenerator.systemUserUUID())) return false;
		return true;
	}
}
