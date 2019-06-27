package org.gcube.data.transfer.library.model;

import java.io.File;

import org.gcube.data.transfer.library.faults.InvalidSourceException;

public class LocalSource extends Source<File> {

	private File theFile=null; 

	public LocalSource(File theFile) throws InvalidSourceException {
		super();
		if(theFile==null)throw new InvalidSourceException("File cannot be null");
		this.theFile = theFile;
	}
	
	@Override
	public boolean validate() throws InvalidSourceException {		
		if(!theFile.exists()) throw new InvalidSourceException("File "+theFile.getAbsolutePath()+"doesn't exist");
		if(!theFile.canRead()) throw new InvalidSourceException("Unable to read from file "+theFile.getAbsolutePath());
		if(theFile.isDirectory()) throw new InvalidSourceException("Transfer of directory is not yet supported");
		return true;
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clean() {
		// TODO Auto-generated method stub

	}

	@Override
	public File getTheSource() {
		return theFile;
	}
}
