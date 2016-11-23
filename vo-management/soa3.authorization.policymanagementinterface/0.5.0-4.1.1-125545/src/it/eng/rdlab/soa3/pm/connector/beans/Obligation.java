package it.eng.rdlab.soa3.pm.connector.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * A bean representing an obligation
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class Obligation 
{
	private final String 	OBLIGATION_VALUE_ACTION = "action",
							OBLIGATION_VALUE_RESOURCE = "resource";
	
	private String value;
	
	private Map<String,Map<String, String>> attributes;
	
	private boolean actionScope;
	private boolean fulfillOn;

	public Obligation() 
	{
		this.attributes = new HashMap<String, Map<String,String>>();
		this.actionScope = false;
		this.value = null;
		this.fulfillOn = true;
	}
	
	/**
	 * 
	 * Adds an obligation attribute
	 * 
	 * @param name
	 * @param datatype
	 * @param value
	 */
	public void addAttribute (String name, String datatype, String value)
	{
		if (datatype != null)
		{
			
			Map<String, String> map = this.attributes.get(datatype);
			
			if (map == null)
			{
				map = new HashMap<String, String>();
				this.attributes.put(datatype, map);
			}
			
			map.put(name, value);
		}
	}
	
	/**
	 * 
	 * Gets the obligatio id
	 * 
	 * @return the obligation id
	 */
	public String getValue() 
	{
		return value;
	}

	/**
	 * 
	 * Sets the obligation id
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * Gets all the attributes
	 * 
	 * @return the obligation attributes
	 */
	public Map<String,Map<String, String>> getAttributes() {
		return attributes;
	}

	

	/**
	 * 
	 * Tells if the obligation will be executed at a permit or a deny
	 * 
	 * @return true if is "permitted", false otherwise
	 */
	public boolean isFulfillOn() {
		return fulfillOn;
	}

	/**
	 * 
	 * Sets the fulfill value
	 * 
	 * @param fulfillOn true if permit, false otherwise
	 */
	public void setFulfillOn(boolean fulfillOn) {
		this.fulfillOn = fulfillOn;
	}

	/**
	 * 
	 * Gives the obligation scope
	 * 
	 * @return action or resource
	 */
	public String getObligationScope ()
	{
		if (this.actionScope) return OBLIGATION_VALUE_ACTION;
		else return OBLIGATION_VALUE_RESOURCE;
	}

	/**
	 * 
	 * Sets the obligation scope
	 * 
	 * @param actionScope: true if is an action scope, false if it's a resource scope
	 */
	public void setActionScope(boolean actionScope) {
		this.actionScope = actionScope;
	}
	
	

}
