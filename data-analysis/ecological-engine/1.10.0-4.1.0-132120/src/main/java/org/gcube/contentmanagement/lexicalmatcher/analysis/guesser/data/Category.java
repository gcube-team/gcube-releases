package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data;


import java.math.BigInteger;

import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.interfaces.Reference;

public class Category implements Reference {


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	private String categoryName;
	private String categoryIndex;
	private String tableName;
	private String description;
	private BigInteger numberOfElements;
	
	public Category(String name,String index,String tablename,String descr){
		categoryName=name;
		categoryIndex=index;
		tableName=tablename;
		description=descr;
	}
	
	public void setName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getName() {
		return categoryName;
	}

	public void setIndex(String categoryIndex) {
		this.categoryIndex = categoryIndex;
	}

	public String getIndex() {
		return categoryIndex;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public String toString(){
		return "["+categoryName+": index "+categoryIndex+" table "+tableName+" description "+description+"]";
	}

	public void setNumberOfElements(BigInteger numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	public BigInteger getNumberOfElements() {
		return numberOfElements;
	}
}
