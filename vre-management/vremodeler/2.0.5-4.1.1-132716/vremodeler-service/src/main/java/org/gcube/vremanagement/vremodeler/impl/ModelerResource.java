package org.gcube.vremanagement.vremodeler.impl;

import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;
import org.globus.wsrf.ResourceException;

public class ModelerResource extends GCUBEWSResource {
	
	protected static final String RP_ID = "Id";
			
	protected static String[] RPNames = { RP_ID};
	
	private boolean isUseCloud;
	
	private int numberOfVMsForCloud;
	
	private DeployReport deployReport;
	
	
	@Override
	protected void initialise(Object... args) throws ResourceException {
		if (args.length!=1) throw new ResourceException();
		this.setId((String) args[0]);
		this.deployReport=null;
		this.isUseCloud=false;
		this.numberOfVMsForCloud=-1;
	}

	
	/**
	 * 
	 * @return
	 */
	public boolean isUseCloud() {
		return isUseCloud;
	}


	/**
	 * 
	 * @param isUseClound
	 */
	public void setUseCloud(boolean isUseCloud) {
		this.isUseCloud = isUseCloud;
	}

	

	public int getNumberOfVMsForCloud() {
		return numberOfVMsForCloud;
	}


	public void setNumberOfVMsForCloud(int numberOfVMsForCloud) {
		this.numberOfVMsForCloud = numberOfVMsForCloud;
	}


	/**
     * {@inheritDoc}
     */
    public String[] getPropertyNames() {
    	return RPNames;
    }
    
    /**
     * Returns the id.
     * 
     * @return the id.
     */
    public String getId() throws ResourceException {
    	return (String) this.getResourcePropertySet().get(RP_ID).get(0);
    }
    
    /**
     * 
     * @param id
     * @throws ResourceException
     */
    public synchronized void setId(String id) throws ResourceException {
    	this.getResourcePropertySet().get(RP_ID).clear();
		this.getResourcePropertySet().get(RP_ID).add(id);
    }


	public DeployReport getDeployReport() {
		return deployReport;
	}


	public void setDeployReport(DeployReport deployReport) {
		this.deployReport = deployReport;
	}
  
    
 
}
