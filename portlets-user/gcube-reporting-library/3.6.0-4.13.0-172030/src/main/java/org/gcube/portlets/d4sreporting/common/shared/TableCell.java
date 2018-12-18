package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
/**
 * 
 * @author massi
 *
 */
public class TableCell implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4551386957527643655L;
	/**
	 * 
	 */
	private String content;
	/**
	 * 
	 */
	private int colspan;	
	private int cellWidth;
	private int cellHeight;
	
	/**
	 * 
	 */
	public TableCell() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param content
	 * @param colspan
	 * @param width
	 * @param height
	 */
	public TableCell(String content, int colspan, int width, int height) {
		super();
		this.content = content;
		this.colspan = colspan;
		this.cellWidth = width;
		this.cellHeight = height;
	}
	/**
	 * 
	 * @param content
	 */
	public TableCell(String content) {
		super();
		this.content = content;
		this.colspan = 1;
		this.cellWidth = 50;
		this.cellHeight = 25;
	}

	/**
	 * 
	 * @return .
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 
	 * @param content .
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 
	 * @return .
	 */
	public int getColspan() {
		return colspan;
	}
	/**
	 * 
	 * @param colspan .
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
