package org.gcube.common.dbinterface.conditions;

public class IntArray implements Listable{

	private int[] list = null ;
	private Integer[] listEncapsulator = null;
	
	public IntArray(int ... items){
		list= items;
	}
	
	public IntArray(Integer ... items){
		listEncapsulator= items;
	}
	
	@Override
	public String asStringList() {
		StringBuffer toReturn = new StringBuffer();
		if (list!=null){
			for (int item: this.list)
				toReturn.append(item+",");
		}else {
			for (Integer item: this.listEncapsulator)
				toReturn.append(item+",");
		}
		return toReturn.substring(0, toReturn.length()-1);
	}

}
