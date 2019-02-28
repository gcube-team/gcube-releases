package org.gcube.portlets.user.td.resourceswidget.client.custom;

import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDType;
import org.gcube.portlets.user.td.resourceswidget.client.resources.ResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sencha.gxt.core.client.XTemplates;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ResourceTDTypeButtonCellAppearance {

	public interface Style extends CssResource {
		String image();
	}

	public interface Template extends XTemplates {
		@XTemplate(source = "ResourceTDTypeButton.html")
		SafeHtml template(Style style, SafeHtml img, String title);
	}

	public interface Resources extends ClientBundle {
		@Source("ResourceTDTypeButton.css")
		Style style();
	}

	private final Style style;
	private final Template template;

	public ResourceTDTypeButtonCellAppearance() {
		this((Resources) GWT.create(Resources.class));
	}

	public ResourceTDTypeButtonCellAppearance(Resources resources) {
		this.style = resources.style();
		this.style.ensureInjected();
		this.template = GWT.create(Template.class);
	}

	public void render(ResourceTDType value, SafeHtmlBuilder sb) {
		String title = SafeHtmlUtils.htmlEscape(value.toString());

		ImageResource imageResource;
		switch (value) {
		case CHART:
			imageResource = ResourceBundle.INSTANCE.chart24();
			break;
		case CODELIST:
			imageResource = ResourceBundle.INSTANCE.codelist24();
			break;
		case CSV:
			imageResource = ResourceBundle.INSTANCE.csv24();
			break;
		case GUESSER:
			imageResource = ResourceBundle.INSTANCE.resources24();
			break;
		case JSON:
			imageResource = ResourceBundle.INSTANCE.json24();
			break;
		case MAP:
			imageResource = ResourceBundle.INSTANCE.gis24();
			break;
		case SDMX:
			imageResource = ResourceBundle.INSTANCE.sdmx24();
			break;
		case GENERIC_FILE:
			imageResource = ResourceBundle.INSTANCE.file24();
			break;	
		case GENERIC_TABLE:
			imageResource = ResourceBundle.INSTANCE.table24();
			break;	
		default:
			imageResource = ResourceBundle.INSTANCE.resources();
			break;

		}

		sb.append(template.template(style, makeImage(imageResource), title));
	}

	/**
	 * Make icons available as SafeHtml to be displayed inside the table
	 * 
	 * @param resource
	 * @return
	 */
	private static SafeHtml makeImage(ImageResource resource) {
		AbstractImagePrototype proto = AbstractImagePrototype.create(resource);
		String html = proto.getHTML();
		return SafeHtmlUtils.fromTrustedString(html);
	}

}