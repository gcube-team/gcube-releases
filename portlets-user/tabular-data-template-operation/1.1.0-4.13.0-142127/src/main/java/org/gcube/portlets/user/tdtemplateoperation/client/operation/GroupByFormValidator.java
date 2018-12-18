/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.client.operation;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;



/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 *
 */
public class GroupByFormValidator {

	protected List<TdColumnData> listSelectedColumns = new ArrayList<TdColumnData>();
	protected TdColumnData columnDataAggreagateSelected = null;
	

	/**
	 * 
	 */
	public  GroupByFormValidator() {
		listSelectedColumns = new ArrayList<TdColumnData>();
	}
	

	
	public void selectColumnsData(List<TdColumnData> value){
		if(value!=null){
			listSelectedColumns.clear();
			listSelectedColumns.addAll(value);
		}
	}
	
	public void selectAggregateData(TdColumnData aggregateSelected){
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
		
		for (TdColumnData column : listSelectedColumns) {
			
			if(column.getId().compareTo(columnDataAggreagateSelected.getId())==0){
				if(column.getLabel().compareTo(columnDataAggreagateSelected.getLabel())==0){
					if(column.getId().compareTo(columnDataAggreagateSelected.getId())==0){
						return true; 
					}
				}
			}
			
		}
		
		return false;
	}
	
}
