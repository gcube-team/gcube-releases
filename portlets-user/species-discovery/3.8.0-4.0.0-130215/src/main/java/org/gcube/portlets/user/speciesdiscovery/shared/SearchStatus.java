/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SearchStatus implements Serializable {
	
	private static final long serialVersionUID = -2234701519362803828L;
	
	protected boolean resultEOF = false;
	protected int size  = 0;
	protected boolean isMaxSize = false;

	public SearchStatus()
	{
		
	}
	
	/**
	 * @param resultEOF
	 * @param size
	 */
	public SearchStatus(boolean resultEOF, int size, boolean isMaxSize) {
		this.resultEOF = resultEOF;
		this.size = size;
		this.isMaxSize = isMaxSize;
	}

	/**
	 * @return the resultEOF
	 */
	public boolean isResultEOF() {
		return resultEOF;
	}

	/**
	 * @param resultEOF the resultEOF to set
	 */
	public void setResultEOF(boolean resultEOF) {
		this.resultEOF = resultEOF;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SearchStatus [resultEOF=");
		builder.append(resultEOF);
		builder.append(", size=");
		builder.append(size);
		builder.append("]");
		return builder.toString();
	}

	public boolean isMaxSize() {
		return isMaxSize;
	}

	public void setIsMaxSize(boolean isMaxSize) {
		this.isMaxSize = isMaxSize;
	}
}
