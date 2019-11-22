/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.shared.csv;

import java.io.Serializable;
import java.util.List;


/**
 * The Class CSVRow.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 29, 2019
 */
public class CSVRow implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 6254861811998867626L;

	private List<String> listValues;

	/**
	 * Instantiates a new CSV row.
	 */
	public CSVRow(){

	}


	/**
	 * Gets the list values.
	 *
	 * @return the listValues
	 */
	public List<String> getListValues() {

		return listValues;
	}



	/**
	 * @param listValues the listValues to set
	 */
	public void setListValues(List<String> listValues) {

		this.listValues = listValues;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("CSVRow [listValues=");
		builder.append(listValues);
		builder.append("]");
		return builder.toString();
	}

}
