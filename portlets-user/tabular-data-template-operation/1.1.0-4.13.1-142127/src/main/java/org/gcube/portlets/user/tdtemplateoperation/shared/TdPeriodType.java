/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared;



/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 27, 2014
 *
 */
public class TdPeriodType extends TdBaseComboDataBean {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4457976060545408794L;



	public TdPeriodType(){}
	/**
	 * @param id
	 * @param name
	 */
	public TdPeriodType(String id, String name) {
		super(id, name);
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdPeriodType [toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
}
