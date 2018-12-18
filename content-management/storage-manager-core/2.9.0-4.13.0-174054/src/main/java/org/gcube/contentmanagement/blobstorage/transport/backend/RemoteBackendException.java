package org.gcube.contentmanagement.blobstorage.transport.backend;

public class RemoteBackendException extends RuntimeException {
	
	 
	private static final long serialVersionUID = 1L;

	public RemoteBackendException()
	  {
	    super("Remote backend problem: impossible to complete operation ");
	  }
	 
	 public RemoteBackendException(String msg)
	  {
	    super(" Remote backend problem: impossible to complete operation "+msg );
	  }
	 
	 
	 public RemoteBackendException(Throwable cause)
	  {
	    super(" Remote backend problem: impossible to complete operation "+cause );
	  }
	 
	 public RemoteBackendException(String msg , Throwable cause){
		 super(msg, cause);
	 }

}
