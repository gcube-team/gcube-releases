/**
 * 
 */
package org.gcube.dataaccess.spql.model.where;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface ParsableValue<T> {
	
	public T getValue();
	
	public void parse() throws Exception;
	
	public String getTextValue();

}
