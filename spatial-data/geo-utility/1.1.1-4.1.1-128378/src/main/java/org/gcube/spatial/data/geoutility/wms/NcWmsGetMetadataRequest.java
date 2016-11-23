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
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gcube.spatial.data.geoutility.bean.WmsParameters;
import org.gcube.spatial.data.geoutility.util.UrlEncoderUtil;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * The Class NcWmsGetColorScaleRange.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 1, 2015
 */
public class NcWmsGetMetadataRequest {

	/**
	 *
	 */
	public static final String GET_METADATA = "GetMetadata";
	public static final int CONNECTION_TIMEOUT = 1000;
	public static Logger logger = Logger.getLogger(NcWmsGetMetadataRequest.class);
	public static final String COLORSCALERANGE = "COLORSCALERANGE";
	public static final String WMS_VERSION_1_3_0 = "1.3.0";
	public static final String COLORSCALERANGE_DEFAULT_VALUE = "auto";

	/**
	 * Gets the color scale range
	 * Calls a ncWMS GetMetadata request with item = minmax.
	 * Returns minmax if GetMedatada works, a null array otherwise.
	 *
	 * @param wmsServerUri the WMS Server Uri
	 * @param wmsVersion the wms version
	 * @param bbox the bbox
	 * @param layerName the layer name
	 * @param crs the crs
	 * @param width the width
	 * @param height the height
	 * @param connectionTimeout the connection timeout
	 * @return the color scale range - an array of 2 elements that contains min and max value for the COLORSCALERANGE
	 * @throws Exception the exception
	 */
	public static String[] getColorScaleRange(String wmsServerUri, String wmsVersion, String bbox, String layerName, String crs, String width, String height, int connectionTimeout) throws Exception {
		logger.trace(NcWmsGetMetadataRequest.class.getName()+".getColorScaleRange working...");
		String[] colorScaleMinMax = null;
//		http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc?item=minmax&layers=analyzed_field&bbox=-180.0,-85.0,180.0,85.0&crs=EPSG%3A4326&request=GetMetadata&width=640&height=480

		if(wmsServerUri==null || wmsServerUri.isEmpty())
			throw new Exception("Invalid wms server uri");

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(WmsParameters.REQUEST.getParameter(), GET_METADATA);
		parameters.put(WmsParameters.LAYERS.getParameter(), layerName);
		parameters.put("item", "minmax");

		parameters.put(WmsParameters.CRS.getParameter(), crs);
		parameters.put(WmsParameters.WIDTH.getParameter(), width);
		parameters.put(WmsParameters.HEIGHT.getParameter(), height);

		if(wmsVersion.compareTo(WMS_VERSION_1_3_0)==0){
			String[] values = bbox.split(",");
			String newbbox = values[1] +","+values[0]+","+values[3]+","+values[2];
			logger.trace("inverted bbox to wms 1.3.0: "+newbbox);
			parameters.put(WmsParameters.BBOX.getParameter(), newbbox);

		}else
			parameters.put(WmsParameters.BBOX.getParameter(), bbox);

		try {

			String query = UrlEncoderUtil.encodeQuery(parameters);
			colorScaleMinMax = openConnectionGetColorScaleRange(wmsServerUri, query, connectionTimeout);
			return colorScaleMinMax;

		}catch (Exception e) {
			logger.error("Error Exception with url " + wmsServerUri);
			return colorScaleMinMax;
		}
	}

	/**
	 * Open connection get color scale range.
	 *
	 * @param urlConn the url conn
	 * @param query the query
	 * @param connectionTimeout the connection timeout
	 * @return the string[] an array with 2 elements witch contains min, max value for the COLORSCALERANGE
	 */
	private static String[] openConnectionGetColorScaleRange(String urlConn, String query, int connectionTimeout) {

		URL url;
		String[] colorScaleRange = null;

		try {
			url = new URL(urlConn + "?" + query);
			URLConnection connection = url.openConnection();

			if(connectionTimeout<0)
				connectionTimeout = CONNECTION_TIMEOUT;

			connection.setConnectTimeout(connectionTimeout);
			connection.setReadTimeout(connectionTimeout*2);

			logger.trace("openConnectionGetColorScaleRange on: " + url);

			// Cast to a HttpURLConnection
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;

				httpConnection.setRequestMethod("GET");
				int code = httpConnection.getResponseCode();

				if (code == 200) {
					colorScaleRange = getColorScaleRange(httpConnection);
				}else
					logger.warn("openConnectionGetColorScaleRange error, code = " + code +", returning");

				httpConnection.disconnect();

			} else {
				logger.error("error - not a http request!");
			}

			return colorScaleRange;

		} catch (SocketTimeoutException e) {
			logger.error("Error SocketTimeoutException with url " + urlConn);
			return colorScaleRange;
		} catch (MalformedURLException e) {
			logger.error("Error MalformedURLException with url " + urlConn);
			return colorScaleRange;
		} catch (IOException e) {
			logger.error("Error IOException with url " + urlConn);
			return colorScaleRange;
		} catch (Exception e) {
			logger.error("Error Exception with url " + urlConn);
			return colorScaleRange;
		}

	}



	/**
	 * Gets the color scale range.
	 *
	 * @param httpConnection the http connection
	 * @return the color scale range
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static String[] getColorScaleRange(HttpURLConnection httpConnection) throws IOException{

		String[] minmax = new String[2];
		InputStream source = null;

		int code = httpConnection.getResponseCode();

		logger.trace("response code is " + code);

		if (code == 200) {
			try{
				source = httpConnection.getInputStream();
				String jsonTxt = IOUtils.toString(source);
				JSONObject json = new JSONObject(jsonTxt);

				String min = json.getString("min");

				if(min!=null && !min.isEmpty())
					minmax[0]=min;

				String max = json.getString("max");

				if(max!=null && !max.isEmpty())
					minmax[1]=max;

				logger.trace("returning: "+Arrays.toString(minmax));
			}catch (JSONException e){
				logger.warn("JSONException occurred when converting stream to JSON");

			}catch (IOException e) {
				logger.warn("IOException occurred when converting stream to JSON");
			}

		} else
			return minmax;

		return minmax;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

//		http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc?item=minmax&layers=analyzed_field&bbox=-180.0,-85.0,180.0,85.0&crs=EPSG%3A4326&request=GetMetadata&width=640&height=480

		try {
			NcWmsGetMetadataRequest.getColorScaleRange("http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc", "1.1.1", "-85.0,-180.0,85.0,180.0", "analyzed_field", "EPSG:4326", "640", "480", 2000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
