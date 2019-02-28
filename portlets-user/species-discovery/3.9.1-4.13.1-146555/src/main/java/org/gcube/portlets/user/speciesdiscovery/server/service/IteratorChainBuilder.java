/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.service;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CastConverter;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CloseableIterator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.ConversionIterator;
import org.gcube.portlets.user.speciesdiscovery.shared.FetchingElement;
import org.gcube.portlets.user.speciesdiscovery.shared.Occurrence;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class IteratorChainBuilder {
	
	@SuppressWarnings("unchecked")
	public static <O extends FetchingElement> CloseableIterator<O> buildChain(CloseableIterator<ResultElement> input, SearchResultType type, ASLSession session)
	{
		switch (type) {
			case SPECIES_PRODUCT: return (CloseableIterator<O>) buildSpecieProductChain(input, session);
			case TAXONOMY_ITEM: return (CloseableIterator<O>) buildTaxonomyItemChain(input, session);
			default:
				break;
		}
		
		return null;
	}
	
	protected static CloseableIterator<ResultRow> buildSpecieProductChain(CloseableIterator<ResultElement> input, ASLSession session)
	{
		//from ResultElement to ResultItem
		ConversionIterator<ResultElement, ResultItem> caster = buildCaster(input);
		
		//from ResultItem to ResultRow
		ResultItemConverter converter = new ResultItemConverter(session);
		ConversionIterator<ResultItem, ResultRow> inputConverter = new ConversionIterator<ResultItem, ResultRow>(caster, converter);
		
		return inputConverter;
	}
	
	protected static CloseableIterator<TaxonomyRow> buildTaxonomyItemChain(CloseableIterator<ResultElement> input, ASLSession session)
	{
		//from ResultElement to TaxonomyItem
		ConversionIterator<ResultElement, TaxonomyItem> caster = buildCaster(input);
		
		//from TaxonomyItem to TaxonomyRow
		TaxonomyItemConverter converter = new TaxonomyItemConverter(session);
		ConversionIterator<TaxonomyItem, TaxonomyRow> inputConverter = new ConversionIterator<TaxonomyItem, TaxonomyRow>(caster, converter);
		
		return inputConverter;
	}
	
	protected static <I,O> ConversionIterator<I, O> buildCaster(CloseableIterator<I> input)
	{		
		CastConverter<I, O> elementConverter = new CastConverter<I, O>();
		ConversionIterator<I, O> caster = new ConversionIterator<I, O>(input, elementConverter);
		return caster;
	}
	
	public static CloseableIterator<Occurrence> buildOccurrenceConverter(CloseableIterator<OccurrencePoint> input)
	{
		OccurrenceConverter occurrenceConverter = new OccurrenceConverter();
		ConversionIterator<OccurrencePoint, Occurrence> converter = new ConversionIterator<OccurrencePoint, Occurrence>(input, occurrenceConverter);
		return converter;
	}

}
