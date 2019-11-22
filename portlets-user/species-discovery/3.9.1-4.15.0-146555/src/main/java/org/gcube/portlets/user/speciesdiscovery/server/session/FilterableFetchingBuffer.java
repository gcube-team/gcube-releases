/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.session;

import java.sql.SQLException;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.shared.FetchingElement;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.FilterCriteria;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface FilterableFetchingBuffer<T extends FetchingElement> extends FetchingBuffer<T> {
	
	
	public List<T> getFilteredList(FilterCriteria filterCriteria) throws SQLException;
	
	//TODO remove ASAP
	public int getFilteredListSize() throws SQLException;

}
