/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 19, 2014
 *
 */
public class TdBaseComboDataBean extends TdBaseData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1241120985282608453L;


	/**
	 * 
	 */
	public TdBaseComboDataBean() {
	}
	
	public TdBaseComboDataBean(String id, String label) {
		super(id,label);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdBaseComboDataBean [toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
}
