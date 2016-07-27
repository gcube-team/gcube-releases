package org.gcube.common.core.resources.runninginstance;
/***
 * 
 * @author   Andrea Manzi (CNR)
 *
 */
public class Endpoint {
	  
	    protected String value;
	 
	    protected String entryName;

	    
	    
	    public Endpoint( )  {   	
	    }
	    /**
	     * Gets the value of the value property.
	     *
	     * @return
	     *     possible object is
	     *     {@link String }
	     *
	     */
	    public String getValue() {
	        return value;
	    }

	    /**
	     * Sets the value of the value property.
	     *
	     * @param value
	     *     allowed object is
	     *     {@link String }
	     *
	     */
	    public void setValue(String value) {
	        this.value = value;
	    }

	    /**
	     * Gets the value of the entryName property.
	     *
	     * @return
	     *     possible object is
	     *     {@link String }
	     *
	     */
	    public String getEntryName() {
	        return entryName;
	    }

	   /**
	     * Sets the value of the entryName property.
	     *
	     * @param value
	     *     allowed object is
	     *     {@link String }
	     *
	     */
	    public void setEntryName(String value) {
	        this.entryName = value;
	    }
	    
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final Endpoint other = (Endpoint) obj;
			
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (! value.equals(other.value))
				return false;
			
			if (entryName == null) {
				if (other.entryName != null)
					return false;
			} else if (! entryName.equals(other.entryName))
				return false;
			
			
			return true;
		}

}
