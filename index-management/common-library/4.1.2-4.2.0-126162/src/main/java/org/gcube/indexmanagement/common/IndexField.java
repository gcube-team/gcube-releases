/*
 * IndexField.java
 *
 * $Author: tsakas $
 * $Date: 2007/12/20 14:37:39 $
 * $Id: IndexField.java,v 1.1 2007/12/20 14:37:39 tsakas Exp $
 *
 * <pre>
 *             Copyright (c) : 2006 Fast Search & Transfer ASA
 *                             ALL RIGHTS RESERVED
 * </pre>
 */

package org.gcube.indexmanagement.common;

import java.util.ArrayList;

/**
 * Represent the characteristics of a single field in a RowSet.
 * 
 * @see FullTextIndexType
 */
public class IndexField {

    /** The name of the field */
    public String name;
    
    /** The type of the field */
    public String type;


    /** An indicator of whether to index the field */
    public boolean index;

    /** An indicator of whether to tokenize the field */
    public boolean tokenize;

    /** An indicator of whether to store the field */
    public boolean store;

    /** An indicator of whether to return the field (in the results from a query) */
    public boolean returned;
    
    /** An indicator of whether to include or exclude the field in the snippets */
    public boolean highlightable;

    /** An indicator of whether to sort the field */
    public boolean sort;

    /** The boost value to use for this field during ranking */
    public float boost = 1.0f;

    /** The child fields of this field */
    @SuppressWarnings("rawtypes")
	public ArrayList childrenFields = null;

    /**
     * Empty constructor. The field attributes must be set in subsequent calls.
     */
    @SuppressWarnings("rawtypes")
	public IndexField() {
        this.childrenFields = new ArrayList();
    }

    /**
     * Constructor setting all field attributes.
     * 
     * @param name -
     *            The name of the new field
     * @param index -
     *            An indicator of whether to index the new field
     * @param tokenize -
     *            An indicator of whether to tokenize the new field
     * @param store -
     *            An indicator of whether to store the new field
     * @param returned -
     *            An indicator of whether to return the new field (in the
     *            results from a query)
     * @param sort -
     *            An indicator of whether to sort the new field
     * @param boost -
     *            The boost value to use for the new field during ranking
     */
    @SuppressWarnings("rawtypes")
	public IndexField(String name, String type, boolean index, boolean tokenize,
            boolean store, boolean returned, boolean highlightable, boolean sort, float boost) {
        this.name = name;
        this.type = type;
        this.index = index;
        this.tokenize = tokenize;
        this.store = store;
        this.returned = returned;
        this.highlightable = highlightable;
        this.sort = sort;
        this.boost = boost;
        this.childrenFields = new ArrayList();
    }

    /**
     * Adds a <code>IndexField</code> sub field to this field
     * 
     * @param field
     *            <code>IndexField</code> - the sub field to be added
     */
    @SuppressWarnings("unchecked")
	public void addChildField(IndexField field) {
        this.childrenFields.add(field);
    }

	@Override
	public String toString() {
		return "IndexField [name=" + name + ", type=" + type + ", index="
				+ index + ", tokenize=" + tokenize + ", store=" + store
				+ ", returned=" + returned + ", highlightable=" + highlightable
				+ ", sort=" + sort + ", boost=" + boost + ", childrenFields="
				+ childrenFields + "]";
	}

   
}
