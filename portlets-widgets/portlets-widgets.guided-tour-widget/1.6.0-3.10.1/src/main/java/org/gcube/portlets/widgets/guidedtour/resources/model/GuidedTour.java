/**
 * 
 */
package org.gcube.portlets.widgets.guidedtour.resources.model;

import java.net.URL;
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
@XmlRootElement(name = "guidedtour")
@XmlAccessorType(XmlAccessType.NONE)
public class GuidedTour {
	
	@XmlElementWrapper(name = "steps", required=true)
	@XmlElement(name = "step")
	protected ArrayList<Step> steps;
	
	@XmlElement(name = "title")
	protected String title;
	
	@XmlElement(name = "guide")
	protected String guide;
	
	@XmlElement(name = "themecolor")
	protected String themeColor;
	
	@XmlAttribute(name = "width")
	protected String width;
	
	@XmlAttribute(name = "height")
	protected String height;
	
	@XmlAttribute(name = "usemask")
	protected String useMask;
	
	protected URL source;
	
	protected String language;
	
	public GuidedTour(){}

	/**
	 * @return the steps
	 */
	public ArrayList<Step> getSteps() {
		return steps;
	}

	/**
	 * @param steps the steps to set
	 */
	public void setSteps(ArrayList<Step> steps) {
		this.steps = steps;
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
	 * @return the guide
	 */
	public String getGuide() {
		return guide;
	}

	/**
	 * @param guide the guide to set
	 */
	public void setGuide(String guide) {
		this.guide = guide;
	}

	/**
	 * @return the themeColor
	 */
	public String getThemeColor() {
		return themeColor;
	}

	/**
	 * @param themeColor the themeColor to set
	 */
	public void setThemeColor(String themeColor) {
		this.themeColor = themeColor;
	}

	/**
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @return the useMask
	 */
	public String getUseMask() {
		return useMask;
	}

	/**
	 * @param useMask the useMask to set
	 */
	public void setUseMask(String useMask) {
		this.useMask = useMask;
	}

	/**
	 * @return the source
	 */
	public URL getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(URL source) {
		this.source = source;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GuidedTour [steps=");
		builder.append(steps);
		builder.append(", title=");
		builder.append(title);
		builder.append(", guide=");
		builder.append(guide);
		builder.append(", themeColor=");
		builder.append(themeColor);
		builder.append(", width=");
		builder.append(width);
		builder.append(", height=");
		builder.append(height);
		builder.append(", useMask=");
		builder.append(useMask);
		builder.append(", source=");
		builder.append(source);
		builder.append(", language=");
		builder.append(language);
		builder.append("]");
		return builder.toString();
	}
}
