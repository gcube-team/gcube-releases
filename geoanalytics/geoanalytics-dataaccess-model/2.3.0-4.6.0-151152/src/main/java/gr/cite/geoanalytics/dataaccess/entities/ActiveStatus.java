package gr.cite.geoanalytics.dataaccess.entities;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ActiveStatus
{
	INACTIVE((short)0), ACTIVE((short)1), LOCKED ((short)2);
	
	private final short code;
	
	private static final Map<Short,ActiveStatus> lookup  = new HashMap<Short, ActiveStatus>();
	 
	static {
	      for(ActiveStatus et : EnumSet.allOf(ActiveStatus.class))
	           lookup.put(et.code(), et);
	 }
	
	ActiveStatus(short code)
	{
		this.code = code;
	}
	
	public short code() { return code; }

	public static ActiveStatus fromCode(short code)
	{
		return lookup.get(code);
	}
}
