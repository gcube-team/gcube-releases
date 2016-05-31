package org.gcube.portlets.user.td.expressionwidget.client.properties;

import java.io.Serializable;

import org.gcube.portlets.user.td.expressionwidget.shared.replace.ReplaceType;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ReplaceElement implements Serializable {

	private static final long serialVersionUID = -1278177714679767844L;

	private Integer id;
	private ReplaceType replaceType;

	public ReplaceElement() {
		super();
	}

	public ReplaceElement(Integer id, ReplaceType replaceType) {
		super();
		this.id = id;
		this.replaceType = replaceType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return replaceType.toString();
	}

	public ReplaceType getReplaceType() {
		return replaceType;
	}

	public void setReplaceType(ReplaceType replaceType) {
		this.replaceType = replaceType;
	}

	@Override
	public String toString() {
		return "ReplaceElement [id=" + id + ", replaceType=" + replaceType
				+ "]";
	}

}
