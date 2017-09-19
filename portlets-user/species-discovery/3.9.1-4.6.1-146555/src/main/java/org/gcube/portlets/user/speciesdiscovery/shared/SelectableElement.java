/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface SelectableElement extends FetchingElement {
	
	public void setSelected(boolean selected);
	
	public boolean isSelected();

}
