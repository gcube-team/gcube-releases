package org.gcube.portlets.user.geoexplorer.server;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.server.util.ASLSessionUtil;
import org.gcube.portlets.user.geoexplorer.server.util.wms.WmsGeoExplorerUrlValidator;
import org.gcube.portlets.user.geoexplorer.shared.SessionExpiredException;
import org.gcube.spatial.data.geoutility.bean.WmsParameters;

/*
 * use: <serverName>/<servletName>?geoserver=<geoserverUrl>&layer=<layerCompleteName>
 * sample: http://127.0.0.1:8888/testgisviewer/MapPreviewGenerator?&layer=aquamaps:primProdMean&style=primprod_style
*/
/**
 * The Class MapPreviewGenerator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 26, 2015
 */
public class MapPreviewGenerator extends HttpServlet {

	/**
	 *
	 */
	private static final String PREVIEW_NOT_AVAILABLE = "preview not available";
	/**
	 *
	 */
	private static final String DEFAULT_WMS_VERSION = "1.1.0";
	private static final long serialVersionUID = 1L;
	private static final Color COLOR_BLACK = new Color(10, 10, 10);
	private static Font FONT = new Font("Monospaced", Font.BOLD, 12);
	private static int defaultW = 168;  // proportional to 503x250
	private static int defaultH = 95;  // proportional to 503x250 no

	private static int maxWidth = 330;
	private static int maxHeigth = 165;

	private static final String WMS_REQUEST_PARAMETER = Constants.WMS_REQUEST_PARAMETER;
	private static final String ADD_TRU_MARBLE_REQUEST_PARAMETER = Constants.ADD_TRU_MARBLE_REQUEST_PARAMETER;
	private static final String DEFAULT_MAP_PREVIEW_BBOX = "-165.5859375,-86.484375,180.0,89.296875";
	private static final String DEFAULT_BBOX = "-180.0,-90.0,180.0,90.0";

	public static Logger logger = Logger.getLogger(MapPreviewGenerator.class);

	// the true marble wms request is not used, a static image is well performant
	//private static final String LINK_TRUE_MARBLE = "http://geoserver.d4science-ii.research-infrastructures.eu:80/geoserver/wms?SERVICE=WMS&version=1.1.0&REQUEST=GetMap&LAYERS=aquamaps:TrueMarble.16km.2700x1350&BBOX=-165.5859375,-86.484375,180.0,89.296875&WIDTH="+w+"&HEIGHT="+h+"&SRS=EPSG:4326&FORMAT=image/png";
	//private static final String LINK_TRUE_MARBLE = "http://iceds.ge.ucl.ac.uk/cgi-bin/icedswms?SERVICE=WMS&version=1.1.0&REQUEST=GetMap&LAYERS=bluemarble_1&BBOX=-165.5859375,-86.484375,180.0,89.296875&WIDTH="+w+"&HEIGHT="+h+"&SRS=EPSG:4326&FORMAT=image/png";


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		try {
			ASLSessionUtil.getASLSession(request.getSession()); //USED TO SET TOKEN AND/OR SCOPE
			// get parameters
			String outputFormat = "image/png";
			String outputExtension = "png";

			String wmsRequest = request.getParameter(WMS_REQUEST_PARAMETER);
			String addTrueMarble = request.getParameter(ADD_TRU_MARBLE_REQUEST_PARAMETER);
			boolean isBaseLayer = true; //DEFAULT IS BASE LAYER IS TRUE

			if(wmsRequest==null || wmsRequest.isEmpty()){
				logger.error(WMS_REQUEST_PARAMETER +" parameter not found, returning error image");
				showErrorImage(response, "No layer found");
			}

			if(addTrueMarble!=null){
				try{
					isBaseLayer = Boolean.parseBoolean(addTrueMarble);
				}catch(Exception e){
					isBaseLayer = true;
				}
			}

			logger.info("GET " +WMS_REQUEST_PARAMETER + " read: "+wmsRequest);
			logger.info("IS BASE LAYER: "+isBaseLayer);
			String url = buildWmsRequestMapPreview(wmsRequest, DEFAULT_MAP_PREVIEW_BBOX);
			BufferedImage img = ImageIO.read(new URL(url));

			if(isBaseLayer){
				logger.info("IS BASE LAYER adding TRUE MARBLE");
	//			Constants.log("url>"+url);
				logger.debug("GET map preview url > "+url);
				InputStream inputStremTrueMarble = MapPreviewGenerator.class.getResourceAsStream(Constants.NAME_IMG_TRUE_MARBLE);
				logger.debug("inputStremTrueMarble available > "+inputStremTrueMarble.available());
				BufferedImage imgTrueMarble = ImageIO.read(inputStremTrueMarble);
				img = overlayImage(img, imgTrueMarble);
			}else{
//				img = addBorderToImage(img);
			}
			//--Send the image data to response
			response.setContentType(outputFormat);
			OutputStream outputStream = response.getOutputStream();
			ImageIO.write(img, outputExtension, outputStream);
			outputStream.close();

		} catch (MalformedURLException e) {
			//e.printStackTrace();
			logger.error("MalformedURLException", e);
			showErrorImage(response, PREVIEW_NOT_AVAILABLE);
		} catch (IOException e) {
			//e.printStackTrace();
			logger.error("IOException", e);
			showErrorImage(response, PREVIEW_NOT_AVAILABLE);
		} catch (Exception e) {

			if(e instanceof SessionExpiredException)
				throw new SessionExpiredException("Session Expired");

			e.printStackTrace();
			logger.error("Exception", e);
			showErrorImage(response, PREVIEW_NOT_AVAILABLE);
		}
	}


	/**
	 * Sets the size.
	 *
	 * @param sizeValue the size value
	 * @param defaultSize the default size
	 * @param maxSize the max size
	 * @return the string
	 */
	private String setSize(String sizeValue, int defaultSize, int maxSize){

		if(sizeValue== null || !sizeValue.isEmpty()){
			sizeValue = ""+defaultSize;
		} else {

			try{
				int tempWidth = new Integer(sizeValue);

				if(tempWidth>maxSize)
					sizeValue = ""+maxSize;
			}catch (Exception e) {
				logger.error(e);
			}
		}

		return sizeValue;

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {

		try {
			ASLSessionUtil.getASLSession(request.getSession()); //USED TO SET TOKEN AND/OR SCOPE
			// get parameters
			String outputFormat = "text/html";
			String outputExtension = "png";

			String bbox =  request.getParameter(Constants.BBOX);
			String width = request.getParameter(Constants.LAYERWIDHT);
			String height = request.getParameter(Constants.LAYERWIDHT);
			String addTrueMarble = request.getParameter(ADD_TRU_MARBLE_REQUEST_PARAMETER);
			boolean isBaseLayer = true; //DEFAULT IS BASE LAYER IS TRUE

			if(addTrueMarble!=null){
				try{
					isBaseLayer = Boolean.parseBoolean(addTrueMarble);
				}catch(Exception e){
					isBaseLayer = true;
				}
			}

			width = setSize(width, maxWidth, maxWidth);
			height = setSize(height, maxHeigth, maxHeigth);

			String wmsRequest = request.getParameter(WMS_REQUEST_PARAMETER);

			if(wmsRequest==null || wmsRequest.isEmpty()){
				logger.error(WMS_REQUEST_PARAMETER +" parameter not found, returning error image");
				showErrorImage(response, "No layer found");
			}

			logger.trace("POST " +WMS_REQUEST_PARAMETER + " read: "+wmsRequest);

			if(bbox==null || bbox.isEmpty())
				wmsRequest = WmsGeoExplorerUrlValidator.setValueOfParameter(WmsParameters.BBOX, wmsRequest, DEFAULT_BBOX,true);

			wmsRequest = WmsGeoExplorerUrlValidator.setValueOfParameter(WmsParameters.WIDTH, wmsRequest, width,true);
			wmsRequest = WmsGeoExplorerUrlValidator.setValueOfParameter(WmsParameters.HEIGHT, wmsRequest, height,true);
			wmsRequest = WmsGeoExplorerUrlValidator.setValueOfParameter(WmsParameters.FORMAT, wmsRequest, "image/png",true);
			wmsRequest = WmsGeoExplorerUrlValidator.setValueOfParameter(WmsParameters.TRANSPARENT, wmsRequest, "true",true);

			String url = wmsRequest;

			logger.debug("POST map preview url > "+url);

			BufferedImage img = ImageIO.read(new URL(url));
			logger.info("IS BASE LAYER: "+isBaseLayer);
			if(isBaseLayer){
				logger.info("IS BASE LAYER adding TRUE MARBLE");
				InputStream inputStremTrueMarble = MapPreviewGenerator.class.getResourceAsStream(Constants.NAME_IMG_BIG_TRUE_MARBLE);
				logger.debug("inputStremTrueMarble available > "+inputStremTrueMarble.available());
				BufferedImage imgTrueMarble = ImageIO.read(inputStremTrueMarble);
				int type = imgTrueMarble.getType() == 0? BufferedImage.TYPE_INT_ARGB : imgTrueMarble.getType();

				if(width.compareTo(""+maxWidth)!=0 || height.compareTo(""+maxHeigth)!=0){
					imgTrueMarble = resizeImageWithHint(imgTrueMarble, new Integer(width), new Integer(height), type);
				}

				img = overlayImage(img, imgTrueMarble);
			}else{
//				img = addBorderToImage(img);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, outputExtension, baos);
			baos.flush();
			byte[] imageInByte = baos.toByteArray();

			//--Send the image data to response
			response.setContentType(outputFormat);
			PrintWriter out = response.getWriter();

//			System.out.println("<img src='data:image/png;base64," + DatatypeConverter.printBase64Binary(imageInByte) + "'></img>");

			out.print("<img src='data:image/png;base64," + DatatypeConverter.printBase64Binary(imageInByte) + "'></img>");
			out.close();
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			logger.error("MalformedURLException", e);
			postShowErrorImage(response, PREVIEW_NOT_AVAILABLE);
		} catch (IOException e) {
			//e.printStackTrace();
			logger.error("IOException", e);
			postShowErrorImage(response, PREVIEW_NOT_AVAILABLE);
		} catch (Exception e) {

			if(e instanceof SessionExpiredException){
				logger.error("Session Expired", e);
				throw new SessionExpiredException("Session Expired");
			}

//			e.printStackTrace();
			logger.error("Exception", e);
			postShowErrorImage(response, PREVIEW_NOT_AVAILABLE);
		}
	}


	/**
	 * Builds the full wms request.
	 *
	 * @param wmsRequest the wms request
	 * @param bbox the bbox
	 * @return an url that represents an wms request
	 */
	/*private String buildFullWmsRequest(String baseGeoserverUrl, String version,
			String layer, String bbox, String crs, String width, String height) {

		String url = baseGeoserverUrl + "?SERVICE=WMS" + "&version=" + version
				+ "&REQUEST=GetMap" + "&LAYERS=" + layer + "&BBOX=" + bbox
				+ "&WIDTH=" + width + "&HEIGHT=" + height
				+ "&FORMAT=image/png" + "&STYLES=" + "&TRANSPARENT=true";

		// + "&STYLES=" + style

		// ADD SAME CRS AND SRS IF CRS WAS PASSED IN INPUT
		if (crs != null && !crs.isEmpty()) {
			url += "&CRS=" + crs + "&SRS=" + crs;
		} else
			url += "&SRS=EPSG:4326";

		return url;

	}*/



	/**
	 * Builds the wms request map preview.
	 *
	 * @param wmsRequest the wms request
	 * @param bbox the bbox
	 * @return the string
	 */
	public String buildWmsRequestMapPreview(String wmsRequest, String bbox) {

		int indexStart = wmsRequest.indexOf("?");
		String url =  "";
		if(indexStart==-1){
			logger.warn("IT IS NOT POSSIBLE TO PARSE WMS URL, '?' not found, wms request has only parameters?: "+wmsRequest);
		}else
			url = wmsRequest.substring(indexStart+1, wmsRequest.length()); //get only parameters of the wms request

		url = WmsGeoExplorerUrlValidator.setValueOfParameter(WmsParameters.BBOX, wmsRequest, bbox,true);
		url = WmsGeoExplorerUrlValidator.setValueOfParameter(WmsParameters.WIDTH, url, defaultW+"", true);
		url = WmsGeoExplorerUrlValidator.setValueOfParameter(WmsParameters.HEIGHT, url, defaultH+"",true);
		url = WmsGeoExplorerUrlValidator.setValueOfParameter(WmsParameters.FORMAT, url, "image/png",true);
		url = WmsGeoExplorerUrlValidator.setValueOfParameter(WmsParameters.TRANSPARENT, url, "true",true);

		return url;
	}

	/**
	 * Resize image with hint.
	 *
	 * @param originalImage the original image
	 * @param newW the new w
	 * @param newH the new h
	 * @param type the type
	 * @return the buffered image
	 */
	private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int newW, int newH, int type){

		BufferedImage resizedImage = new BufferedImage(newW, newH, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newW, newH, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}

	/**
	 * Overlay image.
	 *
	 * @param fgImage the fg image
	 * @param bgImage the bg image
	 * @return the buffered image
	 */
	private BufferedImage overlayImage(BufferedImage fgImage, BufferedImage bgImage) {
        /**Create a Graphics  from the background image**/
        Graphics2D g = bgImage.createGraphics();
        /**Set Antialias Rendering**/
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(bgImage, 0, 0, null);
        g.drawImage(fgImage, 0, 0, null);
        g.dispose();

        return bgImage;
	}

	/**
	 * Adds the border to image.
	 *
	 * @param bgImage the bg image
	 * @return the buffered image
	 */
	private BufferedImage addBorderToImage(BufferedImage bgImage) {
        /**Create a Graphics  from the fgImage image**/
        Graphics2D g = bgImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(bgImage, 0, 0, null);
        Stroke stroke1 = new BasicStroke(1);
        g.setColor(Color.gray);
        g.setStroke(stroke1);
        g.drawRect(0, 0, bgImage.getWidth(), bgImage.getHeight());
        g.dispose();

        return bgImage;
	}

	/**
	 * Show error image.
	 *
	 * @param response the response
	 * @param message the message
	 */
	private void showErrorImage(HttpServletResponse response, String message) {
		try {
			// get an error image
			InputStream inputStremLogo = MapPreviewGenerator.class.getResourceAsStream(Constants.NAME_IMG_ERROR);
			BufferedImage img = ImageIO.read(inputStremLogo);
			// add an error message
			Graphics g = img.getGraphics();
			g.setColor(COLOR_BLACK);
			g.setFont(FONT);
			g.drawString(message, 55, img.getHeight()/2);

			// send the image data to response
			response.setContentType("image/png");
			OutputStream outputStream;
			outputStream = response.getOutputStream();
			ImageIO.write(img, "png", outputStream);
			outputStream.close();
		} catch (Exception e) {
			logger.error(e);
			return;
		}
	}

	/**
	 * Post show error image.
	 *
	 * @param response the response
	 * @param message the message
	 */
	private void postShowErrorImage(HttpServletResponse response, String message) {
		try {
			// get an error image
			InputStream inputStremLogo = MapPreviewGenerator.class.getResourceAsStream(Constants.NAME_IMG_ERROR);
			BufferedImage img = ImageIO.read(inputStremLogo);
			// add an error message
			Graphics g = img.getGraphics();
			g.setColor(COLOR_BLACK);
			g.setFont(FONT);
			g.drawString(message, 55, img.getHeight()/2);

			// send the image data to response
			response.setContentType("image/png");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "png", baos);
			baos.flush();
			byte[] imageInByte = baos.toByteArray();

			PrintWriter out = response.getWriter();

//			System.out.println("<img src='data:image/png;base64," + DatatypeConverter.printBase64Binary(imageInByte) + "'></img>");

			out.print("<img src='data:image/png;base64," + DatatypeConverter.printBase64Binary(imageInByte) + "'></img>");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}