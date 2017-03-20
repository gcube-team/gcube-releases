package org.gcube.datatransformation.datatransformationlibrary.programs.applications;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.programs.Stream2StreamProgram;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <p>
 * Program transforming microsoft word documents to plain text.
 * </p>
 */
public class DocToTextConverter extends Stream2StreamProgram {

	private static Logger log = LoggerFactory.getLogger(DocToTextConverter.class);
	
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
	public InputStream transformStream(InputStream content, ContentType sourceContentType, List<Parameter> programParameters, ContentType targetContentType) throws Exception{
		WordExtractor extractor;
		try {
			extractor = new WordExtractor(content);
			InputStream outputStream = new ByteArrayInputStream(extractor.getText().getBytes()); 
			log.debug("Convertion from doc to text must have been performed successfully");
			return outputStream;
		} catch (Exception e) {
			log.error("Did not manage to perform convertion from doc to text", e);
			throw new Exception("Did not manage to perform convertion from doc to text");
		}
	}
}
