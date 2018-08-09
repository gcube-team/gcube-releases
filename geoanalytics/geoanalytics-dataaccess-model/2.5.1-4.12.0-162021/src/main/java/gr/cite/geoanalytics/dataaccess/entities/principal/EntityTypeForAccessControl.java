package gr.cite.geoanalytics.dataaccess.entities.principal;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EntityTypeForAccessControl {
	PROJECT((short) 0), SHAPE((short) 1), DOCUMENT((short) 2);
	
	private final short code;
	
	private static final Map<Short,EntityTypeForAccessControl> lookup  = new HashMap<Short, EntityTypeForAccessControl>();
	 
	static {
	      for(EntityTypeForAccessControl et : EnumSet.allOf(EntityTypeForAccessControl.class))
	           lookup.put(et.code(), et);
	 }
	
	EntityTypeForAccessControl(short code)
	{
		this.code = code;
	}
	
	public short code() { return code; }

	public static EntityTypeForAccessControl fromCode(short code)
	{
		return lookup.get(code);
	}
}
