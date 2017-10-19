/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.validator;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 1, 2014
 *
 */
public class ColumnOccurrenceComparator{

	
	private Integer minOccurrence;
	private Integer maxOccurrence;
	/**
	 * 
	 * @param minOccurrence mandatory
	 * @param maxOccurrence if null maxOccurrence is seen as "undefined occurrences > 1"
	 * @throws Exception 
	 */
	public ColumnOccurrenceComparator(Integer minOccurrence, Integer maxOccurrence) throws Exception {
		this.minOccurrence = minOccurrence;
		this.maxOccurrence = maxOccurrence;
		
		if(minOccurrence==null)
			throw new Exception("MinOccurrence must not be null");
	}
	
	public Integer getMinOccurrence() {
		return minOccurrence;
	}


	public Integer getMaxOccurrence() {
		return maxOccurrence;
	}


	public void setMinOccurrence(Integer minOccurrence) {
		this.minOccurrence = minOccurrence;
	}


	public void setMaxOccurrence(Integer maxOccurrence) {
		this.maxOccurrence = maxOccurrence;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnOccurrenceComparator [minOccurrence=");
		builder.append(minOccurrence);
		builder.append(", maxOccurrence=");
		builder.append(maxOccurrence);
		builder.append("]");
		return builder.toString();
	}
	
}
