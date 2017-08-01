package gr.cite.geoanalytics.gaap.viewbuilders;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.geoanalytics.gaap.viewbuilders.AbstractViewBuilder;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.gos.client.ShapeManagement;

@Configurable
public abstract class AbstractViewBuilder implements ViewBuilder {

	private static Logger log = LoggerFactory.getLogger(AbstractViewBuilder.class);

	@PersistenceContext
	protected EntityManager entityManager;

	protected ShapeManagement shapeManagement;
	protected Configuration context;
	protected GeocodeManager taxonomyManager;
	protected ConfigurationManager configurationManager;

	protected String identity = null;
	protected Shape shape = null;
	protected String viewStatement = null;
	protected Map<String, ShapeAttributeDataType> attrs = new LinkedHashMap<String, ShapeAttributeDataType>();

	public AbstractViewBuilder(GeocodeManager taxonomyManager, ConfigurationManager configurationManager) {
		this.taxonomyManager = taxonomyManager;
		this.configurationManager = configurationManager;
	}

	@Inject
	public void setShapeManagement(ShapeManagement shapeManagement) {
		this.shapeManagement = shapeManagement;
	}

	@Inject
	public void setContext(Configuration context) {
		this.context = context;
	}

	private ShapeAttributeDataType parseDataType(String dataType, String data) {
		if (dataType.equalsIgnoreCase("short"))
			return ShapeAttributeDataType.SHORT;
		if (dataType.equalsIgnoreCase("int"))
			return ShapeAttributeDataType.INTEGER;
		if (dataType.equalsIgnoreCase("decimal") || dataType.equalsIgnoreCase("integer") || dataType.equalsIgnoreCase("long"))
			return ShapeAttributeDataType.LONG;
		if (dataType.equalsIgnoreCase("float"))
			return ShapeAttributeDataType.FLOAT;
		if (dataType.equalsIgnoreCase("double"))
			return ShapeAttributeDataType.DOUBLE;
		if (dataType.equalsIgnoreCase("date"))
			return ShapeAttributeDataType.DATE;

		if (data.length() < 250)
			return ShapeAttributeDataType.STRING;
		else
			return ShapeAttributeDataType.LONGSTRING;
	}

	@Override
	public ViewBuilder createViewStatement() throws Exception {
		viewStatement = generateViewStatement(identity, attrs);
		return this;
	}

	@Override
	public ViewBuilder removeViewStatement() throws Exception {
		viewStatement = removeViewIfExists();
		return this;
	}

	@Override
	public String getViewStatement() throws Exception {
		if (viewStatement == null)
			createViewStatement();
		return viewStatement;
	}

	@Override
	public ViewBuilder withAttribute(String key, ShapeAttributeDataType value) {
		attrs.put(key, value);
		return this;
	}

	private void mergeInfoForShape() throws Exception {				
		String extraData = shape.getExtraData();
		if (extraData == null)
			extraData = "";

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document data = db.parse(new InputSource(new ByteArrayInputStream(extraData.getBytes("UTF-8"))));

		int len = data.getDocumentElement().getChildNodes().getLength();

		for (int i = 0; i < len; i++) {
			Node n = data.getDocumentElement().getChildNodes().item(i);
			String value = "";
			if (n.getFirstChild() != null)
				value = n.getFirstChild().getNodeValue();

			String attrName = n.getNodeName();
			/*
			 * MappingConfig mcfg = configurationManager.getMappingConfig(n.getNodeName(), value); if(mcfg == null) { List<MappingConfig> mcfgs =
			 * configurationManager.getMappingConfig(n.getNodeName()); if(mcfgs != null && mcfgs.size() == 1) //if (name,value)->term is null, there
			 * should be only one name->taxonomy mapping mcfg = mcfgs.get(0); }else { if(mcfg.getAttributeValue() != null) { TaxonomyTerm tt =
			 * taxonomyManager.findTermById(mcfg.getTermId(), false); attrName = tt.getName(); }else { Taxonomy t =
			 * taxonomyManager.findTaxonomyById(mcfg.getTermId(), false); attrName = t.getName(); } }
			 */

			attrs.put(attrName, parseDataType(((Element) n).getAttribute("type"), value));
		}
	}

	@Override
	public ViewBuilder forShape(Shape shape) throws Exception {
		// if(this.identity != null) throw new Exception("Shape defined after identity");
		this.shape = shape;
		if (this.attrs == null)
			this.attrs = new HashMap<String, ShapeAttributeDataType>();

		if (shape == null)
			throw new IllegalArgumentException("No shape provided");
		if (shape.getGeography() == null)
			throw new IllegalArgumentException("Shape geography not present");
		if (shape.getName() == null)
			throw new IllegalArgumentException("Shape name not assigned");

		mergeInfoForShape();

		return this;
	}

	@Override
	public ViewBuilder forShapes(List<Shape> shapes) throws Exception {
		this.attrs = new HashMap<String, ShapeAttributeDataType>();
		
		for (Shape s : shapes) {
			this.shape = s;
			mergeInfoForShape();
		}
		return this;
	}

	@Override
	public ViewBuilder forIdentity(String identity) throws Exception {
		this.identity = identity;
		if (this.attrs == null)
			this.attrs = new HashMap<String, ShapeAttributeDataType>();
		return this;
	}

	protected abstract String generateViewStatement(String layerId, Map<String, ShapeAttributeDataType> attrs) throws Exception;

	/**
	 * Returns true if database view has been completed successfully
	 */
	@Override
	public boolean execute(String gosEndpoint) throws Exception {
		return shapeManagement.applyOnView(gosEndpoint, getViewStatement());
	}

	@Override
	public abstract String removeViewIfExists() throws Exception;

}
