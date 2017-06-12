package org.gcube.portlets.user.td.mainboxwidget.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.gcube.portlets.user.td.gwtservice.server.SessionUtil;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.mainboxwidget.client.rpc.TabularDataService;
import org.gcube.portlets.user.tdwx.datasource.td.TDXDataSourceFactory;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXFactoryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
@SuppressWarnings("serial")
public class TabularDataServiceImpl extends RemoteServiceServlet implements
		TabularDataService {

	protected static Logger logger = LoggerFactory
			.getLogger(TabularDataServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();

		/*
		 * System.out.println("initializing the TabularDataImportManager");
		 * TabularDataImporterManager importerManager = new
		 * TabularDataImporterManager();
		 * //importerManager.scanAvailableImporters(); importerManager.add(new
		 * CSVTDImporter()); importerManager.setupImporters();
		 */
		// register the demo csv target
		// CSVTargetRegistry.getInstance().add(new DemoCSVTarget());
		// System.out.println("Registered DemoCSVTarget");

		// ScopeProvider.instance.get();
		System.out.println("Fix JAXP: jdk.xml.entityExpansionLimit=0");
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		System.out.println("initializing TDXDataSourceFactory");
		DataSourceXFactoryRegistry.getInstance()
				.add(new TDXDataSourceFactory());
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public String hello() throws Exception {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);
			logger.debug("hello()");

			return "Hello " + serviceCredentials.getUserName();
		} catch (Throwable e) {
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new Exception(e.getLocalizedMessage());
		}

	}
}
