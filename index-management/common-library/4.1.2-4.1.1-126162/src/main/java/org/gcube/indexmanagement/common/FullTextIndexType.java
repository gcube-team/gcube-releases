/*
 * State.java
 *
 * $Author: tsakas $
 * $Date: 2007/12/20 14:37:39 $
 * $Id: FullTextIndexType.java,v 1.1 2007/12/20 14:37:39 tsakas Exp $
 *
 * <pre>
 *             Copyright (c) : 2006 Fast Search & Transfer ASA
 *                             ALL RIGHTS RESERVED
 * </pre>
 */

package org.gcube.indexmanagement.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representation of an index'es IndexType and IndexFormat
 */
public class FullTextIndexType extends IndexType {
	private static final long serialVersionUID = 1L;

	/** logger */
	private static final Logger logger = LoggerFactory.getLogger(FullTextIndexType.class);
	

    /** An array of the fields contained in the IndexType */
    private List<IndexField> fields;

    /**
     * The CSTR of the index type
     * 
     * @param indexTypeName -
     *            The index type name.
     */
    
    
    public FullTextIndexType(String indexTypeName, String scope) {
    	
    	super(indexTypeName);
    	
        try {
        	String indexType = retrieveIndexTypeGenericResource(scope);
            fields = IndexTypeParser.readAllFields(indexType);
            
        } catch (Exception ex) {
        	logger.error("Error initializing FullTextIndexType.", ex);
        }
    }

    public List<IndexField> getFields() {
    	return fields;
    }


    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer ret = new StringBuffer("\nID=" + indexTypeID
                + "\n\nFields:\n");

        for (IndexField field : fields) {
            ret.append(field + "\n");
        }
        return ret.toString();
    }
}
