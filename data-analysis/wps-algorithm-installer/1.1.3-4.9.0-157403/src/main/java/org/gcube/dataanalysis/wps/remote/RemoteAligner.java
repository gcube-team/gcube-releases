package org.gcube.dataanalysis.wps.remote;


public class RemoteAligner extends RemoteInstaller{

	

/*
	public static void align(String dataminer,String password) throws Exception{
		long t0 = System.currentTimeMillis();
		String libdir = "cd ./tomcat/webapps/wps/WEB-INF/lib/";
		String getAlgorithms = "wget -r -l1 -e robots=off --no-parent http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/DataMinerConfiguration/algorithms/";
		String moveAlgorithms = "mv svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/DataMinerConfiguration/algorithms/* ~/tomcat/webapps/wps/WEB-INF/lib/";
		String rmAlgorithms = "rm -r svn.research-infrastructures.eu/";
		
		String libcfg = "cd ../../ecocfg/";
		String getconfig = "wget -r -l1 -e robots=off --no-parent http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/DataMinerConfiguration/cfg/";	
		String moveConfig = "mv svn.research-infrastructures.eu/public/d4science/gcube/trunk/data-analysis/DataMinerConfiguration/cfg/* ~/tomcat/webapps/wps/ecocfg/";
		String rmConfig = "rm -r svn.research-infrastructures.eu/";
		
		String configDir = "cd ../config/";	
		
		String changewpsconfig = "sed -Ei 's/localhost/"+dataminer+"/g' wps_config.xml";
		
		String commands [] = {
				sshConnection+dataminer,"2",
				"y","0",
				password,"0",
				"ls -l","0",
				"./stopContainer.sh","3",
				libdir,"0",
				getAlgorithms,"30",
				moveAlgorithms,"1",
				rmAlgorithms,"1",
				libcfg,"0",
				getconfig,"5",
				moveConfig,"1",
				rmConfig,"1",
				configDir,"0",
				changewpsconfig,"1",
				"cd /home/gcube/","0",
				"./startContainer.sh","60"
				};
			
		
		cmd2(commands);
		
		System.out.println("Elapsed Time: "+(System.currentTimeMillis()-t0));
	}
	
	*/
	
}
