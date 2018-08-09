package org.gcube.data.analysis.excel;

public abstract class BasicTable implements Table{

	private static final String DEFAULT_TABLE_NAME ="table1";
	
	private String tableName;
	private String excelName;
	
	private final char invalidCharacters [] = { '\\','/','*','[',']',':', '?'};
	
	public BasicTable (String tableName)
	{
		this.tableName = tableName;
		this.excelName = removeInvalidCharacters(this.tableName);
		
		if (this.excelName.length()>25)
		{
				this.excelName = this.excelName.substring(0, 25);
		}
		
	}
	
	private String removeInvalidCharacters (String originalString)
	{	
		
		for (char invalidCharacter : this.invalidCharacters)
		{
			originalString = originalString.replace(invalidCharacter, '_');
		}
		
		return originalString;
	}
	
	
	public BasicTable ()
	{
		this (DEFAULT_TABLE_NAME);
	}
	


	@Override
	public String getExcelTableName() {
		return excelName;
	}

	
	@Override
	public String getOriginalTableName() {
		return this.tableName;
	}

	
	
	
}
