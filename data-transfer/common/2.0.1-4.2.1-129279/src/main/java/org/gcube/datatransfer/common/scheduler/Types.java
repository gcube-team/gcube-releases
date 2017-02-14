package org.gcube.datatransfer.common.scheduler;


public class Types {

	@SuppressWarnings("serial")
	public static class FrequencyType implements java.io.Serializable {
	    private java.lang.String _value_;
	    @SuppressWarnings("rawtypes")
		private static java.util.HashMap _table_ = new java.util.HashMap();
	    // Constructor
	    protected FrequencyType(java.lang.String value) {
	        _value_ = value;
	        _table_.put(_value_,this);
	    }

	    public static final java.lang.String _perMinute = "perMinute";
	    public static final java.lang.String _perHour = "perHour";
	    public static final java.lang.String _perDay = "perDay";
	    public static final java.lang.String _perWeek = "perWeek";
	    public static final java.lang.String _perMonth = "perMonth";
	    public static final java.lang.String _perYear = "perYear";
	    public static final FrequencyType perMinute = new FrequencyType(_perMinute);
	    public static final FrequencyType perHour = new FrequencyType(_perHour);
	    public static final FrequencyType perDay = new FrequencyType(_perDay);
	    public static final FrequencyType perWeek = new FrequencyType(_perWeek);
	    public static final FrequencyType perMonth = new FrequencyType(_perMonth);
	    public static final FrequencyType perYear = new FrequencyType(_perYear);
	    public java.lang.String getValue() { return _value_;}
	    public static FrequencyType fromValue(java.lang.String value)
	          throws java.lang.IllegalArgumentException {
	        FrequencyType enumeration = (FrequencyType)
	            _table_.get(value);
	        if (enumeration==null) throw new java.lang.IllegalArgumentException();
	        return enumeration;
	    }
	    public static FrequencyType fromString(java.lang.String value)
	          throws java.lang.IllegalArgumentException {
	        return fromValue(value);
	    }
	    public boolean equals(java.lang.Object obj) {return (obj == this);}
	}
}
