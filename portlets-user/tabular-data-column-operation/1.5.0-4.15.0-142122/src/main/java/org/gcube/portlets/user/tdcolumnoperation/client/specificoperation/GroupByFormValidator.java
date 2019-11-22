/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.client.specificoperation;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 *
 */
public class GroupByFormValidator {

	protected List<ColumnData> listSelectedColumns = new ArrayList<ColumnData>();
	protected ColumnData columnDataAggreagateSelected = null;
	

	/**
	 * 
	 */
	public  GroupByFormValidator() {
		listSelectedColumns = new ArrayList<ColumnData>();
	}
	

	
	public void selectColumnsData(List<ColumnData> value){
		if(value!=null){
			listSelectedColumns.clear();
			listSelectedColumns.addAll(value);
		}
	}
	
	public void selectAggregateData(ColumnData aggregateSelected){
		this.columnDataAggreagateSelected = aggregateSelected;
	}
	
	/**
	 * 
	 * @return TRUE if and only if listSelectedColumns contains columnDataAggreagateSelected (comparing on ID, LABEL and COLUMNID)
	 */
	public boolean containsDataAggregate(){
		
		if(listSelectedColumns == null || listSelectedColumns.size()==0)
			return false;
		
		if(columnDataAggreagateSelected==null)
			return false;
		
		for (ColumnData column : listSelectedColumns) {
			
			if(column.getId().compareTo(columnDataAggreagateSelected.getId())==0){
				if(column.getLabel().compareTo(columnDataAggreagateSelected.getLabel())==0){
					if(column.getColumnId().compareTo(columnDataAggreagateSelected.getColumnId())==0){
						return true; 
					}
				}
			}
			
		}
		
		return false;
	}
	
}
