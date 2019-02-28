/**
 * 
 */
package org.gcube.common.homelibrary.consistency.processor;

import java.util.List;

import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class HomeManagerFactoryProcessor extends AbstractProcessor<HomeManagerFactory, HomeManager>{
	
	protected Logger  logger = LoggerFactory.getLogger(HomeManagerFactoryProcessor.class);



	@Override
	public void process(HomeManagerFactory input) throws Exception {
		List<String> scopes = input.listScopes();
		for (String scope:scopes){
			logger.trace("processing "+scope);
			HomeManager manager = input.getHomeManager();
			subProcess(manager);
		}
	}
}