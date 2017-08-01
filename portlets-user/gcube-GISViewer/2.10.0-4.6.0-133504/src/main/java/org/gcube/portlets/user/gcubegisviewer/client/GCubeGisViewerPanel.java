/**
 *
 */
package org.gcube.portlets.user.gcubegisviewer.client;

import java.util.List;

import org.gcube.portlets.user.gcubegisviewer.client.event.HasSaveHandlers;
import org.gcube.portlets.user.gcubegisviewer.client.event.SaveEvent;
import org.gcube.portlets.user.gcubegisviewer.client.event.SaveHandler;
import org.gcube.portlets.user.gisviewer.client.GisViewerPanel;
import org.gcube.portlets.user.gisviewer.client.GisViewerParameters;
import org.gcube.portlets.user.gisviewer.client.GisViewerSaveHandler;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author ceras
 *
 */
public class GCubeGisViewerPanel extends GisViewerPanel implements HasSaveHandlers {

	private GCubeGisViewerPanel INSTANCE = this;

	public GCubeGisViewerPanel() {
		this(new GisViewerParameters());
	}

	public GCubeGisViewerPanel(GisViewerParameters parameters) {
		this(parameters, parameters.getGisViewerSaveHandler()==null?new GCubeGisViewerSaveHandler():parameters.getGisViewerSaveHandler());

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

	/**
	 * @param parameters
	 * @param i
	 */
	public GCubeGisViewerPanel(GisViewerParameters parameters, GisViewerSaveHandler gisViewerSaveHandler) {
		super(parameters, gisViewerSaveHandler);

		if (gisViewerSaveHandler instanceof GCubeGisViewerSaveHandler) {
			((GCubeGisViewerSaveHandler)gisViewerSaveHandler).setHasSaveHandlers(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gcubegisviewer.client.event.HasSaveHandlers#addSaveHandler(org.gcube.portlets.user.gcubegisviewer.client.event.SaveHandler)
	 */
	@Override
	public HandlerRegistration addSaveHandler(SaveHandler handler) {
		return addHandler(handler, SaveEvent.getType());
	}

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
