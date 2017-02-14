package org.gcube.datatransformation.datatransformationlibrary.imanagers.queries;

//import java.util.HashMap;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Abstract query object.
 * </p>
 */
public class QueryObject {
	
	//Currently only one tr or tp id permitted...
//	public String transformationID = null;
//	public String transformationProgramID = null;
//	
//	public HashMap<Integer, FormatCondition> sourceFormatConditions = new HashMap<Integer, FormatCondition>();
//	public HashMap<Integer, FormatCondition> targetFormatConditions = new HashMap<Integer, FormatCondition>();
	
	/**
	 * AND/OR For the beginning only one will be supported...
	 */
	private String logicOperation=QueryParser.LOGICAND;
	
	private boolean hasWhereClause=false;
	
	private String resultType;

	/**
	 * Returns the logic operation.
	 * @return The logic operation.
	 */
	public String getLogicOperation() {
		return logicOperation;
	}

	/**
	 * Sets the logic operation.
	 * @param logicOperation The logic operation.
	 */
	public void setLogicOperation(String logicOperation) {
		this.logicOperation = logicOperation;
	}

	/**
	 * Returns the result type.
	 * @return the result type.
	 */
	public String getResultType() {
		return resultType;
	}

	/**
	 * Sets the result type.
	 * @param resultType the result type.
	 */
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	/**
	 * @return True if "where" is contained in query.
	 */
	public boolean hasWhereClause() {
		return hasWhereClause;
	}

	/**
	 * Sets if "where" is contained in query.
	 * @param hasWhereClause if "where" is contained in query.
	 */
	public void setHasWhereClause(boolean hasWhereClause) {
		this.hasWhereClause = hasWhereClause;
	}

}
