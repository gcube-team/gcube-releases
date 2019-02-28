package org.gcube.common.gis.datamodel.utils;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerType;
import org.gcube.common.gis.datamodel.enhanced.BoundsInfo;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube.common.gis.datamodel.enhanced.TransectInfo;
import org.gcube.common.gis.datamodel.enhanced.WMSContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadTemplate {
	
	private static final Logger logger = LoggerFactory.getLogger(ReadTemplate.class);
	
	public static WMSContextInfo getWMSContextTemplate() throws DocumentException {
		
		logger.debug("get WMSContextTemplate");
		
		
		InputStream is = ReadTemplate.class.getResourceAsStream("/org/gcube/common/gis/datamodel/resources/template/WMSContext.xml");
		
		WMSContextInfo wmsContext = new WMSContextInfo();
		SAXReader reader = new SAXReader();
    	Document doc = reader.read(is);
    	
    	Element root = doc.getRootElement();
    	for ( Iterator<?> roots = root.elementIterator(); roots.hasNext(); ) {
    		Element el_root = (Element) roots.next();
    		if (el_root.getName().contentEquals("General")) {
    			for ( Iterator<?> i = el_root.elementIterator(); i.hasNext(); ) {
    	    		Element el = (Element) i.next();
    	    		if (el.getName().contentEquals("Window")) {
    	    			if (el.attribute("width") != null) wmsContext.setWidth(Integer.parseInt(el.attributeValue("width")));
    	            	if (el.attribute("height") != null) wmsContext.setHeight(Integer.parseInt(el.attributeValue("height")));
    	    		} else if (el.getName().contentEquals("DisplayProjection")) {
    	    			wmsContext.setDisplayProjection(el.getTextTrim());
    	    		} else if (el.getName().contentEquals("MaxExtent")) {
    	    			BoundsInfo bounds = new BoundsInfo();
    	            	if (el.attribute("SRS") != null) bounds.setCrs(el.attributeValue("SRS").trim());
    	            	if (el.attribute("minx") != null) bounds.setMinx(Double.parseDouble(el.attributeValue("minx").trim()));
    	            	if (el.attribute("maxx") != null) bounds.setMaxx(Double.parseDouble(el.attributeValue("maxx").trim()));
    	            	if (el.attribute("miny") != null) bounds.setMiny(Double.parseDouble(el.attributeValue("miny").trim()));
    	            	if (el.attribute("maxy") != null) bounds.setMaxy(Double.parseDouble(el.attributeValue("maxy").trim()));
    	            	wmsContext.setMaxExtent(bounds);
    	    		} else if (el.getName().contentEquals("MinExtent")) {
    	    			BoundsInfo bounds = new BoundsInfo();
    	            	if (el.attribute("SRS") != null) bounds.setCrs(el.attributeValue("SRS").trim());
    	            	if (el.attribute("minx") != null) bounds.setMinx(Double.parseDouble(el.attributeValue("minx").trim()));
    	            	if (el.attribute("maxx") != null) bounds.setMaxx(Double.parseDouble(el.attributeValue("maxx").trim()));
    	            	if (el.attribute("miny") != null) bounds.setMiny(Double.parseDouble(el.attributeValue("miny").trim()));
    	            	if (el.attribute("maxy") != null) bounds.setMaxy(Double.parseDouble(el.attributeValue("maxy").trim()));
    	            	wmsContext.setMinExtent(bounds);
    	    		} else if (el.getName().contentEquals("NumZoomLevels")) {
    	    			wmsContext.setNumZoomLevels(Integer.parseInt(el.getTextTrim()));
    	    		} else if (el.getName().contentEquals("ZoomTo")) {
    	    			wmsContext.setZoomTo(Integer.parseInt(el.getTextTrim()));
    	    		} else if (el.getName().contentEquals("Lon_center")) {
    	    			wmsContext.setLon_center(Double.parseDouble(el.getTextTrim()));
    				} else if (el.getName().contentEquals("Lat_center")) {
    					wmsContext.setLat_center(Double.parseDouble(el.getTextTrim()));
    				} else if (el.getName().contentEquals("Units")) {
    	    			wmsContext.setUnits(el.getTextTrim());
    				} else if (el.getName().contentEquals("Name")) {
    	    			wmsContext.setName(el.getTextTrim());
    	    		} else if (el.getName().contentEquals("Title")) {
    	    			wmsContext.setTitle(el.getTextTrim());
    	    		} else if (el.getName().contentEquals("Abstract")) {
    	    			wmsContext.set_abstract(el.getTextTrim());
    	    		} else if (el.getName().contentEquals("MaxResolution")) {
    					wmsContext.setMaxResolution(Double.parseDouble(el.getTextTrim()));
    	    		} else if (el.getName().contentEquals("KeywordList")) {
    	    			ArrayList<String> keywords = new ArrayList<String>();
    	    			for ( Iterator<?> ii = el.elementIterator(); ii.hasNext(); ) {
	    					Element m = (Element) ii.next();
	    					if (m.getName().contentEquals("Keyword")) {
	    						keywords.add(m.getTextTrim());
	    					}
    	    			}
						wmsContext.setKeywords(keywords);
    	    		} else if (el.getName().contentEquals("LogoURL")) {
    	    			wmsContext.setLogoFormat(el.attributeValue("format").trim());
    	    			wmsContext.setLogoHeight(Integer.parseInt(el.attributeValue("height").trim()));
    	    			wmsContext.setLogoWidth(Integer.parseInt(el.attributeValue("width").trim()));
    	    			for ( Iterator<?> ii = el.elementIterator(); ii.hasNext(); ) {
	    					Element m = (Element) ii.next();
	    					if (m.getName().contentEquals("OnlineResource")) {
	    						wmsContext.setLogoUrl(m.attributeValue("href").trim());
	    						break;
	    					}
    	    			}
    	    		} else if (el.getName().contentEquals("ContactInformation")) {
    	    			wmsContext.setContactInformation(el.getStringValue().trim());
					}
    			}
    		}
    		if (el_root.getName().contentEquals("LayerList")) {
    			ArrayList<String> layerTypeInfo = new ArrayList<String>();
    			for ( Iterator<?> i = el_root.elementIterator(); i.hasNext(); ) {
    	    		Element el = (Element) i.next();if (el.attribute("queryable") != null) 
    	    		if (el.getName().contentEquals("Layer")) {
    	    			layerTypeInfo.add(el.attributeValue("name"));
    	    			}
    			}
    			//logger.debug(layerTypeInfo.size());
    			wmsContext.setLayers(layerTypeInfo);
    		}
    	}
		return wmsContext;
	}
	
	public static LayerInfo getLayerTemplate(LayerType type) throws Exception {
		
		InputStream in;
		if (type == LayerType.Biodiversity) {
			in = ReadTemplate.class.getResourceAsStream("/org/gcube/common/gis/datamodel/resources/template/BiodiversityTemplate.xml");
		} else if (type == LayerType.Prediction) {
			in = ReadTemplate.class.getResourceAsStream("/org/gcube/common/gis/datamodel/resources/template/PredictionTemplate.xml");
		} else if (type == LayerType.Environment) {
			in = ReadTemplate.class.getResourceAsStream("/org/gcube/common/gis/datamodel/resources/template/EnvironmentTemplate.xml");
		} else if (type == LayerType.PointMap) {
			in = ReadTemplate.class.getResourceAsStream("/org/gcube/common/gis/datamodel/resources/template/PointMapTemplate.xml");	
		} else {
			throw new Exception("Resource tipology isn't defined");
		}
		boolean withTransect = false;
		
		LayerInfo new_layer = new LayerInfo();
		SAXReader reader = new SAXReader();
    	Document doc = reader.read(in);
    	
    	Element root = doc.getRootElement();
    	
		if (root.attribute("queryable") != null) new_layer.setQueryable(root.attributeValue("queryable").contentEquals("1"));
		if (root.attribute("hidden") != null) new_layer.setVisible(root.attributeValue("hidden").contentEquals("0"));
		if (root.attribute("transect") != null) 
			withTransect = root.attributeValue("transect").contentEquals("1");
		
		
		//System.out.println("aaaaaaaaaaaaa");
    	for ( Iterator<?> i = root.elementIterator(); i.hasNext(); ) {
    		Element field = (Element) i.next();
			if (field.getName().contentEquals("Name")) {
				new_layer.setName(field.getTextTrim());
			} else if (field.getName().contentEquals("Title")) {
				new_layer.setTitle(field.getTextTrim());
			} else if (field.getName().contentEquals("Abstract")) {
				new_layer.set_abstract(field.getTextTrim());
			} else if (field.getName().contentEquals("SRS")) {
				new_layer.setSrs(field.getTextTrim());
			} else if (field.getName().contentEquals("BaseLayer")) {
				new_layer.setBaseLayer(field.getTextTrim().contentEquals("true"));
			} else if (field.getName().contentEquals("Trasparent")) {
				new_layer.setTrasparent(field.getTextTrim().contentEquals("true"));
			} else if (field.getName().contentEquals("HasLegend")) {
				new_layer.setHasLegend(field.getTextTrim().contentEquals("true"));
			} else if (field.getName().contentEquals("Selected")) {
				new_layer.setSelected(field.getTextTrim().contentEquals("true"));
			} else if (field.getName().contentEquals("MaxExtent")) {
				BoundsInfo bounds = new BoundsInfo();
				if (field.attribute("SRS") != null) bounds.setCrs(field.attributeValue("SRS").trim());
	            if (field.attribute("minx") != null) bounds.setMinx(Double.parseDouble(field.attributeValue("minx").trim()));
	            if (field.attribute("maxx") != null) bounds.setMaxx(Double.parseDouble(field.attributeValue("maxx").trim()));
	            if (field.attribute("miny") != null) bounds.setMiny(Double.parseDouble(field.attributeValue("miny").trim()));
	            if (field.attribute("maxy") != null) bounds.setMaxy(Double.parseDouble(field.attributeValue("maxy").trim()));
				new_layer.setMaxExtent(bounds);
			} else if (field.getName().contentEquals("MinExtent")) {
				BoundsInfo bounds = new BoundsInfo();
				if (field.attribute("SRS") != null) bounds.setCrs(field.attributeValue("SRS").trim());
	            if (field.attribute("minx") != null) bounds.setMinx(Double.parseDouble(field.attributeValue("minx").trim()));
	            if (field.attribute("maxx") != null) bounds.setMaxx(Double.parseDouble(field.attributeValue("maxx").trim()));
	            if (field.attribute("miny") != null) bounds.setMiny(Double.parseDouble(field.attributeValue("miny").trim()));
	            if (field.attribute("maxy") != null) bounds.setMaxy(Double.parseDouble(field.attributeValue("maxy").trim()));
	            new_layer.setMinExtent(bounds);
			} else if (field.getName().contentEquals("Opacity")) {
				new_layer.setOpacity(Double.parseDouble(field.getTextTrim()));
			} else if (field.getName().contentEquals("Buffer")) {
				new_layer.setBuffer(Integer.parseInt(field.getTextTrim()));
			} else if (field.getName().contentEquals("Type")) {
				if (!field.getTextTrim().contentEquals("")) 
					new_layer.setType(LayerType.valueOf(field.getTextTrim()));
			} else if (field.getName().contentEquals("Server")) {
				new_layer.setServerLogin(field.attributeValue("login").trim());
				new_layer.setServerPassword(field.attributeValue("pasword").trim());
				new_layer.setServerType(field.attributeValue("type").trim());
				new_layer.setServerProtocol(field.attributeValue("service").trim());
				for ( Iterator<?> iii = field.elementIterator(); iii.hasNext(); ) {
					Element ext = (Element) iii.next();
					if (ext.getName().contentEquals("OnlineResource")) {
						if (ext.attribute("url") != null) new_layer.setUrl(ext.attributeValue("url").trim());
						break;
					}
				}
			} else if (field.getName().contentEquals("Transect") && withTransect) {
				TransectInfo transect = new TransectInfo();
				transect.setEnabled(withTransect);
				transect.setMaxelements(Integer.parseInt(field.attributeValue("maxelements").trim()));
				transect.setMinimumgap(Integer.parseInt(field.attributeValue("minimumgap").trim()));
				transect.setTable(field.attributeValue("table").trim());
				ArrayList<String> fields = new ArrayList<String>();
				for ( Iterator<?> iii = field.elementIterator(); iii.hasNext(); ) {
					Element ext = (Element) iii.next();
					if (ext.getName().contentEquals("Field")) {
						fields.add(ext.getTextTrim());
					}
				}
				transect.setFields(fields);
				new_layer.setTransect(transect);
			} else if (field.getName().contentEquals("StyleList")) {
				ArrayList<String> styles = new ArrayList<String>();
				for ( Iterator<?> iii = field.elementIterator(); iii.hasNext(); ) {
					Element ext = (Element) iii.next();
					if (ext.getName().contentEquals("Style")) {
						for ( Iterator<?> iiii = ext.elementIterator(); iiii.hasNext(); ) {
	    					Element sty = (Element) iiii.next();
	    					if (sty.getName().contentEquals("Name")) {
	    						if (ext.attribute("current") != null) {
	    							if (ext.attributeValue("current").contentEquals("1")) {
	    								new_layer.setDefaultStyle(sty.getTextTrim());
	    								styles.add(sty.getTextTrim());
	    							} else {
	    								styles.add(sty.getTextTrim());
	    							}
	    						}
	    						break;
	    					}
						}
						break;
					}
				}
				new_layer.setStyles(styles);
			}
    	}
    	return new_layer;
	}
}
