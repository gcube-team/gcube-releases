/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util.stream;

import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface DataSource {
	
	public void getStreamState(AsyncCallback<StreamState> callback);
	
	public void getData(int start, int limit, ResultFilter activeFiltersObject, final AsyncCallback<List<ModelData>> callback);
	
	public String getInfo();

}
