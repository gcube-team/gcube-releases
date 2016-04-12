package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

/**
 * @author Dimitris Katris, NKUA
 *
 * <p>
 * 
 * </p>
 */
public class PathDataSink implements DataSink {

	private String sinkpath;
	
	private static Logger log = LoggerFactory.getLogger(PathDataSink.class);
	
	/* 
	 * This constructor is used when DTS is instanciating a new PathDataSink.
	 * The output is the directory where the trasnformed objects will be stored
	 * but outputParameters are not used in this DataSink...
	 */
	/**
	 * @param output The output value of the <tt>DataSink</tt>.
	 * @param outputParameters The output parameters of the <tt>DataSink</tt>.
	 */
	public PathDataSink(String output, Parameter[] outputParameters){
		if(output.endsWith(File.separator))
			this.sinkpath=output;
		else
			this.sinkpath=output+File.separator;
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#append(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement)
	 * @param element {@link DataElement} to be appended to this <tt>DataSink</tt>
	 */
	public void append(DataElement element) {
		if(element!=null){
			try {
//				String filename = ("id"+object.getId()+"fmt"+object.getContentFormat().toString()).replaceAll("/", "_").replaceAll(":", "").replaceAll(" ", "").replaceAll("-", "_").replaceAll("\\*", "any");
				String filename = element.getId().hashCode()+element.getContentType().getMimeType().replaceAll("/", ".");
				log.info("Trying to persist stream into file "+sinkpath+filename);
				org.gcube.datatransformation.datatransformationlibrary.utils.FilesUtils.streamToFile(element.getContent(), sinkpath+filename);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	public void close() {}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#getOutput()
	 * @return The output of the transformation.
	 */
	public String getOutput() {
		return sinkpath;
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed() {
		return false;
	}

}
