package org.gcube.contentmanagement.graphtools.core.filters;

public class Filter {

	String firstElement;
	String secondElement;
	String operator;
	
	public Filter(String first,String second, String operator){
		setFirstElement(first);
		setSecondElement(second);
		setOperator(operator);
	}
	
	public Filter(String first,String second){
		setFirstElement(first);
		setSecondElement(second);
		setOperator("=");
	}
	
	public void setFirstElement(String first){
		firstElement = first;
	}
	
	public void setSecondElement(String second){
		secondElement = second;
	}
	
	public void setOperator(String oper){
		operator = oper;
	}
	
	public double getFirstNumber() {
		double d = 0;
		try {
			d = Double.parseDouble(firstElement);
		} catch (Exception e) {

		}
		return d;
	}

	public double getSecondNumber() {
		double d = 0;
		try {
			d = Double.parseDouble(secondElement);
		} catch (Exception e) {

		}
		return d;
	}
	
	public String getFirstElement() {
		return firstElement;
	}
	
	public String getSecondElement() {
		return secondElement;
	}
	
	public String toString(String logicoperator){
		
		return logicoperator+" "+firstElement+operator+"'"+secondElement+"' ";
	}

	public String toString(){
		
		return "or "+firstElement+operator+"'"+secondElement+"' ";
	}

}
