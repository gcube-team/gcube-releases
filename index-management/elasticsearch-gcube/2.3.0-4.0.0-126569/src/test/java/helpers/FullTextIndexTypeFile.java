package helpers;

import org.gcube.indexmanagement.common.FullTextIndexType;


/**
 * Representation of an index'es IndexType and IndexFormat
 */
public class FullTextIndexTypeFile extends FullTextIndexType {
	private static final long serialVersionUID = 1L;

	public FullTextIndexTypeFile(String indexTypeName, String scope) {
    	super(indexTypeName,scope);
    }
    
	@Override
    public String retrieveIndexTypeGenericResource(String scope) throws Exception {
		return Utils.readFile("src/test/resources/file_index_type.xml");
    }
}
