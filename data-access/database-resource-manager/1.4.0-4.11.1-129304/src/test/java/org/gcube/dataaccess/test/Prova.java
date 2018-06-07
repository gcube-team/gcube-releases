package org.gcube.dataaccess.test;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;

public class Prova {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		AnalysisLogger.setLogger("./cfg/"+AlgorithmConfiguration.defaultLoggerFile);
		
//		AnalysisLogger.getLogger().debug("ciao");
		
		
		 ScopeProvider.instance.set("/gcube/devsec");
		
		try {
		
			String password = StringEncrypter.getEncrypter().decrypt("UwNMZOK7FlIjGPR+NZCV6w==");
			
			System.out.println("pwd: " + password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StandardLocalExternalAlgorithm sle = null;
//		System.out.println("CIAO");

	}

}
