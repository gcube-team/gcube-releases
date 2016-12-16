package org.gcube.datatransformation.datatransformationlibrary.programs.applications;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.programs.Stream2StreamProgram;
import org.gcube.datatransformation.datatransformationlibrary.utils.FilesUtils;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Program transforming microsoft ppt documents to plain text.
 * </p>
 */
public class PPTToTextTransformer extends Stream2StreamProgram {

	protected static String PPTMimeType = "application/vnd.ms-powerpoint";
	
	private static Logger log = LoggerFactory.getLogger(PPTToTextTransformer.class);
	
	/**
	 * A simple test.
	 * 
	 * @param args The arguments
	 * @throws Exception If the test could not be performed.
	 */
	public static void main(String[] args) throws Exception {
		FilesUtils.streamToFile(new PPTToTextTransformer().transformStream(
				new FileInputStream("C:\\lala.ppt"), null, null, null), "C:\\lala.txt");
	}

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
		try {
			PowerPointExtractor extractor = new PowerPointExtractor(content);
			InputStream outputStream = new ByteArrayInputStream(extractor.getText().getBytes()); 
			log.debug("Convertion from ppt to text must have been performed successfully");
			return outputStream;
		} catch (Exception e) {
			log.error("Did not manage to perform convertion from ppt to text", e);
			throw new Exception("Did not manage to perform convertion from ppt to text");
		}
	}
}
