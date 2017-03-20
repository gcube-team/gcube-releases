package org.gcube.portlets.docxgenerator.content;



import java.math.BigInteger;
import java.util.ArrayList;

import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.CTTblPrBase.TblStyle;


/**
 * @author Luca Santocono
 * 
 *         This class provides an easy way to insert a Table in a docx document.
 * 
 */
public class TableContent extends AbstractContent {

	private Tbl tbl;
	private ObjectFactory factory;
	private TblPr tblpr;
	private ArrayList<TableRowContent> tablerows;
	
	 /** Constructor for the TableContent class.
	 * 
	 */
	public TableContent() {
		factory = new ObjectFactory();
		tablerows = new ArrayList<TableRowContent>();
		tbl = factory.createTbl();
		tblpr = factory.createTblPr();
	}

	/**
	 * Constructor for the TableContent class.
	 * 
	 */
	public TableContent(int width) {
		factory = new ObjectFactory();
		tablerows = new ArrayList<TableRowContent>();
		tbl = factory.createTbl();
		tblpr = factory.createTblPr();
		
		TblWidth cellWidth = factory.createTblWidth();
		cellWidth.setW(BigInteger.valueOf(width));
		cellWidth.setType("dxa");
		tblpr.setTblW(cellWidth);
		tbl.setTblPr(tblpr);
		
	    
		
		// P p = factory.createP();
		// cell.getEGBlockLevelElts().add(p);
		
	}
	

	// public Table(final TableRow tablerow) {
	// tablerows = new ArrayList<TableRow>();
	// table = new Tbl();
	// tablerows.add(tablerow);
	// }

	/**
	 * Adds a cell and content in a single row table.
	 * @param width TODO
	 * @param content
	 *            The content to be inserted
	 * @param width TODO
	 * 
	 */
	public void insertContent(final Content content, int gridspan, int width) {
		tablerows.get(0).addCell(content, gridspan, width);
	}

	/**
	 * Adds two cells with content in a single row table. Useful to create
	 * double columns layouts.
	 * 
	 * @param content1
	 *            The content to be inserted on the left.
	 * @param content2
	 *            The content to be inserted on the right.
	 * @param width TODO
	 */
	public void insertContent(final Content content1, final Content content2, int gridspan, int width) {
		tablerows.get(0).addCell(content1, gridspan, width);
		tablerows.get(0).addCell(content2, gridspan, width);
	}
	
	/**
	 * Adds two cells with content in a single row table. Useful to create
	 * double columns layouts.
	 * 
	 * @param content1
	 *            The content to be inserted on the left.
	 * @param content2
	 *            The content to be inserted on the right.
	 * @param width TODO
	 */
	public void insertContent(final ArrayList<Content> contents1, final ArrayList<Content> contents2) {
		tablerows.get(0).addCell(contents1);
		tablerows.get(0).addCell(contents2);
	}

	/**
	 * It is assumed that the table has a single row and multiple cells.
	 * 
	 * @param content
	 *            The content to be inserted.
	 * @param cellPosition
	 *            The cell in which the content should be inserted.
	 */
	public void insertContent(final Content content, int cellPosition, int gridspan, int width) {
		TableRowContent tablerow = tablerows.get(0);
		tablerow.addEmptyCell(gridspan, width);
		if (cellPosition >= tablerow.getCellCount()) {
			for (int i = 0; i <= cellPosition - tablerow.getCellCount(); i++) {
				tablerow.addEmptyCell(gridspan, width);
			}
		}
		tablerow.getCell(cellPosition).addContent(content);
	}

	/**
	 * Adds a row without cells to the table.
	 * */
	public void addRow() {
		TableRowContent tblrow = new TableRowContent();
		tbl.getEGContentRowContent().add(tblrow.getTablerow());
		tablerows.add(tblrow);
	}

	/**
	 * Returns the number of rows in the table.
	 * 
	 * @return The number of rows.
	 */
	public int getRowCount() {
		return tablerows.size();
	}

	/**
	 * @see it.cnr.isti.docxgenerator.content.AbstractContent#getContent()
	 * @return The docx tbl element.
	 */
	@Override
	public Object getContent() {
		return tbl;
	}


	/**
	 * Sets the style of the table.
	 * 
	 * @param style
	 *            The name of the style to give to the table.
	 */
	public void setTableStyle(final String style) {
		TblStyle tblStyle = factory.createCTTblPrBaseTblStyle();
		tblStyle.setVal(style);
		tblpr.setTblStyle(tblStyle);
		tbl.setTblPr(tblpr);
	}

	/**
	 * Returns the table row at the given position.
	 * 
	 * @param position
	 *            The row number
	 * @return The row at the given position.
	 */
	public TableRowContent getRow(final int position) {
		return tablerows.get(position);
	}

	/**
	 * Sets the selected row as table header.
	 * 
	 * @param position
	 *            The row number which should be set as header.
	 */
	public void insertHeader(final int position) {
		tablerows.get(position).insertHeader();
	}

}
