/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 15, 2015
 */
public class ZIndexReference {
	
	private TdTemplateController controller;
	
	/**
	 * 
	 */
	public ZIndexReference(TdTemplateController controller) {
		this.controller = controller;
	}
	
	public int getZIndex(){
		return this.controller.getWindowZIndex();
	}

}
