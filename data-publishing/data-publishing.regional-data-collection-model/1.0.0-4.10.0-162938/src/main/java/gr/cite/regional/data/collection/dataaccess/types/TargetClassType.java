/**
 * 
 */
package gr.cite.regional.data.collection.dataaccess.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vfloros
 *
 */
public enum TargetClassType {
	DATA_COLLECTION((short)0), DATA_SUBMISSION((short)1);
	
	private final short code;
	
	private static final Map<Short, TargetClassType> lookup = new HashMap<Short, TargetClassType>();
	
	TargetClassType(short code) {
		this.code = code;
	}
	
	static {
		for(TargetClassType tct : EnumSet.allOf(TargetClassType.class)) {
			lookup.put(tct.code, tct);
		}
	}
	
	public static TargetClassType fromCode(short code) {
		return lookup.get(code);
	}
}
