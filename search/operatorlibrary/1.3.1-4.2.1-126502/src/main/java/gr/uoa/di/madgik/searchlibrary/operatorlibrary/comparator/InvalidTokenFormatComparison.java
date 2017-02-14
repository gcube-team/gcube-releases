package gr.uoa.di.madgik.searchlibrary.operatorlibrary.comparator;


/**
 * The InvalidTokenFormatComparison class defines an exception, used in comparing two tokens.
 * If the tokens cannot be compared due to their inconforming formats,
 * then an InvalidTokenFormatComparison is thrown. 
 * 
 * @author UoA
 */
public class InvalidTokenFormatComparison extends Exception{

	private static final long serialVersionUID=0;
	/**
	 * The message to return
	 */
	private String message = "";
	/**
	 * Default essage
	 */
	private String defMesg = "The comparison could not take place due to the invalid format of the two tokens, being compared";

	/**
	 * Creates a new instance of the class
	 */
	public InvalidTokenFormatComparison()
	{
		message = new String(defMesg);
	}

	/**
	 * Creates a new instance of the class
	 * @param customMesg The message
	 */
	public InvalidTokenFormatComparison(String customMesg)
	{
		message = new String(customMesg);
	}

	/**
	 * Creates a new instance of the class
	 * 
	 * @param token1 Token used
	 * @param token2 Token used
	 */
	public InvalidTokenFormatComparison(String token1, String token2)
	{
		message = defMesg + "\nInvalid tokens: " + token1 + " and " + token2;
	}

	/**
	 * @see java.lang.Object#toString()
	 * @return the string representation of the exception
	 */
	public String toString()
	{
		return this.message;
	}
}
