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
	
	
	public String getStageRepository(){
		return props.getProperty("svn.stage.software.repo");
	}
	
	public String getProdRepository(){
		return props.getProperty("svn.prod.software.repo");
	}
	
	
	public String getGhostAlgoDirectory(){
		return props.getProperty("svn.stage.algo.ghost.repo");
	}
	
	
	public String getProdGhostAlgoDirectory(){
		return props.getProperty("svn.prod.algo.ghost.repo");
	}
	
	
	//RProto
//	public String getSVNRProtoAlgorithmsList(){
//		return props.getProperty("svn.rproto.algorithms-list");
//	}
//
//	public String getSVNRProtoLinuxCompiledDepsList(){
//		return props.getProperty("svn.rproto.deps-linux-compiled");
//	}
//
//	public String getSVNRProtoCRANDepsList(){
//		return props.getProperty("svn.rproto.deps-r");
//	}
//
//	public String getSVNRProtoPreInstalledDepsList(){
//		return props.getProperty("svn.rproto.deps-pre-installed");
//	}
//	
//	public String getSVNRProtoRBDepsList(){
//		return props.getProperty("svn.rproto.deps-r-blackbox");
//	}
//	
//	public String getSVNRProtoJavaDepsList(){
//		return props.getProperty("svn.rproto.deps-java");
//	}
//	
//	public String getSVNRProtoKWDepsList(){
//		return props.getProperty("svn.rproto.deps-knime-workflow");
//	}
//	
//	public String getSVNRProtoOctaveDepsList(){
//		return props.getProperty("svn.rproto.deps-octave");
//	}
//	
//	public String getSVNRProtoPythonDepsList(){
//		return props.getProperty("svn.rproto.deps-python");
//	}
//	
//	public String getSVNRProtoWCDepsList(){
//		return props.getProperty("svn.rproto.deps-windows-compiled");
//	}

	
	//Prod
	public String getSVNProdAlgorithmsList(){
		return props.getProperty("svn.prod.algorithms-list");
	}

	public String getSVNProdLinuxCompiledDepsList(){
		return props.getProperty("svn.prod.deps-linux-compiled");
	}

	public String getSVNProdCRANDepsList(){
		return props.getProperty("svn.prod.deps-r");
	}

	public String getSVNProdPreInstalledDepsList(){
		return props.getProperty("svn.prod.deps-pre-installed");
	}
	
	public String getSVNProdRBDepsList(){
		return props.getProperty("svn.prod.deps-r-blackbox");
	}
	
	public String getSVNProdJavaDepsList(){
		return props.getProperty("svn.prod.deps-java");
	}
	
	public String getSVNProdKWDepsList(){
		return props.getProperty("svn.prod.deps-knime-workflow");
	}
	
	public String getSVNProdOctaveDepsList(){
		return props.getProperty("svn.prod.deps-octave");
	}
	
	public String getSVNProdPythonDepsList(){
		return props.getProperty("svn.prod.deps-python");
	}
	
	public String getSVNProdWCDepsList(){
		return props.getProperty("svn.prod.deps-windows-compiled");
	}
	
	
	
	//PreProd
//		public String getSVNPreProdAlgorithmsList(){
//			return props.getProperty("svn.preprod.algorithms-list");
//		}
//
//		public String getSVNPreProdLinuxCompiledDepsList(){
//			return props.getProperty("svn.preprod.deps-linux-compiled");
//		}
//
//		public String getSVNPreProdCRANDepsList(){
//			return props.getProperty("svn.preprod.deps-r");
//		}
//
//		public String getSVNPreProdPreInstalledDepsList(){
//			return props.getProperty("svn.preprod.deps-pre-installed");
//		}
//		
//		public String getSVNPreProdRBDepsList(){
//			return props.getProperty("svn.preprod.deps-r-blackbox");
//		}
//		
//		public String getSVNPreProdJavaDepsList(){
//			return props.getProperty("svn.preprod.deps-java");
//		}
//		
//		public String getSVNPreProdKWDepsList(){
//			return props.getProperty("svn.preprod.deps-knime-workflow");
//		}
//		
//		public String getSVNPreProdOctaveDepsList(){
//			return props.getProperty("svn.preprod.deps-octave");
//		}
//		
//		public String getSVNPreProdPythonDepsList(){
//			return props.getProperty("svn.preprod.deps-python");
//		}
//		
//		public String getSVNPreProdWCDepsList(){
//			return props.getProperty("svn.preprod.deps-windows-compiled");
//		}
	
	
	
	//dev
//		public String getSVNDevAlgorithmsList(){
//			return props.getProperty("svn.dev.algorithms-list");
//		}
//
//		public String getSVNDevLinuxCompiledDepsList(){
//			return props.getProperty("svn.dev.deps-linux-compiled");
//		}
//
//		public String getSVNDevCRANDepsList(){
//			return props.getProperty("svn.dev.deps-r");
//		}
//
//		public String getSVNDevPreInstalledDepsList(){
//			return props.getProperty("svn.dev.deps-pre-installed");
//		}
//		
//		public String getSVNDevRBDepsList(){
//			return props.getProperty("svn.dev.deps-r-blackbox");
//		}
//		
//		public String getSVNDevJavaDepsList(){
//			return props.getProperty("svn.dev.deps-java");
//		}
//		
//		public String getSVNDevKWDepsList(){
//			return props.getProperty("svn.dev.deps-knime-workflow");
//		}
//		
//		public String getSVNDevOctaveDepsList(){
//			return props.getProperty("svn.dev.deps-octave");
//		}
//		
//		public String getSVNDevPythonDepsList(){
//			return props.getProperty("svn.dev.deps-python");
//		}
//		
//		public String getSVNDevWCDepsList(){
//			return props.getProperty("svn.dev.deps-windows-compiled");
//		}
//		
//	
	
	

//	public String getCSVUrl() {
//		return props.getProperty("HAPROXY_CSV");
//	}

	
//	public String getHost(String env){
//		String a = "";
//		
//		if (env.equals("Dev")||(env.equals("Preprod"))){
//			a = this.getDevStagingHost().trim(); 
//		}
//		
//		if (env.equals("Prod")||(env.equals("Proto"))){
//			a = this.getProtoProdStagingHost().trim(); 
//		}
//		return a;
//	}
	
	
	
//	public String getDevStagingHost() {
//		return props.getProperty("DEV_STAGING_HOST");
//	}
	
	public String getStagingHost() {
		return props.getProperty("STAGE_GHOST");
	}
	
	
	public String getProdHost() {
		return props.getProperty("PROD_GHOST");
	}
	
//	public String getProtoProdStagingHost() {
//		return props.getProperty("PROTO_PROD_STAGING_HOST");
//	}
	
	
	
	
	
	//Staging
			public String getSVNStagingAlgorithmsList(){
				return props.getProperty("svn.stage.algorithms-list");
			}

			public String getSVNStagingLinuxCompiledDepsList(){
				return props.getProperty("svn.stage.deps-linux-compiled");
			}

			public String getSVNStagingCRANDepsList(){
				return props.getProperty("svn.stage.deps-r");
			}

			public String getSVNStagingPreInstalledDepsList(){
				return props.getProperty("svn.stage.deps-pre-installed");
			}
			
			public String getSVNStagingRBDepsList(){
				return props.getProperty("svn.stage.deps-r-blackbox");
			}
			
			public String getSVNStagingJavaDepsList(){
				return props.getProperty("svn.stage.deps-java");
			}
			
			public String getSVNStagingKWDepsList(){
				return props.getProperty("svn.stage.deps-knime-workflow");
			}
			
			public String getSVNStagingOctaveDepsList(){
				return props.getProperty("svn.stage.deps-octave");
			}
			
			public String getSVNStagingPythonDepsList(){
				return props.getProperty("svn.stage.deps-python");
			}
			
			public String getSVNStagingWCDepsList(){
				return props.getProperty("svn.stage.deps-windows-compiled");
			}
	
	
	
	

	public static void main(String[] args) throws FileNotFoundException {
		ServiceConfiguration a = new ServiceConfiguration();
		//System.out.println(a.getStagingHost());
		//System.out.println(a.getDevStagingHost());
		//System.out.println(a.getProtoProdStagingHost());
		//System.out.println(a.getCSVUrl());
		//System.out.println(a.getSVNMainAlgoRepo());
		//System.out.println(a.getSVNRProtoCRANDepsList());
		//System.out.println(a.getProdHost());
		System.out.println(a.getSVNProdRBDepsList());
	}


}
