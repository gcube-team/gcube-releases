package org.gcube.portlets.widgets.netcdfbasicwidgets.client.model;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.NetCDFDataEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.NetCDFDataEvent.NetCDFDataEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SampleVariableDataEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SampleVariableDataEvent.SampleVariableDataEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.rpc.NetCDFBasicWidgetServiceAsync;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.Constants;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.AttributeData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class NetCDFDataModel {
	
	private String url;
	private NetCDFData netCDFData;
	private ArrayList<NetCDFDataEventHandler> handlers = new ArrayList<>();
	private ListDataProvider<VariableData> variableDataProvider = new ListDataProvider<>();
	private ListDataProvider<AttributeData> globalAttributeDataProvider = new ListDataProvider<>();

	
	public NetCDFDataModel(String url) {
		this.url=url;
		retrieveData();
	}

	public void addVariableDataDisplay(HasData<VariableData> display) {
		variableDataProvider.addDataDisplay(display);

	}

	public ListDataProvider<VariableData> getVariableDataProvider() {
		return variableDataProvider;
	}
	
	
	public void addGlobalAttributeDataDisplay(HasData<AttributeData> display) {
		globalAttributeDataProvider.addDataDisplay(display);

	}

	public ListDataProvider<AttributeData> getGlobalAttributeDataProvider() {
		return globalAttributeDataProvider;
	}
	
	
	/**
	 * Refresh all displays.
	 */
	public void refreshVariableDisplays() {
		variableDataProvider.refresh();
	}

	/**
	 * Refresh all displays.
	 */
	public void refreshGlobalAttributeDisplays() {
		globalAttributeDataProvider.refresh();
	}

	
	public void retrieveData() {
		NetCDFBasicWidgetServiceAsync.INSTANCE.getNetCDFFromPublicLink(url, new AsyncCallback<NetCDFData>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error retrieving NetCDFData: " + caught.getLocalizedMessage(), caught);

			}

			@Override
			public void onSuccess(NetCDFData ncData) {
				netCDFData = ncData;
				GWT.log("NetCDFData retrieved: " + netCDFData);
				
				List<VariableData> variables = variableDataProvider.getList();
				variables.addAll(ncData.getVariables());
				
				List<AttributeData> globalAttributes = globalAttributeDataProvider.getList();
				globalAttributes.addAll(ncData.getDetail().getGlobalAttributeDataList());
				
				fireNetCDFDataEvent();

			}
		});
	}

	public void close() {
		NetCDFBasicWidgetServiceAsync.INSTANCE.closeNetCDF(netCDFData.getNetCDFId(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error closing NetCDFData: " + caught.getLocalizedMessage(), caught);

			}

			@Override
			public void onSuccess(Void v) {
				GWT.log("NetCDFData closed");

			}
		});

	}

	public void fireNetCDFDataEvent() {
		NetCDFDataEvent event = new NetCDFDataEvent(netCDFData);
		for (NetCDFDataEventHandler handler : handlers) {
			handler.onNetCDFDataReady(event);
		}
	}

	public void addNetCDFDataEventHandler(NetCDFDataEventHandler handler) {
		handlers.add(handler);
	}

	public void retrieveSampleOfVariable(final SampleVariableDataEventHandler handler, VariableData variableData) {
		readSampleOfVariable(handler, variableData);
	}

	private void readSampleOfVariable(final SampleVariableDataEventHandler handler, final VariableData variableData) {
		NetCDFBasicWidgetServiceAsync.INSTANCE.readDataVariable(netCDFData.getNetCDFId(), variableData, true,
				Constants.SAMPLE_LENGHT, new AsyncCallback<NetCDFValues>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Error retrieving sample for Variable " + variableData.getFullName() + ": "
								+ caught.getLocalizedMessage(), caught);

					}

					@Override
					public void onSuccess(NetCDFValues result) {
						GWT.log("VariableData: " + variableData.getFullName());
						GWT.log("NetCDFValues: " + result);

						SampleVariableDataEvent event = new SampleVariableDataEvent(variableData, result);
						handler.onSample(event);

					}
				});

	}

	

}
