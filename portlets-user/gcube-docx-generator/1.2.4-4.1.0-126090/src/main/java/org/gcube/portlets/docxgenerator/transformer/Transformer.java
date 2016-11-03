package org.gcube.portlets.docxgenerator.transformer;

//import it.cnr.isti.docxgenerator.InputComponent;



import java.util.ArrayList;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.docxgenerator.content.Content;

/**
 * Transformer interface. Supplies methods to transform an InputComponent Object
 * into a Content object, which can be inserted in the WordprocessingMLPackage.
 * 
 * @author Luca Santocono
 * 
 */
public interface Transformer {

	/**
	 * Transforms an InputComponent object into a Content object that can be
	 * inserted in a WordprocessingMLPackage.
	 * 
	 * @param component
	 *            Source InputComponent that is going to be transformed.
	 * @param wmlPack
	 *            WordprocessingMLPackage object, which represents a docx
	 *            archive. Passed to insert intermediate data if needed during
	 *            the transformation.
	 * @return TODO
	 * @return A Content object that can be inserted in the docx archive.
	 */
	 ArrayList<Content> transform(final BasicComponent component,
			final WordprocessingMLPackage wmlPack);

}
