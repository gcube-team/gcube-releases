package org.gcube.datatransfer.common.outcome;

/**
 * 
 * @author Andrea Manzi
 *
 */
public enum TransferStatus {
	DONE_WITH_ERRORS("DONE_WITH_ERRORS"),
	QUEUED("QUEUED"),
	STARTED("STARTED"),
	DONE("DONE"),
	CANCEL("CANCEL"),
	FAILED("FAILED");
	String status;
	TransferStatus(String status){this.status = status;}
	public String toString(){return this.status;}
	public boolean hasCompleted () { return (!this.equals(STARTED) && !this.equals(QUEUED)); }
	public boolean hasErrors () { return (this.equals(DONE_WITH_ERRORS) || this.equals(FAILED)); }
}

