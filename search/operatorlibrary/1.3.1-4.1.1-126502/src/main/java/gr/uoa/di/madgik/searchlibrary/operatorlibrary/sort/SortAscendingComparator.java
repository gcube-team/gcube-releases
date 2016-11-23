package gr.uoa.di.madgik.searchlibrary.operatorlibrary.sort;

import gr.uoa.di.madgik.searchlibrary.operatorlibrary.comparator.*;

import java.util.Comparator;

/**
 * An implementation of the {@link Comparator} interface that sorts in ascending order 
 * 
 * @author UoA
 */
public class SortAscendingComparator implements Comparator{
	
	private ComparisonMode mode;
	
	public SortAscendingComparator() { 
		mode = null;
	}
	
	public SortAscendingComparator(ComparisonMode mode) {
		this.mode = mode;
	}
	
	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * @param o1 the left operant
	 * @param o2 the right operant
	 * @return The result of the comparison
	 */
	public int compare(Object o1, Object o2){
		if(!(o1 instanceof SortArrayElement)) throw new ClassCastException();
	    if(!(o2 instanceof SortArrayElement)) throw new ClassCastException();
	    try{
	    	if(mode != null) {
	    		int cmp = CompareTokens.compare(((SortArrayElement)o1).value, ((SortArrayElement)o2).value, mode);
		    	if(cmp == CompareTokens.COMPARE_GREATER) 
		    		return 1;
		    	if(cmp == CompareTokens.COMPARE_LOWER) 
		    		return -1;
		    	return 0;
	    	}
	    	else {
	    		int cmp = CompareTokens.compare(((SortArrayElement)o1).value, ((SortArrayElement)o2).value);
		    	if(cmp == CompareTokens.COMPARE_GREATER) 
		    		return 1;
		    	if(cmp == CompareTokens.COMPARE_LOWER) 
		    		return -1;
		    	return 0;
	    	}
	    }catch(Exception e){
	    	throw new ClassCastException("Invalid Token Format Comparison");
	    }
	  }
	
	 /**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @param o The object to check for equality
	 * @return the result of the comparison
	 */
	public boolean equals(Object o){
	    if(!(o instanceof SortAscendingComparator))
	        return false;
	    else return true;
	  }
}
