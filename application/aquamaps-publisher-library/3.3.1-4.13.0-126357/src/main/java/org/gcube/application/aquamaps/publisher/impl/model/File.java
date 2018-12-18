package org.gcube.application.aquamaps.publisher.impl.model;

import org.apache.commons.io.FilenameUtils;
import org.gcube.application.aquamaps.publisher.impl.PublisherImpl;
import org.gcube.application.aquamaps.publisher.impl.model.FileType;
import org.gcube.common.core.utils.logging.GCUBELog;

public class File {
	
	private static transient GCUBELog logger= new GCUBELog(File.class);
	
	@Override
	public String toString() {
		return "File [type=" + type + ", uuri=" + originalUri + ", name=" + name + "]";
	}

	
	private FileType type=FileType.InternalProfile;
	
	private String originalUri;
	
	private String storedUri=null;
	
	private String name;

	public File(FileType type,String originalUri,String name) {
		this.type=type;
		this.originalUri=originalUri;
		this.name=name;
	}
	
	public FileType getType() {
		return type;
	}
	public void setType(FileType type) {
		this.type = type;
	}
	public String getOriginalUri() {
		return originalUri;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the storedUri
	 */
	public String getStoredUri() {
		return storedUri;
	}


	protected void publish(String baseDir, boolean deleteIfExists) throws Exception{
		// File (or directory) to be moved
		java.io.File file = new java.io.File(this.originalUri);
		String ext = "."+FilenameUtils.getExtension(file.getAbsolutePath());
		java.io.File renameFile = new java.io.File(PublisherImpl.serverPathDir+java.io.File.separator+baseDir, this.name.replace(" ", "_").replace(":", "")+ext);
		if (deleteIfExists && renameFile.exists()) renameFile.delete();
		logger.trace(" the file "+file.getAbsolutePath()+" exists? "+file.exists());
		logger.trace(" the file "+file.getAbsolutePath()+" is readable? "+file.canRead());
		logger.trace(" the file "+file.getAbsolutePath()+" is writable? "+file.canWrite());
		logger.trace(" the file to rename  "+renameFile.getAbsolutePath()+" exists? "+renameFile.exists());
		// Move file to new directory
		if (!file.renameTo(renameFile)) throw new Exception("the file "+this.name+" has not been published");
		else{
			this.storedUri = baseDir+java.io.File.separator+this.name.replace(" ", "_").replace(":", "")+ext;
			this.originalUri =null;
		}
	}
	
	protected void unpublish() throws Exception{
		// File (or directory) to be moved
		java.io.File file = new java.io.File(PublisherImpl.serverPathDir+java.io.File.separator+this.storedUri);
		// Move file to new directory
		if (!file.delete()) throw new Exception("the file "+this.name+" has not been unpublished");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((originalUri == null) ? 0 : originalUri.hashCode());
		result = prime * result
				+ ((storedUri == null) ? 0 : storedUri.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		File other = (File) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (originalUri == null) {
			if (other.originalUri != null)
				return false;
		} else if (!originalUri.equals(other.originalUri))
			return false;
		if (storedUri == null) {
			if (other.storedUri != null)
				return false;
		} else if (!storedUri.equals(other.storedUri))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
