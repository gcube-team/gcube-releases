/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 * @param <T>
 */
public class SearchResult<T extends FetchingElement> implements Serializable {

	private static final long serialVersionUID = -8014025568421065573L;
	protected ArrayList<T> resultsRow;
	
	public SearchResult(){}

	/**
	 * @param results
	 */
	public SearchResult(ArrayList<T> results) {
		this.resultsRow = results;
	}
	

	/**
	 * @return the results
	 */
	public ArrayList<T> getResults() {
		return resultsRow;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SearchResult [resultsRow=");
		builder.append(resultsRow);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
