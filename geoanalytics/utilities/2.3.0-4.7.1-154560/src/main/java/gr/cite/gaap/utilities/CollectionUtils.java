package gr.cite.gaap.utilities;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class CollectionUtils {

	public static <T> boolean comparatorEquals(Collection<T> col1, Collection<T> col2, Comparator<T> c) {
	    if (col1 == null)
	        return col2 == null;
	    if (col2 == null) 
	        return false;

	    if (col1.size() != col2.size())
	        return false;

	    Iterator<T> i1 = col1.iterator(), i2 = col2.iterator();

	    while(i1.hasNext() && i2.hasNext()) {
	        if (c.compare(i1.next(), i2.next()) != 0) {
	            return false;
	        }
	    }

	    return true;
	}
}
