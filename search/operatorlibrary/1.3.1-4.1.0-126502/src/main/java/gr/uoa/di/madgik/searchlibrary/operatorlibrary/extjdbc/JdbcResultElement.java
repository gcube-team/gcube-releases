package gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc;

import java.sql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class acts as a container for the external service to fill with results.
 * 
 * @author UoA
 */
public class JdbcResultElement
{	
	/**
	 * The logger the class uses
	 */
	private static Logger logger = LoggerFactory.getLogger(JdbcResultElement.class.getName());
	/**
	 * Tranform each tuple to its respective XML representation, with each element correspond to a tuple attribute.
	 * 
	 * @param columnNames The names of the colums
	 * @param rs The current result set
	 * @return The XML representation of the tuple
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static String rs2XML(String[] columnNames,ResultSet rs) throws Exception
	{
		try{
			String str = new String("");
			for(int i=0;i<columnNames.length;i++)
				str+="<"+columnNames[i]+">"+rs.getString(columnNames[i])+"</"+columnNames[i]+">";
			return str;
		}catch(Exception e){
			logger.error("Could not retrieve result string. Throwing Exception",e);
			throw new Exception("Could not retrieve result string");
		}
	}
}
