package org.gcube.common.core.resources.runninginstance;

import java.util.ArrayList;
import java.util.List;

/***
 * 
 * @author   Andrea Manzi (CNR)
 *
 */
public class RIEquivalenceFunction {
	
	protected List<Function> functions;

    /**
     * Gets the value of the Function property.
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Function }
     *
     *
     */
    public List<Function> getFunctions() {
        if (functions == null) {
        	functions = new ArrayList<Function>();
        }
        return this.functions;
    }
    
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		final RIEquivalenceFunction other = (RIEquivalenceFunction) obj;
		
		if (functions == null) {
			if (other.functions != null)
				return false;
		} else if (! functions.equals(other.functions))
			return false;
		
		
		return true;
	}
	
	public static class Function {
		
	
	
	        protected String name;
	     
	        protected ActualParameters actualParameters;

	        /**
	         * Gets the value of the name property.
	         * 
	         * @return
	         *     possible object is
	         *     {@link String }
	         *     
	         */
	        public String getName() {
	            return name;
	        }

	        /**
	         * Sets the value of the name property.
	         * 
	         * @param value
	         *     allowed object is
	         *     {@link String }
	         *     
	         */
	        public void setName(String value) {
	            this.name = value;
	        }

	        /**
	         * Gets the value of the actualParameters property.
	         * 
	         * @return
	         *     possible object is
	         *     {@link ActualParameters }
	         *     
	         */
	        public ActualParameters getActualParameters() {
	            return actualParameters;
	        }

	        /**
	         * Sets the value of the actualParameters property.
	         * 
	         * @param value
	         *     allowed object is
	         *     {@link ActualParameters }
	         *     
	         */
	        public void setActualParameters(ActualParameters value) {
	            this.actualParameters = value;
	        }
	        
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				
				final Function other = (Function) obj;
				
				if (actualParameters == null) {
					if (other.actualParameters != null)
						return false;
				} else if (! actualParameters.equals(other.actualParameters))
					return false;
				
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (! name.equals(other.name))
					return false;
				
				
				return true;
			}

	       
	        public static class ActualParameters {

	           
	            protected List<Param> param;

	            /**
	             * Gets the value of the param property.
	             * 
	             * <p>
	             * Objects of the following type(s) are allowed in the list
	             * {@link Param }
	             * 
	             * 
	             */
	            public List<Param> getParam() {
	                if (param == null) {
	                    param = new ArrayList<Param>();
	                }
	                return this.param;
	            }
	            
	    		public boolean equals(Object obj) {
	    			if (this == obj)
	    				return true;
	    			if (obj == null)
	    				return false;
	    			if (getClass() != obj.getClass())
	    				return false;
	    			
	    			final ActualParameters other = (ActualParameters) obj;
	    			
	    			if (param == null) {
	    				if (other.param != null)
	    					return false;
	    			} else if (! param.equals(other.param))
	    				return false;
	    			
	    			
	    			return true;
	    		}


	            public static class Param {

	               
	                protected String name;
	               
	                protected List<String> value;

	                /**
	                 * Gets the value of the name property.
	                 * 
	                 * @return
	                 *     possible object is
	                 *     {@link String }
	                 *     
	                 */
	                public String getName() {
	                    return name;
	                }

	                /**
	                 * Sets the value of the name property.
	                 * 
	                 * @param value
	                 *     allowed object is
	                 *     {@link String }
	                 *     
	                 */
	                public void setName(String value) {
	                    this.name = value;
	                }

	                /**
	                 * Gets the value of the value property.
	                 * 
	                 * <p>
	                 * Objects of the following type(s) are allowed in the list
	                 * {@link String }
	                 * 
	                 * 
	                 */
	                public List<String> getValue() {
	                    if (value == null) {
	                        value = new ArrayList<String>();
	                    }
	                    return this.value;
	                }
	                
	        		public boolean equals(Object obj) {
	        			if (this == obj)
	        				return true;
	        			if (obj == null)
	        				return false;
	        			if (getClass() != obj.getClass())
	        				return false;
	        			
	        			final Param other = (Param) obj;
	        			
	        			if (name == null) {
	        				if (other.name != null)
	        					return false;
	        			} else if (! name.equals(other.name))
	        				return false;
	        			
	        			if (value == null) {
	        				if (other.value != null)
	        					return false;
	        			} else if (! value.equals(other.value))
	        				return false;
	        			
	        			
	        			return true;
	        		}

	            }

	        }

	    }
}

