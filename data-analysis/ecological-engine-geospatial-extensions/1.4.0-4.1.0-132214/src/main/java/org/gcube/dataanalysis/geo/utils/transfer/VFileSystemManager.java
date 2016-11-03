package org.gcube.dataanalysis.geo.utils.transfer;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

/**
 * 
 * @author andrea
 *
 */

public class VFileSystemManager {
	
	File vfsRoot = null;
	
	FileSystemManager manager = null;

	public VFileSystemManager(String vfsRoot) throws FileSystemException {
		this.vfsRoot = new File( vfsRoot);
		this.manager = VFS.getManager();
				
	}
	
	public FileObject resolveFile(String filePath) throws FileSystemException{
		return this.manager.resolveFile(this.vfsRoot,filePath);
	}
	
	public FileObject resolveFolder(String filePath) throws FileSystemException{
		return this.manager.resolveFile(this.vfsRoot,filePath);
	}

}
