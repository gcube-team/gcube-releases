/**
 *
 */
package org.gcube.portlets.user.gcubegisviewer.client;

import java.util.List;

import org.gcube.portlets.user.gcubegisviewer.client.event.HasSaveHandlers;
import org.gcube.portlets.user.gcubegisviewer.client.event.SaveEvent;
import org.gcube.portlets.user.gcubegisviewer.client.event.SaveHandler;
import org.gcube.portlets.user.gisviewer.client.GisViewer;
import org.gcube.portlets.user.gisviewer.client.GisViewerParameters;
import org.gcube.portlets.user.gisviewer.client.GisViewerSaveHandler;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class GCubeGisViewer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 26, 2016
 */
public class GCubeGisViewer extends GisViewer implements HasSaveHandlers {

	private GCubeGisViewer INSTANCE = this;

	/**
	 * Instantiates a new g cube gis viewer.
	 *
	 * @param parameters the parameters
	 */
	public GCubeGisViewer(GisViewerParameters parameters) {
		this(parameters.getProjection(), parameters.getGisViewerSaveHandler()==null?new GCubeGisViewerSaveHandler():parameters.getGisViewerSaveHandler());
	}

	/**
	 * Instantiates a new g cube gis viewer.
	 *
	 * @param projection the projection
	 * @param gisViewerSaveHandler the gis viewer save handler
	 */
	protected GCubeGisViewer(String projection, GisViewerSaveHandler gisViewerSaveHandler)
	{
		super(new GisViewerParameters(projection, gisViewerSaveHandler));
		if (gisViewerSaveHandler instanceof GCubeGisViewerSaveHandler) {
			((GCubeGisViewerSaveHandler)gisViewerSaveHandler).setHasSaveHandlers(this);
		}

		GCubeGisViewerSaveHandler.service.getBaseLayersToGisViewer(new AsyncCallback<List<? extends GisViewerBaseLayerInterface>>() {

			@Override
			public void onSuccess(List<? extends GisViewerBaseLayerInterface> layers) {
				INSTANCE.addBaseLayersToOLM(layers);

			}

			@Override
			public void onFailure(Throwable arg0) {
				GWT.log("An error occurred when adding base layers to OLM");
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gcubegisviewer.client.event.HasSaveHandlers#addSaveHandler(org.gcube.portlets.user.gcubegisviewer.client.event.SaveHandler)
	 */
	@Override
	public HandlerRegistration addSaveHandler(SaveHandler handler) {
		return addHandler(handler, SaveEvent.getType());
	}

	/**
	 * Sets the mask on save events.
	 */
	public void setMaskOnSaveEvents()
	{
		addSaveHandler(new SaveHandler() {

			@Override
			public void onSaveSuccess(SaveEvent event) {
				unmask();
			}

			@Override
			public void onSaveFailure(SaveEvent event) {
				unmask();
				com.google.gwt.user.client.Window.alert("An error occured saving "+event.getName()+" in the workspace...");
			}

			@Override
			public void onSave(SaveEvent event) {
				mask("Saving "+event.getName()+" in the workspace...");
			}
		});
	}

}
