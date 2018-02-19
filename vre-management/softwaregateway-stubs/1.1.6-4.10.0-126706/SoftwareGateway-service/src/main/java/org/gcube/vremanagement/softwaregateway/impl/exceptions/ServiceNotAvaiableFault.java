package org.gcube.vremanagement.softwaregateway.impl.exceptions;
/**
 * Exception generated if there are problem with services
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class ServiceNotAvaiableFault extends Exception {
	
	 
	private static final long serialVersionUID = 1L;

	public ServiceNotAvaiableFault()
	  {
	    super("Service problem: the service is not avaiable");
	  }
	 
	 public ServiceNotAvaiableFault(String msg)
	  {
	    super(" Service problem: the service is not avaiable "+msg );
	  }

}
