/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.model.grid;

import java.util.List;

import org.gcube.portlets.user.tdwx.client.model.json.JSonTable;
import org.gcube.portlets.user.tdwx.client.model.json.JSonValue;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;

import com.google.gwt.core.client.JsArray;
import com.sencha.gxt.data.shared.loader.DataReader;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class DataRowPagingReader implements DataReader<PagingLoadResult<DataRow>, String> {
	
	protected TableDefinition definition;
	protected JSonValueConverter converter;
	

	public DataRowPagingReader(TableDefinition definition) {
		setDefinition(definition);
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(TableDefinition definition) {
		this.definition = definition;
		converter = new JSonValueConverter(definition.getColumnsAsList());
	}

	
	public PagingLoadResult<DataRow> read(Object loadConfig, String data) {
		JSonTable jSonData = JSonTable.getJSonTable(data);
		JsArray<JSonValue> array = jSonData.getRows(definition.getJsonRowsField());
		List<DataRow> rows = converter.convertToDataRow(array);
		
		int totalLength = jSonData.getTotalLength(definition.getJsonTotalLengthField());
		int offset = jSonData.getOffset(definition.getJsonOffsetField());
		
		PagingLoadResultBean<DataRow> result = new PagingLoadResultBean<DataRow>(rows, totalLength, offset);
		
		System.out.println("DataRowPagingReader Offset: "+result.getOffset());
		System.out.println("DataRowPagingReader TotalLength: "+result.getTotalLength());
		
		return result;
	}


}
