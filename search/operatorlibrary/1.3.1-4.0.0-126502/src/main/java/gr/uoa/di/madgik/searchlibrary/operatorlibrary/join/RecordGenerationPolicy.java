package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

public enum RecordGenerationPolicy {
	/**
	 * The normal join operation policy. Each record produced by the operation will consist of the concatenation
	 * of the fields of the left input with those of the right input excluding the join key of the right input
	 * (that is, the join key of the left input is kept)
	 */
	Concatenate,
	/**
	 * Only the fields of the left input are kept. Useful if the intention of the client is to simulate the intersection
	 * operation. Note that an additional duplicate elimination step is necessary in order to obtain the proper intersection
	 * of the two inputs
	 */
	KeepLeft,
	/**
	 * Similar to the {@link RecordCreationPolicy#KeepLeft} option. Included for completeness
	 */
	KeepRight
}
