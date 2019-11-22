package org.gcube.portlets.widgets.netcdfbasicwidgets.server;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;

import org.gcube.portlets.widgets.netcdfbasicwidgets.client.rpc.NetCDFBasicWidgetService;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.NetCDFResource;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.util.ServiceCredentials;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.exception.ServiceException;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFId;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.session.UserInfo;
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
public class NetCDFBasicWidgetServiceImpl extends RemoteServiceServlet implements NetCDFBasicWidgetService {

	private static Logger logger = LoggerFactory.getLogger(NetCDFBasicWidgetServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("NetCDFBasicWidgetService started!");

	}

	@Override
	public UserInfo hello() throws ServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("hello()");
			UserInfo userInfo = new UserInfo(serviceCredentials.getUserName(), serviceCredentials.getGroupId(),
					serviceCredentials.getGroupName(), serviceCredentials.getScope(), serviceCredentials.getEmail(),
					serviceCredentials.getFullName());
			logger.debug("UserInfo: " + userInfo);
			return userInfo;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("Error in hello(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public NetCDFData getNetCDFFromPublicLink(String publicLink) throws ServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(this.getThreadLocalRequest());
			URL url;
			try {
				url = new URL(publicLink);
			} catch (MalformedURLException e) {
				logger.error(e.getLocalizedMessage(), e);
				throw new ServiceException(e.getLocalizedMessage(), e);
			}
	
			NetCDFResource netCDFResource = new NetCDFResource(url);
			NetCDFData netCDFData = netCDFResource.exploreNetCDF();

			SessionUtil.setNetCDFData(netCDFData, this.getThreadLocalRequest(), serviceCredentials);
			return netCDFData;
		} catch (ServiceException e) {
			logger.error("Error in getCDFResourceFromPublicLink(): " + e.getLocalizedMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Error in getCDFResourceFromPublicLink(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}


	@Override
	public NetCDFValues readDataVariable(NetCDFId netCDFId, VariableData variableData, boolean sample, int limit)
			throws ServiceException {
		try {
			ServiceCredentials serviceCredentials=SessionUtil.getServiceCredentials(this.getThreadLocalRequest());			
			SessionUtil.getNetCDFData(netCDFId, this.getThreadLocalRequest(), serviceCredentials);
			
			NetCDFResource netCDFResource = new NetCDFResource(netCDFId);
			NetCDFValues netCDFValues = netCDFResource.readDataVariable(variableData, sample, limit);

			return netCDFValues;
		} catch (ServiceException e) {
			logger.error("Error in getCDFResourceFromItem(): " + e.getLocalizedMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Error in getCDFResourceFromItem(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void closeNetCDF(NetCDFId netCDFId) throws ServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(this.getThreadLocalRequest());
			SessionUtil.getNetCDFData(netCDFId, this.getThreadLocalRequest(),
					serviceCredentials);
			NetCDFResource netCDFResource = new NetCDFResource(netCDFId);
			netCDFResource.close();
			SessionUtil.removeNetCDFData(netCDFId, this.getThreadLocalRequest(), serviceCredentials);
		} catch (ServiceException e) {
			logger.error("Error in closeNetCDF(): " + e.getLocalizedMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Error in closeNetCDF(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

}