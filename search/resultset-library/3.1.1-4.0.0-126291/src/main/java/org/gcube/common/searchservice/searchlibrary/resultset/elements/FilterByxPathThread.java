package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSXMLHelper;

/**
 * Thread used to background populate a new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} 
 * filtering the results of the existing one based on the provided xpath
 * 
 * @author UoA
 */
public class FilterByxPathThread extends Thread{
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(FilterByxPathThread.class);
	/**
	 * The reference to the underlying writing {@link ResultSet}
	 */
	private ResultSet rs=null;
	/**
	 * The reference to the underlying reading {@link ResultSet}
	 */
	private ResultSet rsRead=null;
	/**
	 * The xPath expression to base teh filtering on
	 */
	private String xpath=null;
	
	/**
	 * Creates a new {@link FilterByxPathThread}
	 * 
	 * @param rs The {@link ResultSet} to whom the records must be appended
	 * @param rsRead The {@link ResultSet} from which to read the records
	 * @param xpath the xpath to base the filtering on
	 */
	public FilterByxPathThread(ResultSet rs,ResultSet rsRead,String xpath){
		this.xpath=xpath;
		this.rs=rs;
		this.rsRead=rsRead;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try{
			while(true){
				try{
					Vector<Integer> res=RSXMLHelper.executeQueryOnResultsFile(rsRead.getCurrentContentPartName(),xpath);
					Vector<String> add=new Vector<String>();
					if(res.size()>0) rsRead.getNumberOfResults(PropertyElementType.XML);
					for(int i=0;i<res.size();i+=1){
						add.add(rsRead.getRSRef().getResults().get(res.get(i).intValue()));
					}
					if(add.size()>0) rs.addResults(add.toArray(new String[0]));
					rsRead.getRSRef().clearResults();
				}catch(Exception e){
					log.error("Could not query and wrap. Continuing",e);
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
