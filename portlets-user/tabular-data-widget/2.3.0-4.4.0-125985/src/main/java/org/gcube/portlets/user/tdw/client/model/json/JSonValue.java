/**
 * 
 */
package org.gcube.portlets.user.tdw.client.model.json;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public final class JSonValue extends JavaScriptObject {

	protected JSonValue()
	{}

	/**
	 * Returns the value for the specified key as String
	 * @param key the value key.
	 * @return the String value.
	 */
	public native String getAsString(String key)
	/*-{
	        return this[key];
		}-*/;

	/**
	 * Set the specified value.
	 * @param key the key.
	 * @param value the value to set.
	 */
	public native void set(String key, String value)
	/*-{
    	this[key] = value;
	}-*/;
	
	/**
	 * Returns the value for the specified key as Number
	 * @param key the value key.
	 * @return the Number value.
	 */
	public native double getAsNumber(String key)
	/*-{
	        return this[key];
		}-*/;
	
	/**
	 * Set the specified value.
	 * @param key the key.
	 * @param value the value to set.
	 */
	public native void set(String key, double value)
	/*-{
    	this[key] = value;
	}-*/;

	/**
	 * Returns the value for the specified key as boolean
	 * @param key the value key.
	 * @return the boolean value.
	 */
	public native boolean getAsBool(String key)
	/*-{
	        return this[key];
		}-*/;
	
	/**
	 * Set the specified value.
	 * @param key the key.
	 * @param value the value to set.
	 */
	public native void set(String key, boolean value)
	/*-{
    	this[key] = value;
	}-*/;

	/**
	 * Check if the specified value is null.
	 * @param key the value key.
	 * @return <code>True</code> if the values is null, <code>false</code> otherwise.
	 */
	public native boolean isNull(String key)
	/*-{
		    return (typeof this[key])=="undefined" || this[key] == null;
		}-*/;
	
	public native String getType(String key)
	/*-{
	    return typeof this[key];
	}-*/;

	public native Boolean getAsBoolean(String key)
	/*-{
			var value = this[key];
			if ((typeof value) == "undefined") return null;
			if ((typeof value) == "boolean") return @java.lang.Boolean::new(Z)(value);
		    return value;
		}-*/;
	
	/**
	 * Set the specified value.
	 * @param key the key.
	 * @param value the value to set.
	 */
	public native void set(String key, Boolean value)
	/*-{
    	this[key] = value;
	}-*/;

	public Date getAsDate(String key)
	{
		long l = getAsDouble(key).longValue();
		return new Date(l);
	}
	
	/**
	 * Set the specified value.
	 * @param key the key.
	 * @param value the value to set.
	 */
	public native void set(String key, Date value)
	/*-{
    	this[key] = value;
	}-*/;

	public native Double getAsDouble(String key)
	/*-{
			var value = this[key];
			if ((typeof value) == "undefined") return null;
			if ((typeof value) == "number") return @java.lang.Double::new(D)(value);
		    return value;
		}-*/;
	
	/**
	 * Set the specified value.
	 * @param key the key.
	 * @param value the value to set.
	 */
	public native void set(String key, Double value)
	/*-{
    	this[key] = value;
	}-*/;

	public native Float getAsFloat(String key)
	/*-{
		var value = this[key];
			if ((typeof value) == "undefined") return null;
		    if ((typeof value) == "number") return @java.lang.Float::new(D)(value);
		    return value;
		}-*/;
	
	/**
	 * Set the specified value.
	 * @param key the key.
	 * @param value the value to set.
	 */
	public native void set(String key, Float value)
	/*-{
    	this[key] = value;
	}-*/;

	public native Integer getAsInteger(String key)
	/*-{
			var value = this[key];
			if ((typeof value) == "undefined") return null;
			if ((typeof value) == "number") return @java.lang.Integer::new(I)(value);
		    return value;
		}-*/;
	
	/**
	 * Set the specified value.
	 * @param key the key.
	 * @param value the value to set.
	 */
	public native void set(String key, Integer value)
	/*-{
    	this[key] = value;
	}-*/;

	public Long getAsLong(String key)
	{
		long l = getAsDouble(key).longValue();		
		return Long.valueOf(l);
	}
	
	/**
	 * Set the specified value.
	 * @param key the key.
	 * @param value the value to set.
	 */
	public native void set(String key, Long value)
	/*-{
    	this[key] = value;
	}-*/;
}
