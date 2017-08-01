package org.gcube.portlets.user.gisviewerapp.client.rpc;

import com.google.gwt.core.client.GWT;

/**
 * The Interface GisViewerAppServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 22, 2016
 */
public interface GisViewerAppServiceAsync
{

    /**
     * Utility class to get the RPC Async interface from client-side code.
     *
     * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
     * Jan 22, 2016
     */
    public static final class Util
    {
        private static GisViewerAppServiceAsync instance;

        /**
         * Gets the single instance of Util.
         *
         * @return single instance of Util
         */
        public static final GisViewerAppServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (GisViewerAppServiceAsync) GWT.create( GisViewerAppService.class );
            }
            return instance;
        }

        /**
         * Instantiates a new util.
         */
        private Util()
        {
            // Utility class should not be instanciated
        }
    }



//	/**
//	 * Gets the styles for wms request.
//	 *
//	 * @param wmsRequest the wms request
//	 * @param callback the callback
//	 * @return the styles for wms request
//	 */
//	void getStylesForWmsRequest(
//		String wmsRequest, AsyncCallback<GeoStyles> callback);
//
//
//
//	void getParametersForWmsRequest(
//		String wmsRequest, String displayName,
//		AsyncCallback<GeoInformation> callback);
}
