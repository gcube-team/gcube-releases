package org.gcube.common.core.monitoring;

/**
 * Interface for GCUBE Probes
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public interface  GCUBEProbe {    
	public void execute() throws Exception;
	public void sendMessage(GCUBEMessage message);   
}
