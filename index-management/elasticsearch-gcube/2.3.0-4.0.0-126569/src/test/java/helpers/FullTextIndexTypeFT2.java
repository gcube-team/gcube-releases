package helpers;

import org.gcube.indexmanagement.common.FullTextIndexType;


/**
 * Representation of an index'es IndexType and IndexFormat
 */
public class FullTextIndexTypeFT2 extends FullTextIndexType {
	private static final long serialVersionUID = 1L;

	public FullTextIndexTypeFT2(String indexTypeName, String scope) {
    	super(indexTypeName,scope);
    }
    
	@Override
    public String retrieveIndexTypeGenericResource(String scope) throws Exception {
    	return Utils.readFile("src/test/resources/ft2_indextype.xml");
    }
}
