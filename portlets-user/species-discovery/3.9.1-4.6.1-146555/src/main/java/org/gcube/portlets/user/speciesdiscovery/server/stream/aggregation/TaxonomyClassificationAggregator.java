/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.speciesdiscovery.client.model.ClassificationModel;
import org.gcube.portlets.user.speciesdiscovery.server.stream.Aggregator;
import org.gcube.portlets.user.speciesdiscovery.shared.MainTaxonomicRankEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyInterface;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyProvider;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * modified by Francesco Mangiacrapa february 2013
 *
 */
public class TaxonomyClassificationAggregator<T extends TaxonomyProvider> implements Aggregator<T, EnumMap<MainTaxonomicRankEnum, HashMap<String, ClassificationModel>>> {

	public static final String NAME = "ClassificationAggregator";
	public static final String TAXONOMYUNKNOWN = "Unknown";
	public static final String BASETAXONOMY = "Kingdom";
	public static final String UNK = "Unk";
	public static final String UNDEFINED = "Undefined";

	public static final Map<String, MainTaxonomicRankEnum> RANKS = new HashMap<String, MainTaxonomicRankEnum>();
	static{
		for (MainTaxonomicRankEnum rank:MainTaxonomicRankEnum.values()) RANKS.put(rank.getLabel().toLowerCase(), rank);
	}

	protected EnumMap<MainTaxonomicRankEnum, HashMap<String, ClassificationModel>> aggregations;

	/**
	 * @param aggregationRank
	 */
	public TaxonomyClassificationAggregator() {
		aggregations = new EnumMap<MainTaxonomicRankEnum, HashMap<String,ClassificationModel>>(MainTaxonomicRankEnum.class);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void aggregate(TaxonomyProvider row) {
//		System.out.println("TaxonomyProvider " + row);
		List<? extends TaxonomyInterface> matchingTaxon = row.getParents();

		if(matchingTaxon == null || matchingTaxon.size()==0) return;

		EnumMap<MainTaxonomicRankEnum, TaxonomyInterface> groupedTaxon = groupTaxonByRank(matchingTaxon);
//		System.out.println("GroupedTaxon Key Set: " + groupedTaxon.keySet());

		for (MainTaxonomicRankEnum aggregationRank: MainTaxonomicRankEnum.values()){

			TaxonomyInterface taxon = groupedTaxon.get(aggregationRank);
			String taxonId;
			
			//IF RANK CLASS EXISTS INSERT INTO HASHMAP GROUP BY RANK
			if (taxon!=null && taxon.getName()!=null) {
				taxonId = addTaxonToAggregation(aggregationRank, taxon, row.getBaseTaxonValue(), row.getBaseTaxonValue(), taxon.getRank());
				setClassification(row, aggregationRank, taxonId);
			} else {

				String unknownRank = matchingTaxon.get(0).getRank()!=null?matchingTaxon.get(0).getRank():TAXONOMYUNKNOWN; // GET THE FIRST RANK CLASS

				taxon = row.getParents().get(0);
				//IF BASETAXONOMY CLASS IS NOT UNKNOWN -  INSERT INTO HASHMAP GROUP BY FIRST RANK
				if(!row.getBaseTaxonValue().equalsIgnoreCase(TAXONOMYUNKNOWN) && (taxon!=null)) {
					taxonId = addTaxonToAggregation(aggregationRank, taxon, row.getBaseTaxonValue(), row.getBaseTaxonValue(), unknownRank);
					setClassification(row, aggregationRank, taxonId);
				} else {
					//BASETAXONOMY UNKNOWN - INSERT INTO HASHMAP GROUP BY haskKey UNKNOWN RANK

					String haskKey = "["+UNK+" "+aggregationRank+"]" + " "+unknownRank;
					String unkName = matchingTaxon.get(0).getName()!=null?matchingTaxon.get(0).getName():TAXONOMYUNKNOWN; // GET THE FIRST RANK NAME

					taxonId = addTaxonToAggregation(aggregationRank, haskKey, unkName, row.getBaseTaxonValue(), row.getBaseTaxonValue(), unknownRank);
					setClassification(row, aggregationRank, taxonId);
				}

			}
		}
	}
	
	protected void setClassification(TaxonomyProvider input, MainTaxonomicRankEnum rank, String value)
	{
		switch (rank) {
			case CLASS: input.setClassID(value); break;
			case FAMILY: input.setFamilyID(value); break;
			case GENUS: input.setGenusID(value); break;
			case KINGDOM: input.setKingdomID(value); break;
			case ORDER: input.setOrderID(value); break;
			case PHYLUM: input.setPhylumID(value); break;
			case SPECIES: input.setSpeciesID(value); break;
		}
		
	}
	
	protected EnumMap<MainTaxonomicRankEnum, TaxonomyInterface> groupTaxonByRank(List<? extends TaxonomyInterface> listTaxonomyInteface)
	{
		EnumMap<MainTaxonomicRankEnum, TaxonomyInterface> groupedTaxon = new EnumMap<MainTaxonomicRankEnum, TaxonomyInterface>(MainTaxonomicRankEnum.class);

		for (TaxonomyInterface taxonomyInterface : listTaxonomyInteface) {
			if (taxonomyInterface.getRank()!=null) {
				MainTaxonomicRankEnum rank = RANKS.get(taxonomyInterface.getRank().toLowerCase());
				if (rank!=null) groupedTaxon.put(rank, taxonomyInterface);
			}
		}

		return groupedTaxon;
	}

	protected String addTaxonToAggregation(MainTaxonomicRankEnum aggregationRank, TaxonomyInterface taxon, String baseTaxonId, String baseTaxonValue, String classificationRank)
	{
		String taxonName =taxon.getName()!=null?taxon.getName().toLowerCase():"No name";
		return addTaxonToAggregation(aggregationRank, taxonName, taxonName, baseTaxonId, baseTaxonValue, classificationRank);
	}

	protected String addTaxonToAggregation(MainTaxonomicRankEnum aggregationRank, String taxonId, String classificationName, String baseTaxonId, String baseTaxonValue, String classificationRank)
	{
		HashMap<String, ClassificationModel> aggregation = getAggregation(aggregationRank);
		ClassificationModel classification = aggregation.get(taxonId);
		if (classification == null) {
			classification = new ClassificationModel(taxonId, classificationName, classificationRank.toLowerCase(), baseTaxonId, baseTaxonValue, true, 1);
			aggregation.put(taxonId, classification);
		} else classification.incrCountOf();
//		classification.getGroupedIdClassificationList().add(rowId);
			
		return taxonId;
	}
	
	protected HashMap<String, ClassificationModel> getAggregation(MainTaxonomicRankEnum rank)
	{
		HashMap<String, ClassificationModel> aggregation = aggregations.get(rank);
		if (aggregation == null) {
			aggregation = new HashMap<String, ClassificationModel>();
			aggregations.put(rank, aggregation);
		}
		return aggregation;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public EnumMap<MainTaxonomicRankEnum, HashMap<String, ClassificationModel>> getAggregation() {
		return aggregations;
	}

}
