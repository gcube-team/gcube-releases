package org.gcube.dataanalysis.dataminer.poolmanager.util.impl;

import java.io.File;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.util.CheckMethod;
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.SVNCommitException;
import org.tmatesoft.svn.core.SVNException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class CheckMethodProduction extends CheckMethod{


	public CheckMethodProduction() 
	{
		super (DMPMClientConfiguratorManager.getInstance().getProductionConfiguration());
		
	}
	
	
	@Override
	protected void copyFromDmToSVN(File a) throws SVNException, SVNCommitException, JSchException, SftpException  {
		super.copyFromDmToSVN(a, new SVNUpdaterProduction());
		
	}
    

	
	public static void main(String[] args) throws Exception {
//		ServiceConfiguration a = new ServiceConfiguration();
//		System.out.println(a.getStagingHost());
	
	CheckMethodProduction a = new CheckMethodProduction();
	
	//a.getFiles("/trunk/data-analysis/RConfiguration/RPackagesManagement/r_deb_pkgs.txt, /trunk/data-analysis/RConfiguration/RPackagesManagement/r_cran_pkgs.txt, /trunk/data-analysis/RConfiguration/RPackagesManagement/r_github_pkgs.txt");
	
//	File aa = new File("OCTAVEBLACKBOX.jar");
//	System.out.println(aa.getName());
//	System.out.println(aa.getPath());
	
	
	
	
	
	
	//a.copyFromDmToSVN(aa);
//	if (a.checkMethod("dataminer-ghost-d.dev.d4science.org", "708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548")){
//		System.out.println("AAA");	}
//		
//	if (a.doesExist("/home/gcube/wps_algorithms/algorithms/WINDOWS_BLACK_BOX_EXAMPLE.jar")){
//		System.out.println("BBBB");
//	
//	}
//	if (a.doesExist("/home/gcube/wps_algorithms/algorithms/WINDOWS_BLACK_BOX_EXAMPLE_interface.jar")){
//		System.out.println("CCCC");}
//	
//	File aa = new File("/home/gcube/wps_algorithms/algorithms/RBLACKBOX_interface.jar");
//	a.copyFromDmToSVN(aa, "Dev");
	
//
	
	
//System.out.println(a.checkMethod("dataminer-ghost-t.pre.d4science.org",
//		"2eceaf27-0e22-4dbe-8075-e09eff199bf9-98187548"));

//System.out.println(a.checkMethod("dataminer-proto-ghost.d4science.org",
	//	"3a23bfa4-4dfe-44fc-988f-194b91071dd2-843339462"));

	try
	{
		a.checkMethod("dataminer-ghost-d.dev.d4science.org",
				"708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548");
	} catch (Exception e)
	{
		e.printStackTrace();
	}



//Algorithm aa = new Algorithm();
//aa.setName("UDPIPE_WRAPPER");
//System.out.println(a.algoExists(aa));
////
//ServiceConfiguration bp = new ServiceConfiguration();
////
//SecurityTokenProvider.instance.set("708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548");
////
//if (a.checkMethod(bp.getStagingHost(), SecurityTokenProvider.instance.get())&&a.algoExists(aa)); {
//System.out.println("ciao");
//
//}

//
//Algorithm al = new Algorithm();
//	al.setName("UDPIPE_WRAPPER");
//	a.deleteFiles(al);
	
	
	
	
	
}
}
