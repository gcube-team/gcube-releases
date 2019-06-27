package org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configurations.AbstractConfiguration;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configurations.Prod;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configurations.Stage;

public class ConfigurationImpl implements Configuration {

	enum CONFIGURATIONS {
		STAGE (new Stage ()),
		PROD (new Prod ());
		
		private AbstractConfiguration type;
		
		private CONFIGURATIONS(AbstractConfiguration type) {
			this.type = type;
		}
		
		@Override
		public String toString() {
			return this.type.getType();
		}
		
		public AbstractConfiguration getType ()
		{
			return this.type;
		}
		

	}
	
	
//	enum REPOSITORIES {
//		REPO ("svn.repository"),
//		MAIN_ALGO ("svn.algo.main.repo");
//		
//		private String type;
//		
//		private REPOSITORIES(String type) {
//			this.type = type;
//		}
//		
//		@Override
//		public String toString() {
//			return this.type;
//		}
//	}
	
	
	private CONFIGURATIONS type;
	private ClientConfigurationCache cache;
	
	public  ConfigurationImpl(CONFIGURATIONS type,ClientConfigurationCache cache) {
		this.type = type;
		this.cache = cache;
	}
	
	
	@Override
	public String getHost() {

		return this.cache.getConfiguration(this.type).getHost ();
	}

	@Override
	public String getSVNAlgorithmsList() {

		return this.cache.getConfiguration(this.type).getAlgorithmsList();
	}

	@Override
	public String getRepository() {

		return this.cache.getConfiguration(this.type).getSoftwareRepo();
	}

	@Override
	public String getSVNLinuxCompiledDepsList() 
	{
		return this.cache.getConfiguration(this.type).getDepsLinuxCompiled();
	}

	@Override
	public String getSVNPreInstalledDepsList() {

		return this.cache.getConfiguration(this.type).getDepsPreInstalled();
	}

	@Override
	public String getSVNRBDepsList() 
	{
		return this.cache.getConfiguration(this.type).getDepsRBlackbox();
	}

	@Override
	public String getSVNCRANDepsList() {

		return this.cache.getConfiguration(this.type).getDepsR();
	}

	@Override
	public String getSVNJavaDepsList() {

		return this.cache.getConfiguration(this.type).getDepsJava();
	}

	@Override
	public String getSVNKWDepsList() {

		return this.cache.getConfiguration(this.type).getDepsKnimeWorkflow();
	}

	@Override
	public String getSVNOctaveDepsList() {

		return this.cache.getConfiguration(this.type).getDepsOctave();
	}

	@Override
	public String getSVNPythonDepsList() {

		return this.cache.getConfiguration(this.type).getDepsPython();
	}


	@Override
	public String getSVNPython3_6DepsList() {

		return this.cache.getConfiguration(this.type).getDepsPython3_6();
	}

	
	@Override
	public String getSVNWCDepsList() {

		return this.cache.getConfiguration(this.type).getDepsWindowsCompiled();
	}

	@Override
	public SVNRepository getSVNRepository() 
	{
		return this.cache.getSVNRepository();
	}
	

	
	@Override
	public String getGhostAlgoDirectory() {

		return this.cache.getConfiguration(this.type).getGhostRepo();
	}
}
