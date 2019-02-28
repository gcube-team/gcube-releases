package org.gcube.portal.custom.communitymanager.components;
/**
 * 
 * @author Massimiliano Assante, massimiliano.assante@isti.cnr.it
 * @version 0.1
 *
 */
public class GCUBEPortlet {

	/**
	 * 
	 */
	private String portletId;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 * @param name .
	 * @param portletId .
	 */
	public GCUBEPortlet(String name, String portletId) {
		super();
		this.name = name;
		this.portletId = portletId;
	}
	/**
	 * 
	 * @return -
	 */ 
	public String getPortletId() {
		return portletId;
	}
	/**
	 * 
	 * @param portletId -
	 */
	public void setPortletId(String portletId) {
		this.portletId = portletId;
	}
	/**
	 * 
	 * @return -
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param name -
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
