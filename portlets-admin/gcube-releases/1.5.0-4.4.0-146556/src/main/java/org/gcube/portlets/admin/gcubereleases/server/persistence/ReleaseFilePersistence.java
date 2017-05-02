/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gcube.portlets.admin.gcubereleases.server.exception.DatabaseServiceException;
import org.gcube.portlets.admin.gcubereleases.shared.ReleaseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JavadocUriPersistence.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ReleaseFilePersistence extends AbstractPersistence<ReleaseFile>{
	
	protected static Logger logger = LoggerFactory.getLogger(ReleaseFile.class);
	public static final String tableName = ReleaseFile.class.getSimpleName();
	
	
	/**
	 * Instantiates a new javadoc uri persistence.
	 *
	 * @param factory the factory
	 */
	public ReleaseFilePersistence(EntityManagerFactory factory) {
		super(factory, tableName);
		
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.persistence.AbstractPersistence#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public Root<ReleaseFile> rootFrom(CriteriaQuery<Object> cq){
		return cq.from(ReleaseFile.class);
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
	public int removeRelations(ReleaseFile item) {
		// TODO Auto-generated method stub
		return 0;
	}

}
