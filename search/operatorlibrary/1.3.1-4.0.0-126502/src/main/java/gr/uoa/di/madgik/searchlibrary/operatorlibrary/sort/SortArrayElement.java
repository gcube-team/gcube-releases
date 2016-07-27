package gr.uoa.di.madgik.searchlibrary.operatorlibrary.sort;

/**
 * Placeholder used during sorting operation
 * 
 * @author UoA
 */
public class SortArrayElement {
	/**
	 * The value extracted by the <code>record</code>
	 */
	public final String value;
	/**
	 * The record index
	 */
	public final long index;
	/**
	 * Creates a new <code>SortArrayElement</code> with the provided values
	 * 
	 * @param pos The index of the record
	 * @param value The value that was extracted by the provided <code>record</code>
	 */
	public SortArrayElement(long index, String value){
		this.index = index;
		this.value = value;
	}
}
