package org.gcube.portal.wssynclibrary;


// TODO: Auto-generated Javadoc
/**
 * The Interface DoConnectRepository.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 14, 2018
 */
public interface DoConnectRepository {
	
	/**
	 * Inits the repository.
	 *
	 * @return true, if successful
	 */
	Boolean initRepository();

	/**
	 * Shutdown repository.
	 *
	 * @return true, if successful
	 */
	Boolean shutDownRepository();

}
