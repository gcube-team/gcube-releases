/**
 * 
 */
package org.gcube.data.spd.obisplugin.util;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Taxon;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class Util {
	
	
	/**
	 * Partially clones the passed result item.
	 * @param item
	 * @return
	 */
	public static ResultItem cloneResultItem(ResultItem item)
	{
		if (item==null) return null;
		
		ResultItem clone = new ResultItem(item.getId(), item.getScientificName());
		copyTaxon(item, clone);
		clone.setCommonNames(cloneCommonName(item.getCommonNames()));
		
		return clone;
	}
	
	protected static Taxon cloneTaxon(Taxon taxon)
	{
		if (taxon==null) return null;
		
		Taxon clone = new Taxon(taxon.getId(), taxon.getScientificName());
		copyTaxon(taxon, clone);
		return clone;
	}
	
	protected static void copyTaxon(Taxon taxon, Taxon clone)
	{
		clone.setId(taxon.getId());
		clone.setScientificName(taxon.getScientificName());
		clone.setCitation(taxon.getCitation());
		clone.setCredits(taxon.getCredits());
		clone.setScientificNameAuthorship(taxon.getScientificNameAuthorship());
		clone.setRank(taxon.getRank());
		
		clone.setParent(cloneTaxon(taxon.getParent()));
	}
	
	protected static List<CommonName> cloneCommonName(List<CommonName> commonNames)
	{
		if (commonNames==null) return null;
		List<CommonName> clones = new ArrayList<CommonName>(commonNames.size());
		for (CommonName commonName:commonNames) clones.add(cloneCommonName(commonName));
		return clones;
	}
	
	protected static CommonName cloneCommonName(CommonName commonName)
	{
		if (commonName==null) return null;
		return new CommonName(commonName.getLanguage(), commonName.getName());
	}

	public static String stripNotValidXMLCharacters(String input) {

		if (input == null) return null;
		if (input.isEmpty()) return "";
		
		StringBuffer out = new StringBuffer();

		for (char current:input.toCharArray()){
			if ((current == 0x9) ||
					(current == 0xA) ||
					(current == 0xD) ||
					((current >= 0x20) && (current <= 0xD7FF)) ||
					((current >= 0xE000) && (current <= 0xFFFD)) ||
					((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}
}
