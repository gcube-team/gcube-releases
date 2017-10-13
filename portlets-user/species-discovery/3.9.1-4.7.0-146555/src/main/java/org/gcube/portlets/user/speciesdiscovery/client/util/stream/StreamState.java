/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util.stream;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class StreamState {
	
	protected int size;
	protected boolean complete;
	private boolean isBufferFull;
	
	/**
	 * @param size
	 * @param complete
	 * @param isMaxSize 
	 */
	public StreamState(int size, boolean complete, boolean isBufferFull) {
		this.size = size;
		this.complete = complete;
		this.isBufferFull = isBufferFull;
	}
	
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * @return the complete
	 */
	public boolean isComplete() {
		return complete;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StreamState [size=");
		builder.append(size);
		builder.append(", complete=");
		builder.append(complete);
		builder.append("]");
		return builder.toString();
	}

	public boolean isBufferFull() {
		return isBufferFull;
	}

}
