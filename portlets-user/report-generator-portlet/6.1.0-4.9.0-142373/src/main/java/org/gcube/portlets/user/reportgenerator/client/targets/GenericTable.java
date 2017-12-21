package org.gcube.portlets.user.reportgenerator.client.targets;



import java.util.ArrayList;

import org.gcube.portlets.d4sreporting.common.shared.Table;
import org.gcube.portlets.d4sreporting.common.shared.TableCell;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class GenericTable extends Composite {

	public static final int DEFAULT_HEIGHT = 200;

	public static final int DEFAULT_ROWS_NUM = 5;
	public static final int DEFAULT_COLS_NUM = 2;

	private FocusPanel focusPanel = new FocusPanel();

	private VerticalPanel myPanel = new VerticalPanel();

	private FlexTable myTable;	

	Presenter presenter;

	private int rows;
	private int cols;

	private int width;

	private boolean isEditTableMode = false;

	private AttributeSingleSelection attrSS;

	private Button cellMerger = new Button("Merge Cells");
	private Button colMerger = new Button("Merge Selected");
	private Button cancel = new Button("Cancel");

	private Button removeB = new Button("Remove Table");
	
	private Button resetTabelB = new Button("Reset");

	private Button addRowB = new Button("Add Row");
	private Button addColB = new Button("Add Col");
	private Button deleteRowB = new Button("Del. Row");
	private Button deleteColB = new Button("Del. Col");
	
	

	private TextBox titleTB = new TextBox();
	private TextBox descTB = new TextBox();

	private int cellWidth;
	private int cellSpacing = 1;
	private int cellPadding = 0;

	private GenTableCell selectedCell = null;

	private Table originalTable;

	/**
	 * constructor called when reading the model 
	 * @param sTable
	 */
	public GenericTable(Table sTable, Presenter presenter, int top, int left, int width, int height, boolean isLocked, boolean isRemovable, TextTableImage owner) {
		saveTable(sTable);
		commonConstructorCode(presenter);
		this.rows = sTable.getRowCount();
		this.cols = sTable.getColsNo();
		this.width = width;

		/**
		 * construct the table
		 */
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < sTable.getCellCount(i); j++) {
				TableCell toPut =  sTable.getValue(i, j);
				int cellWidth = toPut.getCellWidth();
				//int cellHeight = toPut.getCellHeight();
				int colspan =  toPut.getColspan();
				GenTableCell toAdd = new GenTableCell(i, j, cellWidth, colspan);
				toAdd.setText(toPut.getContent());
				toAdd.setStyleName("tableBorder");
				toAdd.setWidth(""+cellWidth+"px");				
				myTable.setWidget(i, j, toAdd);
				myTable.getFlexCellFormatter().setColSpan(i, j, colspan);
				toAdd.addClickHandler(cellClicker);
			}
		}

		HorizontalPanel controlPanel = getControlPanel(isRemovable, owner);
		VerticalPanel attributesPanel = getAttributesPanel(sTable);
		attributesPanel.setStyleName("tableAttributesPanel");

		if (! isLocked) {
			controlPanel.setStyleName("tableControlPanel");
			myPanel.add(controlPanel);
		}
		myPanel.add(myTable);		
		myPanel.add(attributesPanel);
		myPanel.setStyleName("tablePanel");


		for (int i = 0; i < controlPanel.getWidgetCount(); i++) {
			if (controlPanel.getWidget(i) instanceof Button) {
				Button b = (Button) controlPanel.getWidget(i);
				b.addStyleName("tableButton");
			}
		}
		if (isRemovable) {
			removeB.removeStyleName("tableButton");
			removeB.addStyleName("deleteEntryButton");
			removeB.getElement().getStyle().setMarginRight(10, Unit.PX);
		}

		focusPanel.add(myPanel);
		initWidget(focusPanel);
	}

	private void saveTable(Table sTable) {
		originalTable = new Table(sTable.getColsNo());
		originalTable.setTable(sTable.getTable());		
	}

	/**
	 * common Constructors Code
	 */
	private void commonConstructorCode(Presenter presenter) {
		this.presenter = presenter;
		myTable= new FlexTable();
		myTable.setWidth("90%");
		myTable.setCellSpacing(cellSpacing);
		myTable.setCellPadding(cellPadding);		

	}
	/**
	 * calculate the cell width
	 * @param cols
	 * @return
	 */
	private int getCellWidth(int cols) {
		int cellWidth = width / cols;
		return cellWidth;
	}

	private VerticalPanel getAttributesPanel(Table sTable) {
		VerticalPanel toReturn = new VerticalPanel();

		HorizontalPanel hp1 = new HorizontalPanel();
		HTML title = new HTML("<div style=\"white-space:nowrap;\">Title: <span style=\"color: red;\">*&nbsp;</span></div>", true);
		hp1.add(title);
		hp1.add(titleTB);
		titleTB.setWidth("135px");
		titleTB.setText(sTable.getTitle());

		HTML desc = new HTML("<div style=\"white-space:nowrap;\">&nbsp;Description: <span style=\"color: red;\">*&nbsp;</span></div>", true);
		//desc.setStyleName("attributeArea");

		hp1.add(desc);
		hp1.add(descTB);
		descTB.setWidth("435px");
		descTB.setText(sTable.getDescription());
		toReturn.add(hp1);

		if (sTable.getAttrArea() != null) {
			attrSS = new AttributeSingleSelection(presenter, 0, 0, 700, 0, sTable.getAttrArea(), false);
		}
		else
			attrSS = new AttributeSingleSelection(presenter, 0, 0, 700, 0, "DisplayType:Inline|Link|Popup", false);
		toReturn.add(attrSS);

		return toReturn;
	}

	
	/**
	 * 
	 * @return
	 */
	private HorizontalPanel getControlPanel(boolean isRemovable, final TextTableImage owner) {
		final HorizontalPanel toReturn = new HorizontalPanel();

		final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {  
			public void handleEvent(MessageBoxEvent ce) {  
				Info.display("Info", "Table Edit Mode Enabled");  
			}  
		};  

		removeB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				if (owner != null) {
					 remove(owner);
				}
			}
		});

		
		resetTabelB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				resetMe();
			}
		});

		addRowB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				addRow();
			}
		});

		deleteRowB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				deleteLastRow();
			}
		});

		addColB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				addColumn();
			}
		});

		deleteColB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				deleteLastColumn();
			}
		});


		cellMerger.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				if (! isEditTableMode) {
					isEditTableMode = true;
					MessageBox.alert("Table Edit Mode Enabled", "Click on the cells you want to merge", null);  
					myTable.addStyleName("highlight_background");
					cellMerger.setEnabled(false);

					//rowMerger.setVisible(true);
					colMerger.setVisible(true);
					cancel.setVisible(true);
				}
			}
		});

		if (isRemovable)
			toReturn.add(removeB);	
		//the control buttons
		toReturn.add(resetTabelB);		
		toReturn.add(addRowB);
		toReturn.add(addColB);
		toReturn.add(deleteRowB);
		toReturn.add(deleteColB);

		toReturn.add(cellMerger);
		toReturn.add(colMerger);
		toReturn.add(cancel);

		colMerger.setVisible(false);
		cancel.setVisible(false);

		toReturn.setSpacing(3);


		cancel.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				endEditMode();	
			}
		});


		colMerger.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				mergeSelectedCols();	
				endEditMode();
			}
		});
		return toReturn;
	}



	/**
	 * 
	 * End the edit mode
	 */
	private void endEditMode() {
		isEditTableMode = false;
		myTable.removeStyleName("highlight_background");
		cellMerger.setEnabled(true);	
		colMerger.setVisible(false);
		cancel.setVisible(false);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				try {
					GenTableCell tc = (GenTableCell) myTable.getWidget(i, j);
					if (tc.isSelected()) {
						tc.selected = false;
						tc.removeStyleName("selectedCell");		
					}
				}catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * merges the contiguos cols per row
	 */
	private void mergeSelectedCols() {
		for_i: for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				try {
					GenTableCell tc = (GenTableCell) myTable.getWidget(i, j);
					if (tc.isSelected()) {
						int nToMerge = checkContiguosColsPerRows(i, j);
						if (nToMerge > 1) {							
							//myTable.removeCells(i, j+1, nToMerge-1);
							int cellsData[] = deleteCells(i, j+1, nToMerge-1);
							int colSpanDeleted = cellsData[0];
							int deletedCellWidth = cellsData[1];
							tc.setColspan(tc.getColspan()+colSpanDeleted);
							GWT.log("i=" + i + " j=" +j + " nTomerge" + nToMerge +  " COLSPAN: " + tc.getColspan());
							myTable.getFlexCellFormatter().setColSpan(i, j, tc.getColspan());
							int newWidth = (tc.getWidth() + deletedCellWidth  + cellSpacing * tc.getColspan() + tc.getColspan());
							tc.setWidth(newWidth+"px");
							tc.setCellWidth(newWidth);
							tc.removeStyleName("selectedCell");		
							continue for_i;	
						}									
					}		
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 *
	 * @param row the row of the cells to be removed
	 * @column the column of the first cell to be removed
	 * @num the number of cells to be removed
	 * @return the total colspan of the cells in [0] is deleting, the total width of cells deleted in [1]
	 */
	private int[] deleteCells(int row, int col, int num) {
		int[] toReturn = new int[2];
		toReturn[0] = 0;
		toReturn[1] = 0;
		for (int j = col; j < (col+num); j++) {
			toReturn[0] += ((GenTableCell) myTable.getWidget(row, j)).getColspan();
			toReturn[1] += ((GenTableCell) myTable.getWidget(row, j)).getCellWidth();
		}
		myTable.removeCells(row, col, num);
		GWT.log("REMOVED CELL from" + col + " to " + (col+num));
		return toReturn;
	}
	/**
	 * 
	 * @return
	 */
	private int checkContiguosColsPerRows(int i, int j) {
		int counter = 1;
		GenTableCell next = (GenTableCell) myTable.getWidget(i, j);
		j++;
		while (j < myTable.getCellCount(i)) {
			next = (GenTableCell) myTable.getWidget(i, j);
			if (next.isSelected())
				counter++;
			else
				break;
			j++;
		}
		return counter;
	}


	public FlexTable getMyTable() {
		return myTable;
	}

	public void setMyTable(FlexTable myTable) {
		this.myTable = myTable;
	}

	public int getRowsNo() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	/**
	 * 
	 * @return
	 */
	public Table getSerializable() {
		Table toReturn = new Table(this.cols);
		for (int i = 0; i < myTable.getRowCount(); i++) {
			toReturn.addRow(getRow(i));
		}
		toReturn.setTitle(titleTB.getText());
		toReturn.setDescription(descTB.getText());

		toReturn.setAttrArea(attrSS.getSerializable());
		return toReturn;
	}

	public ArrayList<TableCell> getRow(int i) {
		ArrayList<TableCell> toReturn = new ArrayList<TableCell>();
		for (int j = 0; j < myTable.getCellCount(i); j++) {
			GenTableCell tb = (GenTableCell) myTable.getWidget(i, j);
			int colspan = tb.getColspan();

			toReturn.add(new TableCell(tb.getText(), colspan, tb.getWidth(), tb.getHeight()));
		}
		return toReturn;
	}

	/**
	 * 
	 */
	ClickHandler cellClicker = new ClickHandler() {		
		@Override
		public void onClick(ClickEvent event) {
			GenTableCell toSelect = (GenTableCell) event.getSource();
			setSelected(toSelect);
			if (isEditTableMode) {

				if (! toSelect.isSelected()) {
					toSelect.addStyleName("selectedCell");		
					toSelect.setSelected(true);
				}
				else {
					toSelect.setSelected(false);
					toSelect.removeStyleName("selectedCell");	
				}
			}			
		}
	};

	/**
	 * tells the presenter this tableb is selected
	 */
	private void setSelected(GenTableCell tc) {
		selectedCell = tc;
	}

	public GenTableCell getSelectedCell() {
		return selectedCell;
	}
	/**
	 * delete the selected row from the table
	 */
	public void deleteLastRow() {
		deleteRow(rows-1);
		rows--;
	}

	private void deleteLastColumn() {
		deleteColumn(cols-1);
	}
	/**
	 * adda a row at the bottom of the tableb
	 */
	public void addRow() {

		Table sTable =  getSerializable();
		//Window.alert("Adding row Above: " + rowIndex + " cells no: " + sTable.getCellCount(rowIndex));
		int rowIndex = myTable.getRowCount();
		for (int j = 0; j < myTable.getCellCount(rowIndex-1); j++) {
			TableCell toPut =  sTable.getValue(rowIndex-1, j);
			int cellWidth = toPut.getCellWidth();
			int cellHeight = toPut.getCellHeight();
			int colspan =  toPut.getColspan();
			GenTableCell toAdd = new GenTableCell(rowIndex, j, cellWidth, colspan);
			toAdd.setText(toPut.getContent());
			toAdd.setStyleName("tableBorder");
			toAdd.setWidth(""+cellWidth+"px");				
			myTable.setWidget(rowIndex, j, toAdd);
			myTable.getFlexCellFormatter().setColSpan(rowIndex, j, colspan);
			toAdd.addClickHandler(cellClicker);
		}
		rows += 1;
		Info.display("Info", "Table Row added successfully");  
	}
	/**
	 * add a column next to the last onw
	 */
	public void addColumn() {
		int colIndex = cols;
		int cellWidth = getCellWidth(cols+1);

		for (int i = 0; i < rows; i++) {
			GenTableCell toAdd = new GenTableCell(i, colIndex, cellWidth, 1);
			toAdd.setStyleName("tableBorder");
			GWT.log("Width=" + cellWidth);
			myTable.setWidget(i, colIndex, toAdd);
			toAdd.addClickHandler(cellClicker);
		}
		cols++;
		updateCellsWidth(getCellWidth(cols));
		Info.display("Info", "Table Column added successfully");  
	}



	private void updateCellsWidth(int newWidth) {
		Table sTable =  getSerializable();
		/**
		 * construct the table
		 */
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < sTable.getCellCount(i); j++) {
				TableCell toPut =  sTable.getValue(i, j);
				int cellWidth = newWidth;
				//int cellHeight = toPut.getCellHeight();
				int colspan =  toPut.getColspan();
				GenTableCell toAdd = new GenTableCell(i, j, cellWidth, colspan);
				toAdd.setText(toPut.getContent());
				toAdd.setStyleName("tableBorder");
				toAdd.setWidth(""+cellWidth+"px");				
				myTable.setWidget(i, j, toAdd);
				myTable.getFlexCellFormatter().setColSpan(i, j, colspan);
				toAdd.addClickHandler(cellClicker);
			}
		}

	}
	/**
	 * adda a column at the left of the selected cell of the selected tableb
	 * TODO: next version
	 */
	public void addColumnLeft(int colindex) {
		//	myTable.insertCell(beforeRow, beforeColumn)
	}
	/**
	 * delete the rowIndex row from the table
	 */
	public void deleteRow(int rowIndex) {
		GWT.log("Removing row: " + rowIndex);
		if (rows == 1) {
			Info.display("Error", "Only one row left, delete the table");  
		} else {
			myTable.removeRow(rowIndex);
			Info.display("Info", "Table Row removed successfully");  
		}
	}

	/**
	 * delete the colIndex column from the table
	 */
	public void deleteColumn(int colIndex) {
		GWT.log("Removing column: " + colIndex);
		if (cols <= 1) {
			Info.display("Error", "Only one column left, delete the table");  
		} else {
			//check if the column has all rows
			for (int i = 0; i < rows; i++) {
				try {
					myTable.getWidget(i, colIndex);
				}
				catch (IndexOutOfBoundsException e) {
					Info.display("Error", "Table Column cannot be removed as at least one row has merged cells");
					return;
				}				
			}
			// at this point you are sure there are no merged cells in the column to be deleted (last one)
			for (int i = 0; i < rows; i++) {
				myTable.removeCell(i, colIndex);
			}
			cols--;
			updateCellsWidth(getCellWidth(cols));
			//Info.display("Info", "Table Column removed successfully");  
		}
	}


	protected void resetMe() {
		titleTB.setText("");
		descTB.setText("");		
		myTable.removeAllRows();	
		/**
		 * construct the table
		 */
		for (int i = 0; i < originalTable.getRowCount(); i++) {
			for (int j = 0; j < originalTable.getColsNo(); j++) {
				int cellWidth = getCellWidth(originalTable.getColsNo());
				GenTableCell toAdd = new GenTableCell(i, j, cellWidth, 1);
				toAdd.setText("");
				toAdd.setStyleName("tableBorder");
				toAdd.setWidth(""+cellWidth+"px");				
				myTable.setWidget(i, j, toAdd);		
				toAdd.addClickHandler(cellClicker);
			}
		}
		cols = originalTable.getColsNo();
		rows = originalTable.getRowCount();
	}
	/**
	 * heleper method that removes a table from the texxTableImage widget
	 * @param owner
	 */
	private void remove(TextTableImage owner) {
		owner.removeFromParent(this);
		removeFromParent();	
	}
}
