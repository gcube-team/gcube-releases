package org.gcube.datatransfer.agent.vfs.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
//import org.apache.commons.vfs2.provider.gridftp.cogjglobus.GridFtpFileSystemConfigBuilder;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.junit.Test;

public class Gridtest {
	
	GSSCredential proxy;
	GCUBEClientLog logger = new GCUBEClientLog(this.getClass());

	
	@Test
	public void testVFSCopy() throws FileSystemException{
	    
		StandardFileSystemManager fsManager = new StandardFileSystemManager();
		
		FileSystemOptions option = new FileSystemOptions();
		
		this.load("/Users/andrea/proxy.pem");
		
		/*GridFtpFileSystemConfigBuilder.getInstance().setGSSCredential(option, proxy);
		GridFtpFileSystemConfigBuilder.getInstance().setRootURI(option, "");
		GridFtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(option, true);
		*/fsManager.init();
		

		FileObject dest = fsManager.resolveFile("gsiftp://dpm.research-infrastructures.eu:2811/data/d4science-research-infrastructures.eu/2012-12-02/testfile",option);
		FileObject src = fsManager.resolveFile(new File("/Users/andrea/selFAO.csv").getAbsolutePath());
		dest.copyFrom(src, Selectors.SELECT_SELF);

		
	}
	
	/* * Load proxy from a valid proxy file. It is implemented by calling Java COG API
	 * @param certFile local proxy file
	 */
	public void load(String certFile) {
		proxy = null;
		try{
			// commented code below is not recommended by cog manual. listed here for illustration purpose
			//GlobusCredential cred = new GlobusCredential(proxyFile);	        
       	//GSSCredential gssCred = new GlobusGSSCredentialImpl(cred, GSSCredential.INITIATE_AND_ACCEPT);
			//proxy = gssCred;
       //} catch (GlobusCredentialException e1){
      	// 	e1.printStackTrace();
			
			File proxyFile = new File(certFile);
			if (proxyFile.exists()) {
				byte [] credData = new byte[(int)proxyFile.length()];
				FileInputStream in = new FileInputStream(proxyFile);
				in.read(credData);
				in.close();
				// create credential by loading from proxy file
				ExtendedGSSManager manager = (ExtendedGSSManager)ExtendedGSSManager.getInstance();
				proxy = manager.createCredential(credData,
		  		                         ExtendedGSSCredential.IMPEXP_OPAQUE,
		                                 GSSCredential.DEFAULT_LIFETIME,
		                                 null, // use default mechanism - GSI
		                                 GSSCredential.INITIATE_AND_ACCEPT);
			}
       } catch (GSSException e2) {
       	e2.printStackTrace();           
       } catch (IOException ioe) {
       	ioe.printStackTrace();
       }
	}
}
