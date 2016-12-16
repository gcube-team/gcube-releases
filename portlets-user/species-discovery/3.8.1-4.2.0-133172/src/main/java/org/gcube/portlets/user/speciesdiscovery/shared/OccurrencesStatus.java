/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class OccurrencesStatus implements Serializable {
	
	private static final long serialVersionUID = -2234701519362803828L;
	
	protected boolean resultEOF = false;
	protected int size  = 0;

	public OccurrencesStatus()
	{
		
	}
	
	/**
	 * @param resultEOF
	 * @param size
	 */
	public OccurrencesStatus(boolean resultEOF, int size) {
		this.resultEOF = resultEOF;
		this.size = size;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OccurrencesStatus [resultEOF=");
		builder.append(resultEOF);
		builder.append(", size=");
		builder.append(size);
		builder.append("]");
		return builder.toString();
	}
}
