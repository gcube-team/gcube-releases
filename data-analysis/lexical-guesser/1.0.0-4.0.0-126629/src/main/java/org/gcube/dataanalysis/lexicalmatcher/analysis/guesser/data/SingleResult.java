package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data;


public class SingleResult {
	private String category;
	private String column;
	
	private String tablename;
	private String familyID;
	
	private double score;
	
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategory() {
		return category;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getColumn() {
		return column;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getScore() {
		return score;
	}
	
	public String getStringScore() {
		double scored = Math.round((int)(score*100))/(double)100;
		
		return ""+scored;
	}
	
	public String toString(){
		double scored = Math.round((int)(score*100))/(double)100;
		if (column!=null)
			return category+"="+column+":"+scored+" tab:"+tablename+":"+familyID;
		else
			return category+"="+":"+scored;
	}
	
	public SingleResult (String Category,String Column,double Score, String TableName,String FamilyID){
		category = Category;
		column = Column;
		score = Score;
		tablename = TableName;
		familyID = FamilyID;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public String getTablename() {
		return tablename;
	}
	public void setFamilyID(String familyID) {
		this.familyID = familyID;
	}
	public String getFamilyID() {
		return familyID;
	}
}
