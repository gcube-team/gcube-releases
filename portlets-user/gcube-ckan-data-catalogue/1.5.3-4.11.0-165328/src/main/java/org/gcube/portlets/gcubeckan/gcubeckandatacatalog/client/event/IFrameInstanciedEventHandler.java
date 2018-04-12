package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Interface IFrameInstanciedEentHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 23, 2016
 */
public interface IFrameInstanciedEventHandler extends EventHandler {


	/**
	 * On new instance.
	 *
	 * @param iFrameInstanciedEent the i frame instancied eent
	 */
	void onNewInstance(IFrameInstanciedEvent iFrameInstanciedEent);

}