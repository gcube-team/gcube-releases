package org.gcube.datatransfer.common.objs;

import java.io.Serializable;
import java.net.URI;

import com.thoughtworks.xstream.XStream;

public class LocalSource implements Serializable{
	private static final long serialVersionUID = 1L;
	//------------------------
	protected static XStream xstream = new XStream();

	public String path;
	public String vfsRoot;
	public long size;
	public boolean directory;
	
	public LocalSource(){
		path=null;
		vfsRoot=null;
	}

	public String getPath() {
		return path;
	}

	public long getSize() {
		return size;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public String getVfsRoot() {
		return vfsRoot;
	}

	public void setVfsRoot(String vfsRoot) {
		this.vfsRoot = vfsRoot;
	}
	

	
}
