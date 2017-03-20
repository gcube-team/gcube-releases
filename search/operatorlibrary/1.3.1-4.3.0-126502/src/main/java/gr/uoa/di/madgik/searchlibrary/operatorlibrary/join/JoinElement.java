package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

import java.util.Vector;

/**
 * Placeholder for the records that must be joined
 * 
 * @author UoA
 */
public class JoinElement {
	/**
	 * The key of the record
	 */
	private HashKey value;
	/**
	 * The positions of the records
	 */
	private Vector<Long> pos;
	
	/**
	 * The input it belongs to
	 */
	private short collectionID=0;
	/**
	 * Creates a new {@link JoinElement}
	 * 
	 * @param pos The indices of the stored records
	 * @param value The vale of the key to use
	 * @param collectionID The id of the input this record comes from
	 */
	public JoinElement(Vector<Long> pos,HashKey value,short collectionID){
		this.pos=pos;
		this.value=value;
		this.collectionID=collectionID;
	}
	
	/**
	 * Retrieves the value of the element
	 * 
	 * @return The value
	 */
	public HashKey getValue(){
		return value;
	}

	/**
	 * Retrieves the record indices of the stored records
	 * 
	 * @return The position
	 */
	public Vector<Long> getRecordIndices(){
		return pos;
	}

	/**
	 * Retrieves the input id of the record
	 * 
	 * @return The input id
	 */
	public short getCollectionID() {
		return collectionID;
	}

	/**
	 * Sets the input id
	 * 
	 * @param collectionID The input id
	 */
	public void setCollectionID(short collectionID) {
		this.collectionID = collectionID;
	}
}
