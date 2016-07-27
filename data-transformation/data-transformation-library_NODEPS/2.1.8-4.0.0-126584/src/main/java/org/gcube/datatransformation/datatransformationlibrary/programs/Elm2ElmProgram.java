package org.gcube.datatransformation.datatransformationlibrary.programs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Abstract helper class that implements the {@link Program} interface.
 * </p>
 * <p>
 * Each class that extends the <tt>Elm2ElmProgram</tt> shall implement the {@link Elm2ElmProgram#transformDataElement(DataElement, List, ContentType)} method which takes as input a <tt>DataElement</tt> and returns the transformed <tt>DataElement</tt>.  
 * </p>
 */
public abstract class Elm2ElmProgram implements Program {

	private static Logger log = LoggerFactory.getLogger(Elm2ElmProgram.class);
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Program#transform(java.util.List, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink)
	 * @param sources The <tt>DataSources</tt> from which the <tt>Program</tt> will get the <tt>DataElements</tt>.
	 * @param programParameters The parameters of the <tt>Program</tt> which are primarily set by the <tt>TransformationUnit</tt>.
	 * @param targetContentType The <tt>ContentType</tt> in which the source data will be transformed.
	 * @param sink The <tt>DataSink</tt> in which the <tt>Program</tt> will append the transformed <tt>DataElements</tt>.
	 * @throws Exception If the program is not capable to transform <tt>DataElements</tt>.
	 */
	public void transform(List<DataSource> sources, List<Parameter> programParameters, ContentType targetContentType, DataSink sink) throws Exception{
		if(sources.size()!=1){
			throw new Exception("Elm2ElmProgram is only applicable for programs with one Input");
		}
		DataSource source = sources.get(0);
		while(!sink.isClosed() && source.hasNext()){
			log.debug("Source has next...");
			DataElement sourceDataElement = source.next();
			if(sourceDataElement!=null){
				DataElement transformedDataElement;
				try {
					log.debug("Got next object with id "+sourceDataElement.getId());
					transformedDataElement = transformDataElement(sourceDataElement, programParameters, targetContentType);
					if(transformedDataElement==null){
						log.warn("Got null transformed object");
						throw new NullPointerException();
					}
					transformedDataElement.setId(sourceDataElement.getId());
					log.debug("Got transformed object with id: "+transformedDataElement.getId()+" and content type: "+transformedDataElement.getContentType().toString()+", appending into the sink");
					ReportManager.manageRecord(sourceDataElement.getId(), "Data element with id "+sourceDataElement.getId()+" and content type "+sourceDataElement.getContentType().toString()+" " +
							"was transformed successfully to "+transformedDataElement.getContentType().toString(), Status.SUCCESSFUL, Type.TRANSFORMATION);
				} catch (Exception e) {
					log.error("Could not transform Data Element, continuing to next...",e);
					ReportManager.manageRecord(sourceDataElement.getId(), "Data element with id "+sourceDataElement.getId()+" and content type "+sourceDataElement.getContentType().toString()+" " +
							"could not be transformed to "+targetContentType.toString(), Status.FAILED, Type.TRANSFORMATION);
					continue;
				}
				sourceDataElement.destroy();
				sink.append(transformedDataElement);
				log.debug("Transformed object with id: "+transformedDataElement.getId()+", was appended successfully");
			}else{
				log.warn("Got null object from the data source");
			}
		}
		log.debug("Source does not have any objects left, closing the sink...");
		sink.close();
	}

	/**
	 * Transforms the <tt>sourceDataElement</tt> to the <tt>targetContentType</tt> and returns the transformed <tt>DataElement</tt>.  
	 * 
	 * @param sourceDataElement The source <tt>DataElement</tt>.
	 * @param programParameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @return The transformed <tt>DataElement</tt>.
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	public abstract DataElement transformDataElement(DataElement sourceDataElement, List<Parameter> programParameters, ContentType targetContentType) throws Exception;
}
