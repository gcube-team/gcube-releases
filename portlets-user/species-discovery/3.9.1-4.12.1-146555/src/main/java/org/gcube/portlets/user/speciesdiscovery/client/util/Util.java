/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util;

import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class Util {

	protected static final String[] MAIN_TAXONOMIC_RANK = new String[]{"unranked", "domain", "kingdom", "phylum", "division", "class", "order", "family", "genus", "species"};


	public static boolean isMainTaxonomicRank(String rank)
	{
		for (String mainRank:MAIN_TAXONOMIC_RANK) if (mainRank.equalsIgnoreCase(rank)) return true;
		return false;
	}
	
	public static ColumnConfig createColumnConfig(GridField field, int width)
	{
		ColumnConfig columnConfig = new ColumnConfig(field.getId(), field.getName(), width);
		columnConfig.setSortable(field.isSortable());
		
		return columnConfig;
	}
	
	public static SpeciesCapability getCapabilityFromResultType(SearchResultType resultType)
	{
		
		switch (resultType) {
			case SPECIES_PRODUCT: return SpeciesCapability.RESULTITEM;
			case OCCURRENCE_POINT: return SpeciesCapability.OCCURRENCESPOINTS;
			case TAXONOMY_ITEM: return SpeciesCapability.TAXONOMYITEM;
		}
		return null;
	}
	
	public static String cleanValue(String value)
	{
		if (value==null || value.isEmpty()) return "";
		return value;
	}
	
}
