package org.gcube.common.core.resources.runninginstance;

import java.util.ArrayList;
import java.util.List;
/***
 * 
 * @author   Andrea Manzi (CNR)
 *
 */
public class RunningInstanceInterfaces {
	 
	    protected List<Endpoint> endpoint;

	    /**
	     * Gets the value of the endpoint property.
	     * <p>
	     * Objects of the following type(s) are allowed in the list
	     * {@link Endpoint }
	     *
	     *
	     */
	    public List<Endpoint> getEndpoint() {
	        if (endpoint == null) {
	            endpoint = new ArrayList<Endpoint>();
	        }
	        return this.endpoint;
	    }
	    
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final RunningInstanceInterfaces other = (RunningInstanceInterfaces) obj;
			
			if (endpoint == null) {
				if (other.endpoint != null)
					return false;
			} else if (! endpoint.equals(other.endpoint))
				return false;
			
			
			return true;
		}

}
