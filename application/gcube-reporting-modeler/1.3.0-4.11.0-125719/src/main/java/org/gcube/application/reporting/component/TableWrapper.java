package org.gcube.application.reporting.component;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Table;
import org.gcube.portlets.d4sreporting.common.shared.TableCell;

public class TableWrapper extends AbstractComponent {
	private int numberOfColumns;
	private Table table;
	
	public TableWrapper(int numberOfColumns) {
		this.table = new Table(numberOfColumns);
	}
	
	public TableWrapper(Table table) {
		this.table = table;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	public ArrayList<ArrayList<TableCell>> getTable() {
		return table.getTable();
	}

	@Override
	public ReportComponentType getType() {
		return ReportComponentType.TABLE;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public List<ReportComponent> getChildren() {
		return null;
	}

	@Override
	public String getStringValue() {
		return table.toString();
	}

	@Override
	public BasicComponent getModelComponent() {
		BasicComponent bc = new BasicComponent(0, 0, COMP_WIDTH, COMP_HEIGHT, 
				1, ComponentType.FLEX_TABLE, "", table, false, false, convertProperties());
		bc.setId(getId());
		return bc;
	}

}
