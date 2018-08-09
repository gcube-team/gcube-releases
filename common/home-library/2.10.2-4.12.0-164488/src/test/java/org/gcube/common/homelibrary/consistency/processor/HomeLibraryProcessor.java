/**
 * 
 */
package org.gcube.common.homelibrary.consistency.processor;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManagerFactory;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class HomeLibraryProcessor extends AbstractProcessor<String, HomeManagerFactory>{

	@Override
	public void process(String rootDir) throws Exception
	{
		HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory(rootDir);
		subProcess(factory);
		factory.shutdown();
	}

}
