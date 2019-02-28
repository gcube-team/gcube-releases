package org.gcube.portal.wssynclibrary.thredds;

import org.gcube.portal.wssynclibrary.DoCheckSyncItem;
import org.gcube.portal.wssynclibrary.DoConnectRepository;
import org.gcube.portal.wssynclibrary.DoSyncItem;


// TODO: Auto-generated Javadoc
/**
 * The Interface WorkspaceThreddsSynchronizedRepository.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 14, 2018
 * @param <O> the generic type
 * @param <T> the generic type
 */
public interface WorkspaceThreddsSynchronizedRepository<O, T> extends DoSyncItem<O>, DoCheckSyncItem<T>, DoConnectRepository {
	

	/**
	 * Decribe sync repository.
	 *
	 * @return the string
	 */
	String decribeSyncRepository();
	
	
}
