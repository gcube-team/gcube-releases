/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.session;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.TaxonomyGridField;
import org.gcube.portlets.user.speciesdiscovery.server.asl.SessionUtil;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.DaoSession;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.OccurrenceBuffer;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.ResultRowBuffer;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.TaxonomyRowBuffer;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CloseableIterator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation.FieldAggregator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation.SpeciesKeyProvider;
import org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation.TaxonomyClassificationAggregator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation.TaxonomyKeyProvider;
import org.gcube.portlets.user.speciesdiscovery.shared.FetchingElement;
import org.gcube.portlets.user.speciesdiscovery.shared.Occurrence;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class FetchingSessionUtil {
	
	@SuppressWarnings("unchecked")
	public static FetchingSession<? extends FetchingElement> createFetchingSession(CloseableIterator<? extends FetchingElement> source, SearchResultType type, ASLSession session) throws Exception
	{
		switch (type) {
			case SPECIES_PRODUCT: return createSpeciesProductFetchingSession((CloseableIterator<ResultRow>) source, session);
			case TAXONOMY_ITEM: return createTaxonomyItemFetchingSession((CloseableIterator<TaxonomyRow>) source, session);
			default:
				break;
		}
		return null;
	}

	protected static FetchingSession<ResultRow> createSpeciesProductFetchingSession(CloseableIterator<ResultRow> source, ASLSession session) throws Exception
	{
		FetchingBuffer<ResultRow> buffer = new ResultRowBuffer(DaoSession.getResultRowDAO(session), DaoSession.getTaxonDAO(session));
		FetchingSession<ResultRow> fetchingSession = new FetchingSession<ResultRow>(source, buffer);
		fetchingSession.addAggregator(new FieldAggregator<SpeciesGridFields, ResultRow>(new SpeciesKeyProvider(SpeciesGridFields.DATASOURCE)));
		fetchingSession.addAggregator(new FieldAggregator<SpeciesGridFields, ResultRow>(new SpeciesKeyProvider(SpeciesGridFields.MATCHING_RANK)));
		fetchingSession.addAggregator(new FieldAggregator<SpeciesGridFields, ResultRow>(new SpeciesKeyProvider(SpeciesGridFields.DATAPROVIDER)));
		fetchingSession.addAggregator(new TaxonomyClassificationAggregator<ResultRow>());
		fetchingSession.startFetching();
		SessionUtil.setCurrentSearchSession(session, fetchingSession);
		return fetchingSession;
	}
	
	protected static FetchingSession<TaxonomyRow> createTaxonomyItemFetchingSession(CloseableIterator<TaxonomyRow> source, ASLSession session) throws Exception
	{
		FetchingBuffer<TaxonomyRow> buffer = new TaxonomyRowBuffer(DaoSession.getTaxonomyDAO(session));
		FetchingSession<TaxonomyRow> fetchingSession = new FetchingSession<TaxonomyRow>(source, buffer);
		fetchingSession.addAggregator(new FieldAggregator<TaxonomyGridField, TaxonomyRow>(new TaxonomyKeyProvider(TaxonomyGridField.MATCHING_RANK)));
		fetchingSession.addAggregator(new FieldAggregator<TaxonomyGridField, TaxonomyRow>(new TaxonomyKeyProvider(TaxonomyGridField.DATASOURCE)));
		fetchingSession.addAggregator(new TaxonomyClassificationAggregator<TaxonomyRow>());
		fetchingSession.startFetching();
		SessionUtil.setCurrentSearchSession(session, fetchingSession);
		return fetchingSession;
	}
	
	public static FetchingSession<Occurrence> createOccurrenceFetchingSession(CloseableIterator<Occurrence> source, ASLSession session) throws Exception
	{
		FetchingBuffer<Occurrence> buffer = new OccurrenceBuffer(DaoSession.getOccurrenceDAO(session));
		FetchingSession<Occurrence> fetchingSession = new FetchingSession<Occurrence>(source, buffer);
		fetchingSession.startFetching();
		SessionUtil.setCurrentOccurrenceSession(session, fetchingSession);
		return fetchingSession;
	}

}
