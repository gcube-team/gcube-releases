package org.gcube.portlets.user.templates.client.components;

import com.google.gwt.user.client.ui.TextArea;
/**
 * 
 * @author massi
 *
 */
public class GenTableCell extends TextArea {
	
	/**
	 * 
	 */
	private int cellWidth;
	/**
	 * 
	 */
	private int cellHeight;
	/**
	 * 
	 */
	private int rowindex;
	/**
	 * 
	 */
	private int colindex;
	/**
	 * 
	 */
	private int colspan;
	/**
	 * 
	 */
	boolean selected = false;
	/**
	 * 
	 * @param rowindex
	 * @param colindex
	 */
	public GenTableCell() {
		super();
		this.rowindex = 0;
		this.colindex = 0;
		this.colspan = 1;

	}
	/**
	 * 
	 * @param rowindex
	 * @param colindex
	 */
	public GenTableCell(int rowindex, int colindex, int cellWidth, int colspan) {
		super();
		this.rowindex = rowindex;
		this.colindex = colindex;
		this.colspan = colspan;
		this.cellWidth = cellWidth;
	}
	/**
	 * 
	 * @return
	 */
	public int getWidth() {
		return cellWidth;
	}
	/**
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		this.cellWidth = width;
	}
	/**
	 * 
	 * @return
	 */
	public int getHeight() {
		return cellHeight;
	}
	/**
	 * 
	 * @param height
	 */
	public void setHeight(int height) {
		this.cellHeight = height;
	}
	/**
	 * 
	 * @return
	 */
	public int getRowindex() {
		return rowindex;
	}
	/**
	 * 
	 * @param rowindex
	 */
	public void setRowindex(int rowindex) {
		this.rowindex = rowindex;
	}
	/**
	 * 
	 * @return
	 */
	public int getColindex() {
		return colindex;
	}
	/**
	 * 
	 * @param colindex
	 */
	public void setColindex(int colindex) {
		this.colindex = colindex;
	}
	/**
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	/**
	 * 
	 * @return
	 */
	public int getColspan() {
		return colspan;
	}
	/**
	 * 
	 * @param colspan
	 */
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	public int getCellWidth() {
		return cellWidth;
	}
	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}
	public int getCellHeight() {
		return cellHeight;
	}
	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}
	
}


