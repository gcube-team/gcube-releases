/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gcube.portlets.admin.gcubereleases.server.exception.DatabaseServiceException;
import org.gcube.portlets.admin.gcubereleases.shared.JavadocHtmlUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JavadocUriPersistence.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class JavadocUriPersistence extends AbstractPersistence<JavadocHtmlUri>{
	
	protected static Logger logger = LoggerFactory.getLogger(JavadocHtmlUri.class);
	public static final String tableName = "JavadocHtmlUri";
	
	
	/**
	 * Instantiates a new javadoc uri persistence.
	 *
	 * @param factory the factory
	 */
	public JavadocUriPersistence(EntityManagerFactory factory) {
		super(factory, tableName);
		
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.persistence.AbstractPersistence#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public Root<JavadocHtmlUri> rootFrom(CriteriaQuery<Object> cq){
		return cq.from(JavadocHtmlUri.class);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.server.persistence.AbstractPersistence#deleteItemByIdField(java.lang.String)
	 */
	@Override
	public int deleteItemByIdField(String idField)
			throws DatabaseServiceException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.server.persistence.AbstractPersistence#removeAllReleations()
	 */
	@Override
	public int removeAllReleations() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.server.persistence.AbstractPersistence#removeReleation(java.lang.Object)
	 */
	@Override
	public int removeRelations(JavadocHtmlUri item) {
		// TODO Auto-generated method stub
		return 0;
	}

}
