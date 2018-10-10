package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
import java.util.List;


/**
 * <code> SerializableComponent </code> class represent a template component that can be serializable
 * the TemplateComponent class used in the model cannot be serializable since it contains the Widget used as component
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class BasicComponent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5754420069210486624L;
	/**
	 * 
	 */
	private int x;
	private int y;
	private int width;
	private int height;	
	private int templatePage;
	private ComponentType type;
	private boolean isDoubleColLayout;
	private boolean locked;
	
	private BasicComponent child;
	
	/**
	 * optional,  valid only for images Dynamic Content
	 */
	private String id;
	/**
	 * the paramName for assigning it a value when exporting to pdf, valid only for Dynamic Content
	 */
	private String paramName;

	
	private Serializable possibleContent;
	
	/**
	 * holds the metadata(s) for the component
	 */
	private List<Metadata> metadata;
	
	/**
	 * Default Constructor for serialization
	 */
	public BasicComponent() {
		super();
		child = null;
	}
	/**
	 * 
	 * @param x .
	 * @param y .
	 * @param width . 
	 * @param height .
	 * @param templatePage .
	 * @param i . 
	 * @param paramName for assigning it a value when exporting to pdf, valid only for Dynamic Content
	 * @param possibleContent .
	 * @param isDoubleColLayout tell if the comp. is double column
	 * @param locked .
	 * @param metadata metadata
	 */
	public BasicComponent(int x, int y, int width, int height, int templatePage, ComponentType i, String paramName, Serializable possibleContent, boolean isDoubleColLayout, boolean locked,  List<Metadata> metadata) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.templatePage = templatePage;
		this.type = i;
		this.possibleContent = possibleContent;
		this.paramName = paramName;
		this.isDoubleColLayout = isDoubleColLayout;
		this.locked = locked;
		this.metadata = metadata;
	}

	/**
	 * 
	 * @param x .
	 * @param y .
	 * @param width . 
	 * @param height .
	 * @param templatePage .
	 * @param type . 
	 * @param id .
	 * @param paramName for assigning it a value when exporting to pdf, valid only for Dynamic Content
	 * @param possibleContent .
	 * @param isDoubleColLayout tell if the comp. is double column
	 * @param locked .
	 * @param metadata metadata
	 */
	public BasicComponent(int x, int y, int width, int height, int templatePage, ComponentType type, String idInBasket, String paramName, Serializable possibleContent, boolean isDoubleColLayout, boolean locked,  List<Metadata> metadata) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.templatePage = templatePage;
		this.type = type;
		this.id = idInBasket;
		this.paramName = paramName;
		this.possibleContent = possibleContent;
		this.isDoubleColLayout = isDoubleColLayout;
		this.locked = locked;
		this.metadata = metadata;		
	}
	
	
	/***
	 * NOT USED SO FAR
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param templatePage
	 * @param type
	 * @param isDoubleColLayout
	 * @param locked
	 * @param child
	 * @param id
	 * @param paramName
	 * @param possibleContent
	 * @param metadata
	 */
	public BasicComponent(int x, int y, int width, int height,
			int templatePage, ComponentType type, boolean isDoubleColLayout,
			boolean locked, BasicComponent child, String idInBasket,
			String paramName, Serializable possibleContent,
			List<Metadata> metadata) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.templatePage = templatePage;
		this.type = type;
		this.isDoubleColLayout = isDoubleColLayout;
		this.locked = locked;
		this.child = child;
		this.id = idInBasket;
		this.paramName = paramName;
		this.possibleContent = possibleContent;
		this.metadata = metadata;
	}
	/**
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * 
	 * @param id an id for this component
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 
	 * @return
	 */
	public BasicComponent getChild() {
		return child;
	}
	/**
	 * 
	 * @param child
	 */
	public void setChild(BasicComponent child) {
		this.child = child;
	}
	/**
	 * 
	 * @return
	 */
	public boolean hasChild() {
		return (child!=null);
	}
	/**
	 * 
	 * @return .
	 */
	public List<Metadata> getMetadata() {
		return metadata;
	}
	
	/**
	 * 
	 * @param metadata .
	 */
	public void setMetadata(List<Metadata> metadata) {
		this.metadata = metadata;
	}
	/**
	 * @return .
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height .
	 */
	public void setHeight(int height) {	this.height = height; }
	/**
	 * @return .
	 */
	public int getTemplatePage() {	return templatePage;	}
	/**
	 * @param templatePage .
	 */
	public void setTemplatePage(int templatePage) {	this.templatePage = templatePage;}
	/**
	 * @return .
	 */
	public ComponentType getType() {	return type;	}
	/**
	 * @param type .
	 */
	public void setType(ComponentType type) {	this.type = type; }
	/**
	 * @return .
	 */
	public int getWidth() {	return width; 	}
	/**
	 * @param width .
	 */
	public void setWidth(int width) { this.width = width; }
	/**
	 * @return .
	 */
	public int getX() {	return x; }
	/**
	 * @param x .
	 */
	public void setX(int x) {this.x = x; }
	/**
	 * @return .
	 */
	public int getY() {	return y;}
	/**
	 * @param y .
	 */
	public void setY(int y) {this.y = y;}
	/**
	 * @return .
	 */
	public Serializable getPossibleContent() {return possibleContent;	}
	/**
	 * @param possibleContent .
	 */
	public void setPossibleContent(Serializable possibleContent) {this.possibleContent = possibleContent;	}

	/**
	 * 
	 * @return the paramName
	 */
	public String getParamName() {
		return paramName;
	}
	/**
	 * 
	 * @param paramName .
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	/**
	 * 
	 * @return .
	 */
	public boolean isDoubleColLayout() {
		return isDoubleColLayout;
	}
	/**
	 * 
	 * @param isDoubleColLayout .
	 */
	public void setDoubleColLayout(boolean isDoubleColLayout) {
		this.isDoubleColLayout = isDoubleColLayout;
	}
	/**
	 * 
	 * @return .
	 */
	public boolean isLocked() {
		return locked;
	}
	/**
	 * 
	 * @param locked .
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	@Override
	public String toString() {
		return "BasicComponent [type="
				+ type + ", locked=" + locked + ", child=" + child
				+ ", id=" + id + ", paramName=" + paramName
				+ ", possibleContent=" + possibleContent + ", metadata="
				+ metadata + "]";
	}
	
	
}
