package org.acme.sample;

import org.gcube.common.core.state.GCUBEWSResource;
import org.globus.wsrf.ResourceProperty;

public class Resource extends GCUBEWSResource {

	

	private static final String NAME_RP_NAME = "Name";
	/** Client visits.*/ 
	int visits;
	/** Client name. */
	String name;
	
     /**{@inheritDoc}*/
    public void initialise(Object... args) throws Exception {
		if (args == null || args.length!=1) throw new IllegalArgumentException();
		this.setName((String) args[0]);
	}

     /** Returns the number of client visits.
     * @return the visits.*/
    public synchronized int getVisits() {return visits;}
    
    /** Returns the client name.
     * @return the name.*/
    public synchronized String getName() {
    	return (String) this.getResourcePropertySet().get(NAME_RP_NAME).get(0);
    	
    }
    
    /** Sets the client name.
     * @params the name.*/
    public synchronized void setName(String name) {
    	ResourceProperty property = this.getResourcePropertySet().get(NAME_RP_NAME);
    	property.clear();
    	property.add(name);
    }

    /**Sets the number of client visits.
     * the visits.*/
    protected synchronized void addVisit() {this.visits++;}
    
    @Override
	protected String[] getPropertyNames() {
		return new String[]{NAME_RP_NAME};
	}
    
}
