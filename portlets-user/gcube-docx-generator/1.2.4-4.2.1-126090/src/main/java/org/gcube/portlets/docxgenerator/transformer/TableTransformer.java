package org.gcube.portlets.docxgenerator.transformer;



import java.util.ArrayList;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.gcube.portlets.d4sreporting.common.shared.Attribute;
import org.gcube.portlets.d4sreporting.common.shared.AttributeArea;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.Table;
import org.gcube.portlets.d4sreporting.common.shared.TableCell;
import org.gcube.portlets.docxgenerator.content.Content;
import org.gcube.portlets.docxgenerator.content.PContent;
import org.gcube.portlets.docxgenerator.content.RContent;
import org.gcube.portlets.docxgenerator.content.TableContent;


/**
 * Transforms a table source InputComponent into a Content object.
 * 
 * @author Luca Santocono
 *
 */
public class TableTransformer implements Transformer {

	private static final Log log = LogFactory.getLog(TableTransformer.class);

	/**
	 * @see it.cnr.isti.docxgenerator.transformer.Transformer#transform(Component,
	 *      org.docx4j.openpackaging.packages.WordprocessingMLPackage)
	 * 
	 * @param component
	 *            Source InputComponent that is going to be transformed.
	 * @param wmlPack
	 *            WordprocessingMLPackage object, which represents a docx
	 *            archive. Passed to insert intermediate data if needed during
	 *            the transformation.
	 * @return A Content object that can be inserted in the docx archive.
	 * 
	 */
	@Override
	public ArrayList<Content> transform(final BasicComponent component,
			final WordprocessingMLPackage wmlPack) {
		
	
		Table t = (Table)component.getPossibleContent();
		ArrayList<ArrayList<TableCell>> cells = t.getTable();
		
		int writableWidthTwips = wmlPack.getDocumentModel().getSections().get(0).getPageDimensions().getWritableWidthTwips();
		
		int cellWidthTwips = new Double(Math.floor(writableWidthTwips)).intValue();
		
		TableContent tableContent = new TableContent(cellWidthTwips);
		tableContent.setTableStyle("TableGrid");
		int i = 0;
		for(ArrayList<TableCell> row: cells) {
			tableContent.addRow();
			for (TableCell cell: row) {
				log.debug("Size cell : " + cell.getCellWidth() + "and colSpan :" + cell.getColspan());
				PContent pcontent = new PContent();
				RContent rcontent = new RContent();
				rcontent.addText(cell.getContent());
				pcontent.addRun(rcontent);
				
				tableContent.getRow(i).addCell(pcontent, cell.getColspan(), cell.getCellWidth());
			}
			i++;
		}
		
		PContent p = new PContent();
		ArrayList<Content> list = new ArrayList<Content>();
		list.add(tableContent);
		list.add(p);
		
		
		PContent metadata = new PContent();
		metadata.setStyle("Attribute");
		
		String title = t.getTitle();
		String description = t.getDescription();
		
		RContent r = new RContent();
		
		r.addText("Title: " + title + "  Description: " + description);
		metadata.addRun(r); 
		list.add(metadata);
		
		PContent attributes = new PContent();
		attributes.setStyle("Attribute");
		String areaAttributes = t.getAttrArea().getAttrName();
		RContent rAttribute = new RContent();
		rAttribute.addText(areaAttributes + ":");
		
		String values = " ";
		ArrayList<Attribute> listAttributes = t.getAttrArea().getValues();
		for(Attribute attr : listAttributes ) {
			if(attr.getValue()) 
				values += attr.getName() + " | ";
		}
		values = (values.lastIndexOf("|") > 1) ? values.substring(0,values.length() - 3) : values;
		rAttribute.addText(values);
		attributes.addRun(rAttribute);
		list.add(attributes);
		
		return list;
		
		//wmlPack.getMainDocumentPart().addObject(tableContent.getContent());
		
	}

}
