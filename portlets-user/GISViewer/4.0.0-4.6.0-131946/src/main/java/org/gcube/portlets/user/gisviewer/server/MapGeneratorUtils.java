/**
 *
 */
package org.gcube.portlets.user.gisviewer.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WmsRequest;
import org.gcube.portlets.user.gisviewer.client.commons.utils.MapServerRecognize;
import org.gcube.portlets.user.gisviewer.client.commons.utils.MapServerRecognize.SERVERTYPE;

/**
 * The Class MapGeneratorUtils.
 * @author ceras
 * @author modified by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 27, 2015
 */
public class MapGeneratorUtils {

	private static final String NAME_IMG_LOGO = "resources/D4ScienceInfrastructureLogo.png";
	private static final int MAX_THREADS = 5;
	private static final String NAME_IMG_ERROR = "resources/error.png";
	private static final Color COLOR_BLACK = new Color(10, 10, 10);
	private static Font FONT = new Font("Monospaced", Font.BOLD, 12);
	private static final Color BGCOLOR = new Color(255, 255, 255);

	private static Logger logger = Logger.getLogger(MapGeneratorUtils.class);

	/**
	 * Creates the map image.
	 *
	 * @param outputFormat the output format
	 * @param bbox the bbox
	 * @param width the width
	 * @param height the height
	 * @param geoservers the geoservers
	 * @param layers the layers
	 * @param styles the styles
	 * @param opacities the opacities
	 * @param cqlfilters the cqlfilters
	 * @param gsrefs the gsrefs
	 * @return the buffered image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static BufferedImage createMapImage(
			String outputFormat,
			String bbox,
			String width,
			String height,
			String[] geoservers,
			String[] layers,
			String[] styles,
			String[] opacities,
			String[] cqlfilters,
			String[] gsrefs) throws IOException {

		if(geoservers!=null)
			logger.trace("GEOSERVERS (server url's): "+Arrays.toString(geoservers));
		else
			logger.warn("GEOSERVERS (server url's): is null");

		if(gsrefs!=null)
			logger.trace("GEOSERVERS REF: "+Arrays.toString(gsrefs));
		else
			logger.warn("GEOSERVERS REF is null!");

		if(layers!=null)
			logger.trace("Layers: "+Arrays.toString(layers));
		else
			logger.warn("Layers is null!");

		try {
			// instantiate thread pool (one thread for each layer map request)
			ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
			List<ImageLoaderThread> threads = new ArrayList<ImageLoaderThread>();

			// start each thread
			for (int i=0; i<layers.length; i++) {
				int gsref = Integer.parseInt(gsrefs[i]);
				String geoserver = geoservers[gsref];

				logger.info("creating requesting for wms url: "+geoserver);
				String layer = layers[i];

				String style = "";
				if(styles!=null && i+1<=styles.length)
					style = styles[i];

				String cqlfilter = "null";
				if(cqlfilters!=null && i+1<=cqlfilters.length)
					cqlfilter = cqlfilters[i];


				//ADDED BY FRANCESCO M. FOR Map Server parameters
				String wmsStyleParam="";
//				if(style!=null && !style.isEmpty() && !style.equals("null")){
//					wmsStyleParam = "&STYLES="+style;
//				}

				//CASE MAP SERVER
				SERVERTYPE mapserverType = MapServerRecognize.recongnize(geoserver);
				logger.info("Recongnized SERVERTYPE: "+mapserverType);

				if(mapserverType!=null){
					if(mapserverType.equals(SERVERTYPE.MAPSERVER)){
						if(style!=null && !style.isEmpty() && !style.equals("null")){
							wmsStyleParam = "&STYLES="+style;
						}
					}else if(!style.equals("null")){
						wmsStyleParam = "&STYLES="+style;
					}else{
						wmsStyleParam = "&STYLES=";
					}
				}

				String url = geoserver + ""
						+ (geoserver.contains("?") ? "&" : "?")
						+ "SERVICE=WMS&version=1.1.0"
						+ "&REQUEST=GetMap"
						+ "&LAYERS=" + layer
						+  wmsStyleParam
						+ "&BBOX=" + bbox
						+ "&WIDTH=" + width
						+ "&HEIGHT=" + height
						+ "&SRS=EPSG:4326"
						+ (cqlfilter.equals("null") ? "" : "&CQL_FILTER="+cqlfilter)
						+ "&FORMAT=image/png"
						+ "&TRANSPARENT=true";

				logger.info("wms request created: "+url);

				ImageLoaderThread thread = i==0 ? new ImageLoaderThread(url, opacities[0]) : new ImageLoaderThread(url);
				executor.execute(thread);
				threads.add(thread);
			}

			// waiting for threads termination
			executor.shutdown();
			if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
				return errorImage("Error: Timeout expired.");
			}

			// for each thread, merge obtained images each other
			int i=0;
			BufferedImage imgRis=null;
			for (ImageLoaderThread thread: threads) {
				BufferedImage img = thread.getImg();
				if (img!=null) {
					// initializing first image
					if (i==0)
						imgRis = img;
					else
						mergeImage(imgRis, img, Float.valueOf(opacities[i]), 0, 0);
					i++;
				}
			}

			// overlay logo image
			InputStream inputStremLogo = MapGeneratorUtils.class.getResourceAsStream(NAME_IMG_LOGO);
			BufferedImage imgLogo = ImageIO.read(inputStremLogo);
			int x=imgRis.getWidth()-imgLogo.getWidth()-4, y=imgRis.getHeight()-imgLogo.getHeight()-4;
			mergeImage(imgRis, imgLogo, 0.8f, x, y);

			return imgRis;

		}  catch (MalformedURLException e) {
			return errorImage(e.toString());
		} catch (IOException e) {
			return errorImage("Error: I/O Exception.");
		} catch (Exception e) {
			e.printStackTrace();
			return errorImage("Error: Invalid parameters.");
		}
	}

	/**
	 * Creates the map image.
	 *
	 * @param outputFormat the output format
	 * @param bbox the bbox
	 * @param width the width
	 * @param height the height
	 * @param wmsServices the wms services
	 * @param layers the layers
	 * @param styles the styles
	 * @param opacities the opacities
	 * @param cqlfilters the cqlfilters
	 * @param gsrefs the gsrefs
	 * @param srs the srs
	 * @param crs the crs
	 * @param formats the formats
	 * @param wmsServerVersions the wms server versions
	 * @param wmsNonStandardParameters key1=value1&key2=value2...keyN=valueN of wms non-standard parameters
	 * @return the buffered image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static BufferedImage createMapImage(
			String outputFormat,
			String bbox,
			String width,
			String height,
			String[] wmsServices,
			String[] layers,
			String[] styles,
			String[] opacities,
			String[] cqlfilters,
			String[] gsrefs, String[] srs, String[] crs, String[] formats, String[] wmsServerVersions, String[] wmsNonStandardParameters, String[] elevations) throws IOException {

		if(wmsServices!=null)
			logger.trace("GEOSERVERS (server url's): "+Arrays.toString(wmsServices));
		else
			logger.warn("GEOSERVERS (server url's): is null");

		if(gsrefs!=null)
			logger.trace("GEOSERVERS REF: "+Arrays.toString(gsrefs));
		else
			logger.warn("GEOSERVERS REF is null!");

		if(layers!=null)
			logger.trace("Layers: "+Arrays.toString(layers));
		else
			logger.warn("Layers is null!");

		try {
			// instantiate thread pool (one thread for each layer map request)
			ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
			List<ImageLoaderThread> threads = new ArrayList<ImageLoaderThread>();

			// start each thread
			for (int i=0; i<layers.length; i++) {
				int gsref = Integer.parseInt(gsrefs[i]);
				String wmsURI = wmsServices[gsref];

				logger.info("creating requesting for wms url: "+wmsURI);
				String layer = layers[i];

				String style = "";
				if(styles!=null && i+1<=styles.length)
					style = styles[i];

				String cqlfilter = "null";
				if(cqlfilters!=null && i+1<=cqlfilters.length)
					cqlfilter = cqlfilters[i];

				formats[i] = "image/png"; //FORCE FORMAT AT "image/png" to set transparency

				String wmsNonStarndardParams = "";
				if(wmsNonStandardParameters!=null && i+1<=wmsNonStandardParameters.length)
					wmsNonStarndardParams = wmsNonStandardParameters[i];

				//CHECK ELEVATION
				Double elevation = null;
				if(elevations[i]!=null && !elevations[i].equals("null")){
					elevation = Double.parseDouble(elevations[i]);
				}

				WmsRequest request = new WmsRequest(bbox, width, height, wmsURI, wmsServerVersions[i], layer, style, cqlfilter, srs[i], crs[i], formats[i], "TRUE", wmsNonStarndardParams, elevation);

				String wmsRequest = createWmsRequest(request);

				ImageLoaderThread thread = i==0 ? new ImageLoaderThread(wmsRequest, opacities[0]) : new ImageLoaderThread(wmsRequest);
				executor.execute(thread);
				threads.add(thread);
			}

			// waiting for threads termination
			executor.shutdown();
			if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
				return errorImage("Error: Timeout expired.");
			}

			// for each thread, merge obtained images each other
			int i=0;
			BufferedImage imgRis=null;
			for (ImageLoaderThread thread: threads) {
				BufferedImage img = thread.getImg();
				if (img!=null) {
					// initializing first image
					if (i==0)
						imgRis = img;
					else
						mergeImage(imgRis, img, Float.valueOf(opacities[i]), 0, 0);
					i++;
				}
			}

			// overlay logo image
			InputStream inputStremLogo = MapGeneratorUtils.class.getResourceAsStream(NAME_IMG_LOGO);
			BufferedImage imgLogo = ImageIO.read(inputStremLogo);
			int x=imgRis.getWidth()-imgLogo.getWidth()-4, y=imgRis.getHeight()-imgLogo.getHeight()-4;
			mergeImage(imgRis, imgLogo, 0.8f, x, y);

			return imgRis;

		}  catch (MalformedURLException e) {
			return errorImage(e.toString());
		} catch (IOException e) {
			return errorImage("Error: I/O Exception.");
		} catch (Exception e) {
			e.printStackTrace();
			return errorImage("Error: Invalid parameters.");
		}
	}


	/**
	 * Added by Francesco M.
	 *
	 * @param request the request
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String createWmsRequest(WmsRequest request) throws Exception {

			if(request.getWmsServerURI()==null)
				throw new Exception("WMS server URI is null");

			if(request.getLayer()==null)
				throw new Exception("Layer is null");

			logger.info("creating requesting for: "+request);

			String layer = request.getLayer();
			String style = request.getStyle();
			String cqlfilter = request.getCqlfilter();
			String geoserver = request.getWmsServerURI();

			//ADDED BY FRANCESCO M. FOR Map Server parameters
			String wmsStyleParam="";

			//CASE MAP SERVER
			SERVERTYPE mapserverType = MapServerRecognize.recongnize(geoserver);
			logger.info("Recongnized SERVERTYPE: "+mapserverType);

			if(mapserverType!=null){
				if(mapserverType.equals(SERVERTYPE.MAPSERVER)){
					if(style!=null && !style.isEmpty() && !style.equals("null")){
						wmsStyleParam = "&STYLES="+style;
					}
				}else if(!style.equals("null")){
					wmsStyleParam = "&STYLES="+style;
				}else{
					wmsStyleParam = "&STYLES=";
				}
			}

			//FORCE FORMAT USING
			String format = "image/png";

			if(request.getFormat()!=null && !request.getFormat().isEmpty())
				format = request.getFormat();
			else
				logger.warn("Format not found. Using default value "+format);

			String crsParam = "";
			if(request.getCrs()!=null && !request.getCrs().isEmpty() && !request.getCrs().equals("null"))
				crsParam = "&CRS="+request.getCrs();
			else
				logger.info("Crs version not found. Not using it");

			String srs = "epsg:4326"; //MANDATORY
			if(request.getSrs()!=null && !request.getSrs().isEmpty() && !request.getSrs().equals("null"))
				srs = request.getSrs();
			else
				logger.info("Srs version not found. Using default value "+srs);

			String serverVersion = "1.1.0";
			if(request.getWmsServerVersion()!=null && !request.getWmsServerVersion().isEmpty() && !request.getWmsServerVersion().equals("null"))
				serverVersion = request.getWmsServerVersion();
			else
				logger.warn("Server version not found. Using default value "+serverVersion);

			String transparent = "TRUE";
			if(request.getTransparent()!=null && !request.getTransparent().isEmpty() && !request.getTransparent().equals("null"))
				transparent = request.getTransparent();
			else
				logger.warn("Transparent not found. Using default value "+transparent);

			String wmsNonStandard = "";
			if(request.getPairWmsNonStandardParameters()!=null && !request.getPairWmsNonStandardParameters().isEmpty() && !request.getPairWmsNonStandardParameters().equals("null")){

				if(request.getPairWmsNonStandardParameters().startsWith("&"))
					wmsNonStandard = request.getPairWmsNonStandardParameters().substring(1, request.getPairWmsNonStandardParameters().length());
				else
					wmsNonStandard = request.getPairWmsNonStandardParameters();
			}

			Double elevation = null;
			if(request.getElevation()!=null){
				elevation = request.getElevation();
			}

			String url = geoserver + ""
					+ (geoserver.endsWith("?") ? "&" : "?")
					+ "SERVICE=WMS"
					+ "&REQUEST=GetMap"
					+ "&VERSION="+serverVersion
					+ "&LAYERS=" + layer
					+ "&BBOX=" + request.getBbox()
					+ "&WIDTH=" + request.getWidth()
					+ "&HEIGHT=" + request.getHeight()
					+ "&SRS="+srs
					+ "&FORMAT="+format
					+  wmsStyleParam
					+ (cqlfilter==null || cqlfilter.isEmpty() || cqlfilter.equals("null") ? "" : "&CQL_FILTER="+cqlfilter)
					+  crsParam
					+ "&TRANSPARENT="+transparent
					+ (elevation==null ? "" : "&ELEVATION="+elevation)
					+ (wmsNonStandard.isEmpty()?"":"&"+wmsNonStandard);



		logger.info("wms request created: "+url);

		return url;
	}

	/**
	 * Added by Francesco M.
	 *
	 * @param image the image
	 * @return the buffered image
	 */
	public static BufferedImage toRGB(BufferedImage image) {

		BufferedImage rgb;
		try{
			rgb = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

//			Color c = new Color(rgb.getRGB(0, 0), true);
	        Graphics2D g = rgb.createGraphics();
//	        g.drawImage(image, 0, 0, c, null);
	        g.drawImage(image, 0, 0, null);
	        g.dispose();

		}catch (Exception e) {
			logger.error("Converting to rgb fail ",e);
			return image;
		}

	    return rgb;
	}

	/**
	 * Error image.
	 *
	 * @param message the message
	 * @return the buffered image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static BufferedImage errorImage(String message) throws IOException {
		// get an error image
		InputStream inputStremLogo = MapGeneratorUtils.class.getResourceAsStream(NAME_IMG_ERROR);
		BufferedImage img = ImageIO.read(inputStremLogo);
		// add an error message
		Graphics g = img.getGraphics();
		g.setColor(COLOR_BLACK);
		g.setFont(FONT);
		g.drawString(message, 55, img.getHeight()/2);

		return img;
	}

	/**
	 * Merge image.
	 *
	 * @param imgRis the img ris
	 * @param img the img
	 * @param opacity the opacity
	 * @param x the x
	 * @param y the y
	 */
	private static void mergeImage(BufferedImage imgRis, BufferedImage img, float opacity, int x, int y) {
		if (img.getHeight() > imgRis.getHeight()
				|| img.getWidth() > img.getWidth()) {
			JOptionPane.showMessageDialog(null,
					"Foreground Image Is Bigger In One or Both Dimensions"
							+ "\nCannot proceed with overlay."
							+ "\n\n Please use smaller Image for foreground");
			return;
		}

		// Create a Graphics  from the background image
		Graphics2D g = imgRis.createGraphics();
		// Set Antialias Rendering
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// draw background image (at location (0,0))
		g.drawImage(imgRis, 0, 0, null);

		// create a rescale filter op that makes the image opaque.
		float[] scales = { 1f, 1f, 1f, opacity };
		float[] offsets = new float[4];
		RescaleOp rop = new RescaleOp(scales, offsets, null);

		try{
			// draw the image, applying the opacity filter
			g.drawImage(img, rop, x, y);

		}catch(Exception e){
			logger.warn("Draw image applying the opacity filter error: ",e);
			logger.info("Trying converting to rgb image and merge");
			BufferedImage rgbImg = toRGB(img);
			g.drawImage(rgbImg, rop, x, y);
		}

		g.dispose();
	}


	/**
	 * The Class ImageLoaderThread.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Nov 27, 2015
	 */
	private static class ImageLoaderThread implements Runnable{
		private String url;
		private boolean first=false;
		private float opacity;
		private BufferedImage img=null;

		/**
		 * Instantiates a new image loader thread.
		 *
		 * @param url the url
		 * @param opacity the opacity
		 */
		public ImageLoaderThread(String url, String opacity){
			this.url = url;
			this.first = true;
			this.opacity = Float.valueOf(opacity);
		}

		/**
		 * Instantiates a new image loader thread.
		 *
		 * @param url the url
		 */
		public ImageLoaderThread(String url){
			this.url = url;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run(){
			logger.trace(" loading image at url: "+url+(first? " (first)" : ""));
			try {
				img = ImageIO.read(new URL(url));
				if (first) {
					BufferedImage imgRis = getBackgroundImage(img);
					mergeImage(imgRis, img, opacity, 0, 0);
					img = imgRis;
				}
			} catch (Exception e) {
				e.printStackTrace();
				img=null;
			}
		}

		/**
		 * Gets the img.
		 *
		 * @return the img
		 */
		public BufferedImage getImg() {
			return img;
		}
	}

	/**
	 * Gets the background image.
	 *
	 * @param referImg the refer img
	 * @return the background image
	 */
	private static BufferedImage getBackgroundImage(BufferedImage referImg) {
		int w = referImg.getWidth();
		int h = referImg.getHeight();

		BufferedImage backgroundImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g = backgroundImg.getGraphics();
		g.setColor(BGCOLOR);
		g.fillRect(0, 0, w, h);
		g.dispose();

		return backgroundImg;
	}

	/**
	 * Gets the output extension.
	 *
	 * @param outputFormat the output format
	 * @return the output extension
	 */
	public static String getOutputExtension(String outputFormat) {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("image/jpeg", "jpeg");
		hash.put("image/gif", "gif");
		hash.put("image/png", "png");

		if (outputFormat==null || hash.get(outputFormat)==null)
			return "jpeg";
		else
			return hash.get(outputFormat);
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
	    String LINK1 = "http://romeo.jrc.it/maps/mapserv.cgi?map=../mapfiles/acpmap_static.map&LAYERS=truemarble&TRANSPARENT=FALSE&FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&EXCEPTIONS=application%2Fvnd.ogc.se_inimage&SRS=EPSG%3A4326&BBOX=-180,90,180,-90&WIDTH=1024&HEIGHT=768";
	    String LINK2 = "http://egip.brgm-rec.fr/wxs/?SERVICE=WMS&version=1.1.0&REQUEST=GetMap&LAYERS=HeatFlowUnit&BBOX=-14.52392578125,37.94677734375,43.70361328125,56.27197265625&WIDTH=1325&HEIGHT=417&SRS=EPSG:4326&FORMAT=image/png";

	    BufferedImage img1 = null;
	    BufferedImage img2 = null;
        try {
        	System.out.println("reading link 1");
        	img1 = ImageIO.read(new URL(LINK1));
        	System.out.println("reading link 2");
         	img2 = ImageIO.read(new URL(LINK2));

         	System.out.println("merging");
         	mergeImage(img1, img2, 0.8f, 0, 0);

            File outputfile = new File("saved.gif");
            ImageIO.write(img1, "gif", outputfile);
//	            img = ImageIO.read(new File("image.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

	}

	/**
	 * Split parameter.
	 *
	 * @param request the request
	 * @param paramName the param name
	 * @return the string[]
	 */
	public static String[] splitParameter(HttpServletRequest request, String paramName) {
		String paramValue = request.getParameter(paramName);

		if(paramName==null)
			return new String[1];

		String[] result = paramValue.split(";");
		return result;
	}

	/**
	 * Split parameter.
	 *
	 * @param values the values
	 * @param paramName the param name
	 * @return the string[]
	 */
	public static String[] splitParameter(String values, String paramName) {
		String[] result = values.split(";");
		return result;
	}


	/**
	 * Creates the map image.
	 *
	 * @param mimeType the mime type
	 * @param parameters the parameters
	 * @return the buffered image
	 * @throws Exception the exception
	 */
	public static BufferedImage createMapImage(String mimeType, Map<String, String> parameters) throws Exception {

		if(parameters!=null){

			String bbox = parameters.get("bbox");
			String width = parameters.get("width");
			String height = parameters.get("height");

			String[] geoservers = splitParameter(parameters.get("geoservers"), "geoservers");
			String[] layers = splitParameter(parameters.get("layers"), "layers");
			String[] styles = splitParameter(parameters.get("styles"), "styles");
			String[] opacities = splitParameter(parameters.get("opacities"), "opacities");
			String[] cqlfilters = splitParameter(parameters.get("cqlfilters"), "cqlfilters");
			String[] gsrefs = splitParameter(parameters.get("gsrefs"), "gsrefs");

			String[] crs = splitParameter(parameters.get("crs"), "crs");
			String[] wmsServerVersions = splitParameter(parameters.get("wmsServerVersions"), "wmsServerVersions");
			String[] srs = splitParameter(parameters.get("srs"), "srs");
			String[] formats = splitParameter(parameters.get("formats"), "formats");

			String[] wmsNotStandardParameters = splitParameter(parameters.get("wmsNonStandardParameters"), "wmsNonStandardParameters");

			String[] elevations = splitParameter(parameters.get("elevations"), "elevations");

			try {

				return createMapImage(mimeType, bbox, width, height, geoservers, layers, styles, opacities, cqlfilters, gsrefs, srs, crs, formats, wmsServerVersions, wmsNotStandardParameters, elevations);
			} catch (IOException e) {
				logger.error("An error occurred when createMapImage: ",e);
				throw new Exception("An error occurred when generating map: ",e);
			}

		}else
			throw new Exception("An error occurred when generating map: Map parameters is null");
	}

}
