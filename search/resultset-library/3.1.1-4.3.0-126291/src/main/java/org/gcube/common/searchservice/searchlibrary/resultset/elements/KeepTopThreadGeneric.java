package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;

/**
 * Thread used to background populate a new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} keeping a top count of results
 * 
 * @author UoA
 */
public class KeepTopThreadGeneric extends Thread{
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(KeepTopThreadGeneric.class);
	/**
	 * The operation should be performed on a per part basis
	 */
	public static short PERPART=0;
	/**
	 * The operation should be performed on a per record basis
	 */
	public static short PERRECORD=1;
	/**
	 * The reference to the underlying writing {@link ResultSet}
	 */
	private ResultSet rs=null;
	/**
	 * The reference to the underlying reading {@link ResultSet}
	 */
	private ResultSet rsRead=null;
	/**
	 * The basis the operation should be perform on
	 */
	private int type=KeepTopThreadGeneric.PERPART;
	/**
	 * The number of elements to keep depending on the type of operation
	 */
	private int count=0;
	
	/**
	 * Creates a new {@link KeepTopThreadGeneric}
	 * 
	 * @param rs The {@link ResultSet} to whom the records must be appended
	 * @param rsRead The {@link ResultSet} from which to read the records
	 * @param count The number of records to keep
	 * @param type The type of keep op operation to perform. This can be {@link KeepTopThreadGeneric#PERPART}
	 * or {@link KeepTopThreadGeneric#PERRECORD}
	 */
	public KeepTopThreadGeneric(ResultSet rs,ResultSet rsRead,int count,int type){
		this.rs=rs;
		this.count=count;
		this.rsRead=rsRead;
		if(type!=KeepTopThreadGeneric.PERPART && type!=KeepTopThreadGeneric.PERRECORD) this.type=KeepTopThreadGeneric.PERPART;
		else this.type=type;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try{
			int soFar=0;
			int thisTime=0;
			while(true){
				String newFile=null;
				try{
					newFile=RSFileHelper.generateName(RSConstants.CONTENT,null);
					if(this.type==KeepTopThreadGeneric.PERPART){
						RSFileHelper.copy(rsRead.getCurrentContentPartName(),newFile);
						thisTime=1;
					}
					else{
						thisTime=rsRead.getNumberOfResults(PropertyElementType.XML);
						if(thisTime+soFar<this.count){
							RSFileHelper.copy(rsRead.getCurrentContentPartName(),newFile);
						}
						else{
							RSFileHelper.persistContent(newFile,rsRead.getRSRef().getResults(),this.count-soFar,rsRead.getRSRef().getMnemonic());
							thisTime=this.count-soFar;
						}
						rs.getRSRef().clearResults();
					}
				}catch(Exception e){
					log.error("Could not copy current content part. Continuing",e);
				}
				rs.wrapFile(newFile);
				soFar+=thisTime;
				rs.startNewPart();
				if(soFar>=this.count) break;
				if(!rsRead.getNextPart(-1)) break;
			}
			if(this.type==KeepTopThreadGeneric.PERRECORD) rs.startNewPart();
			rs.endAuthoring();
		}catch(Exception e){
			log.error("Could not end localization procedure.Ending Authoring",e);
			try{
				rs.endAuthoring();
			}catch(Exception ee){
				log.error("Could not end Authoring",ee);
			}
		}
	}
}
