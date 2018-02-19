package gr.cite.regional.data.collection.dataaccess.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vfloros
 *
 */
public enum AnnotationStatusType {
	
	DELETED((short)0), ACTIVE((short)1);
	
	private final short code;
	
	private static final Map<Short,AnnotationStatusType> lookup  = new HashMap<Short, AnnotationStatusType>();
	 
	static {
	      for(AnnotationStatusType et : EnumSet.allOf(AnnotationStatusType.class))
	           lookup.put(et.code(), et);
	 }
	
	AnnotationStatusType(short code)
	{
		this.code = code;
	}
	
	public short code() { return code; }

	public static AnnotationStatusType fromCode(short code)
	{
		return lookup.get(code);
	}
}
