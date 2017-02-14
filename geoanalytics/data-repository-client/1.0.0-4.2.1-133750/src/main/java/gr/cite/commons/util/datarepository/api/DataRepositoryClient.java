package gr.cite.commons.util.datarepository.api;

import gr.cite.commons.util.datarepository.DataRepository;

public interface DataRepositoryClient extends DataRepository {

	/**
	 * 
	 * @param base64URI
	 *            eg: data:image/png;base64,iVBORw0KGg...
	 * @return the uri that represents the image
	 */
	public String insertBase64File(String base64URI);
	
	/**
	 * 
	 * @param input stream
	 *            eg: data:image/png;base64,iVBORw0KGg...
	 * @return the uri that represents the input stream
	 */
	public String insertBytes(byte[] inputBytes, String DataType);

	/**
	 * 
	 * @param imageId
	 * @return base64 URI (eg: data:image/png;base64,iVBORw0KGg...)
	 */
	public String getFileInBase64URI(String imageId);
	
	/**
	 * 
	 * @param fileUrl
	 *            eg: data:image/png;base64,iVBORw0KGg...
	 * @return the uri that represents the image
	 */
	public String insertFileFromUrl(String fileUrl);
	
	/**
	 * 
	 * @param imageId
	 * @return data repository image URL
	 */
	public String getFileUrl(String fileId);
	
	/**
	 * 
	 * @param imageId
	 * @return nothing
	 */
	public void removeFile(String fileId);
}
