package org.gcube.portlets.admin.fulltextindexportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A field used to represent a field of a Full Text Index Type
 * 
 */
public class FieldBean implements IsSerializable {

    /** The field name */
    private String name;

    /** An indication of whether the field should be indexed */
    private boolean index;

    /** An indication of whether the field should be stored */
    private boolean store;

    /** An indication of whether the field should be returned */
    private boolean returned;

    /** An indication of whether the field should be tokenized */
    private boolean tokenize;

    /** An indication of whether the field should be sorted */
    private boolean sort;

    /** The ranking boost to be used for this field */
    private String boost;

    /** An empty constructor */
    public FieldBean() {
    }

    /**
     * A method use to get the field's name
     * 
     * @return - the name of this field
     */
    public String getName() {
        return name;
    }

    /**
     * A method used to the indicator of whether the field should be indexed or
     * not
     * 
     * @return - true if the field should be indexed
     */
    public boolean getIndex() {
        return index;
    }

    /**
     * A method used to the indicator of whether the field should be stored or
     * not
     * 
     * @return - true if the field should be stored
     */
    public boolean getStore() {
        return store;
    }

    /**
     * A method used to the indicator of whether the field should be returned or
     * not
     * 
     * @return - true if the field should be returned
     */
    public boolean getReturned() {
        return returned;
    }

    /**
     * A method used to the indicator of whether the field should be tokenized
     * or not
     * 
     * @return - true if the field should be tokenized
     */
    public boolean getTokenize() {
        return tokenize;
    }

    /**
     * A method used to the indicator of whether the field should be sorted or
     * not
     * 
     * @return - true if the field should be sorted
     */
    public boolean getSort() {
        return sort;
    }

    /**
     * A method used to the ranking boost to be used for this field
     * 
     * @return - the ranking boost to be used for this field
     */
    public String getBoost() {
        return boost;
    }

    /**
     * Sets the name of this field
     * 
     * @param name -
     *            the name of the field
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the indicator if whether this field should be indexed
     * 
     * @param index -
     *            true if the field should be indexed
     */
    public void setIndex(boolean index) {
        this.index = index;
    }

    /**
     * Sets the indicator if whether this field should be stored
     * 
     * @param store -
     *            true if the field should be stored
     */
    public void setStore(boolean store) {
        this.store = store;
    }

    /**
     * Sets the indicator if whether this field should be returned
     * 
     * @param returned -
     *            true if the field should be returned
     */
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /**
     * Sets the indicator if whether this field should be tokenized
     * 
     * @param tokenize -
     *            true if the field should be tokenized
     */
    public void setTokenize(boolean tokenize) {
        this.tokenize = tokenize;
    }

    /**
     * Sets the indicator if whether this field should be sorted
     * 
     * @param sort -
     *            true if the field should be sorted
     */
    public void setSort(boolean sort) {
        this.sort = sort;
    }

    /**
     * Sets the ranking boost to be used for this field
     * 
     * @param boost -
     *            the ranking boost to be used for this field
     */
    public void setBoost(String boost) {
        this.boost = boost;
    }
}
