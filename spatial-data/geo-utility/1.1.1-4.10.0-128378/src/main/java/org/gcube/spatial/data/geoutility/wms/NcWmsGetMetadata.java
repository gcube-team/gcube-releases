/**
 *
 */
package org.gcube.spatial.data.geoutility.wms;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gcube.spatial.data.geoutility.bean.LayerZAxis;
import org.gcube.spatial.data.geoutility.bean.NcWmsLayerMetadata;
import org.gcube.spatial.data.geoutility.bean.NcWmsLayerMetadata.METADATA;
import org.gcube.spatial.data.geoutility.bean.WmsParameters;
import org.gcube.spatial.data.geoutility.util.UrlEncoderUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class NcWmsGetMetadata.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 18, 2015
 */
public class NcWmsGetMetadata {

	/** The Constant CONNECTION_TIMEOUT. */
	protected static final int CONNECTION_TIMEOUT = 1000; //DEFAULT CONNECTION TIMEOUT
	public static Logger logger = Logger.getLogger(NcWmsGetMetadata.class);

	/**
	 * Gets the metadata.
	 *
	 * @param wmsServerUri the wms server uri
	 * @param layerName the layer name
	 * @return the metadata
	 * @throws Exception the exception
	 */
	public static NcWmsLayerMetadata getMetadata(String wmsServerUri, String layerName) throws Exception {
		return getMetadata(wmsServerUri, layerName, CONNECTION_TIMEOUT, METADATA.values());
	}


	/**
	 * Gets the metadata.
	 *
	 * @param wmsServerUri the wms server uri
	 * @param layerName the layer name
	 * @param connectionTimeout the connection timeout
	 * @param meta the meta
	 * @return the metadata
	 * @throws Exception the exception
	 */
	public static NcWmsLayerMetadata getMetadata(String wmsServerUri, String layerName, int connectionTimeout, NcWmsLayerMetadata.METADATA...meta) throws Exception {

		if(wmsServerUri==null || wmsServerUri.isEmpty())
			throw new Exception("Invalid wms server uri");

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(WmsParameters.REQUEST.getParameter(), "GetMetadata");
		parameters.put("layerName", layerName);
		parameters.put("item", "layerDetails");
		parameters.put(WmsParameters.SERVICE.getParameter(), "WMS");

		try {

			String query = UrlEncoderUtil.encodeQuery(parameters);
			return  openConnectionGetMetadata(wmsServerUri, query, connectionTimeout, meta);

		}catch (Exception e) {
			logger.error("Error Exception with url " + wmsServerUri);
			return null;
		}
	}



	/**
	 * Open connection get metadata.
	 *
	 * @param urlConn the url conn
	 * @param query the query
	 * @param connectionTimeout the connection timeout sets a specified timeout value, in milliseconds, to be used when opening URLConnection.
	 * @param meta the meta
	 * @return the nc wms layer metadata
	 */
	private static NcWmsLayerMetadata openConnectionGetMetadata(String urlConn, String query, int connectionTimeout, NcWmsLayerMetadata.METADATA...meta) {

		URL url;
		NcWmsLayerMetadata metadata = null;

		try {
			url = new URL(urlConn + "?" + query);
			URLConnection connection = url.openConnection();

			if(connectionTimeout<0)
				connectionTimeout = CONNECTION_TIMEOUT;

			connection.setConnectTimeout(connectionTimeout);
			connection.setReadTimeout(connectionTimeout + connectionTimeout);

			logger.trace("openConnectionGetMetadata on: " + url);

			// Cast to a HttpURLConnection
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;

				httpConnection.setRequestMethod("GET");
				int code = httpConnection.getResponseCode();

				if (code == 200) {
					metadata = getMetadata(httpConnection, meta);
					metadata.setResponseCode(code);
				}else{
					logger.warn("openConnectionGetMetadata error, code = " + code +", returning");
				}

				httpConnection.disconnect();

			} else {
				logger.error("error - not a http request!");
			}

			return metadata;

		} catch (SocketTimeoutException e) {
			logger.error("Error SocketTimeoutException with url " + urlConn);
			return metadata;
		} catch (MalformedURLException e) {
			logger.error("Error MalformedURLException with url " + urlConn);
			return metadata;
		} catch (IOException e) {
			logger.error("Error IOException with url " + urlConn);
			return metadata;
		} catch (Exception e) {
			logger.error("Error Exception with url " + urlConn);
			return metadata;
		}

	}

	/**
	 * Gets the metadata.
	 *
	 * @param httpConnection the http connection
	 * @param meta the meta
	 * @return the metadata
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static NcWmsLayerMetadata getMetadata(HttpURLConnection httpConnection, METADATA[] meta) throws IOException{

		InputStream source = null;
		NcWmsLayerMetadata metadata = new NcWmsLayerMetadata();

		try{
			source = httpConnection.getInputStream();
			String jsonTxt = IOUtils.toString(source);
			metadata.setRawJson(jsonTxt);

			if(meta==null)
				meta = METADATA.values();

			List<METADATA> listMeta = Arrays.asList(meta);

			JSONObject json = new JSONObject(jsonTxt);

			if(listMeta.contains(NcWmsLayerMetadata.METADATA.SUPPORTEDSTYLES)){
				JSONArray supportedStyles = json.getJSONArray(NcWmsLayerMetadata.METADATA.SUPPORTEDSTYLES.getKey());
				if(supportedStyles!=null){
					List<String> s = new ArrayList<String>(supportedStyles.length());
					for (int i=0; i<supportedStyles.length(); i++) {
						s.add(supportedStyles.getString(i));
					}
					metadata.setSupportedStyles(s);
				}
			}

			if(listMeta.contains(NcWmsLayerMetadata.METADATA.PALETTES)){
				JSONArray palettes = json.getJSONArray(NcWmsLayerMetadata.METADATA.PALETTES.getKey());
				if(palettes!=null){
					List<String> s = new ArrayList<String>(palettes.length());
					for (int i=0; i<palettes.length(); i++) {
						s.add(palettes.getString(i));
					}
					metadata.setPalettes(s);
				}
			}

			if(listMeta.contains(NcWmsLayerMetadata.METADATA.DEFAULTPALETTE)){
				metadata.setDefaultPalette(json.getString(NcWmsLayerMetadata.METADATA.DEFAULTPALETTE.getKey()));
			}

			if(listMeta.contains(NcWmsLayerMetadata.METADATA.Z_AXIS)){
				JSONObject zaxis = json.getJSONObject(NcWmsLayerMetadata.METADATA.Z_AXIS.getKey());
				if(zaxis!=null){
					logger.trace("z-axis: "+zaxis.toString());
					LayerZAxis zAxis = new LayerZAxis();
					zAxis.setUnits(zaxis.getString(LayerZAxis.UNITS));
					zAxis.setPositive(zaxis.getBoolean(LayerZAxis.POSITIVE));
					JSONArray values = zaxis.getJSONArray(LayerZAxis.VALUES);
					if(values!=null){
						List<Double> s = new ArrayList<Double>(values.length());
						for (int i=0; i<values.length(); i++) {
							s.add(values.getDouble(i));
						}
						zAxis.setValues(s);
					}
					metadata.setZAxis(zAxis);
				}
			}

			logger.trace("returning: "+metadata.toString());
		}catch (JSONException e){
			logger.warn("JSONException occurred when converting stream to JSON");

		}catch (IOException e) {
			logger.warn("IOException occurred when converting stream to JSON");
		}

		return metadata;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

//		http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc?item=minmax&layers=analyzed_field&bbox=-180.0,-85.0,180.0,85.0&crs=EPSG%3A4326&request=GetMetadata&width=640&height=480

		try {
			System.out.println(NcWmsGetMetadata.getMetadata("http://thredds.research-infrastructures.eu/thredds/wms/public/netcdf/WOA2005TemperatureAnnual_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_.nc", "t00an1"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
