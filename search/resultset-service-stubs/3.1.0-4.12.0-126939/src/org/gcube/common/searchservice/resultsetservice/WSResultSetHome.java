package org.gcube.common.searchservice.resultsetservice;
 
import java.util.Hashtable;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;

/**
 * Resource home for non wsrf functionality 
 * 
 * @author UoA
 */
public class WSResultSetHome {
	/**
	 * Stores resources
	 */
	private Hashtable<String,ResultSet> home;
	
	/**
	 * Constructor
	 */
	public WSResultSetHome(){
		this.home=new Hashtable<String,ResultSet>();
	}
	
	/**
	 * adds the result set
	 * 
	 * @param sessionToken the sesion token
	 * @param rs the ResultSet
	 */
	public void addResultSet(String sessionToken,ResultSet rs){
		this.home.put(sessionToken,rs);
	}
	
	/**
	 * retrieves the result set associated with the specific ID
	 * 
	 * @param rsID teh rs id
	 * @return the result set
	 * @throws Exception the id does not exist
	 */
	public ResultSet getResultSet(String rsID) throws Exception{
		ResultSet rs=this.home.get(rsID);
		if(rs==null) throw new Exception("resource not exist");
		return rs;
	}
	
	/**
	 * removes the result set associated with the specific id
	 * 
	 * @param rsID the rs id
	 * @throws Exception the id does not exist
	 */
	public void remove(String rsID) throws Exception{
		this.home.remove(rsID);
	}
}
