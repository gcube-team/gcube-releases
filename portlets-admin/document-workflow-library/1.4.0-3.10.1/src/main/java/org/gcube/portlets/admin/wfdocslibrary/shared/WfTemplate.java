package org.gcube.portlets.admin.wfdocslibrary.shared;

import java.io.Serializable;
import java.util.Date;
/**
 * <code> WfTemplatesPresenter </code> class is the bean that goes on the wire carrying the workflow template representation
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
@SuppressWarnings("serial")
public class WfTemplate implements Serializable{

	private String templateid;
	private String templatename;
	private String author;
	private Date dateCreated;
	private WfGraph graph;
	
	public WfTemplate() {}

	public WfTemplate(String templateid, String templatename, String author,Date dateCreated, WfGraph graph) {
		this.templateid = templateid;
		this.templatename = templatename;
		this.author = author;
		this.dateCreated = dateCreated;
		this.graph = graph;
	}
//G&S
	public String getTemplateid() {	return templateid;	}
	public void setTemplateid(String templateid) {	this.templateid = templateid;	}
	public String getTemplatename() {return templatename;	}
	public void setTemplatename(String templatename) {	this.templatename = templatename;	}
	public String getAuthor() {	return author;	}
	public void setAuthor(String author) {	this.author = author;	}
	public Date getDateCreated() {	return dateCreated;	}
	public void setDateCreated(Date dateCreated) {	this.dateCreated = dateCreated;	}
	public WfGraph getGraph() {	return graph;	}
	public void setGraph(WfGraph graph) {this.graph = graph; }	
}
