package org.gcube.portlets.docxgenerator.transformer;

import java.util.ArrayList;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.docxgenerator.content.Content;
import org.gcube.portlets.docxgenerator.content.PContent;
import org.gcube.portlets.docxgenerator.content.RContent;

/**
 * Transforms a page break source InputComponent into a Content object.
 * 
 * @author Luca Santocono
 *
 */
public class PageBreakTransformer implements Transformer {

	/**
	 * @see it.cnr.isti.docxgenerator.transformer.Transformer#transform(it.cnr.isti.docxgenerator.InputComponent,
	 *      org.docx4j.openpackaging.packages.WordprocessingMLPackage)
	 * 
	 * @param ic
	 *            Source InputComponent that is going to be transformed.
	 * @param wmlPack
	 *            WordprocessingMLPackage object, which represents a docx
	 *            archive. Passed to insert intermediate data if needed during
	 *            the transformation.
	 * @return A Content object that can be inserted in the docx archive.
	 * 
	 */
	@Override
	public ArrayList<Content> transform(BasicComponent component,
			WordprocessingMLPackage wmlPack) {
		
		PContent pcontent = new PContent();
		RContent rcontent = new RContent();
		rcontent.addPageBreak();
		pcontent.addRun(rcontent);
		ArrayList<Content> listContents = new ArrayList<Content>();
		listContents.add(pcontent);
		// TODO Auto-generated method stub
		return listContents;
	}

}
