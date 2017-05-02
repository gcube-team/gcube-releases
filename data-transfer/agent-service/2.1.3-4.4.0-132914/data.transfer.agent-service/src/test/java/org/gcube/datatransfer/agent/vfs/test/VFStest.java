package org.gcube.datatransfer.agent.vfs.test;


import java.io.File;

import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.webdav.WebdavFileSystemConfigBuilder;
import org.junit.Test;


public class VFStest {
	
		@Test
		public void testVFS() throws Exception{
			FileSystemManager fsManager = VFS.getManager();
			FileObject file = fsManager.resolveFile( "webdav://andrea.manzi:mnzndr80r05@node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-custom-webapp-2.4.1/repository/default/Home/andrea.manzi/Workspace/Angola Capture Time Series(0)");
			FileObject outFile =fsManager.resolveFile ("/Users/andrea/test");		
			outFile.copyFrom(file, Selectors.SELECT_SELF);

	}	
	
		public static FileSystemOptions createDefaultOptions()
				throws FileSystemException {
	
			FileSystemOptions opts = new FileSystemOptions();


			// Root directory set to user home
			WebdavFileSystemConfigBuilder.getInstance().setRootURI(opts,"/");
	

			// Timeout is count by Milliseconds
			WebdavFileSystemConfigBuilder.getInstance().setMaxTotalConnections(opts,10000);

			return opts;
		}



}
