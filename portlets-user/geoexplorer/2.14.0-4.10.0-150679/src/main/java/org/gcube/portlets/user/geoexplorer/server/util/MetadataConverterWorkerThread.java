/**
 *
 */
package org.gcube.portlets.user.geoexplorer.server.util;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl;
import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.model.faults.AuthorizationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;



/**
 * The Class MetaConverterWorkerThread.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 27, 2016
 *
 * TODO MUST BE UPDATED IN THE FUTURE IN ORDER TO USE GEONETWORK LIBRARY CONFIGURATION
 */
public class MetadataConverterWorkerThread implements Runnable{

	protected Logger logger = Logger.getLogger(MetadataConverterWorkerThread.class);
	private String uuid;
	private LayerItem layerItem;
	private GeoNetworkReader geoNetworkReader;

	private String geoNetworkUrl;
	private GeoNetworkPublisher publish;
	private String geoNetworkPwd;
	private String geoNetworkUser;


	/**
	 * Instantiates a new meta converter worker thread.
	 *
	 * @param metadataUUID the metadata uuid
	 * @param httpSession the http session
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	public MetadataConverterWorkerThread(String metadataUUID, HttpSession httpSession, String scope) throws Exception {
		GeonetworkInstance gn = GeoExplorerServiceImpl.getGeonetworkInstanceFromSession(httpSession, scope);
		this.uuid=metadataUUID;
//		gn.authenticateOnGeoenetwork(true);
		this.geoNetworkReader = gn.getGeonetworkReader();
//		System.out.println("NEL COSTRUTTORE "+Thread.currentThread().getName() + " id "+Thread.currentThread().getId());
	}

	/**
	 * Instantiates a new metadata converter worker thread.
	 *
	 * @param geoNetworkUrl the geo network url
	 * @param geoNetworkPwd the geo network pwd
	 * @param geoNetworkUser the geo network user
	 * @param metaUUID the meta uuid
	 * @throws Exception the exception
	 */
	public MetadataConverterWorkerThread(String geoNetworkUrl, String geoNetworkPwd, String geoNetworkUser, String metaUUID) throws Exception {
		this.geoNetworkUrl = geoNetworkUrl;
		this.geoNetworkPwd = geoNetworkPwd;
		this.geoNetworkUser = geoNetworkUser;
		this.publish = GeoNetwork.get();
		this.uuid=metaUUID;
//		System.out.println("NEL COSTRUTTORE "+Thread.currentThread().getName() + " id "+Thread.currentThread().getId());
	}

	/**
	 * Instantiates a new metadata converter worker thread.
	 *
	 * @param geonetworkReader2 the geonetwork reader2
	 * @param uuid2 the uuid2
	 */
	public MetadataConverterWorkerThread(GeoNetworkReader geonetworkReader2, String uuid2) {
		this.uuid=uuid2;
		this.geoNetworkReader = geonetworkReader2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.trace("NEL RUN "+Thread.currentThread().getName() + " id "+Thread.currentThread().getId());
		try {
//			publish.login(LoginLevel.DEFAULT);
			this.geoNetworkReader.login(LoginLevel.SCOPE);
			layerItem = MetadataConverter.getLayerItemFromMetadataUUID(geoNetworkReader, uuid);
		}
		catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (MissingConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (MissingServiceEndpointException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the layer item.
	 *
	 * @return the layerItem
	 */
	public LayerItem getLayerItem() {

		return layerItem;
	}
}
