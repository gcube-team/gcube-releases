/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Dec 19, 2013
 * 
 */
public class Test {
	
	/**
	 * An instance of the constants.
	 */
	private FlexTable flexTableTemplate = new FlexTable();
	private int numColumns;
	private HorizontalPanel horizontalPanel;
	private VerticalPanel operationsPanel;
	
	/**
	 * 
	 */
	public Test(int numColumns) {
		this.numColumns = numColumns;
		inizializeTemplate();
		inizializeOperationsPanel();
		this.horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(flexTableTemplate);
		horizontalPanel.add(operationsPanel);
	}


	/**
	 * 
	 */
	private void inizializeOperationsPanel() {
		
		this.operationsPanel = new VerticalPanel();
		this.operationsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.operationsPanel.setWidth("200px");
		this.operationsPanel.setSpacing(5);
		this.operationsPanel.addStyleName("TemplateOperationPanel");
		// Add a button that will add more rows to the table
		Button addColumnButton = new Button("Add Column",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
//						int columIndex = getNumColumns();
						addColumn(flexTableTemplate);
					}
				});
		
//		addColumnButton.addStyleName("sc-FixedWidthButton");

		Button removeRowButton = new Button("Remove Column",
		new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(numColumns>1){
					removeColumn(flexTableTemplate, numColumns-1);
					setNumColumns(numColumns-1);
				}
			}
		});
		
//		removeRowButton.addStyleName("sc-FixedWidthButton");

//		operationsPanel.setStyleName("cw-FlexTable-buttonPanel");
		operationsPanel.add(addColumnButton);
		operationsPanel.add(removeRowButton);
		
	}


	/**
	 * Initialize this example.
	 */

	public Widget inizializeTemplate() {
		// Create a Flex Table

//		FlexCellFormatter cellFormatter = flexTable.getFlexCellFormatter();
//		cellFormatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
		
		flexTableTemplate.addStyleName("FlexTableTemplate");
//		flexTable.setWidth("32em");
		flexTableTemplate.setCellSpacing(5);
		flexTableTemplate.setCellPadding(3);
		
		// Add some text
//		cellFormatter.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
//		flexTable.setHTML(0, 0, constants.cwFlexTableDetails());
//		cellFormatter.setColSpan(0, 0, numColumns);
//		
//		addHeaderColumns(flexTable);
		
		initTableColumns(flexTableTemplate, 0, numColumns);


		
//		flexTable.setWidget(0, numColumns, buttonPanel);


		// Add two rows to start
//		addRow(flexTable);
//		addRow(flexTable);

		// Return the panel
		flexTableTemplate.ensureDebugId("cwFlexTable");
		return flexTableTemplate;
	}

	/**
	 * Add a row to the flex table.
	 */
	private void addRow(FlexTable flexTable) {
		int numRows = flexTable.getRowCount();
//		flexTable.setWidget(numRows, 0, new Image(ResourcesTemplate.INSTANCE.getArrowDown()));
//		flexTable.setWidget(numRows, 1, new Image(ResourcesTemplate.INSTANCE.getArrowDown()));
		flexTable.getFlexCellFormatter().setRowSpan(0, 1, numRows + 1);
	}
	
	
	private void initTableColumns(FlexTable flexTable, int columnIndex, int columnsOffset){
		
		for (int i = 0; i < columnsOffset; i++) {
//			flexTable.setWidget(0, columnIndex+i, new Label("Column "+(columnIndex+i+1)));
//			flexTable.setWidget(1, columnIndex+i, new Image(ResourcesTemplate.INSTANCE.getArrowDown()));
//			flexTable.setWidget(2, columnIndex+i, new Label("Not defined"));
		}
		
	}
	
	
	
	/**
	 * Add a row to the flex table.
	 */
	private void addColumn(FlexTable flexTable) {
		
//		int numRows = flexTable.getRowCount();
//		int numColumns = flexTable.getCellCount(0);

		initTableColumns(flexTable, numColumns, 1);
		
		setNumColumns(numColumns+1);
		
//		flexTable.getFlexCellFormatter().setColSpan(0, 1, numRows + 1);
	}
	

	/**
	 * @param numColumns
	 */
	private void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
		
	}


	private void addHeaderColumns(FlexTable flexTable) {
		int numRows = flexTable.getRowCount();
		
		int numColumns = flexTable.getCellCount(numRows);
		
		for (int i = 0; i < numColumns; i++) {
			flexTable.setWidget(1, i, new Label("Column "+i+1));
		}
	}
	
	/**
	 * Remove a row from the flex table.
	 */
	private void removeColumn(FlexTable flexTable, int columnIndex) {

		if (columnIndex > 0) {
			int numRows = flexTable.getRowCount();
			for (int i=0; i<numRows; i++) {
				flexTable.removeCell(i, columnIndex);
			}
	
//			flexTable.getFlexCellFormatter().setRowSpan(0, 1, columnIndex - 1);
		}
	}

	/**
	 * Remove a row from the flex table.
	 */
	private void removeRow(FlexTable flexTable) {
		int numRows = flexTable.getRowCount();
		if (numRows > 1) {
			flexTable.removeRow(numRows - 1);
			flexTable.getFlexCellFormatter().setRowSpan(0, 1, numRows - 1);
		}
	}


	public int getNumColumns() {
		return numColumns;
	}


	public HorizontalPanel getPanel() {
		return horizontalPanel;
	}
}

