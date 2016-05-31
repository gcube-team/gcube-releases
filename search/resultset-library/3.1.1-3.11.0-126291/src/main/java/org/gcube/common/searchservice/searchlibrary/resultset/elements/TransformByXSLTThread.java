package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.io.StringReader;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;

/**
 * Thread used to background populate a new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} 
 * transforming the results of the existing one based on the provided xslt
 * 
 * @author UoA
 */
public class TransformByXSLTThread extends Thread{
	/**
	 * The Logger this class uses
	 */
	private static Logger log = Logger.getLogger(TransformByXSLTThread.class);
	/**
	 * The {@link ResultSet} to write to 
	 */
	private ResultSet rs=null;
	/**
	 * The {@link ResultSet} to read from 
	 */
	private ResultSet rsRead=null;
	/**
	 * The xslt to apply
	 */
	private String xslt=null;
	
	/**
	 * Creates a new {@link TransformByXSLTThread}
	 * 
	 * @param rs The {@link ResultSet} to whom the records must be appended
	 * @param rsRead The {@link ResultSet} from which to read the records
	 * @param xslt the XSLT transformation to apply
	 */
	public TransformByXSLTThread(ResultSet rs,ResultSet rsRead,String xslt){
		this.xslt=xslt;
		this.rs=rs;
		this.rsRead=rsRead;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try{
			Templates txslt=TransformerFactory.newInstance().newTemplates(new StreamSource(new StringReader(xslt)));
			while(true){
				String ret=null;
				try{
					ret=rsRead.transformByXSLTAndPersist(txslt);
					rs.wrapFile(ret);
				}catch(Exception e){
					log.error("Could not transform and wrap. Continuing",e);
				}
				try{
					rs.startNewPart();
					if(!rsRead.getNextPart(-1)) break;
				}catch(Exception e){
					log.error("could not get next part / start new part. Throwing Exception",e);
					throw new Exception("could not get next part / start new part");
				}
			}
			rs.endAuthoring();
			rsRead.getRSRef().clearResults();
			rs.getRSRef().clearResults();
		}catch(Exception e){
			log.error("Could not end localization procedure.Ending Authoring",e);
			try{
				rs.endAuthoring();
				rsRead.getRSRef().clearResults();
				rs.getRSRef().clearResults();
			}catch(Exception ee){
				log.error("Could not end Authoring",ee);
			}
		}
	}
}
