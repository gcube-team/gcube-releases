package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;

/**
 * Thread used to create a clone o an existing {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * 
 * @author UoA
 */
public class CloneThreadGeneric extends Thread{
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(CloneThreadGeneric.class);
	/**
	 * The reference to the underlying writing {@link ResultSet}
	 */
	private ResultSet rs=null;
	/**
	 * The reference to the underlying reading {@link ResultSet}
	 */
	private ResultSet rsRead=null;
	
	/**
	 * Creates a new {@link CloneThreadGeneric}
	 * 
	 * @param rs The target {@link ResultSet}
	 * @param rsRead The source {@link ResultSet}
	 */
	public CloneThreadGeneric(ResultSet rs,ResultSet rsRead){
		this.rs=rs;
		this.rsRead=rsRead;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try{
			while(true){
				String newFile=null;
				try{
					newFile=RSFileHelper.generateName(RSConstants.CONTENT,null);
					RSFileHelper.copy(rsRead.getCurrentContentPartName(),newFile);
				}catch(Exception e){
					log.error("Could not copy current content part. Continuing",e);
				}
				rs.wrapFile(newFile);
				rs.startNewPart();
				if(!rsRead.getNextPart(-1)) break;
			}
			rs.endAuthoring();
		}catch(Exception e){
			log.error("Could not end cloning procedure.Ending Authoring",e);
			try{
				rs.endAuthoring();
			}catch(Exception ee){
				log.error("Could not end Authoring",ee);
			}
		}
	}
}
