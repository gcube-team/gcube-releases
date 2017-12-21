package org.gcube.portlets.widgets.netcdfbasicwidgets.client.rpc;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFId;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.session.UserInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface NetCDFBasicWidgetServiceAsync {

	public static NetCDFBasicWidgetServiceAsync INSTANCE = (NetCDFBasicWidgetServiceAsync) GWT
			.create(NetCDFBasicWidgetService.class);

	void hello(AsyncCallback<UserInfo> callback);

	void getNetCDFFromPublicLink(String publicLink, AsyncCallback<NetCDFData> callback);

	void closeNetCDF(NetCDFId netCDFId, AsyncCallback<Void> callback);

	void readDataVariable(NetCDFId netCDFId, VariableData variableData, boolean sample, int limit,
			AsyncCallback<NetCDFValues> callback);

}
