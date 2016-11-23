package gr.uoa.di.madgik.hive.parse.responses;

public class CreateResponse extends ParseResponse{
	private String affectedTable;
	private String delimiter;
	
	/**
	 * @return the affectedTable
	 */
	public String getAffectedTable() {
		return affectedTable;
	}

	/**
	 * @param affectedTable the affectedTable to set
	 */
	public void setAffectedTable(String affectedTable) {
		this.affectedTable = affectedTable;
	}

	/**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
}
