package org.gcube.dataanalysis.geo.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ucar.nc2.constants.FeatureType;
import ucar.nc2.ft.FeatureDataset;
import ucar.nc2.ft.FeatureDatasetFactoryManager;
/**
 * Explores the contents of a Thredds instance
 * Guesses the nature of a file hosted on the server
 * @author coro
 *
 */
public class ThreddsExplorer {

	// http://thredds.research-infrastructures.eu:8080/thredds/catalog/public/netcdf/catalog.xml
	public static String timePrefix = "time:";

	public static List<String> getFiles(String catalogURL) throws Exception {

		String xml = HttpRequest.sendGetRequest(catalogURL, null);
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression xPathExpression = xpath.compile("//child::*[local-name()='catalog']/child::*[local-name()='dataset']/child::*[local-name()='dataset']");
		InputSource inputSource = new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		NodeList nodes = (NodeList) xPathExpression.evaluate(inputSource, XPathConstants.NODESET);
		List<String> fileNames = new ArrayList<String>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			if (name != null)
				fileNames.add(name);
		}

		return fileNames;
	}

	// A GridDatatype is like a specialized Variable that explicitly handles X,Y,Z,T dimensions
	public static boolean isGridDataset(String filename) {
		try {
			AnalysisLogger.getLogger().debug("Analyzing file " + filename);
			Formatter errlog = new Formatter();
			FeatureDataset fdataset = FeatureDatasetFactoryManager.open(FeatureType.GRID, filename, null, errlog);
			if (fdataset == null) {
				// System.out.printf("GRID Parse failed --> %s\n", errlog);
				AnalysisLogger.getLogger().debug("ThreddsDataExplorer-> NOT GRID");
				return false;
			} else
				return true;
		} catch (Throwable e) {
			return false;
		}
	}

	// A GridDatatype is like a specialized Variable that explicitly handles X,Y,Z,T dimensions
	public static boolean isPointDataset(String filename) {
		try {
			Formatter errlog = new Formatter();
			FeatureDataset fdataset = FeatureDatasetFactoryManager.open(FeatureType.POINT, filename, null, errlog);
			if (fdataset == null) {
				AnalysisLogger.getLogger().debug("ThreddsDataExplorer-> NOT POINT");
				return false;
			} else
				return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDataset(String filename) throws Exception {
		boolean isdataset = false;
		try {
			Formatter errlog = new Formatter();
			FeatureType[] fts = FeatureType.values();
			for (int i = 0; i < fts.length; i++) {
				FeatureDataset fdataset = FeatureDatasetFactoryManager.open(fts[i], filename, null, errlog);
				if (fdataset == null) {
					// System.out.printf(fts[i]+": Parse failed --> %s\n",errlog);
				} else {
					AnalysisLogger.getLogger().debug("ThreddsDataExplorer-> " + fts[i] + " OK!");
					isdataset = true;
				}
			}
		} catch (Exception e) {
		}
		return isdataset;
	}

}
