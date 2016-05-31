/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.csv;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.destination.Destination;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class CSVExportSession implements Serializable {

	private static final long serialVersionUID = 407790340509190329L;

	private String id;
	private Destination destination;
	private String itemId;
	private String fileName;
	private String fileDescription;
	private ArrayList<ColumnData> columns;
	private String encoding;
	private String separator;
	private boolean exportViewColumns;
	private TabResource tabResource;

	public CSVExportSession() {
		super();
	}
		
	

	public ArrayList<ColumnData> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<ColumnData> columns) {
		this.columns = columns;
	}

	public ArrayList<String> getColumnsAsString() {
		ArrayList<String> columnsAsString = new ArrayList<String>();
		for (ColumnData cData : columns) {
			columnsAsString.add(cData.getColumnId());
		}
		return columnsAsString;
	}

	public String[] getColumnsAsArrayOfString() {
		ArrayList<String> columnsAsString = new ArrayList<String>();
		for (ColumnData cData : columns) {
			columnsAsString.add(cData.getColumnId());
		}
		return columnsAsString.toArray(new String[columnsAsString.size()]);
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

	public boolean isExportViewColumns() {
		return exportViewColumns;
	}

	public void setExportViewColumns(boolean exportViewColumns) {
		this.exportViewColumns = exportViewColumns;
	}
	
	public TabResource getTabResource() {
		return tabResource;
	}

	public void setTabResource(TabResource tabResource) {
		this.tabResource = tabResource;
	}



	@Override
	public String toString() {
		return "CSVExportSession [id=" + id + ", destination=" + destination
				+ ", itemId=" + itemId + ", fileName=" + fileName
				+ ", fileDescription=" + fileDescription + ", columns="
				+ columns + ", encoding=" + encoding + ", separator="
				+ separator + ", exportViewColumns=" + exportViewColumns
				+ ", tabResource=" + tabResource + "]";
	}
	
	

}
