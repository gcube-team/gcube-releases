/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client.csvgrid;

import org.gcube.portlets.user.td.csvimportwidget.client.data.CSVData;
import org.gcube.portlets.user.td.csvimportwidget.client.data.CSVRow;

import com.sencha.gxt.data.shared.loader.DataReader;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class CSVJsonReader implements DataReader<ListLoadResult<CSVRow>, String> {

	/**
	 * {@inheritDoc}
	 */
	public ListLoadResult<CSVRow> read(Object loadConfig, String json) {
		CSVData data = CSVData.getCSVData(json);
		return new ListLoadResultBean<CSVRow>(data.getRows());
	}
}
