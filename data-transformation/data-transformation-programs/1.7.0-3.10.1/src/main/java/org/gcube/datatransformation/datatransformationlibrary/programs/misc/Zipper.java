package org.gcube.datatransformation.datatransformationlibrary.programs.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.CompoundDataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.LocalFileDataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.programs.Elm2ElmProgram;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.tmpfilemanagement.TempFileManager;
import org.gcube.datatransformation.datatransformationlibrary.utils.MimeUtils;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <p>Creates zip files from single or multipart data elements.</p>
 */
public class Zipper extends Elm2ElmProgram {

	private static Logger log = LoggerFactory.getLogger(Zipper.class);
	
	private String subdir;
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Elm2ElmProgram#transformDataElement(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType)
	 * @param sourceDataElement The data element which will be transformed.
	 * @param programParameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @return The transformed data element.
	 * @throws Exception If the program fails to transform the data element.
	 */
	@Override
	public DataElement transformDataElement(DataElement sourceDataElement, List<Parameter> programParameters, ContentType targetContentType) throws Exception {
		
		// These are the files to include in the ZIP file
	    List<DataElement> uncompressedElements = new ArrayList<DataElement>();
	    if(sourceDataElement instanceof CompoundDataElement){
	    	uncompressedElements = ((CompoundDataElement)sourceDataElement).getParts();
	    }else{
	    	uncompressedElements.add(sourceDataElement);
	    }
	    
	    // Create a buffer for reading the files
	    byte[] buf = new byte[1024];

	    if(subdir==null){
	    	subdir = TempFileManager.genarateTempSubDir();
	    }
	    
	    LocalFileDataElement transformedDataElement = LocalFileDataElement.getSinkDataElement(sourceDataElement);
	    ContentType contentType = new ContentType();
	    contentType.setMimeType("application/zip");
	    
	    transformedDataElement.setContentType(contentType);
	    String outFilename = TempFileManager.generateTempFileName(subdir);
	    
        // Create the ZIP file
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
    
        // Compress the files
        for (int i=0; i<uncompressedElements.size(); i++) {
        	DataElement uncompressedElement = uncompressedElements.get(i);
        	InputStream in = uncompressedElement.getContent();
            // Add ZIP entry to output stream.
        	String entryName = uncompressedElement.getId()+"."+MimeUtils.getFileExtension(uncompressedElement.getContentType().getMimeType());
        	log.trace("Adding zip entry with name: "+entryName);
            out.putNextEntry(new ZipEntry(entryName));
            
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
    
            // Complete the entry
            out.closeEntry();
            in.close();
        }
    
        out.close();
        File transformedCOntent = new File(outFilename);
        if(!transformedCOntent.exists()){
        	log.error("Zip file does not exist for data element with id "+sourceDataElement.getId());
        	throw new Exception("Did not manage to create zip file for dataelement with id "+sourceDataElement.getId());
        }
        transformedDataElement.setContent(transformedCOntent);
		return transformedDataElement;
	}

}
