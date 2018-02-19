package gr.cite.regional.data.collection.dataaccess.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum DataCollectionStatusType {
	NEW((short)0);
	
	private final short code;
	
	private static final Map<Short,DataCollectionStatusType> lookup  = new HashMap<Short, DataCollectionStatusType>();
	 
	static {
	      for(DataCollectionStatusType et : EnumSet.allOf(DataCollectionStatusType.class))
	           lookup.put(et.code(), et);
	 }
	
	DataCollectionStatusType(short code)
	{
		this.code = code;
	}
	
	public short code() { return code; }

	public static DataCollectionStatusType fromCode(short code)
	{
		return lookup.get(code);
	}

}
