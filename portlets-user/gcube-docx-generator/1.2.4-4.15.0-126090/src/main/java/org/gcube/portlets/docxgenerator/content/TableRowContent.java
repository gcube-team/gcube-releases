package org.gcube.portlets.docxgenerator.content;



import java.util.ArrayList;

import javax.xml.bind.JAXBElement;

import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTCnf;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;

/**
 * Convenience class for representing a table row in a docx document.
 * 
 * @author Luca Santocono
 * 
 */
/**
 * @author Luca
 *
 */
/**
 * @author Luca
 * 
 */
public class TableRowContent {
	private Tr tablerow;
	private TrPr rowproperties;
	private ArrayList<TableCellContent> cells;
	private ObjectFactory factory;

	private void initDefaultRowProperties() {
		rowproperties = factory.createTrPr();
		BooleanDefaultTrue booleandefaulttrue = factory
				.createBooleanDefaultTrue();
		booleandefaulttrue.setVal(false);
		javax.xml.bind.JAXBElement<BooleanDefaultTrue> cantsplit = factory
				.createCTTrPrBaseCantSplit(booleandefaulttrue);
		rowproperties.getCnfStyleOrDivIdOrGridBefore().add(cantsplit);
		
	}

	/**
	 * Constructor.
	 */
	public TableRowContent() {
		factory = new ObjectFactory();
		cells = new ArrayList<TableCellContent>();
		tablerow = factory.createTr();
		initDefaultRowProperties();
		tablerow.setTrPr(rowproperties);
	}

	/**
	 * Adds an empty cell to the current table row.
	 * @param width TODO
	 */
	public void addCell(int gridspan, int width) {
		TableCellContent tableCell = new TableCellContent(gridspan, width);
		tablerow.getEGContentCellContent().add(tableCell.getCell());
		cells.add(tableCell);
	}

	/**
	 * Gets the cell at the given position.
	 * 
	 * @param position
	 *            The position of the cell of interest.
	 * @return The cell at the given position.
	 */
	public TableCellContent getCell(int position) {
		return cells.get(position);
	}

	/**
	 * Creates a new cell with the specified content and inserts the cell in
	 * this table row.
	 * 
	 * @param content
	 *            The content to be inserted in the new table cell.
	 * @param width TODO
	 */
	public void addCell(final Content content, int gridspan, int width) {
		TableCellContent tableCell = new TableCellContent(gridspan, width);
		tableCell.addContent(content.getContent());
		tablerow.getEGContentCellContent().add(tableCell.getCell());
		cells.add(tableCell);
	}
	
	/**
	 * Creates a new cell with the specified content and inserts the cell in
	 * this table row.
	 * 
	 * @param content
	 *            The content to be inserted in the new table cell.
	 */
	public void addCell(final Content content) {
		TableCellContent tableCell = new TableCellContent();
		tableCell.addContent(content.getContent());
		tablerow.getEGContentCellContent().add(tableCell.getCell());
		cells.add(tableCell);
	}

	/**
	 * Creates a new cell with the specified content and inserts the cell in
	 * this table row.
	 * 
	 * @param content
	 *            The content to be inserted in the new table cell.
	 * @param width TODO
	 */
	public void addCell(final ArrayList<Content> contents) {
		TableCellContent tableCell = new TableCellContent();
		
		for(Content content: contents)
			tableCell.addContent(content.getContent());
		
		tablerow.getEGContentCellContent().add(tableCell.getCell());
		cells.add(tableCell);
	}

	/**
	 * Gets the number of cells in this table row.
	 * 
	 * @return The number of cells contained in this table row.
	 */
	public int getCellCount() {
		return cells.size();
	}

	/**
	 * Getter for the tablerow field.
	 * 
	 * @return The docx4j Tr (TableRow) object
	 */
	public Tr getTablerow() {
		return tablerow;
	}

	/**
	 * Getter for the tablerow field.
	 * 
	 * @param tablerow
	 *            The row to assign.
	 */
	public void setTablerow(final Tr tablerow) {
		this.tablerow = tablerow;
	}

	/**
	 * Adds an empty cell to this table row.
	 * @param width TODO
	 * 
	 */
	public void addEmptyCell(int gridspan, int width) {
		TableCellContent tableCell = new TableCellContent(gridspan, width);
		tableCell.addEmptyContent();
		tablerow.getEGContentCellContent().add(tableCell.getCell());
		cells.add(tableCell);
	}

	/**
	 * Inserts a background color for this table row.
	 * 
	 * @param colorCode
	 *            The code of the color to be inserted.
	 */
	public void insertColor(final String colorCode) {
		CTCnf ctcnf = factory.createCTCnf();
		ctcnf.setVal(colorCode);
		JAXBElement<CTCnf> ctcnfJaxb = factory.createCTTrPrBaseCnfStyle(ctcnf);
		rowproperties.getCnfStyleOrDivIdOrGridBefore().add(ctcnfJaxb);
	}

	/**
	 * Sets this table row as header.
	 */
	public void insertHeader() {
		BooleanDefaultTrue bdt = factory.createBooleanDefaultTrue();
		rowproperties.getCnfStyleOrDivIdOrGridBefore().add(
				factory.createCTTrPrBaseTblHeader(bdt));
	}

}
