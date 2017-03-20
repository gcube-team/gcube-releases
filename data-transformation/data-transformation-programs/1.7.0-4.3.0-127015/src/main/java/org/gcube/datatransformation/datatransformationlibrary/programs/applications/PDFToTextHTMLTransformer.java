package org.gcube.datatransformation.datatransformationlibrary.programs.applications;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.programs.Stream2StreamProgram;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFText2HTML;
import org.pdfbox.util.PDFTextStripper;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Program transforming a pdf to a plain text document.
 * </p>
 */
public class PDFToTextHTMLTransformer extends Stream2StreamProgram {

	private static Logger log = LoggerFactory.getLogger(PDFToTextHTMLTransformer.class);
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Stream2StreamProgram#transformStream(java.io.InputStream, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType)
	 * @param content The content of the source {@link DataElement}.
	 * @param sourceContentType The <tt>ContentType</tt> of the source {@link DataElement}. 
	 * @param programParameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @return The transformed content
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	@Override
	public InputStream transformStream(InputStream content, ContentType sourceContentType, List<Parameter> programParameters, ContentType targetContentType) throws Exception {
		log.info("PDFToTextHTML transform prog called.");
		if(targetContentType.getMimeType().equals("text/plain")){
			PDDocument document = PDDocument.load(content);
			try {
				PDFTextStripper textExtractor = new PDFTextStripper();
				String text = textExtractor.getText(document);
				log.trace("PDFToTextHTML transform to text");
//				log.trace("PDFToTextHTML transform to text: "+text);
				return new ByteArrayInputStream(text.getBytes());
			} catch (Exception e) {
				log.error("Could not transform pdf to text", e);
				throw new Exception("Could not transform pdf to text");
			} finally {
				if (document != null) {
					document.close();
				}
			}
		} else if(targetContentType.getMimeType().equals("text/html")) {
			PDDocument document = PDDocument.load(content);
			try {
				PDFText2HTML htmlExtractor = new PDFText2HTML();
				String text = htmlExtractor.getText(document);
//				log.trace("PDFToHTML transform to HTML: "+text);
				log.trace("PDFToHTML transform to HTML");
				return new ByteArrayInputStream(text.getBytes());
			} catch (Exception e) {
				log.error("Could not transform pdf to html", e);
				throw new Exception("Could not transform pdf to html");
			} finally {
				if (document != null) {
					document.close();
				}
			}
		} else {
			log.error("Transformation not supported...");
			throw new Exception("Transformation not supported...");
		}
	}

}
