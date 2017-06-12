/**
 * AphiaNameServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Nov 16, 2004 (12:19:44 EST) WSDL2Java emitter.
 */

package aphia.v1_0.wordss;

public interface AphiaNameServicePortType extends java.rmi.Remote {

    /**
     * <strong>Get the (first) exact matching AphiaID for a given
     * name.<br/>Parameters:
     *    <ul>
     *     <li><u>marine_only</u>: limit to marine taxa. Default=true.</li>
     * </ul>
     *   </strong>
     */
    public int getAphiaID(java.lang.String scientificname, boolean marine_only) throws java.rmi.RemoteException;

    /**
     * <strong>Get one or more matching (max. 50) AphiaRecords for
     * a given name.<br/>Parameters:
     *    <ul>
     *     <li><u>like</u>: add a '%'-sign added after the ScientificName
     * (SQL LIKE function). Default=true.</li>
     * 	<li><u>fuzzy</u>: fuzzy matching. Default=true.</li>
     * 	<li><u>marine_only</u>: limit to marine taxa. Default=true.</li>
     * 	<li><u>offset</u>: starting recordnumber, when retrieving next chunck
     * of (50) records. Default=1.</li>
     *    </ul>
     *   </strong>
     */
    public aphia.v1_0.wordss.AphiaRecord[] getAphiaRecords(java.lang.String scientificname, boolean like, boolean fuzzy, boolean marine_only, int offset) throws java.rmi.RemoteException;

    /**
     * <strong>Get the correct name for a given AphiaID</strong>.
     */
    public java.lang.String getAphiaNameByID(int aphiaID) throws java.rmi.RemoteException;

    /**
     * <strong>Get the complete Aphia Record for a given AphiaID.</strong>
     */
    public aphia.v1_0.wordss.AphiaRecord getAphiaRecordByID(int aphiaID) throws java.rmi.RemoteException;

    /**
     * <strong>Get the Aphia Record for a given TSN (ITIS Taxonomic
     * Serial Number).</strong>
     */
    public aphia.v1_0.wordss.AphiaRecord getAphiaRecordByTSN(int TSN) throws java.rmi.RemoteException;

    /**
     * <strong>For each given scientific name, try to find one or
     * more AphiaRecords.<br/>
     *   This allows you to match multiple names in one call. Limited to
     * 500 names at once for performance reasons.
     *   <br/>Parameters:
     *    <ul>
     *     <li><u>like</u>: add a '%'-sign after the ScientificName (SQL
     * LIKE function). Default=false.</li>
     * 	<li><u>fuzzy</u>: fuzzy matching. Default=true.</li>
     * 	<li><u>marine_only</u>: limit to marine taxa. Default=true.</li>
     *    </ul></strong>
     */
    public aphia.v1_0.wordss.AphiaRecord[][] getAphiaRecordsByNames(java.lang.String[] scientificnames, boolean like, boolean fuzzy, boolean marine_only) throws java.rmi.RemoteException;

    /**
     * <strong>Get one or more Aphia Records (max. 50) for a given
     * vernacular.</strong><br/>Parameters:
     *    <ul>
     *     <li><u>like</u>: add a '%'-sign before and after the input (SQL
     * LIKE '%vernacular%' function). Default=false.</li>
     * 	<li><u>offset</u>: starting record number, when retrieving next chunck
     * of (50) records. Default=1.</li>
     *    </ul>
     *   </strong>
     */
    public aphia.v1_0.wordss.AphiaRecord[] getAphiaRecordsByVernacular(java.lang.String vernacular, boolean like, int offset) throws java.rmi.RemoteException;

    /**
     * <strong>Get the complete classification for one taxon. This
     * also includes any sub or super ranks.</strong>
     */
    public aphia.v1_0.wordss.Classification getAphiaClassificationByID(int aphiaID) throws java.rmi.RemoteException;

    /**
     * <strong>Get one or more sources/references including links,
     * for one AphiaID</strong>
     */
    public aphia.v1_0.wordss.Source[] getSourcesByAphiaID(int aphiaID) throws java.rmi.RemoteException;

    /**
     * <strong>Get all synonyms for a given AphiaID.</strong>
     */
    public aphia.v1_0.wordss.AphiaRecord[] getAphiaSynonymsByID(int aphiaID) throws java.rmi.RemoteException;

    /**
     * <strong>Get all vernaculars for a given AphiaID.</strong>
     */
    public aphia.v1_0.wordss.Vernacular[] getAphiaVernacularsByID(int aphiaID) throws java.rmi.RemoteException;

    /**
     * <strong>Get the direct children (max. 50) for a given AphiaID.</strong><br
     * />Parameters:
     *    <ul>
     * 	<li><u>offset</u>: starting record number, when retrieving next chunck
     * of (50) records. Default=1.</li>
     *    </ul>
     */
    public aphia.v1_0.wordss.AphiaRecord[] getAphiaChildrenByID(int aphiaID, int offset) throws java.rmi.RemoteException;
}
