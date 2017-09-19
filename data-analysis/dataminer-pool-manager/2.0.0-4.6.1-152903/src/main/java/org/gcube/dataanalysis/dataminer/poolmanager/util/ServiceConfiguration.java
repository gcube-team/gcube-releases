package org.gcube.dataanalysis.dataminer.poolmanager.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ServiceConfiguration {
	public static String home = System.getProperty("user.home");

	private Properties props;

	public ServiceConfiguration() {
		this(home+"/dataminer-pool-manager/dpmConfig/service.properties");
	}

	public ServiceConfiguration(String configFile) {
		this.props = new Properties();

		FileInputStream input;

		try {
			input = new FileInputStream(configFile);
			// loading properites from properties file
			try {
				props.load(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}



	public String getSVNRepository(){
		return props.getProperty("svn.repository");
	}
	
	
	public String getSVNMainAlgoRepo(){
		return props.getProperty("svn.algo.main.repo");
	}
	
	
	
	//RProto
	public String getSVNRProtoAlgorithmsList(){
		return props.getProperty("svn.rproto.algorithms-list");
	}

	public String getSVNRProtoLinuxCompiledDepsList(){
		return props.getProperty("svn.rproto.deps-linux-compiled");
	}

	public String getSVNRProtoCRANDepsList(){
		return props.getProperty("svn.rproto.deps-r");
	}

	public String getSVNRProtoPreInstalledDepsList(){
		return props.getProperty("svn.rproto.deps-pre-installed");
	}
	
	public String getSVNRProtoRBDepsList(){
		return props.getProperty("svn.rproto.deps-r-blackbox");
	}
	
	public String getSVNRProtoJavaDepsList(){
		return props.getProperty("svn.rproto.deps-java");
	}
	
	public String getSVNRProtoKWDepsList(){
		return props.getProperty("svn.rproto.deps-knime-workflow");
	}
	
	public String getSVNRProtoOctaveDepsList(){
		return props.getProperty("svn.rproto.deps-octave");
	}
	
	public String getSVNRProtoPythonDepsList(){
		return props.getProperty("svn.rproto.deps-python");
	}
	
	public String getSVNRProtoWCDepsList(){
		return props.getProperty("svn.rproto.deps-windows-compiled");
	}

	
	//Prod
	public String getSVNProdAlgorithmsList(){
		return props.getProperty("svn.prod.algorithms-list");
	}

	public String getSVNRProdLinuxCompiledDepsList(){
		return props.getProperty("svn.prod.deps-linux-compiled");
	}

	public String getSVNRProdCRANDepsList(){
		return props.getProperty("svn.prod.deps-r");
	}

	public String getSVNRProdPreInstalledDepsList(){
		return props.getProperty("svn.prod.deps-pre-installed");
	}
	
	public String getSVNRProdRBDepsList(){
		return props.getProperty("svn.prod.deps-r-blackbox");
	}
	
	public String getSVNRProdJavaDepsList(){
		return props.getProperty("svn.prod.deps-java");
	}
	
	public String getSVNRProdKWDepsList(){
		return props.getProperty("svn.prod.deps-knime-workflow");
	}
	
	public String getSVNRProdOctaveDepsList(){
		return props.getProperty("svn.prod.deps-octave");
	}
	
	public String getSVNRProdPythonDepsList(){
		return props.getProperty("svn.prod.deps-python");
	}
	
	public String getSVNRProdWCDepsList(){
		return props.getProperty("svn.prod.deps-windows-compiled");
	}
	
	
	
	
	//dev
		public String getSVNDevAlgorithmsList(){
			return props.getProperty("svn.dev.algorithms-list");
		}

		public String getSVNRDevLinuxCompiledDepsList(){
			return props.getProperty("svn.dev.deps-linux-compiled");
		}

		public String getSVNRDevCRANDepsList(){
			return props.getProperty("svn.dev.deps-r");
		}

		public String getSVNRDevPreInstalledDepsList(){
			return props.getProperty("svn.dev.deps-pre-installed");
		}
		
		public String getSVNRDevRBDepsList(){
			return props.getProperty("svn.dev.deps-r-blackbox");
		}
		
		public String getSVNRDevJavaDepsList(){
			return props.getProperty("svn.dev.deps-java");
		}
		
		public String getSVNRDevKWDepsList(){
			return props.getProperty("svn.dev.deps-knime-workflow");
		}
		
		public String getSVNRDevOctaveDepsList(){
			return props.getProperty("svn.dev.deps-octave");
		}
		
		public String getSVNRDevPythonDepsList(){
			return props.getProperty("svn.dev.deps-python");
		}
		
		public String getSVNRDevWCDepsList(){
			return props.getProperty("svn.dev.deps-windows-compiled");
		}
		
	
	
	

	public String getCSVUrl() {
		return props.getProperty("HAPROXY_CSV");
	}

	
	public String getHost(String env){
		String a = "";
		
		if (env.equals("Dev")){
			a = this.getDevStagingHost().trim(); 
		}
		
		if (env.equals("Prod")||(env.equals("Prod"))){
			a = this.getProtoProdStagingHost().trim(); 
		}
		return a;
		
		
	}
	
	
	
	public String getDevStagingHost() {
		return props.getProperty("DEV_STAGING_HOST");
	}
	
	
	public String getProtoProdStagingHost() {
		return props.getProperty("PROTO_PROD_STAGING_HOST");
	}
	
	

	public static void main(String[] args) throws FileNotFoundException {
		ServiceConfiguration a = new ServiceConfiguration();
		//System.out.println(a.getStagingHost());
		System.out.println(a.getDevStagingHost());
		System.out.println(a.getProtoProdStagingHost());
		System.out.println(a.getCSVUrl());
		System.out.println(a.getSVNMainAlgoRepo());
		System.out.println(a.getSVNRProtoCRANDepsList());
	}


}
