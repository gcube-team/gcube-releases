package gr.uoa.di.madgik.searchlibrary.operatorlibrary.indexfuse;

/**
 * Placeholder used to identify records written in a file
 * 
 * @author UoA
 */
public class FilePosition {
	/**
	 * The starting position in the file
	 */
	private long startOffset=0;
	/**
	 * The ending position in the file
	 */
	private long stopOffset=0;
	
	/**
	 * Creates a new {@link FilePosition}
	 */
	public FilePosition(){}
	
	/**
	 * Creates a new {@link FilePosition}
	 * 
	 * @param startOffset The starting offset of the record
	 * @param stopOffset The ending offset of the record
	 */
	public FilePosition(long startOffset,long stopOffset){
		this.startOffset=startOffset;
		this.stopOffset=stopOffset;
	}

	/**
	 * Retrieves the starting offset
	 * 
	 * @return The starting offset
	 */
	public long getStartOffset() {
		return startOffset;
	}

	/**
	 * Sets the starting offset
	 * 
	 * @param startOffset The starting offset
	 */
	public void setStartOffset(long startOffset) {
		this.startOffset = startOffset;
	}

	/**
	 * Retrieves the ending offset
	 * 
	 * @return The ending offset
	 */
	public long getStopOffset() {
		return stopOffset;
	}

	/**
	 * Sets the ending offset
	 * 
	 * @param stopOffset The ending offset
	 */
	public void setStopOffset(long stopOffset) {
		this.stopOffset = stopOffset;
	}
}
