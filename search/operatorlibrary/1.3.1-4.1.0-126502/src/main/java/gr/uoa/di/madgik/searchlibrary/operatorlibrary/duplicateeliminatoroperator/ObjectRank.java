/**
 * 
 */
package gr.uoa.di.madgik.searchlibrary.operatorlibrary.duplicateeliminatoroperator;

/**
 * A simple pair of OID (DocID) and its rank
 * 
 * @author paul
 *
 */
public class ObjectRank implements Comparable<ObjectRank> {

	/** object ID (DocID) **/
	public String objID;
	/** object rank **/
	public Double rank;
	
	/**
	 * Returns true if both objects match; false otherwise.
	 * @return true if both objects match; false otherwise.
	 * @param o another object
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof ObjectRank)
			if(((ObjectRank)o).objID.equals(this.objID) && ((ObjectRank)o).rank.equals(this.rank))
				return true;
		return false;
	}
	
	/**
	 * Returns the object's hash code
	 * @return object's hash code
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		long bits = Double.doubleToLongBits(rank);
		int rank_code = (int)(bits ^ (bits >>> 32));
		hash = 31 * hash + rank_code;
		hash = 31 * hash + objID.hashCode();
		return hash;
	}

	/**
	 * Returns 0 if both objects match, -1 if this object is less that the other object, +1 otherwise
	 * @return 0 if both objects match, -1 if this object is less that the other object, +1 otherwise
	 */
	public int compareTo(ObjectRank o) {
		int c1 = this.objID.compareTo(o.objID);
		if(c1 != 0)
			return c1;
		return this.rank.compareTo(o.rank);
	}
	
	/**
	 * String representation
	 * @return string representation
	 */
	@Override
	public String toString() {
		return this.objID + ": " + this.rank;
	}
	
}
