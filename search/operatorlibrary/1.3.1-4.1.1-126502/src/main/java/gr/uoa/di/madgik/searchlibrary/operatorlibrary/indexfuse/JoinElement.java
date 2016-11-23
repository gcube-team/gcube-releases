package gr.uoa.di.madgik.searchlibrary.operatorlibrary.indexfuse;

/**
 * Placeholder for the records that must be joined
 * 
 * @author UoA
 */
public class JoinElement {
	/**
	 * The ID of the object, this record refers to
	 */
	private String id;
	/**
	 * The rank of the object, this record refers to
	 */
	private double rank;
	/**
	 * The position in the file buffer - We don't use a buffer in this 
	 * implementation because we keep only 3 values(OID-ColID-rank) for each
	 * element and not the entire element
	 */
	private FilePosition pos;
	
	/**
	 * The sequence number of the collection this element belongs to
	 */
	private short collectionID=0;
	/**
	 * The sequence number of the metadata collection this element belongs to or 0 if it belongs to the content collection
	 */
	private short metaColID=0;
	/**
	 * Creates a new {@link JoinElement}
	 * 
	 * @param id The id of the object, this record refers to
	 * @param rank The rank of this record
	 * @param collectionID The sequence number of the collection this record comes from
	 * @param metaColID The sequence number of the metadata collection this record comes from(0 for content)
	 */
	public JoinElement(String id,double rank,short collectionID,short metaColID){
		this.id=id;
		this.rank=rank;
		this.collectionID=collectionID;
		this.metaColID=metaColID;
	}
	
	/**
	 * Retrieves the id for this element
	 * 
	 * @return The id
	 */
	public String getId(){
		return id;
	}

	/**
	 * Retrieves the rank of this element
	 * 
	 * @return The rank
	 */
	public double getRank(){
		return rank;
	}
	
	/**
	 * Retrieves the file position this record is stored at
	 * 
	 * @return The position
	 */
	public FilePosition getFilePosition(){
		return pos;
	}

	/**
	 * Retrieves the collection id of the record
	 * 
	 * @return The col id
	 */
	public short getCollectionID() {
		return collectionID;
	}
	
	/**
	 * Retrieves the metadata col id of the record
	 * 
	 * @return The metadata col id
	 */
	public short getMetaColID() {
		return metaColID;
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
