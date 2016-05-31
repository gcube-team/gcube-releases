/**
 * 
 */
package org.gcube.portlets.widgets.guidedtour.resources.client;

import java.util.ArrayList;

import org.gcube.portlets.widgets.guidedtour.client.steps.TourStep;
import org.gcube.portlets.widgets.guidedtour.client.types.ThemeColor;
import org.gcube.portlets.widgets.guidedtour.resources.GuidedTourResourceGenerator;
import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.resources.ext.DefaultExtensions;
import com.google.gwt.resources.ext.ResourceGeneratorType;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
@DefaultExtensions(value = {".xml"})
@ResourceGeneratorType(GuidedTourResourceGenerator.class)
public interface GuidedTourResource extends ResourcePrototype {
	
	/**
	 * The tour title.
	 * @return
	 */
	String getTitle();
	
	/**
	 * The component guide url.
	 * @return
	 */
	String getGuide();
	
	/**
	 * The tour color theme.
	 * @return
	 */
	ThemeColor getThemeColor();
	
	/**
	 * The tour window width.
	 * @return
	 */
	int getWidth();
	
	/**
	 * The tour window height.
	 * @return
	 */
	int getHeight();
	
	/**
	 * @return
	 */
	boolean useMask();
	
	/**
	 * The tour languages.
	 * @return
	 */
	TourLanguage[] getLanguages();
	
	/**
	 * The tour steps.
	 * @return
	 */
	ArrayList<TourStep> getSteps();

}
