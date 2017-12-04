package org.gcube.portlets.user.gisviewer.client.openlayers;

import org.gwtopenmaps.openlayers.client.handler.PointHandler;
import org.gwtopenmaps.openlayers.client.util.JSObject;

/**
 *
 *
 * @author Edwin Commandeur - Atlis EJS
 *
 */
public class LineHandler extends PointHandler {

	protected LineHandler(JSObject element) {
		super(element);
	}

	public LineHandler(){
		this(LineHandlerImpl.create());
	}

}
