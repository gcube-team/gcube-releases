/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class AccountingReport.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 4, 2015
 */
public class AccountingReport implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3712222819936382651L;
	
	private Map<String, Integer> mapReport = new HashMap<String, Integer>();
	
	/**
	 * Instantiates a new accounting report.
	 */
	public AccountingReport(){	
	}
	
	/**
	 * Put.
	 *
	 * @param reference the reference
	 * @param total the total
	 */
	public void put(AccoutingReference reference, Integer total){
		mapReport.put(reference.toString(), total);
	}
	
	/**
	 * Gets the.
	 *
	 * @param reference the reference
	 * @return the integer
	 */
	public Integer get(AccoutingReference reference){
		Integer total = mapReport.get(reference.toString());
		return total!=null?total:0;
	}

	/**
	 * Gets the map report.
	 *
	 * @return the mapReport
	 */
	public Map<String, Integer> getMapReport() {
		return mapReport;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountingReport [mapReport=");
		builder.append(mapReport);
		builder.append("]");
		return builder.toString();
	}
}
