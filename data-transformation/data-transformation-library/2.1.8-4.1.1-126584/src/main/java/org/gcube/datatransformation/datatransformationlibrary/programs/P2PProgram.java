package org.gcube.datatransformation.datatransformationlibrary.programs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.tmpfilemanagement.TempFileManager;
import org.gcube.datatransformation.datatransformationlibrary.utils.FilesUtils;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Abstract helper class that implements the {@link Program} interface.
 * </p>
 * <p>
 * Each class that extends the <tt>P2PProgram</tt> shall implement the {@link P2PProgram#transformObjectFromItsPath(String, List, ContentType)} method which takes as input a file location and returns the location of a file which contains the transformed content.  
 * </p>
 */
public abstract class P2PProgram extends Stream2StreamProgram{
	
	private static Logger log = LoggerFactory.getLogger(P2PProgram.class);
	
	private String subdir=null;
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Stream2StreamProgram#transformStream(java.io.InputStream, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType)
	 * @param content The content of the source {@link DataElement}.
	 * @param sourceFormat The <tt>ContentType</tt> of the source {@link DataElement}. 
	 * @param progparameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @return The transformed content.
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	@Override
	public InputStream transformStream(InputStream content, ContentType sourceFormat, List<Parameter> progparameters, ContentType targetContentType) throws Exception {
		
		if(subdir==null){
			subdir = TempFileManager.genarateTempSubDir();
		}
			
		String inputfile = TempFileManager.generateTempFileName(subdir);
		try {
			FilesUtils.streamToFile(content, inputfile);
		} catch (Exception e) {
			log.error("Could not persist input to file", e);
			throw new Exception("Could not persist input to file", e);
		}
		String outputfile = TempFileManager.generateTempFileName(subdir);
		InputStream outputstream=null;
		try {
			outputstream = new FileInputStream(transformObjectFromItsPath(inputfile, progparameters, targetContentType, outputfile));
		} catch (Exception e) {
			log.error("Could not transform data", e);
			throw new Exception("Could not transform data", e);
		} 
		return outputstream;
	}
	
	/**
	 * Transforms the content of a file whose location is <tt>contentpath</tt> to the <tt>targetContentType</tt> and returns the transformed content in a file.
	 * 
	 * @param sourceContentPath The location of the source file.
	 * @param progparameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @param targetContentPath The path for the transformed content.
	 * @return The path of the transformed content.
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	public abstract String transformObjectFromItsPath(String sourceContentPath, List<Parameter> progparameters, ContentType targetContentType, String targetContentPath) throws Exception;
}
