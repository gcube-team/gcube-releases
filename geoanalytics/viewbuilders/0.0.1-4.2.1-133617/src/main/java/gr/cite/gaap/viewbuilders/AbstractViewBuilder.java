package gr.cite.gaap.viewbuilders;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.ShapeImportManager;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

public abstract class AbstractViewBuilder implements ViewBuilder
{	
	private static Logger log = LoggerFactory.getLogger(AbstractViewBuilder.class);
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	protected Configuration context;
	protected ShapeManager shapeManager;
	protected ShapeImportManager shapeImportManager;
	protected TaxonomyManager taxonomyManager;
	protected ConfigurationManager configurationManager;
	
	protected String identity = null;
	protected String identityName = null;
	protected Shape shape = null;
	protected String viewStatement = null;
	protected Map<String, ShapeAttributeDataType> attrs = new LinkedHashMap<String, ShapeAttributeDataType>();
	
	public AbstractViewBuilder(ShapeManager shapeManager, ShapeImportManager shapeImportManager,
			TaxonomyManager taxonomyManager, ConfigurationManager configurationManager)
	{
		this.shapeManager = shapeManager;
		this.shapeImportManager = shapeImportManager;
		this.taxonomyManager = taxonomyManager;
		this.configurationManager = configurationManager;
	}
	
	@Inject
	public void setContext(Configuration context) {
		this.context = context;
	}
	
	private ShapeAttributeDataType parseDataType(String dataType, String data)
	{
		if(dataType.equalsIgnoreCase("short")) return ShapeAttributeDataType.SHORT;
		if(dataType.equalsIgnoreCase("int")) return ShapeAttributeDataType.INTEGER;
		if( dataType.equalsIgnoreCase("decimal") || dataType.equalsIgnoreCase("integer") ||
				dataType.equalsIgnoreCase("long")) return ShapeAttributeDataType.LONG;
		if(dataType.equalsIgnoreCase("float")) return ShapeAttributeDataType.FLOAT;
		if(dataType.equalsIgnoreCase("double")) return ShapeAttributeDataType.DOUBLE;
		if(dataType.equalsIgnoreCase("date")) return ShapeAttributeDataType.DATE;
		
		if(data.length() < 250) return ShapeAttributeDataType.STRING;
		else return ShapeAttributeDataType.LONGSTRING;
	}
	
	@Override
	public ViewBuilder createViewStatement() throws Exception
	{
		viewStatement = generateViewStatement(identity, identityName != null ? identityName : shape.getName(), attrs);
		return this;
	}

	@Override
	public String getViewStatement() throws Exception
	{
		if(viewStatement == null) createViewStatement();
		return viewStatement;
	}
	
	@Override
	public ViewBuilder withAttribute(String key, ShapeAttributeDataType value)
	{
		attrs.put(key, value);
		return this;
	}
	
	private void mergeInfoForShape() throws Exception
	{
		String extraData = shape.getExtraData();
		if(extraData == null) extraData = "";
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document data = db.parse(new InputSource(new ByteArrayInputStream(extraData.getBytes("UTF-8"))));
		
		int len = data.getDocumentElement().getChildNodes().getLength();
		
		for(int i = 0; i < len; i++)
		{
			Node n = data.getDocumentElement().getChildNodes().item(i);
			String value = "";
			if(n.getFirstChild() != null) value = n.getFirstChild().getNodeValue();
			
			String attrName = n.getNodeName();
			/*MappingConfig mcfg = configurationManager.getMappingConfig(n.getNodeName(), value);
			if(mcfg == null)
			{
				List<MappingConfig> mcfgs = configurationManager.getMappingConfig(n.getNodeName());
				if(mcfgs != null && mcfgs.size() == 1) //if (name,value)->term is null, there should be only one name->taxonomy mapping
					mcfg = mcfgs.get(0);
			}else
			{
				if(mcfg.getAttributeValue() != null)
				{
					TaxonomyTerm tt = taxonomyManager.findTermById(mcfg.getTermId(), false);
					attrName = tt.getName();
				}else
				{
					Taxonomy t = taxonomyManager.findTaxonomyById(mcfg.getTermId(), false);
					attrName = t.getName();
				}
			}*/
			
			attrs.put(attrName, parseDataType(((Element)n).getAttribute("type"), value));
		}
	}
	
	@Override
	public ViewBuilder forShape(Shape shape) throws Exception
	{
		//if(this.identity != null) throw new Exception("Shape defined after identity");
		this.shape = shape;
		this.identity = null;
		this.identityName = null;
		this.attrs = new HashMap<String, ShapeAttributeDataType>();
		
		if(shape == null) throw new IllegalArgumentException("No shape provided");
		if(shape.getGeography() == null) throw new IllegalArgumentException("Shape geography not present");
		if(shape.getName() == null) throw new IllegalArgumentException("Shape name not assigned");
		
		mergeInfoForShape();
		
		return this;
	}
	
	@Override
	public ViewBuilder forIdentity(String identity, String identityName) throws Exception
	{
		//if(this.shape != null) throw new Exception("Identity defined after shape");
		this.identity = identity;
		this.identityName = identityName;
		this.attrs = new HashMap<String, ShapeAttributeDataType>();
		
		//List<Shape> shapes = new ArrayList<Shape>();
		//List<ShapeImport> sis = shapeImportManager.findByImportIdentity(identity);
//		for(ShapeImport si : sis)
//		{
//			shapes.addAll(shapeManager.findShapesOfImport(si)); 
//		}
		TaxonomyTerm tt = taxonomyManager.findTermById(identity, true);
		List<Shape> shapes = shapeManager.getShapesOfLayer(tt.getName(), tt.getTaxonomy().getName());
		
		System.out.println("Creating new Shapes (TSV Import)");
		
		for(Shape s : shapes){
			System.out.println("shape = " + s.getId());
			this.shape = s;
			mergeInfoForShape();
		}
		return this;
	}
	
	protected abstract String generateViewStatement(String identity, String name, Map<String, ShapeAttributeDataType> attrs) throws Exception;
	
	@Override
	public void execute() throws Exception
	{
		Connection con = null;
        Statement st = null;

        try 
        {
            con = DriverManager.getConnection(context.getDataLayerConfig().getDbUrl(), context.getDataLayerConfig().getDbUser(), context.getDataLayerConfig().getDbPass());
            st = con.createStatement();
            st.executeUpdate(getViewStatement());
        }catch (SQLException ex) 
        {
            log.error(ex.getMessage(), ex);
            throw ex;

        }finally 
        {
        	try 
            {
                if (st != null)  st.close();
                if (con != null) con.close();
            } catch (SQLException ex) 
            {
                log.warn(ex.getMessage(), ex);
            }
        }
	}
	
/*	@Override
	public void execute() throws Exception{
		try{
			String statement = getViewStatement();
			entityManager.createNativeQuery(statement);
			log.debug("Create the materialized view" + identityName);
			//entityManager.getTransaction().commit();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}*/

	@Override
	public abstract void removerViewIfExists() throws Exception;
	
	
}
