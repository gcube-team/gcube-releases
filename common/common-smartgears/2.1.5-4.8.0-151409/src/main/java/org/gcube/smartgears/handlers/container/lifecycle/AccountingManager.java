/**
 * 
 */
package org.gcube.smartgears.handlers.container.lifecycle;

import static org.gcube.smartgears.Constants.accounting_management;

import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.smartgears.handlers.container.ContainerHandler;
import org.gcube.smartgears.handlers.container.ContainerLifecycleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@XmlRootElement(name = accounting_management)
public class AccountingManager extends ContainerHandler {
	
	private static Logger logger = LoggerFactory.getLogger(AccountingManager.class);
	
	@Override
	public void onStop(ContainerLifecycleEvent.Stop e) {
		logger.trace("Going to flush accounting data");
		AccountingPersistenceFactory.shutDown(1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public String toString() {
		return accounting_management;
	}
}
