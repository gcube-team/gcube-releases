package gr.cite.geoanalytics.dataaccess.entities.principal;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AccessRightStatus
{
	NOT_GRANTED((short)0), GRANTED((short)1), DENIED((short)2);
	
	private final short code;
	
	private static final Map<Short,AccessRightStatus> lookup  = new HashMap<Short, AccessRightStatus>();
	 
	static {
	      for(AccessRightStatus et : EnumSet.allOf(AccessRightStatus.class))
	           lookup.put(et.code(), et);
	 }
	
	AccessRightStatus(short code)
	{
		this.code = code;
	}
	
	public short code() { return code; }

	public static AccessRightStatus fromCode(short code)
	{
		return lookup.get(code);
	}
}
