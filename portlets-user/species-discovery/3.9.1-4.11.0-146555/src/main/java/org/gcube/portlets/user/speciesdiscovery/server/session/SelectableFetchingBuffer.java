/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.session;

import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.shared.SelectableElement;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface SelectableFetchingBuffer<T extends SelectableElement> extends FetchingBuffer<T> {
	
	public List<T> getSelected() throws Exception;
	
	public void updateSelection(int rowId, boolean selection) throws Exception;
	
	public void updateAllSelection(boolean selection) throws Exception;
	
	public void updateAllSelectionByIds(boolean selection, List<String> listIds) throws Exception;
	
	public int sizeSelected() throws Exception;

}
