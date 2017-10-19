package org.gcube.portlets.user.td.tablewidget.client.type;

import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TableTypeStore {
	
	private static TableTypeElement genericElement=new TableTypeElement(1, TableType.GENERIC);
	private static TableTypeElement codelistElement=new TableTypeElement(2, TableType.CODELIST);
	private static TableTypeElement datasetElement=new TableTypeElement(3, TableType.DATASET);
	
	
	protected static ArrayList<TableTypeElement> tableType = new ArrayList<TableTypeElement>() {
		private static final long serialVersionUID = -6559885743626876431L;
		{
			add(genericElement);
			add(codelistElement);
			add(datasetElement);

		}
	};

	public static ArrayList<TableTypeElement> getTableTypes() {
		return tableType;
	}
	
	public static TableTypeElement getTableTypeElement(String tableType){
		if(tableType.compareTo(TableType.GENERIC.toString())==0){
			return genericElement;
		} else {
			if(tableType.compareTo(TableType.CODELIST.toString())==0){
				return codelistElement;
			} else {
				if(tableType.compareTo(TableType.DATASET.toString())==0){
					return datasetElement;
				} else {
					return null;
				}	
			}
		}
	} 
	
}