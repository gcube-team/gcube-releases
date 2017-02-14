package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Initializator implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(Initializator.class);
	private static FetchDataThread speciesThread=new FetchDataThread();

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try{			
			logger.debug("Context destroyed, DB deletion process");
			for(String  scope: DBManager.getInitializedScopes()){
					try{
						DBManager.deleteDb(scope);
					}catch(Exception e){
						logger.warn("Unable to drop database under scope "+scope,e);
					}
			}
		}catch(Exception e){
			logger.error("Unexpected Exception ", e);
		}
		speciesThread.stop();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		logger.debug("Context Initialized, gonna start init thread.. ");		
		Thread t=new Thread(){
			@Override
			public void run() {
				try{
					Set<String> scopes=new HashSet<String>(Utils.getAvailableScopes());
					for(String scope: scopes){						
							try{
								DBManager.getInstance(scope.toString());
							}catch(Exception e){
								logger.warn("Unable to init databse under scope "+scope,e);
							}
						}
					speciesThread.start();
				}catch(Exception e){
					logger.error("Unexpected Exception ", e);
				}
			}
		};
		t.setName("AquaMapsPortletINIT");
		t.start();
	}
}