/**
 * 
 */
package org.gcube.portlets.widgets.guidedtour.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gcube.portlets.widgets.guidedtour.client.GCUBEGuidedTour;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate1Text1Image;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate1Text1ImageML;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate1Text2Image;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate1Text2ImageML;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate2Text2Image;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate2Text2ImageML;
import org.gcube.portlets.widgets.guidedtour.client.types.VerticalAlignment;
import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class GuidedTourConfigurationExctractor {

	public static void analyze(GCUBEGuidedTour tour)
	{

		StringBuilder mainConfiguration = new StringBuilder("<guidedtour width=\"");
		mainConfiguration.append(String.valueOf(tour.getWidth()));
		mainConfiguration.append("\" height=\"");
		mainConfiguration.append(String.valueOf(tour.getHeight()));
		mainConfiguration.append("\" usemask=\"");
		mainConfiguration.append(String.valueOf(tour.isMask()));
		mainConfiguration.append("\">\n");

		mainConfiguration.append("<title>"+tour.getPortletName()+"</title>\n");
		mainConfiguration.append("<guide>"+tour.getUserGuideLink()+"</guide>\n");
		mainConfiguration.append("<themecolor>"+tour.getColor().toString()+"</themecolor>\n<steps>\n");

		Map<TourLanguage, StringBuilder> configurations = new LinkedHashMap<TourLanguage, StringBuilder>();

		for (TourLanguage language:tour.getSupportedLanguages()) {
			if (language==TourLanguage.EN) continue;
			configurations.put(language, new StringBuilder("<guidedtour>\n<steps>\n"));
		}

		ArrayList<Composite> steps = tour.getSteps();
		for (Composite step:steps)
		{
			if (step instanceof GCUBETemplate1Text1Image) {
				GCUBETemplate1Text1Image template = (GCUBETemplate1Text1Image)step;

				StringBuilder sb = new StringBuilder("<step showTitle=\"");

				sb.append(String.valueOf(template.isShowTitle()).toLowerCase());

				VerticalAlignment verticalAlignment = getVerticalAlignment(template.getTitleElement());
				sb.append("\" v-alignment=\"");
				sb.append(verticalAlignment.toString());
				sb.append("\">\n<title>");
				sb.append(template.setStepTitle());
				sb.append("</title>\n<bodies>\n");

				sb.append("<body><![CDATA[\n");
				sb.append(template.setStepBody());
				sb.append("\n]]></body>\n");

				sb.append("</bodies>\n<images>");

				sb.append("\n<image url=\"");
				sb.append(template.setStepImage());
				sb.append("\"/>\n");

				sb.append("</images>\n</step>\n");

				mainConfiguration.append(sb.toString());
			} else if (step instanceof GCUBETemplate1Text2Image) {
				GCUBETemplate1Text2Image template = (GCUBETemplate1Text2Image)step;

				StringBuilder sb = new StringBuilder("<step showTitle=\"");

				sb.append(String.valueOf(template.isShowTitle()).toLowerCase());

				VerticalAlignment verticalAlignment = getVerticalAlignment(template.getTitleElement());
				sb.append("\" v-alignment=\"");
				sb.append(verticalAlignment.toString());
				sb.append("\">\n<title>");
				sb.append(template.setStepTitle());
				sb.append("</title>\n<bodies>\n");

				sb.append("<body><![CDATA[\n");
				sb.append(template.setStepBody());
				sb.append("\n]]></body>\n");

				sb.append("</bodies>\n<images>");

				sb.append("\n<image url=\"");
				sb.append(template.setStepImage());
				sb.append("\"/>\n");
				sb.append("<image url=\"");
				sb.append(template.setStepOtherImage());
				sb.append("\"/>\n");

				sb.append("</images>\n</step>\n");

				mainConfiguration.append(sb.toString());

			} else  if (step instanceof GCUBETemplate2Text2Image) {
				GCUBETemplate2Text2Image template = (GCUBETemplate2Text2Image)step;

				StringBuilder sb = new StringBuilder("<step showTitle=\"");

				sb.append(String.valueOf(template.isShowTitle()).toLowerCase());

				VerticalAlignment verticalAlignment = getVerticalAlignment(template.getTitleElement());
				sb.append("\" v-alignment=\"");
				sb.append(verticalAlignment.toString());
				sb.append("\">\n<title>");
				sb.append(template.setStepTitle());
				sb.append("</title>\n<bodies>\n");

				sb.append("<body><![CDATA[\n");
				sb.append(template.setStepBody());
				sb.append("\n<]]></body>\n");

				sb.append("<body><![CDATA[\n");
				sb.append(template.setStepOtherBody());
				sb.append("\n]]></body>\n");

				sb.append("</bodies>\n<images>");

				sb.append("\n<image url=\"");
				sb.append(template.setStepImage());
				sb.append("\"/>\n");
				sb.append("<image url=\"");
				sb.append(template.setStepOtherImage());
				sb.append("\"/>\n");

				sb.append("</images>\n</step>\n");

				mainConfiguration.append(sb.toString());

			} else if (step instanceof GCUBETemplate1Text1ImageML) {
				GCUBETemplate1Text1ImageML template = (GCUBETemplate1Text1ImageML)step;

				for (TourLanguage language:tour.getSupportedLanguages()) {

					StringBuilder sb = new StringBuilder("<step");

					if (language==TourLanguage.EN){
						sb.append(" showTitle=\"");
						sb.append(String.valueOf(template.isShowTitle()).toLowerCase());
						VerticalAlignment verticalAlignment = getVerticalAlignment(template.getTitleElement());
						sb.append("\" v-alignment=\"");
						sb.append(verticalAlignment.toString());
						sb.append("\"");
					}

					sb.append(">\n");


					sb.append("<title>");
					sb.append(template.setStepTitle().get(language));
					sb.append("</title>\n<bodies>\n");

					sb.append("<body><![CDATA[\n");
					sb.append(template.setStepBody().get(language));
					sb.append("\n]]></body>\n");

					sb.append("</bodies>");

					if (language==TourLanguage.EN){
						sb.append("\n<images>");

						sb.append("\n<image url=\"");
						sb.append(template.setStepImage());
						sb.append("\"/>\n");

						sb.append("</images>");
					}
					sb.append("\n</step>\n");

					if (language==TourLanguage.EN){
						mainConfiguration.append(sb.toString());
					} else configurations.get(language).append(sb.toString());

				}
			} else if (step instanceof GCUBETemplate1Text2ImageML) {
				GCUBETemplate1Text2ImageML template = (GCUBETemplate1Text2ImageML)step;

				for (TourLanguage language:tour.getSupportedLanguages()) {

					StringBuilder sb = new StringBuilder("<step");

					if (language==TourLanguage.EN){
						sb.append(" showTitle=\"");
						sb.append(String.valueOf(template.isShowTitle()).toLowerCase());
						VerticalAlignment verticalAlignment = getVerticalAlignment(template.getTitleElement());
						sb.append("\" v-alignment=\"");
						sb.append(verticalAlignment.toString());
						sb.append("\"");
					}

					sb.append(">\n");


					sb.append("<title>");
					sb.append(template.setStepTitle().get(language));
					sb.append("</title>\n<bodies>\n");

					sb.append("<body><![CDATA[\n");
					sb.append(template.setStepBody().get(language));
					sb.append("\n]]></body>\n");

					sb.append("</bodies>");

					if (language==TourLanguage.EN){
						sb.append("\n<images>");

						sb.append("\n<image url=\"");
						sb.append(template.setStepImage());
						sb.append("\"/>\n");

						sb.append("<image url=\"");
						sb.append(template.setStepOtherImage());
						sb.append("\"/>\n");

						sb.append("</images>");
					}
					sb.append("\n</step>\n");

					if (language==TourLanguage.EN){
						mainConfiguration.append(sb.toString());
					} else configurations.get(language).append(sb.toString());

				}
			} else  if (step instanceof GCUBETemplate2Text2ImageML) {
				GCUBETemplate2Text2ImageML template = (GCUBETemplate2Text2ImageML)step;

				for (TourLanguage language:tour.getSupportedLanguages()) {

					StringBuilder sb = new StringBuilder("<step");

					if (language==TourLanguage.EN){
						sb.append(" showTitle=\"");
						sb.append(String.valueOf(template.isShowTitle()).toLowerCase());
						VerticalAlignment verticalAlignment = getVerticalAlignment(template.getTitleElement());
						sb.append("\" v-alignment=\"");
						sb.append(verticalAlignment.toString());
						sb.append("\"");
					}

					sb.append(">\n");


					sb.append("<title>");
					sb.append(template.setStepTitle().get(language));
					sb.append("</title>\n<bodies>\n");

					sb.append("<body><![CDATA[\n");
					sb.append(template.setStepBody().get(language));
					sb.append("\n]]></body>\n");

					sb.append("<body><![CDATA[\n");
					sb.append(template.setStepOtherBody().get(language));
					sb.append("\n]]></body>\n");

					sb.append("</bodies>");

					if (language==TourLanguage.EN){
						sb.append("\n<images>");

						sb.append("\n<image url=\"");
						sb.append(template.setStepImage());
						sb.append("\"/>\n");

						sb.append("<image url=\"");
						sb.append(template.setStepOtherImage());
						sb.append("\"/>\n");

						sb.append("</images>");
					}
					sb.append("\n</step>\n");

					if (language==TourLanguage.EN){
						mainConfiguration.append(sb.toString());
					} else configurations.get(language).append(sb.toString());

				}
			}
		}
		
		mainConfiguration.append("</steps>\n</guidedtour>");
		for (TourLanguage language:configurations.keySet()) {
			configurations.get(language).append("</steps>\n</guidedtour>");
		}

		System.out.println("****************** this is your main configuration XML (example name GuidedTour.xml), the source tag have to link this file:");
		System.out.println(mainConfiguration.toString());
		System.out.println();
		System.out.println();
		for (TourLanguage language:configurations.keySet()) {
			System.out.println("****************** This is a translation configuration, so a file with the same name of main configuration file but before .xml you have to put _"+language.toString()+" (example GuidedTour_"+language.toString()+".xml)");
			System.out.println(configurations.get(language).toString());
			System.out.println();
			System.out.println();
		}

	}

	protected static VerticalAlignment getVerticalAlignment(HTML titleElement)
	{
		String attribute = titleElement.getElement().getParentElement().getParentElement().getAttribute("valign");


		if ("middle".equals(attribute)) return VerticalAlignment.ALIGN_MIDDLE;
		if ("bottom".equals(attribute)) return VerticalAlignment.ALIGN_BOTTOM;

		//null or top
		return VerticalAlignment.ALIGN_TOP;
	}
}
