package it.eng.rdlab.soa3.pm.connector.beans;

/**
 *
 * Bean used to communicate the result of a XACML based operation
 *
 * @author Ciro Formisano (ENG)
 *
 */
public class Status 
{
	
	public static final int OPERATION_OK = 0,
							SERVER_ERROR = 1,
							INTERNAL_ERROR = 2;
	
	public static final int RESULT_TRUE = 0,
							RESULT_FALSE = 1;
	
	private int status,
				result;
	
	private String info;

	
	public Status (int status, int result)
	{
		this.status = status;
		this.result = result;
	}
	
	/**
	 * 
	 * the status can be:
	 * 0 = OK
	 * 1 = SERVER ERROR
	 * 2 = INTERNAL ERROR
	 * 
	 * @return the status 
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 
	 * The result can be
	 * 0 = TRUE
	 * 1 = FALSE
	 * 
	 * @return the result of the operation
	 */
	public int getResult() {
		return result;
	}

	/**
	 * 
	 * A string representation of the result of the operation (for example the XACML string of a policy)
	 * 
	 * @return the info string
	 */
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	

	@Override
	public String toString ()
	{
		return "Status "+this.status+", Result "+this.result+", Info "+this.info;
	}

	
	

}
