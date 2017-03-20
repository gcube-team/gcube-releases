package gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc;

import java.sql.ResultSet;

/**
 * This class reads a configuration XML file and sets the behaviour of the operator as far as its 
 * JDBC connection behaviour is concerned. Currently, everything is hard coded.
 * @author UoA
 */
public class JDBCConnectorProfile
{
	/**
	 * Query Constant
	 */
	public static int ResultSetType = ResultSet.TYPE_FORWARD_ONLY;
	/**
	 * Query Constant
	 */
	public static int ResultSetConcurreny = ResultSet.CONCUR_READ_ONLY;
	/**
	 * Query Constant
	 */
	public static int ResultSetHoldability = ResultSet.HOLD_CURSORS_OVER_COMMIT;
	/**
	 * Query Constant
	 */
	public static int FetchDirection = ResultSet.FETCH_FORWARD;

	/**
	 * Creates a new Instance of the class
	 */
	public JDBCConnectorProfile()
	{
		
	}
}
