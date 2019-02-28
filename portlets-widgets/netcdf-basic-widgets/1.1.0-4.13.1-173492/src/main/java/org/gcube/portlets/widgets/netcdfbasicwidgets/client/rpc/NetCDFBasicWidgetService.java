package org.gcube.portlets.widgets.netcdfbasicwidgets.client.rpc;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.exception.ServiceException;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFId;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.session.UserInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
@RemoteServiceRelativePath("netcdfservice")
public interface NetCDFBasicWidgetService extends RemoteService {

	public UserInfo hello() throws ServiceException;

	public NetCDFData getNetCDFFromPublicLink(String publicLink) throws ServiceException;

	public void closeNetCDF(NetCDFId netCDFId) throws ServiceException;

	public NetCDFValues readDataVariable(NetCDFId netCDFId, VariableData variableData, boolean sample, int limit) throws ServiceException;

	
}
