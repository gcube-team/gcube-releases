package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * <code> Table </code> class represent a template component that can be serializable
 * the TableModel class used in the model cannot be serializable
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class Table implements Serializable {
	/**
	 * column number of the table
	 */
	private int colsNo = 0;

	private static final long serialVersionUID = -6309655149509624445L;

	ArrayList<ArrayList<TableCell>> table;
	
	private String title;
	private String description;
	private AttributeArea attrArea;

	/**
	 * default constructor
	 */
	public Table() {
		super();
	}


	/**
	 * actual one
	 * @param colsNo the number of columns of the table
	 */
	public Table(int colsNo) {
		this.colsNo = colsNo;
		table = new ArrayList<ArrayList<TableCell>>();
	}

	/**
	 * only for serialization purposes
	 * @param colsNo
	 * @param table
	 * @param title
	 * @param description
	 * @param attrArea
	 */
	public Table(int colsNo, ArrayList<ArrayList<TableCell>> table,
			String title, String description, AttributeArea attrArea) {
		super();
		this.colsNo = colsNo;
		this.table = table;
		this.title = title;
		this.description = description;
		this.attrArea = attrArea;
	}


	/**
	 * 
	 * Gets the ArrayList of Strings row in the specified row.
	 * @param row the table's row
	 */
	public boolean addRow(ArrayList<TableCell> row) {
		if (row == null)
			throw new NullPointerException("Row is null");
		else
			return table.add(row);		
	}

	////******* GETTER n SETTERS


	/**
	 * Gets the value in the specified cell.
	 * 
	 * @param row the cell's row
	 * @param column the cell's column
	 * @return the String in the specified cell, or <code>null</code> if none is
	 *         present
	 * @throws IndexOutOfBoundsException
	 */
	public TableCell getValue(int row, int column) {
	    checkCellBounds(row, column);
		return table.get(row).get(column);
	}

	
	
	/**
	 * Bounds checks that the cell exists at the specified location.
	 * 
	 * @param row cell's row
	 * @param column cell's column
	 * @throws IndexOutOfBoundsException
	 */
	protected void checkCellBounds(int row, int column) {
		checkRowBounds(row);
		if (column < 0) {
			throw new IndexOutOfBoundsException("Column " + column + " must be non-negative: " + column);
		}
		int cellSize = getCellCount(row);
		if (cellSize <= column) {
			throw new IndexOutOfBoundsException("Column index: " + column + ", Column size: " + getCellCount(row));
		}
	}
	
	
	/**
	 * Checks that the row is within the correct bounds.
	 * 
	 * @param row row index to check
	 * @throws IndexOutOfBoundsException
	 */
	protected void checkRowBounds(int row) {
		int rowSize = getRowCount();
		if ((row >= rowSize) || (row < 0)) {
			throw new IndexOutOfBoundsException("Row index: " + row + ", Row size: " + rowSize);
		}
	}

	/**
	 * 
	 * @return .
	 */
	public int getRowCount() {
		return table.size();
	}

	/**
	 * 
	 * @param row .
	 * @return .
	 */
	public int getCellCount(int row) {
	    checkRowBounds(row);
		return table.get(row).size();
	}
	/**
	 * 
	 * @return .
	 */
	public int getColsNo() {
		return colsNo;
	}
	/**
	 * 
	 * @param colsNo .
	 */
	public void setColsNo(int colsNo) {
		this.colsNo = colsNo;
	}
	/**
	 * 
	 * @param i
	 * @return
	 */
	public ArrayList<TableCell> getRow(int i) {
		return  table.get(i);
	}
	/**
	 * 
	 * @return .
	 */
	public ArrayList<ArrayList<TableCell>> getTable() {
		return table;
	}
	/**
	 * 
	 * @param table .
	 */
	public void setTable(ArrayList<ArrayList<TableCell>> table) {
		this.table = table;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public AttributeArea getAttrArea() {
		return attrArea;
	}


	public void setAttrArea(AttributeArea attrArea) {
		this.attrArea = attrArea;
	}
	
	
}
