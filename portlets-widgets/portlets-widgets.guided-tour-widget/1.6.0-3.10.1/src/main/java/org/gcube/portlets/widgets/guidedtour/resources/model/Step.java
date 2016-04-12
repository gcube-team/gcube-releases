/**
 * 
 */
package org.gcube.portlets.widgets.guidedtour.resources.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
@XmlRootElement(name="step")
@XmlAccessorType(XmlAccessType.NONE)
public class Step {
	
	@XmlAttribute(name="template",required=false)
	protected String template;
	
	@XmlAttribute(name="showtitle",required=false)
	protected String showTitle;
	
	@XmlAttribute(name="v-alignment",required=false)
	protected String verticalAlignment;
	
	@XmlElement(name = "title", required=true)
	protected String title;
	
	@XmlElementWrapper(name = "bodies", required=true)
	@XmlElement(name = "body")
	protected ArrayList<String> bodies;
	
	@XmlElementWrapper(name = "images", required=false)
	@XmlElement(name = "image")
	protected ArrayList<Image> images;
	
	public Step(){}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param type the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @return the showTitle
	 */
	public String getShowTitle() {
		return showTitle;
	}

	/**
	 * @param showTitle the showTitle to set
	 */
	public void setShowTitle(String showTitle) {
		this.showTitle = showTitle;
	}

	/**
	 * @return the verticalAlignment
	 */
	public String getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * @param verticalAlignment the verticalAlignment to set
	 */
	public void setVerticalAlignment(String verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the bodies
	 */
	public ArrayList<String> getBodies() {
		return bodies;
	}

	/**
	 * @param bodies the bodies to set
	 */
	public void setBodies(ArrayList<String> bodies) {
		this.bodies = bodies;
	}

	/**
	 * @return the images
	 */
	public ArrayList<Image> getImages() {
		return images;
	}

	/**
	 * @param images the images to set
	 */
	public void setImages(ArrayList<Image> images) {
		this.images = images;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Step [template=");
		builder.append(template);
		builder.append(", showTitle=");
		builder.append(showTitle);
		builder.append(", verticalAlignment=");
		builder.append(verticalAlignment);
		builder.append(", title=");
		builder.append(title);
		builder.append(", bodies=");
		builder.append(bodies);
		builder.append(", images=");
		builder.append(images);
		builder.append("]");
		return builder.toString();
	}
}
