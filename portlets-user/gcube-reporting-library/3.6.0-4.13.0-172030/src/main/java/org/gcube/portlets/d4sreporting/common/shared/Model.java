package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * <code> SerializableModel </code> class represent The template Model that can be serializable
 * the TemplateModel class cannot be serializable since it contains <code> TemplateComponent</code>  instances
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2008 (0.2) 
 */
public class Model implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3470041232713152540L;
	/**
	 * templateName
	 */
	private String templateName;
	/**
	 * the Unique identifier
	 */
	private String uniqueID;	
	/**
	 * pageWidth
	 */
	private int pageWidth;

	/**
	 * Template page height
	 */
	private int pageHeight;

	/**
	 * Template Total Number of pages
	 */
	private int totalPages;
	
	/**
	 * Template current displayed page, used only on page refreshes to restore the right page
	 */
	private int currPage;

	/**
	 * Template left margin
	 */
	private int marginLeft;
	/**
	 * Template right margin 
	 */
	private int marginRight;
	/**
	 * Template top margin
	 */
	private int marginTop;
	/**
	 * Template bottom margin
	 */
	private int marginBottom;
	
	/**
	 * columnWidth is the actual page width without margins, when columns is equal to 1 (which is always true in my case, since UI doesn't allow multi columns)
	 */
	private int columnWidth;
	/**
	 * The name of the author
	 */
	private String author;
	/**
	 * The name of the author
	 */
	private String lastEditBy;
	/**
	 * The name of the author
	 */
	private Date dateCreated;
	/**
	 * The name of the author
	 */
	private Date lastEdit;
	/**
	 * each object of this Vector its a <class>Section</class> containing all the TemplateComponent of a template section
	 *
	 * object: a <class>Section</class> of Component containing all the TemplateComponent of the section
	 */

	private Vector<BasicSection> sections = new Vector<BasicSection>();
	
	/**
	 * holds the metadata(s) for the model
	 */
	private List<Metadata> metadata;
	
	/**
	 *  
	 *
	 */
	public Model() {
		super();
	}

//**************************************************	

	/**
	 * 
	 * @param templateName .
	 * @param pageWidth .
	 * @param pageHeight .
	 * @param totalPages .
	 * @param currPage .
	 * @param marginLeft .
	 * @param marginRight .
	 * @param marginTop .
	 * @param marginBottom .
	 * @param columnWidth .
	 * @param author .
	 * @param dateCreated .
	 * @param lastEditBy .
	 * @param lastEdit .
	 * @param sections .
	 * @param metadata .
	 */
	public Model(String uniqueID, String author, Date dateCreated, Date lastEdit, String lastEditBy, String templateName, int columnWidth, int currPage,
											int marginBottom, int marginLeft, int marginRight, int marginTop, 
												int pageHeight, int pageWidth, Vector<BasicSection> sections, int totalPages, List<Metadata> metadata) {
		super();
		this.uniqueID = uniqueID;
		this.author = author;
		this.columnWidth = columnWidth;
		this.currPage = currPage;
		this.dateCreated = dateCreated;
		this.lastEdit = lastEdit;
		this.lastEditBy = lastEditBy;
		this.marginBottom = marginBottom;
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.pageHeight = pageHeight;
		this.pageWidth = pageWidth;
		this.sections = sections;
		this.templateName = templateName;
		this.totalPages = totalPages;
		this.metadata = metadata;
	}


	/**
	 * @return a vector containg pages that are a vector as well containing template elements
	 */
	public Vector<BasicSection> getSections() {return sections;}
	/**
	 * @param sections .
	 */
	public void setSections(Vector<BasicSection> sections) {
		this.sections = sections;
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
	public int getMarginBottom() {return marginBottom;}
	/**
	 * @param marginBottom .
	 */
	public void setMarginBottom(int marginBottom) {	this.marginBottom = marginBottom;}
	/**
	 * @return .
	 */
	public int getMarginLeft() {return marginLeft;}
	/**
	 * @param marginLeft .
	 */
	public void setMarginLeft(int marginLeft) {	this.marginLeft = marginLeft;	}

	/**
	 * @return .
	 */
	public int getMarginRight() {return marginRight;}
	/**
	 * @param marginRight .
	 */	
	public void setMarginRight(int marginRight) {this.marginRight = marginRight;}
	/**
	 * @return .
	 */
	public int getMarginTop() {	return marginTop;}
	/**
	 * @param marginTop .
	 */
	public void setMarginTop(int marginTop) {this.marginTop = marginTop;}
	/**
	 * @return .
	 */
	public int getPageHeight() {return pageHeight;}
	/**
	 * @param pageHeight .
	 */
	public void setPageHeight(int pageHeight) {this.pageHeight = pageHeight;}
	/**
	 * @return .
	 */
	public int getPageWidth() {	return pageWidth;	}
	/**
	 * @param pageWidth .
	 */
	public void setPageWidth(int pageWidth) {this.pageWidth = pageWidth;}
	/**
	 * @return .
	 */
	public String getTemplateName() {return templateName;}
	/**
	 * @param templateName .
	 */
	public void setTemplateName(String templateName) {this.templateName = templateName; }
	/**
	 * @return .
	 */
	public int getTotalPages() {return totalPages; }
	/**
	 * @param totalPages .
	 */
	public void setTotalPages(int totalPages) {	this.totalPages = totalPages; }
	/**
	 * @return .
	 */
	public int getColumnWidth() {return columnWidth;}
	/**
	 * @param columnWidth .
	 */
	public void setColumnWidth(int columnWidth) {this.columnWidth = columnWidth;}

	/**
	 * 
	 * @return .
	 */
	public int getCurrPage() {
		return currPage;
	}

	/**
	 * 
	 * @param currPage .
	 */
	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	/**
	 * 
	 * @return .
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * 
	 * @param author .
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	/**
	 * 
	 * @return .
	 */
	public String getLastEditBy() {
		return lastEditBy;
	}
	/**
	 * 
	 * @param lastEditBy .
	 */
	public void setLastEditBy(String lastEditBy) {
		this.lastEditBy = lastEditBy;
	}
	/**
	 * 
	 * @return .
	 */
	public Date getDateCreated() {
		return dateCreated;
	}
	/**
	 * 
	 * @param dateCreated .
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	/**
	 * 
	 * @return .
	 */
	public Date getLastEdit() {
		return lastEdit;
	}
	/**
	 * 
	 * @param lastEdit .
	 */
	public void setLastEdit(Date lastEdit) {
		this.lastEdit = lastEdit;
	}
	/**
	 * 
	 * @return the id
	 */
	public String getUniqueID() {
		return uniqueID;
	}
	/**
	 * 
	 * @param uniqueID the id
	 */
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	
}