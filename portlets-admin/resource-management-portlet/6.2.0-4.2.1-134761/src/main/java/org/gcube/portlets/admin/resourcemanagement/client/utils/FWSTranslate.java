package org.gcube.portlets.admin.resourcemanagement.client.utils;

import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
/**
 * Simply return the correspondant Feather Weight Stack label to the gCore Label
 * @author Massimiliano Assante (ISTI-CNR)
 *
 */
public class FWSTranslate {
	/**
	 * 
	 * @param gCoreLabel
	 * @return the Feather Weight Stack label if present, the same label otherwise
	 */
	public static String getFWSNameFromLabel(String gCoreLabel) {
		ResourceTypeDecorator[] toCheck = ResourceTypeDecorator.values();
		for (int i = 0; i < toCheck.length; i++) {
			if (gCoreLabel.compareTo(toCheck[i].name()) == 0) 
				return toCheck[i].getFWSName();
		}	
		return gCoreLabel;
	}
}
