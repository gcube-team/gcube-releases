package org.gcube.common.core.resources.runninginstance;

import org.gcube.common.core.utils.proxies.AccessControlProxyContext.Restricted;


/***
 * 
 * @author   Andrea Manzi (CNR)
 *
 */
public class RunningInstanceSecurity {
	
	    protected Identity runningInstanceIdentity;
	
	    protected String entryName;

	    /**
	     * Gets the value of the runningInstanceIdentity property.
	     * 
	     * @return
	     *     possible object is
	     *     {@link Identity }
	     *     
	     */
	    public Identity getRunningInstanceIdentity() {
	        return runningInstanceIdentity;
	    }

	    /**
	     * Sets the value of the runningInstanceIdentity property.
	     * 
	     * @param value
	     *     allowed object is
	     *     {@link Identity }
	     *     
	     */
	    public void setRunningInstanceIdentity(Identity value) {
	        this.runningInstanceIdentity = value;
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
	    @Restricted public void setEntryName(String value) {
	        this.entryName = value;
	    }
	    
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final RunningInstanceSecurity other = (RunningInstanceSecurity) obj;
			
			if (runningInstanceIdentity == null) {
				if (other.runningInstanceIdentity != null)
					return false;
			} else if (! runningInstanceIdentity.equals(other.runningInstanceIdentity))
				return false;
			
			if (entryName == null) {
				if (other.entryName != null)
					return false;
			} else if (! entryName.equals(other.entryName))
				return false;
			
			
			return true;
		}
	    
	    public static class Identity {

	        protected String subject;
	        
	        protected String caSubject;

	        /**
	         * Gets the value of the subject property.
	         * 
	         * @return
	         *     possible object is
	         *     {@link String }
	         *     
	         */
	        public String getSubject() {
	            return subject;
	        }

	        /**
	         * Sets the value of the subject property.
	         * 
	         * @param value
	         *     allowed object is
	         *     {@link String }
	         *     
	         */
	        @Restricted public void setSubject(String value) {
	            this.subject = value;
	        }

	        /**
	         * Gets the value of the caSubject property.
	         * 
	         * @return
	         *     possible object is
	         *     {@link String }
	         *     
	         */
	        public String getCASubject() {
	            return caSubject;
	        }

	        /**
	         * Sets the value of the caSubject property.
	         * 
	         * @param value
	         *     allowed object is
	         *     {@link String }
	         *     
	         */
	        @Restricted public void setCASubject(String value) {
	            this.caSubject = value;
	        }
	        
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				
				final Identity other = (Identity) obj;
				
				if (subject == null) {
					if (other.subject != null)
						return false;
				} else if (! subject.equals(other.subject))
					return false;
				
				if (caSubject == null) {
					if (other.caSubject != null)
						return false;
				} else if (! caSubject.equals(other.caSubject))
					return false;
				
				
				return true;
			}

	    }

}
