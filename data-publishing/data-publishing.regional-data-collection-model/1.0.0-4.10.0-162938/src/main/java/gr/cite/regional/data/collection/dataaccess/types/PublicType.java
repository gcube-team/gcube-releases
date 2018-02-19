package gr.cite.regional.data.collection.dataaccess.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vfloros
 *
 */
public enum PublicType {
	PRIVATE_TO_USER((short)0), PRIVATE_TO_LEVEL_OF_ITEM((short)1), PUBLIC_TO_DOMAIN((short)2);
	
	private final short code;
	
	private static Map<Short, PublicType> lookup = new HashMap<Short, PublicType>();
	
	static {
		for(PublicType pt : EnumSet.allOf(PublicType.class)) {
			lookup.put(pt.code, pt);
		}
	}
	
	PublicType(short code) {
		this.code = code;
	}
	
	public short code() {
		return this.code;
	}

	public static PublicType fromCode(short code) {
		return lookup.get(code);
	}
}
