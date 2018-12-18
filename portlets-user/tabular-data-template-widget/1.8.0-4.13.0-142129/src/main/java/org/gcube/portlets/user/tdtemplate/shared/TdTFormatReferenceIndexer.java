/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 12, 2015
 *
 */
public class TdTFormatReferenceIndexer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7321768699202902725L;
	
	protected LinkedHashMap<String, TdTFormatReference> formatsIndexer = new LinkedHashMap<String, TdTFormatReference>();
	
	public TdTFormatReferenceIndexer(){
	}

	public void putFormat(TdTFormatReference format){
		formatsIndexer.put(format.getId(), format);
	}
	
	public void remove(TdTFormatReference format){
		formatsIndexer.remove(format);
	}
	
	public void resetIndexer(){
		formatsIndexer.clear();
	}
	
	public TdTFormatReference getFormatByFormatId(String formatId){
		return formatsIndexer.get(formatId);
	}

	public LinkedHashMap<String, TdTFormatReference> getFormatsIndexer() {
		return formatsIndexer;
	}
	
	public  ArrayList<TdTFormatReference> getFormats(){
		return new ArrayList<TdTFormatReference>(formatsIndexer.values());
	}
	
	public int size(){
		return formatsIndexer.size();
	}

	public boolean haveFormats(){
		return formatsIndexer.size()>0;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTValueFormatIndexer [formatsIndexer=");
		builder.append(formatsIndexer);
		builder.append("]");
		return builder.toString();
	}
}
