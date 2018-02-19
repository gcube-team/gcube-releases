package org.gcube.dataanalysis.dataminer.poolmanager.util.exception;

import org.tmatesoft.svn.core.SVNErrorMessage;

public class SVNCommitException extends DMPMException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5225403308313619585L;

	private SVNErrorMessage svnErrorMessage;

	private String fileName;
	
	public SVNCommitException(SVNErrorMessage errorMessage, String fileName) {
		super ("Unable to commit");
		this.svnErrorMessage = errorMessage;
		this.fileName = fileName;
	}

	public SVNCommitException(String message,SVNErrorMessage errorMessage,String fileName) {
		super (message);
		this.svnErrorMessage = errorMessage;
		this.fileName = fileName;
	}
	
	public SVNErrorMessage getSvnErrorMessage() {
		return svnErrorMessage;

	}
	


	@Override
	public String getErrorMessage() {

		return "Commit operation failed for "+this.fileName
						+ "the message of the SVN Server is the following:\n"+this.svnErrorMessage.getMessage();

	}
	
	
}
