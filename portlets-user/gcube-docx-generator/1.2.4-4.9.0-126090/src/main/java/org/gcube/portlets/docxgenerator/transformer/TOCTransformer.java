/**
 * 
 */
package org.gcube.portlets.docxgenerator.transformer;


import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.wml.Body;
import org.docx4j.wml.Document;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.Text;
import org.docx4j.wml.PPrBase.PStyle;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.docxgenerator.content.Content;

/**
 * @author gioia
 *
 */
public class TOCTransformer implements Transformer {

	@Override
	public ArrayList<Content> transform(BasicComponent component, WordprocessingMLPackage wmlPack) {
		
		//Create TOC
		Document wmlDocumentEl = wmlPack.getMainDocumentPart().getJaxbElement();
		Body body =  wmlDocumentEl.getBody();
		body.getEGBlockLevelElts().add(createTOC());
		return new ArrayList<Content>();
	}

	private P createTOC() {
		 ObjectFactory factory = Context.getWmlObjectFactory();
	        
		 P p = factory.createP();
		
		 
		 PStyle pstyle = factory.createPPrBasePStyle();
		 pstyle.setVal("TOC2");
		 PPr ppr = factory.createPPr();
		 ppr.setPStyle(pstyle);
		 p.setPPr(ppr); 
		 
		 R r = factory.createR();

		 FldChar fldchar = factory.createFldChar();
		 fldchar.setFldCharType(STFldCharType.BEGIN);
		 fldchar.setDirty(true);
		 r.getRunContent().add(getWrappedFldChar(fldchar));
		 p.getParagraphContent().add(r);

		 R r1 = factory.createR();
		 Text txt = new Text();
		 txt.setSpace("preserve");
		 
		 txt.setValue("TOC \\o \"1-3\" \\p \".\" \\u \\x \\h");
		 r.getRunContent().add(factory.createRInstrText(txt) );
		 p.getParagraphContent().add(r1);

		 FldChar fldcharend = factory.createFldChar();
		 fldcharend.setFldCharType(STFldCharType.END);
		 R r2 = factory.createR();
		 r2.getRunContent().add(getWrappedFldChar(fldcharend));
		 p.getParagraphContent().add(r2);

		 return p;
	}
	
	private JAXBElement<FldChar> getWrappedFldChar(FldChar fldchar) {
		return new JAXBElement<FldChar>( new QName(Namespaces.NS_WORD12, "fldChar"), 
				FldChar.class, fldchar);
	}
}
