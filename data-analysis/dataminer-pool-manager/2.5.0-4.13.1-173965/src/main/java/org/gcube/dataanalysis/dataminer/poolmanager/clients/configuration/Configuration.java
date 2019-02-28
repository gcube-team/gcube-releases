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
	
	public String getSVNPython3_6DepsList();

	public String getSVNWCDepsList();
	
	public SVNRepository getSVNRepository();
	
	public String getGhostAlgoDirectory();

}