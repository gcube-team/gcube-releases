package gr.cite.geoanalytics.manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.cite.clustermanager.actuators.layers.DataCreatorGeoanalytics;
import gr.cite.clustermanager.actuators.layers.DataMonitor;
import gr.cite.clustermanager.exceptions.NoAvailableGos;
import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.gaap.servicelayer.GeospatialBackendClustered;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.style.Style;
import gr.cite.geoanalytics.dataaccess.entities.style.dao.StyleDao;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.gos.client.GeoserverManagement;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@Service
public class StyleManager {

	public static Logger logger = LoggerFactory.getLogger(StyleManager.class);
	public static ObjectMapper mapper = new ObjectMapper();
	
	@Autowired private StyleDao styleDao;
	
	@Autowired private LayerManager layerManager;
	
	//the following is part of the client to exchange information with the gos nodes
	@Autowired private GeospatialBackendClustered geospatialBackendClustered;
	//these two are part of the Zookeeper Cluster management (monitoring and editing) 
	@Autowired private DataMonitor dataMonitor;
	@Autowired private DataCreatorGeoanalytics dataCreatorGeoanalytics;
	//this is for managing the geoserver instances
	@Autowired private GeoserverManagement geoserverManagement;
	//this is for traffic shaping
	@Autowired private TrafficShaper trafficShaper;
	
	public StyleManager() {}
	
	public boolean addStyle() {
		return true;
	}
	
	public boolean removeStyle() {
		return true;
	}
	
	public boolean updateLayerStyle() {
		return true;
	}
	
	public List<Style> listAllStyles() throws CustomException {
		List<Style> styles = styleDao.getAll();
		if (styles == null) {
			throw new CustomException(HttpStatus.NOT_FOUND, "Styles not found");
		}else {
			return styles;
		}
	}
	
	@Transactional
	public void createStyle(Style style) throws Exception {
		if (style != null) {
			if (this.styleDao.create(style) == null) {
				throw new Exception("Could not create " + style);
			}
			
			Set<GosDefinition> gosDefinitions = trafficShaper.getAllGosEndpoints();
			
			for(GosDefinition gd : gosDefinitions) {
				geoserverManagement.addStyle(gd.getGosEndpoint(), style.getName(), style.getContent());
			}
			
			logger.info("Created " + style + " successfully!");
		}
	}
	
	@Transactional
	public void deleteStyle(Style style) throws Exception {
	
		//TODO:delete from Geoserver and from layer references
		
		List<Layer> layersWithStyle = layerManager.findLayersWithStyle(style.getName());
		
		if(layersWithStyle != null) {
		
			for(Layer layer : layersWithStyle){
			
				Set<GosDefinition> gosLayerDefinitions = dataMonitor.getAvailableGosFor(layer.getId().toString());
				
				for(GosDefinition gd : gosLayerDefinitions) {
					geoserverManagement.setDefaultLayerStyle(gd.getGosEndpoint(), layer.getId().toString(), "line", null, null, null);
				}
			}
		}
		
		Set<GosDefinition> gosDefinitions = trafficShaper.getAllGosEndpoints();
		
		for(GosDefinition gd : gosDefinitions) {
			geoserverManagement.removeStyle(gd.getGosEndpoint(), style.getName());
			
		}
		
		layerManager.deleteLayersStyle(style.getName());
		
		this.styleDao.delete(style);
		
		
	}
	
	public Style findStyleById(String id) throws Exception {
		Style style = styleDao.read(UUID.fromString(id));
		if (style == null) {
			throw new CustomException(HttpStatus.NOT_FOUND, "Style not found");
		}
		return style;
	}
	
	public Style findStyleById(UUID id) throws Exception {
		Style style = styleDao.read(id);
		if (style == null) {
			throw new CustomException(HttpStatus.NOT_FOUND, "Style not found");
		}
		return style;
	}
	
	@Transactional
	public void checkStyleNotExists(String name) throws Exception {
		Style style = this.styleDao.findStyleByName(name);
		if (style != null) {
			throw new CustomException(HttpStatus.CONFLICT, "Style \"" + name + "\" already exists!");
		}
	}
	
	@Transactional
	public void editStyle(Style style, String name, String description, String content) throws Exception {
		
		Set<GosDefinition> gosDefinitions = trafficShaper.getAllGosEndpoints();
		
		for(GosDefinition gd : gosDefinitions) {
			geoserverManagement.removeStyle(gd.getGosEndpoint(), style.getName());
			geoserverManagement.addStyle(gd.getGosEndpoint(), name, style.getContent());
		}
		
		layerManager.editLayersStyle(name, style.getName());
		
		style.setName(name);
		style.setDescription(description);
		style.setContent(content);
		this.styleDao.update(style);
		
		
		logger.info(style + " has been edited successfully!");
	}
	
	public List<String> getAllStyles() throws NoAvailableGos, IOException {
		
		GosDefinition gosDefinition = trafficShaper.getGosForNewLayer();
		
		return geoserverManagement.getAllStyles(gosDefinition.getGosEndpoint());
		
	}

	public String createStyleXml(Map<String, Object> styleMap, List<String> iconFiles) throws ParserConfigurationException, TransformerException {
		String xmlString = "";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("StyledLayerDescriptor");
		doc.appendChild(rootElement);

		Attr schemaLocation = doc.createAttribute("xsi:schemaLocation");
		schemaLocation.setValue("http://www.opengis.net/sld StyledLayerDescriptor.xsd");
		rootElement.setAttributeNode(schemaLocation);

		Attr xmlns = doc.createAttribute("xmlns");
		xmlns.setValue("http://www.opengis.net/sld");
		rootElement.setAttributeNode(xmlns);

		Attr ogc = doc.createAttribute("xmlns:ogc");
		ogc.setValue("http://www.opengis.net/ogc");
		rootElement.setAttributeNode(ogc);

		Attr xlink = doc.createAttribute("xmlns:xlink");
		xlink.setValue("http://www.w3.org/1999/xlink");
		rootElement.setAttributeNode(xlink);

		Attr xsi = doc.createAttribute("xmlns:xsi");
		xsi.setValue("http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(xsi);
		// staff elements
		Element namedLayer = doc.createElement("NamedLayer");
		rootElement.appendChild(namedLayer);

		// firstname elements
		Element name = doc.createElement("Name");
		name.appendChild(doc.createTextNode((String) styleMap.get("name")));
		namedLayer.appendChild(name);

		Element userStyle = doc.createElement("UserStyle");
		namedLayer.appendChild(userStyle);
        Element titleName = doc.createElement("Title");
        titleName.appendChild(doc.createTextNode((String) styleMap.get("name")));
        userStyle.appendChild(titleName);

		if ( styleMap.get("description") != null){
			Element description = doc.createElement("Abstract");
			description.appendChild(doc.createTextNode((String) styleMap.get("description")));
            userStyle.appendChild(description);
		}


		Element featureTypeStyle = doc.createElement("FeatureTypeStyle");
		userStyle.appendChild(featureTypeStyle);

		int i = 0;

		while (styleMap.get("Rule"+i) != null) {
			Map<String, String> ruleMap = mapper.convertValue(styleMap.get("Rule"+i) , Map.class);
			if ( ruleMap != null && !ruleMap.isEmpty()) {
				Element rule = doc.createElement("Rule");
				featureTypeStyle.appendChild(rule);

				Element filter = doc.createElement("ogc:Filter");

				if (ruleMap.get("title") != null) {
					Element title = doc.createElement("Title");
					title.appendChild(doc.createTextNode(ruleMap.get("title")));
					rule.appendChild(title);
					rule.appendChild(filter);
				}

				if (ruleMap.get("propertyLessThan") != null && ruleMap.get("propertyMoreThan") != null) {
					if (!(ruleMap.get("propertyLessThan")).equals("") && !(ruleMap.get("propertyMoreThan")).equals("")) {
//						Element and = doc.createElement("ogc:And");
//						filter.appendChild(and);

						if (ruleMap.get("propertyName") != null) {
							Element propertyName = doc.createElement("ogc:PropertyName");
							propertyName.appendChild(doc.createTextNode(ruleMap.get("propertyName")));
							filter.appendChild(propertyName);
						}

						Element lowerBoundary = doc.createElement("ogc:LowerBoundary");
						filter.appendChild(lowerBoundary);
						Element literalLower = doc.createElement("ogc:Literal");
						literalLower.appendChild(doc.createTextNode(ruleMap.get("propertyMoreThan")));
						lowerBoundary.appendChild(literalLower);

						Element upperBoundary = doc.createElement("ogc:UpperBoundary");
						filter.appendChild(upperBoundary);
						Element literalUpper = doc.createElement("ogc:Literal");
						literalUpper.appendChild(doc.createTextNode(ruleMap.get("propertyLessThan")));
						upperBoundary.appendChild(literalUpper);
					} else if (!ruleMap.get("propertyLessThan").equals("")) {
						Element propertyLessThan = doc.createElement("ogc:PropertyIsLessThan");
						filter.appendChild(propertyLessThan);

						if (ruleMap.get("propertyName") != null) {
							Element propertyName = doc.createElement("ogc:PropertyName");
							propertyName.appendChild(doc.createTextNode(ruleMap.get("propertyName")));
							propertyLessThan.appendChild(propertyName);
						}
						Element literal = doc.createElement("ogc:Literal");
						literal.appendChild(doc.createTextNode(ruleMap.get("propertyLessThan")));
						propertyLessThan.appendChild(literal);
					} else if (!ruleMap.get("propertyMoreThan").equals("")) {
						Element propertyMoreThan = doc.createElement("ogc:PropertyIsGreaterThanOrEqual");
						filter.appendChild(propertyMoreThan);
						if (ruleMap.get("propertyName") != null) {
							Element propertyName = doc.createElement("ogc:PropertyName");
							propertyName.appendChild(doc.createTextNode(ruleMap.get("propertyName")));
							propertyMoreThan.appendChild(propertyName);
						}
						Element literal = doc.createElement("ogc:Literal");
						literal.appendChild(doc.createTextNode(ruleMap.get("propertyMoreThan")));
						propertyMoreThan.appendChild(literal);
					}
				}
				Element symbol = null;
				if (ruleMap.get("symbol") != null) {

					if (ruleMap.get("symbol").equals("polygon")) {
						symbol = doc.createElement("ogc:PolygonSymbolizer");
						rule.appendChild(symbol);
					} else if (ruleMap.get("symbol").equals("point")) {
						symbol = doc.createElement("ogc:PointSymbolizer");
						rule.appendChild(symbol);
					} else if (ruleMap.get("symbol").equals("line")) {
						symbol = doc.createElement("ogc:LineSymbolizer");
						rule.appendChild(symbol);
					}
					if (ruleMap.get("propertyFill") != null) {
						if (!ruleMap.get("propertyFill").equals("-") && !ruleMap.get("propertyFill").equals("icon")) {
							Element fill = doc.createElement("ogc:Fill");
							symbol.appendChild(fill);

							Element cssParameter = doc.createElement("ogc:CssParameter");
							fill.appendChild(cssParameter);

							Attr attr = doc.createAttribute("name");
							attr.setValue("fill");
							cssParameter.setAttributeNode(attr);

							if (ruleMap.get("propertyFill").equals("red")) {
								// red is #e80000
								cssParameter.appendChild(doc.createTextNode("#e80000"));
							} else if (ruleMap.get("propertyFill").equals("blue")) {
								cssParameter.appendChild(doc.createTextNode("#0e25d1"));
							} else if (ruleMap.get("propertyFill").equals("green")) {
								cssParameter.appendChild(doc.createTextNode("#10b218"));
							} else if (ruleMap.get("propertyFill").equals("yellow")) {
								cssParameter.appendChild(doc.createTextNode("#deef23"));
							}

							Element cssParameterOpacity = doc.createElement("ogc:CssParameter");
							cssParameterOpacity.appendChild(doc.createTextNode("0.8"));
							fill.appendChild(cssParameterOpacity);

							Attr attrOpacity = doc.createAttribute("name");
							attrOpacity.setValue("fill-opacity");
							cssParameterOpacity.setAttributeNode(attrOpacity);

							if (ruleMap.get("symbol").equals("polygon")) {
								Element stroke = doc.createElement("ogc:Stroke");
								symbol.appendChild(stroke);
								Element cssStrokeParameter = doc.createElement("ogc:CssParameter");
								cssStrokeParameter.appendChild(doc.createTextNode("#000000"));
								stroke.appendChild(cssStrokeParameter);
								Attr attrStroke = doc.createAttribute("name");
								attrStroke.setValue("stroke");
								cssStrokeParameter.setAttributeNode(attrStroke);

								Element cssStrokeWidthParameter = doc.createElement("ogc:CssParameter");
								cssStrokeWidthParameter.appendChild(doc.createTextNode("1"));
								stroke.appendChild(cssStrokeWidthParameter);
								Attr attrStrokeWidth = doc.createAttribute("name");
								attrStrokeWidth.setValue("stroke-width");
								cssStrokeWidthParameter.setAttributeNode(attrStrokeWidth);
							}

						}
						else if (ruleMap.get("propertyFill").equals("icon")) {
							Element graphic = doc.createElement("Graphic");
							symbol.appendChild(graphic);
							Element exGraphic = doc.createElement("ExternalGraphic");
							graphic.appendChild(exGraphic);
							Element onlineSource = doc.createElement("OnlineResource");
							exGraphic.appendChild(onlineSource);

							Attr attrOnlineType = doc.createAttribute("xlink:type");
							attrOnlineType.setValue("simple");
							onlineSource.setAttributeNode(attrOnlineType);
							Attr attrOnlineHref = doc.createAttribute("xlink:href");
							attrOnlineHref.setValue(iconFiles.get(i));
							onlineSource.setAttributeNode(attrOnlineHref);
							Element format = doc.createElement("Format");
							format.appendChild(doc.createTextNode("image/"+ FilenameUtils.getExtension(iconFiles.get(i))));
							exGraphic.appendChild(format);
							Element size = doc.createElement("Size");
							graphic.appendChild(size);
							Element literal = doc.createElement("ogc:Literal");
							literal.appendChild(doc.createTextNode("30"));
							size.appendChild(literal);

						}
					}
				}
			}
			i++;
		}

		/*****************************************Extra Default Rule*********************************************************/

		String node = "<Rule><Title>Boundary</Title>" +
				"<LineSymbolizer>" +
				"<Stroke>" +
				"<CssParameter name=\"stroke-width\">0.2</CssParameter>" +
				"</Stroke>" +
				"</LineSymbolizer>" +
				"<TextSymbolizer>" +
				"<Label>" +
				"<ogc:PropertyName>STATE_ABBR</ogc:PropertyName>" +
				"</Label>" +
				"<Font>" +
				"<CssParameter name=\"font-family\">Times New Roman</CssParameter>" +
				"<CssParameter name=\"font-style\">Normal</CssParameter>" +
				"<CssParameter name=\"font-size\">14</CssParameter>" +
				"</Font>" +
				"<LabelPlacement>" +
				"<PointPlacement>" +
				"<AnchorPoint>" +
				"<AnchorPointX>0.5</AnchorPointX>" +
				"<AnchorPointY>0.5</AnchorPointY>" +
				"</AnchorPoint>" +
				"</PointPlacement>" +
				"</LabelPlacement>" +
				"</TextSymbolizer></Rule>";

		try {
			Element defaultRule =  DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(new ByteArrayInputStream(node.getBytes()))
					.getDocumentElement();

			Node importedNode = doc.importNode(defaultRule, true);
			userStyle.appendChild(importedNode);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		xmlString = toString(doc);

		return xmlString;
	}



	public static String toString(Document doc) {
		try {
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}

	public void addStyleIcons(String styleName, ArrayList<MultipartFile> iconFiles) {
		Set<GosDefinition> gosDefinitions = null;
		try {
			gosDefinitions = trafficShaper.getAllGosEndpoints();
		} catch (NoAvailableGos noAvailableGos) {
			logger.error("Error on gosEndpoint acquiring");
			noAvailableGos.printStackTrace();
		}
		for(GosDefinition gd : gosDefinitions) {
				geoserverManagement.addStyleIcons(gd.getGosEndpoint(), styleName, iconFiles);
		}
	}
}
