package org.gcube.datatransfer.common.outcome;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class FileTransferOutcome extends TransferOutcome{
	private static final long serialVersionUID = 906856612001819618L;

	private String filename;
	
	private String dest;
	
	private Long transferTime;
	private Long transferredBytes;
	private Long total_size;

	public Long getTransferTime() {
		return transferTime;
	}


	public void setTransferTime(Long transferTime) {
		this.transferTime = transferTime;
	}


	public String getDest() {
		return dest;
	}


	public void setDest(String dest) {
		this.dest = dest;
	}


	/**
	 * Creates an instance with successfully added tree.
	 * @param tree the tree
	 */
	public FileTransferOutcome(String fileName) {
		this.filename=fileName;
	}

	
	/**
	 * Returns the successfully transfer a file.
	 * @return the tree, or <code>null</code> if the outcome indicates a failure
	 */
	public String fileName() {
		return filename;
	}
	

	
	@Override
	public String toString() {
		return isSuccess() ? fileName() : failure();
	}

	
	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}


	public Long getTransferredBytes() {
		return transferredBytes;
	}


	public void setTransferredBytes(Long transferredBytes) {
		this.transferredBytes = transferredBytes;
	}


	public Long getTotal_size() {
		return total_size;
	}


	public void setTotal_size(Long total_size) {
		this.total_size = total_size;
	}
	
}
