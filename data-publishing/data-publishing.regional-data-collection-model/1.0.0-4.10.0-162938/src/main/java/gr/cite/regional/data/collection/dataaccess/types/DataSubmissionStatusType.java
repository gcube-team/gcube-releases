package gr.cite.regional.data.collection.dataaccess.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vfloros
 *
 */
public enum DataSubmissionStatusType {
	
	TEMPORARY((short)0), INVITATION_TO_SUBMIT((short)1),SUBMITTED_FOR_CHECK((short)2),
	VALIDATED((short)3), REJECTED((short)4), WITHDRAWN_ENTRY((short)5);
	
	private final short code;
	
	private static final Map<Short,DataSubmissionStatusType> lookup  = new HashMap<Short, DataSubmissionStatusType>();
	 
	static {
	      for(DataSubmissionStatusType et : EnumSet.allOf(DataSubmissionStatusType.class))
	           lookup.put(et.code(), et);
	 }
	
	DataSubmissionStatusType(short code)
	{
		this.code = code;
	}
	
	public short code() { return code; }

	public static DataSubmissionStatusType fromCode(short code)
	{
		return lookup.get(code);
	}
}
