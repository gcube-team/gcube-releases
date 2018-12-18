/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.locale;

import java.util.List;

import com.google.gwt.core.shared.GWT;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 31, 2014
 *
 */
public class LocaleViewManager extends LocalesManager{

	private boolean asExclusive;

	/**
	 * 
	 * @param loadedLocales
	 * @param asExclusiveValues if true each combo have exclusive value
	 */
	public LocaleViewManager(List<String> loadedLocales, boolean asExclusiveValues) {
		super(loadedLocales);
		this.asExclusive = asExclusiveValues;
	}

	public boolean isAsExclusive() {
		return asExclusive;
	}
	

	public List<String> getLocales(){
		if(asExclusive)
			return super.getExclusivesLocales();
		
		return super.getListLocales();
	}
	
	public void selectLocale(String value){
		GWT.log("Selected locale: "+value);
		super.selectLocale(value);
	}
	
	public boolean deselectLocale(String value){
		GWT.log("Deselected locale: "+value);
		return super.deselectLocale(value);
	}

}
