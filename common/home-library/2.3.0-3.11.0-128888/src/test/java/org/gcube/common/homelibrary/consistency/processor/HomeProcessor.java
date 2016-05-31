/**
 * 
 */
package org.gcube.common.homelibrary.consistency.processor;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.workspace.Workspace;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class HomeProcessor extends AbstractProcessor<Home, Workspace>{

	@Override
	public void process(Home home) throws Exception {
		Workspace wa = home.getWorkspace();
		subProcess(wa);			
	}
	
}