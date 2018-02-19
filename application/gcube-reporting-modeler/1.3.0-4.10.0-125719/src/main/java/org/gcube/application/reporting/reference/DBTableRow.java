package org.gcube.application.reporting.reference;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.reporting.component.AbstractComponent;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ReportReferences;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * an abstraction over {@link ReportReferences} 
 */
public class DBTableRow extends AbstractComponent {
	
	private List<Column> columns;
	private int level;
	
	/**
	 * 
	 * @param id the identifier ia a String value that identifies the DBTableRow
	 * @param columns the columns (name, value) to show once selected
	 */
	public DBTableRow(String id, List<Column> columns) {
		super();
		setId(id);
		this.columns = columns;
		this.level = 2;
	}
	
	/**
	 * 
	 * @param metadata the metadata (attribute, value) to show once selected
	 */
	public DBTableRow(List<Column> columns) {
		this(columns.get(0).getValue(), columns);
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public int getHeadingLevel() {
		return level;
	}

	public void setHeadingLevel(int level) {
		this.level = level;
	}

	@Override
	public ReportComponentType getType() {
		return ReportComponentType.TB_ROW;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public List<ReportComponent> getChildren() {
		List<ReportComponent> toReturn = new ArrayList<ReportComponent>();
		toReturn.addAll(columns);
		return toReturn;
	}

	@Override
	public String getStringValue() {
		return null;
	}

	@Override
	public BasicComponent getModelComponent() {
		return null;
	}
}
