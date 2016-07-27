/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class OccurrenceBatch implements Serializable {

	private static final long serialVersionUID = -8014025568421065573L;
	
	protected ArrayList<Occurrence> occurrences;
	protected boolean resultEOF = false;
	
	public OccurrenceBatch(){}
	
	/**
	 * @param occurences
	 * @param resultEOF
	 */
	public OccurrenceBatch(ArrayList<Occurrence> occurrences) {
		this.occurrences = occurrences;
	}
	/**
	 * @return the occurences
	 */
	public ArrayList<Occurrence> getOccurrences() {
		return occurrences;
	}
	/**
	 * @param occurrences the occurences to set
	 */
	public void setOccurrences(ArrayList<Occurrence> occurrences) {
		this.occurrences = occurrences;
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
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OccurrenceBatch [occurences=");
		builder.append(occurrences);
		builder.append(", resultEOF=");
		builder.append(resultEOF);
		builder.append("]");
		return builder.toString();
	}
}
