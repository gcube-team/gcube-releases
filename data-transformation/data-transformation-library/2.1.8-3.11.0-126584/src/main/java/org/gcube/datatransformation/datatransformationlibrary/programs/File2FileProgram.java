package org.gcube.datatransformation.datatransformationlibrary.programs;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.LocalFileDataElement;
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
 * Each class that extends the <tt>P2PProgram</tt> shall implement the {@link File2FileProgram#transformObjectFromItsPath(String, List, ContentType)} method which takes as input a file location and returns the location of a file which contains the transformed content.  
 * </p>
 */
public abstract class File2FileProgram extends Elm2ElmProgram{
	
	private static Logger log = LoggerFactory.getLogger(File2FileProgram.class);
	
	private String subdir=null;
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Elm2ElmProgram#transformDataElement(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType)
	 * @param sourceDataElement The source <tt>DataElement</tt>.
	 * @param programParameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @return The transformed <tt>DataElement</tt>.
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	@Override
	public DataElement transformDataElement(DataElement sourceDataElement, List<Parameter> programParameters, ContentType targetContentType) throws Exception {
		if(subdir==null){
			subdir = TempFileManager.genarateTempSubDir();
		}
		File sudDir = new File(subdir);
		sudDir.deleteOnExit();
		File sourceFile;
		if(sourceDataElement instanceof LocalFileDataElement){
			sourceFile=((LocalFileDataElement)sourceDataElement).getFileContent();
		}else{
			String inputfile = TempFileManager.generateTempFileName(subdir);
			try {
				FilesUtils.streamToFile(sourceDataElement.getContent(), inputfile);
			} catch (Exception e) {
				log.error("Could not persist input to file", e);
				throw new Exception("Could not persist input to file", e);
			}
			sourceFile=new File(inputfile);
		}
		
		File transformedFile;
		try {
			String targetContentPath = TempFileManager.generateTempFileName(subdir);
			transformedFile = transformFile(sourceFile, programParameters, targetContentType, targetContentPath);
		} catch (Exception e) {
			log.error("Did not manage to transform file", e);
			throw new Exception("Did not manage to transform file", e);
		}
		LocalFileDataElement transformedObject = LocalFileDataElement.getSinkDataElement(sourceDataElement);
		//Here we specify that the id of the transformed object will be the same with the source
		//This will be used by the sink in order to manage in the proper way the relationship source - transformed object
		transformedObject.setId(sourceDataElement.getId());
		transformedObject.setContentType(targetContentType);
		transformedObject.setContent(transformedFile);
		return transformedObject;
	}
	
	/**
	 * Transforms the content of the <tt>sourceFile</tt> file to the <tt>targetContentType</tt> and returns the transformed content in a file.
	 * 
	 * @param sourceFile The source file.
	 * @param programParameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @param targetContentPath The path for the transformed content.
	 * @return The file of the transformed content.
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	public abstract File transformFile(File sourceFile, List<Parameter> programParameters, ContentType targetContentType, String targetContentPath) throws Exception;
}
