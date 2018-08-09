package org.gcube.portlets_widgets.catalogue_sharing_widget.server;

import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.portlets.user.urlshortener.UrlShortener;
import org.gcube.portlets_widgets.catalogue_sharing_widget.client.ShareServices;
import org.gcube.portlets_widgets.catalogue_sharing_widget.shared.ItemUrls;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import eu.trentorise.opendata.jackan.model.CkanDataset;

public class ShareServicesImpl extends RemoteServiceServlet implements ShareServices{

	private static final long serialVersionUID = -2060855544534802987L;
	private static final Log logger = LogFactoryUtil.getLog(ShareServicesImpl.class);

	/**
	 * Retrieve an instance of the library for the scope
	 * @param scope if it is null it is evaluated from the session
	 * @return
	 * @throws Exception 
	 */
	public DataCatalogue getCatalogue(String scope) throws Exception{

		String scopeInWhichDiscover = (scope != null && !scope.isEmpty()) ? scope : ServerUtils.getCurrentContext(getThreadLocalRequest(), false);
		logger.debug("Discovering ckan instance into scope " + scopeInWhichDiscover);
		return DataCatalogueFactory.getFactory().getUtilsPerScope(scopeInWhichDiscover);

	}

	@Override
	public ItemUrls getPackageUrl(String uuid) throws Exception{

		String scopePerCurrentUrl = ServerUtils.getScopeFromClientUrl(getThreadLocalRequest());
		DataCatalogue catalogue = getCatalogue(scopePerCurrentUrl);
		CkanDataset dataset = catalogue.getDataset(uuid, catalogue.getApiKeyFromUsername(ServerUtils.getUserInSession(getThreadLocalRequest())));
		String longUrl = catalogue.getUnencryptedUrlFromDatasetIdOrName(uuid);

		if(longUrl == null || longUrl.isEmpty())
			throw new Exception("There was a problem while retrieving the item's url, retry later");

		String shortUrl = null;

		try{
			UrlShortener shortener = new UrlShortener();
			shortUrl = shortener.shorten(longUrl);
		}catch(Exception e){
			logger.warn("Short url not available");
		}

		return new ItemUrls(shortUrl, longUrl, uuid, dataset.getName(), dataset.getTitle());
	}

}
