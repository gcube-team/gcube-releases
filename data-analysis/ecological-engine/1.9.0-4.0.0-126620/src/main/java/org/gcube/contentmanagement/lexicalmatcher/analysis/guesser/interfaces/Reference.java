package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.interfaces;

import java.math.BigInteger;

public interface Reference {

	public void setName(String categoryName);
	public String getName();
	public void setIndex(String categoryIndex);
	public String getIndex();
	public void setTableName(String tableName);
	public String getTableName();
	public void setDescription(String description);
	public String getDescription();
	public String toString();
	public void setNumberOfElements(BigInteger numberOfElements);
	public BigInteger getNumberOfElements();	
	
}
