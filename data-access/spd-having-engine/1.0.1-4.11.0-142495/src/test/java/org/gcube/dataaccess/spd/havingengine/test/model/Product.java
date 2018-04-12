/**
 * 
 */
package org.gcube.dataaccess.spd.havingengine.test.model;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class Product {
	public enum Type {TAXON, OCCURRENCE}
	
	protected Type type;
	protected int counter;
	
	/**
	 * @param type
	 * @param counter
	 */
	public Product(Type type, int counter) {
		this.type = type;
		this.counter = counter;
	}
	
	

}
