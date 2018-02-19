package gr.cite.regional.data.collection.dataaccess.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vfloros
 *
 */
public enum CDTStatusType {
	
	PUBLISHED((short)0), ACCEPTED((short)1),REJECTED((short)2),
	TEMPORARY((short)3), COMMITTED((short)4);
	
	private final short code;
	
	private static final Map<Short,CDTStatusType> lookup  = new HashMap<Short, CDTStatusType>();
	 
	static {
	      for(CDTStatusType et : EnumSet.allOf(CDTStatusType.class))
	           lookup.put(et.code(), et);
	 }
	
	CDTStatusType(short code)
	{
		this.code = code;
	}
	
	public short code() { return code; }

	public static CDTStatusType fromCode(short code)
	{
		return lookup.get(code);
	}
}
