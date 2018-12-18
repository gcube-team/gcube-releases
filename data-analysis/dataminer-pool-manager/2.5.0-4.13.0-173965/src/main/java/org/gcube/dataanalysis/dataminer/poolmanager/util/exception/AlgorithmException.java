package org.gcube.dataanalysis.dataminer.poolmanager.util.exception;

public class AlgorithmException extends DMPMException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5678597187512954288L;
	private String algorithmName;
	
	public AlgorithmException (String algorithmName)
	{
		super ("Algorithm exception");
		this.algorithmName = algorithmName;
		
	}


	
	public AlgorithmException (String algorithmName, Throwable cause)
	{
		super ("Algorithm exception", cause);
		this.algorithmName = algorithmName;
	}

	@Override
	public String getErrorMessage() {

		return "Installation completed but DataMiner Interface not working correctly or files "
				+ this.algorithmName + ".jar and " + this.algorithmName
				+ "_interface.jar not availables at the expected path";
	}

	
}
