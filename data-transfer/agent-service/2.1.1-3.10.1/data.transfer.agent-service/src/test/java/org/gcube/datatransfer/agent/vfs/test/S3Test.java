package org.gcube.datatransfer.agent.vfs.test;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.junit.Test;

public class S3Test {
	
	@Test
	public void testVFSCopy() throws FileSystemException{
	    
		StandardFileSystemManager fsManager = new StandardFileSystemManager();
		
		fsManager.init();
		//FileObject dir = fsManager.resolveFile("s3://simple-bucket-andrea");
		//dir.createFolder();

		// Upload file to S3
		FileObject dest = fsManager.resolveFile("s3://simple-bucket-andrea/test3");
		FileObject src = fsManager.resolveFile(new File("/Users/andrea/selFAO.csv").getAbsolutePath());
		dest.copyFrom(src, Selectors.SELECT_SELF);

		
	}

}
