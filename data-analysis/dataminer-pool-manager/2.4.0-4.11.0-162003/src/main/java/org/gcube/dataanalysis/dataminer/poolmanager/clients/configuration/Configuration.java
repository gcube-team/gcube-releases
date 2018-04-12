package org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration;

public interface Configuration {


	public String getHost();

	public String getSVNAlgorithmsList();

	public String getRepository();

	public String getSVNLinuxCompiledDepsList();

	public String getSVNPreInstalledDepsList();

	public String getSVNRBDepsList();

	public String getSVNCRANDepsList();

	public String getSVNJavaDepsList();

	public String getSVNKWDepsList();

	public String getSVNOctaveDepsList();

	public String getSVNPythonDepsList();

	public String getSVNWCDepsList();
	
	public String getSVNRepository();
	
	public String getSVNMainAlgoRepo();
	
	public String getGhostAlgoDirectory();

}