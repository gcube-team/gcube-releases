package org.gcube.application.perform.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.ws.rs.core.Application;

import org.gcube.application.perform.service.engine.impl.ImporterImpl;
import org.gcube.application.perform.service.engine.impl.PerformanceManagerImpl;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.utils.ISUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;

public class CommonTest extends JerseyTest{

	
	@BeforeClass
	public static void init() throws IOException, SQLException, InternalException {
		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());
		TokenSetter.set("/gcube/preprod/preVRE");
		ISUtils.setFixedToken(SecurityTokenProvider.instance.get());
		PerformServiceLifecycleManager.initSchema("src/main/webapp/WEB-INF");
		
		
		new ImporterImpl().init();
		PerformanceManagerImpl.initDatabase();
	}
	
	
	@Override
	protected Application configure() {		

		return new PerformService();
	}

}
