package org.gcube.datatransformation.datatransformationlibrary.reports;

import java.util.ArrayList;

import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.FilterDataBridge;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;

//TODO: Think of doing it more generic: Create Records with custom attributes. Not sure if helpful if used as log too.
/**
 * @author Dimitris Katris, NKUA
 * 
 * <p><tt>Record</tt> class maintains operations performed over a specific <tt>DataElement</tt> in the transformationUnit process.</p>
 */
public class Record {

	/**
	 * @author Dimitris Katris, NKUA
	 * 
	 * <p>The status of a specific operation.
	 * <li>SUCCESSFUL - If the operation was performed successfully.</li>
	 * <li>FAILED - If the operation failed to be performed.</li></p>
	 */
	public enum Status {
		/**
		 * If the operation was performed successfully.
		 */
		SUCCESSFUL, 
		/**
		 * If the operation failed to be performed.
		 */
		FAILED
	}
	
	/**
	 * @author Dimitris Katris, NKUA
	 * 
	 * <p>
	 * The type of a specific operation.
	 * 
	 * <li>SOURCE - For operations performed by {@link DataSource} implementations.</li>
	 * <li>FILTER - For operations performed by the {@link FilterDataBridge} or from {@link DTSCore} methods.</li>
	 * <li>TRANSFORMATION - For operations performed by {@link Program} implementations.</li>
	 * <li>SINK - For operations performed by {@link DataSink} implementations.</li>
	 * </p>
	 */
	public enum Type {
		/**
		 * For operations performed by {@link DataSource} implementations.
		 */
		SOURCE, 
		/**
		 * For operations performed by the {@link FilterDataBridge} or from {@link DTSCore} methods.
		 */
		FILTER, 
		/**
		 * For operations performed by {@link Program} implementations.
		 */
		TRANSFORMATION, 
		/**
		 * For operations performed by {@link DataSink} implementations.
		 */
		SINK
	}

	protected String objectID;
	
	protected String sourceRec;
	protected Status sourceStatus;
	protected String filterRec;
	protected Status passFilterStatus;
	protected ArrayList <RecordStruct> transformationRecs = new ArrayList<RecordStruct>();
	protected String sinkRec;
	protected Status sinkStatus;
	
	protected Report report;
	
	/**
	 * Adds a new message in this <tt>Record</tt>.
	 * 
	 * @param message The message.
	 * @param status The status of the operation.
	 * @param type The type of the operation.
	 */
	public void setRecord(String message, Status status, Type type){
		switch(type){
			case SOURCE:
				this.sourceRec = message;
				this.sourceStatus = status;
				if(status.equals(Status.FAILED)){
					report.commitRecord(this);
				}
				break;
			case FILTER:
				this.filterRec = message;
				this.passFilterStatus = status;
				if(status.equals(Status.FAILED)){
					report.commitRecord(this);
				}
				break;
			case TRANSFORMATION:
				RecordStruct struct = new RecordStruct();
				struct.message = message;
				struct.status = status;
				transformationRecs.add(struct);
				if(status.equals(Status.FAILED)){
					report.commitRecord(this);
				}
				break;
			case SINK:
				this.sinkRec = message;
				this.sinkStatus = status;
				report.commitRecord(this);
				break;
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 * 
	 * @return The <tt>Record</tt> in <tt>XML</tt> representation.
	 */
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("<REPORTREC>");
		builder.append("<OBJECTID>");
		builder.append(this.objectID);
		builder.append("</OBJECTID>");
		
		if(this.sourceRec != null && this.sourceStatus!=null){
			builder.append("<SOURCE>");
			builder.append("<MSG>"+this.sourceRec+"</MSG>");
			builder.append("<STATUS>"+this.sourceStatus+"</STATUS>");
			builder.append("</SOURCE>");
		}
		
		if(this.filterRec != null && this.passFilterStatus!=null){
			builder.append("<FILTER>");
			builder.append("<MSG>"+this.filterRec+"</MSG>");
			builder.append("<STATUS>"+this.passFilterStatus+"</STATUS>");
			builder.append("</FILTER>");
		}
		
		if(this.transformationRecs!=null && this.transformationRecs.size()>0){
			for(RecordStruct struct: this.transformationRecs){
				builder.append("<TRANSFORMATION>");
				builder.append("<MSG>"+struct.message+"</MSG>");
				builder.append("<STATUS>"+struct.status+"</STATUS>");
				builder.append("</TRANSFORMATION>");
			}
		}
		
		if(this.sinkRec != null && this.sinkStatus!=null){
			builder.append("<SINK>");
			builder.append("<MSG>"+this.sinkRec+"</MSG>");
			builder.append("<STATUS>"+this.sinkStatus+"</STATUS>");
			builder.append("</SINK>");
		}
		
		
		builder.append("</REPORTREC>");
		
		return builder.toString();
	}
}

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * A simple structure for internal use.
 * </p>
 */
class RecordStruct{
	public String message;
	public Record.Status status;
}
