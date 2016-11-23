package org.gcube.portlets.docxgenerator.content;

import java.math.BigInteger;

import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;

/**
 * 
 * Convenience class for representing a table cell in a docx document.
 * 
 * @author Luca Santocono
 * 
 */
public class TableCellContent {

	private Tc cell;
	private ObjectFactory factory;

	/**
	 * Constructor.
	 * @param width TODO
	 * 
	 */
	public TableCellContent() {
		factory = new ObjectFactory();
		cell = factory.createTc();
	}
	
	public TableCellContent(int gridspan, int width) {
		factory = new ObjectFactory();
		cell = factory.createTc();
		
		
	
	    org.docx4j.wml.TcPr tcpr = factory.createTcPr();
	    TblWidth cellWidth = factory.createTblWidth();
		
		cellWidth.setType("dxa");
		cellWidth.setW(BigInteger.valueOf(width * 15));
		tcpr.setTcW(cellWidth);
	    
	    org.docx4j.wml.TcPrInner.GridSpan gspan = factory.createTcPrInnerGridSpan();
	    gspan.setVal(new BigInteger("" + gridspan));
	    tcpr.setGridSpan(gspan);
	    
		cell.setTcPr(tcpr);
	}
	
	/**
	 * Adds a Content to this table cell.
	 * 
	 * @param content
	 *            The content to be added.
	 */
	public void addContent(final Object content) {
		cell.getEGBlockLevelElts().add(content);
	}

	/**
	 * Getter for the cell field.
	 * 
	 * @return The docx4j Tc (TableCell) object.
	 */
	public Tc getCell() {
		return cell;
	}

	/**
	 * Setter for the cell field.
	 * 
	 * @param cell
	 *            The cell to assign.
	 */
	public void setCell(final Tc cell) {
		this.cell = cell;
	}

	/**
	 * Adds an empty content to this table cell content.
	 * 
	 */
	public void addEmptyContent() {
		P p = factory.createP();
		cell.getEGBlockLevelElts().add(p);
	}

}
