package org.gcube.contentmanagement.blobstorage.coding;

import java.io.InputStream;
import java.util.List;

/**
 * Interface for coding a generic File in bytes, or base64 code Used for terrastore system
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public interface IEncode {
	/**
	 * Encode a generic file in byte array
	 * @param path file's path
	 * @param isChunk indicates if the file is chuncked
	 * @param isBase64 the base64 coding option
	 * @return a byte array
	 */
	public byte[] encodeGenericFile(String path, boolean isChunk, boolean isBase64);

	/**
	 * Reads a file storing intermediate data into a list. Fast method.
	 * @param path
	 * @param isChunk
	 * @param chunkDimension
	 * @return the list that contains the file 
	 */
	public List<String> encodeFileChunked(String path, boolean isChunk, int chunkDimension);
	
	/**
	 * Reads a file storing intermediate data into an array.
	 * @param in
	 * @param path
	 * @param isChunk
	 * @param chunkDimension
	 * @return the byte array that contains the file
	 */
	public byte[] encodeFileChunked2(InputStream in, String path, boolean isChunk, long chunkDimension);
	
	/**
	 * Decode a byte array in a File
	 * @param encode
	 * @param path
	 * @param isBase64
	 */
	public void decodeByte2File(byte[] encode, String path, boolean isBase64);

}
