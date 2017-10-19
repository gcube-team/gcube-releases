package org.gcube.portlets.user.gisviewer.server;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class MapGenerator extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(MapGenerator.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		// get parameters

		String[] strRis = getOutputExtension(request);
		String outputFormat = strRis[0];
		String outputExtension = strRis[1];

		String bbox = request.getParameter("bbox");
		String width = request.getParameter("width");
		String height = request.getParameter("height");

		String[] geoservers = MapGeneratorUtils.splitParameter(request, "geoservers");
		String[] layers = MapGeneratorUtils.splitParameter(request, "layers");
		String[] styles = MapGeneratorUtils.splitParameter(request, "styles");
		String[] opacities = MapGeneratorUtils.splitParameter(request, "opacities");
		String[] cqlfilters = MapGeneratorUtils.splitParameter(request, "cqlfilters");
		String[] gsrefs = MapGeneratorUtils.splitParameter(request, "gsrefs");

		String[] crs = MapGeneratorUtils.splitParameter(request, "crs");
		String[] wmsServerVersions = MapGeneratorUtils.splitParameter(request, "wmsServerVersions");
		String[] srs = MapGeneratorUtils.splitParameter(request, "srs");
		String[] formats = MapGeneratorUtils.splitParameter(request, "formats");

		String[] wmsNotStandardParameters = MapGeneratorUtils.splitParameter(request, "wmsNonStandardParameters");

		String[] elevations = MapGeneratorUtils.splitParameter(request, "elevations");

		/*Double[] elevationValues = null;
		if(elevations!=null){
			elevationValues = new Double[elevations.length];
			for (int i=0; i<elevations.length; i++) {
				elevationValues[i] = elevations[i]==null || elevations[i].isEmpty()?null:Double.parseDouble(elevations[i]);
			}
		}*/

//			testUrlImages(urls, opacities, response);

//		logger.trace("geoservers: "+geoservers);
//		logger.trace("gsrefs: "+gsrefs);
		BufferedImage imgRis;
		try {
//			imgRis = MapGeneratorUtils.createMapImage(outputFormat, bbox, width, height, geoservers, layers, styles, opacities, cqlfilters, gsrefs);

			imgRis = MapGeneratorUtils.createMapImage(outputFormat, bbox, width, height, geoservers, layers, styles, opacities, cqlfilters, gsrefs, srs,crs, formats, wmsServerVersions, wmsNotStandardParameters, elevations);

			//--Send the image data to response
			response.setContentType(outputFormat);
			OutputStream outputStream = response.getOutputStream();
			ImageIO.write(imgRis, outputExtension, outputStream);
			outputStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String[] getOutputExtension(HttpServletRequest request) {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("image/jpeg", "jpeg");
		hash.put("image/gif", "gif");
		hash.put("image/png", "png");

		String ris[] = new String[2];
		ris[0] = request.getParameter("outputFormat");
		if (ris[0]==null || hash.get(ris[0])==null) {
			ris[0] = "image/jpeg";
			ris[1] = "jpeg";
		} else
			ris[1] = hash.get(ris[0]);
		return ris;
	}

	@SuppressWarnings("unused")
	private void testUrlImages(String[] urls, String[] opacities, HttpServletResponse response) throws Exception {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print("<html><body bgcolor='#FFFF00'><h1><center>TEST</center></h1>");

		int i=0;
		for (String lUrl : urls) {
			out.println(lUrl+"<br/><b>"+opacities[i++]+"</b><br/><img src='"+lUrl+"'/><br>");
		}

		out.println("</body></html>");
	}

	@SuppressWarnings("unused")
	private String decodeUrl(String url) {
		String url2 = url.toString();
		url2 = url2.replaceAll("%25", "%");
		url2 = url2.replaceAll("%26", "&");
		url2 = url2.replaceAll("%2C", ",");
		url2 = url2.replaceAll("%3B", ";");
		url2 = url2.replaceAll("%3A", ":");
		url2 = url2.replaceAll("%2F", "/");
		return url2;
	}
}