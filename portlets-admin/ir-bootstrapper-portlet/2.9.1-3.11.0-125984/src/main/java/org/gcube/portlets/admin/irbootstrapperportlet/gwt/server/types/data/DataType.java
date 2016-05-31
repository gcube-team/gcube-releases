/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.IRBootstrapperData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.Resource;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.Evaluable;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.EvaluationResult;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityParsingUtil;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * @author Spyros Boutsis, NKUA
 *
 */
public abstract class DataType<T extends Resource> implements Cloneable, Evaluable {

	/** Logger */
	private static Logger logger = Logger.getLogger(DataType.class);
	
	/** The name of this dataType */
	protected String typeName;
	
	/** The XML definition of the dataType */
	protected Document xml;
	
	private T associatedResource;
	
	private Class<T> associatedResourceClass;
	
	private String scope;

	private Map<String,String> dataTypeAttrToResourceAttrMappings;
	
	/** 
	 * Class constructor 
	 */
	DataType(String scope, T associatedResource, Class<T> associatedResourceClass) {
		try {
			this.scope = scope;
			this.associatedResource = associatedResource;
			this.associatedResourceClass = associatedResourceClass;
			this.dataTypeAttrToResourceAttrMappings = getDataTypeAttrToResourceAttrMappings();
			this.xml = Util.copyNodeAsNewDocument(this.getXMLTypeDefinitionDocument().getDocumentElement());
			if (this.associatedResource != null)
				this.copyDataFromResource();
			else {
				Constructor<T> ctor = associatedResourceClass.getConstructor(String.class);
				this.associatedResource = ctor.newInstance(scope);
			}
		} catch (Exception e) { 
			logger.error(e);
		}
	}

	/**
	 * Initializes this DataType instance
	 * @param typeName the name of the data type
	 * @param xml the XML definition of the data type
	 */
	public void initialize(String typeName) throws Exception {
		this.typeName = typeName;
	}

	/**
	 * Returns the XML definition of this data type
	 * @return
	 */
	public Node getTypeDefinition() {
		return this.xml.getDocumentElement();
	}
	
	public String getScope() {
		return this.scope;
	}

	public String getTypeName() {
		return this.typeName;
	}
	
	/**
	 * Returns the Node object representing a node in the XML definition of this data type.
	 * @param expression the expression to evaluate, in "a.b.c..." notation
	 * @return the Node object that represents the requested node
	 */
	public EvaluationResult evaluate(String expression) throws Exception {
		try {
			return new EvaluationResult(this, _evaluate(expression));
		} catch (Exception e) {
			throw new Exception("Failed to evaluate the expression: " + expression);
		}
	}
	
	private Node _evaluate(String expression) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String xpathExpr = "type/" + expression.replace('.', '/');
		Node n = (Node) xpath.evaluate(xpathExpr, xml, XPathConstants.NODE);
		if (n == null)
			throw new Exception();
		return n;
	}
	
	/**
	 * Sets the value of an attribute defined in the XML definition of this data type.
	 * Works only for attributes represented by "leaf" nodes in the XML definition,
	 * whose value is only a string and not a node tree.
	 * @param expression the expression to evaluate, in "a.b.c..." notation
	 * @param value the value to set to the attribute identified by the given expression
	 */
	public void setAttributeValue(String expression, String value) throws Exception {
		Node n = this._evaluate(expression);
		n.setTextContent(value);
		
		/* Update the corresponding resource attribute with the new value */
		String resourceAttrName = this.dataTypeAttrToResourceAttrMappings.get(expression);
		this.associatedResource.setAttributeValue(resourceAttrName, value);
	}
	
	/**
	 * Gets the value of an attribute defined in the XML definition of this data type.
	 * Works only for attributes represented by "leaf" nodes in the XML definition,
	 * whose value is only a string and not a node tree. If the requested attribute is 
	 * not currently set, returns null.
	 * @param expression the expression to evaluate, in "a.b.c..." notation
	 * @return the value of the attribute identified by the given expression
	 */
	public String getAttributeValue(String expression) throws Exception {
		Node n = this._evaluate(expression);
		String s = n.getTextContent();
		if (s.trim().length() == 0)
			return null;
		return s;
	}
	
	/**
	 * Returns a list of DataTypeInstances representing all the resources that
	 * match the properties defined in this DataType in the given scope
	 * @param scope the scope to search in
	 * @return the list of DataType instances
	 */
	public List<DataType<T>> findMatchesInScope(String scope) throws Exception {

		logger.debug("search matches for: " + this.typeName);
		for (String s: associatedResource.getAttributeNames()) {
			logger.debug("->Searching for: " + s + ", " + associatedResource.getAttributeValue(s));
		}
		
		List<? extends Resource> resources = IRBootstrapperData.getInstance().getBootstrappingConfiguration(scope).
			getResourceCache().getResourcesWithGivenAttributes(associatedResource);

		
		
		/* TODO: remove
		 * 
		 */
		if (resources != null) {
			logger.debug("Found " + resources.size() + " resources!");
			for (int i=0; i<resources.size(); i++) {
				logger.debug("Resource " + i + ":");
				for (String s : resources.get(i).getAttributeNames()) {
					logger.debug(s + ": " + resources.get(i).getAttributeValue(s));
				}
			}
		}
		else
			logger.debug("Found 0 resources!");

		
		
		/* Construct DataType instances wrapping the found Resources and return them in a list */
		List<DataType<T>> ret = new LinkedList<DataType<T>>();
		for (Resource r : resources) {
			DataType<T> dt = null;
			try {				
				dt = (DataType) this.clone();
				dt.associatedResource = (T) r;
				dt.copyDataFromResource();
			} catch (Exception e) { }
			ret.add(dt);
		}
		return ret;
	}
	
	public T getAssociatedResource() {
		return associatedResource;
	}
	
	/**
	 * Updates all the attributes of this data types with the values of the corresponding
	 * attributes defined in the associated resource.
	 * @throws Exception
	 */
	public void copyDataFromResource() throws Exception {
		String resourceAttrName;
		List<String> resAttrVals;
		
		for (String dataTypeAttrName : this.dataTypeAttrToResourceAttrMappings.keySet()) {
			resourceAttrName = this.dataTypeAttrToResourceAttrMappings.get(dataTypeAttrName);
			resAttrVals = this.associatedResource.getAttributeValue(resourceAttrName);
			if (resAttrVals != null) {
				this.setAttributeValue(dataTypeAttrName, EntityParsingUtil.arrayOfValuesToAttrValue(resAttrVals));
			}
		}
	}
	
	/**
	 * Updates all the attributes of the associated resource with the values of the
	 * corresponding attributes defined in this data type.
	 * @param resource
	 * @throws Exception
	 */
	public void copyDataToResource() throws Exception {
		String resourceAttrName;
		String attrVal;
		
		for (String dataTypeAttrName : this.dataTypeAttrToResourceAttrMappings.keySet()) {
			resourceAttrName = this.dataTypeAttrToResourceAttrMappings.get(dataTypeAttrName);
			attrVal = this.getAttributeValue(dataTypeAttrName);
			if (attrVal != null) {
				this.associatedResource.setAttributeValue(resourceAttrName, EntityParsingUtil.attrValueToArrayOfValues(attrVal));
			}
		}
	}
	
	/**
	 * Returns (dataTypeAttrValue, resourceAttrValue) pairs, which describe the mappings
	 * between attributes defined in this data types and attributes defined in the wrapped
	 * type of resource.
	 * @return
	 */
	public abstract Map<String,String> getDataTypeAttrToResourceAttrMappings();
	
	 /**
	 * Returns true if the currently defined attributes of this DataType are adequate
	 * in order to uniquely identify a resource in the infrastructure. If the DataType
	 * attributes could possibly describe more than one resources, this method should
	 * return false;
	 * @return
	 */
	public abstract boolean doesIdentifyUniqueResource();
	
	/**
	 * Returns an identifier that uniquely identifies the specific DataType instance. It is
	 * guaranteed that when this method is called, the DataType will be fully defined, meaning
	 * that none of its attributes will be empty. This guarantee is given so that subclasses
	 * implementing this method can freely depend on any attribute required in order to construct
	 * the unique ID.
	 * @return the DataType's UID
	 */
	public abstract String getUID();
	
	/**
	 * Returns a string which will be displayed as the name of this DataType in the UI.
	 * @return the DataType's name
	 */
	public abstract String getUIName() throws Exception;
	
	/**
	 * Returns a string which will be displayed as the description of this DataType in the UI.
	 * @return the DataType's description
	 */
	public abstract String getUIDescription() throws Exception;
	
	/**
	 * Returns a {@link Document} object defining the XML structure of this DataType's contents.
	 * @return the XML type definition document
	 */
	public abstract Document getXMLTypeDefinitionDocument() throws Exception;
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		try {
			DataType<T> dt = (DataType<T>) super.clone();
			dt.xml = Util.copyNodeAsNewDocument(this.xml.getDocumentElement());
			
			Constructor<T> ctor = this.associatedResourceClass.getConstructor(String.class);
			dt.associatedResource = ctor.newInstance(scope);
			
			return dt;
		} catch (Exception e) {
			logger.error("Error while cloning DataType object", e);
			return null;
		}
	}
}
