/*
 * LuceneSearcher.java
 *
 * $Author: tsakas $
 * $Date: 2007/12/20 14:37:39 $
 * $Id: LuceneSearcher.java,v 1.1 2007/12/20 14:37:39 tsakas Exp $
 *
 * <pre>
 *             Copyright (c) : 2006 Fast Search & Transfer ASA
 *                             ALL RIGHTS RESERVED
 * </pre>
 */

package org.gcube.indexmanagement.lucenewrapper;


/**
 * An IndexSerch implementation used to search a Lucene index.
 */
public class LuceneSearcher  {
	
	public enum SupportedRelations { adj, fuzzy, proximity, within , lt, le, gt, ge , eq}
	
	//'=' is also a supported relation
	public static final String EQUALS = "=";
	
	public static final String ALL_INDEXES = "allIndexes";

	
}
