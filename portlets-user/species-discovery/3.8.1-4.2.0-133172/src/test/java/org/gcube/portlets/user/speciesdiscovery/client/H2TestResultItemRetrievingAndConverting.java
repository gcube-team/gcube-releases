/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.DaoSession;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class H2TestResultItemRetrievingAndConverting {
	
	private static final int MAX_ELEMENTS = 20;
	static String sessionID = "1";
	static String user = "test.user";
	static ScopeBean scope;
	static ASLSession session;
	
	
	public static void main(String[] args) throws Exception
	{
		
		session = SessionManager.getInstance().getASLSession(sessionID, user);
		scope = new ScopeBean("/gcube/devsec");
		session.setScope(scope.toString());
		
		EntityManagerFactory factory = DaoSession.getEntityManagerFactory(session);
		
		EntityManager em = factory.createEntityManager();
//		ScopeProvider.instance.set("/gcube/devsec");
		
		int removed;
		String tableName;
		List<ResultRow> list;
		
//		String tableName = "ParentTaxonomyRow";
//		
//		List<ResultRow> list = getList(em, tableName);
//		
//		System.out.println("list "+tableName+ "size is "+list.size());
		
//		em = factory.createEntityManager();
//		removed = removeAll(em, tableName);
//		
//		System.out.println("removed " +removed);
		
		tableName = "ResultRow";
		
		em = factory.createEntityManager();
		list = getList(em, tableName);
		
		System.out.println("list "+tableName+ "size is "+list.size());
		
	
//		removeAll(em, "ParentTaxonomyRow");
//		em = factory.createEntityManager();
//		removed = removeAll(em, tableName);
		
//		System.out.println("removed " +removed);


//		SpeciesService taxonomyService = new SpeciesService(scope, session);


		long start = System.currentTimeMillis();
		long last = System.currentTimeMillis();
		int counter = 0;

//		CloseableIterator<ResultElement> input = taxonomyService.searchByQuery("'sarda sarda' as ScientificName return Taxon");// searchByFilters(searchTerm, SearchTypeEnum.BY_SCIENTIFIC_NAME, searchFilters);
//		SearchFilters searchFilters = new SearchFilters();
//		searchFilters.setResultType(SpeciesCapability.TAXONOMYITEM);
//		SearchResultType resultType = QueryUtil.getResultType(searchFilters);
//		CloseableIterator<FetchingElement> output = IteratorChainBuilder.buildChain(input, resultType, session);
//
//		FetchingSession<TaxonomyRow> fetchingSession = (FetchingSession<TaxonomyRow>) FetchingSessionUtil.createFetchingSession(output, resultType, session);
//		

//		
//		while(!fetchingSession.isComplete()) {
//			
//			System.out.println((System.currentTimeMillis()-start)+" buffer size "+fetchingSession.getBufferSize());
//			last = System.currentTimeMillis();
//			
//			Thread.sleep(1000);
//			
//			System.out.println("COMPLETE: "+(System.currentTimeMillis()-last)+"ms from last item to close the stream; buffer size "+fetchingSession.getBufferSize());
//
//			System.out.println(fetchingSession.getBufferSize() + " results in "+(System.currentTimeMillis()-start)+"ms total; "+(last-start)+"ms from first to last result");
//		}
	

//		System.out.println("Start time: "+last);
//		
//		while(output.hasNext()) {
//			
//			System.out.println((System.currentTimeMillis()-last)+"ms "+output.next());
//			last = System.currentTimeMillis();
//			counter++;
//		}
		
//		Stream<ResultElement> input = taxonomyService.searchByQuery2("'sarda sarda' as ScientificName return Taxon");
//		//from ResultItem to ResultRow
////		List<ResultItem> listElements = new ArrayList<ResultItem>();
////		List<ResultRow> listRR = new ArrayList<ResultRow>();
////		ResultItemConverter converter = new ResultItemConverter(session);
//		
//		TaxonomyItemConverter converter = new TaxonomyItemConverter(session);
//		List<TaxonomyRow> listRR = new ArrayList<TaxonomyRow>();
//		List<TaxonomyItem> listElements = new ArrayList<TaxonomyItem>();
//		
//		while(input.hasNext()) {
//			
////			ResultItem result = (ResultItem) input.next();
//			
//			TaxonomyItem result = (TaxonomyItem) input.next();
//			
//			System.out.println((System.currentTimeMillis()-last)+" ms to recover "+result);
//			last = System.currentTimeMillis();
//			counter++;
//			
//			listElements.add(result);
//			
//			listRR.add(converter.convert(result));
//			
//			System.out.println((System.currentTimeMillis()-last)+" ms to convert "+result);
//			
//			if(MAX_ELEMENTS == counter)
//				break;
//		}
//		
//		input.close();
//
//		counter = 0;
//		start = System.currentTimeMillis();
//		last = System.currentTimeMillis();
		

//		
////		for (ResultRow resultRow : listRR) {
//		for (TaxonomyRow resultRow : listRR) {
//			
//			System.out.println(counter + ")" + (System.currentTimeMillis()-last)+" ms "+resultRow);
//			last = System.currentTimeMillis();
//			
//			
//			try{
//		
////				storeTaxonParents(resultRow);
////				storeCommonName(listElements.get(counter), resultRow);
//				storeRR(resultRow);
//			
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			if(counter==50)
//				break;
//			
//			counter++;
//		}
		
//		System.out.println("BUFFER SIZE: " + fetchingSession.getBuffer().getList().size());
		
		
//		System.out.println("COMPLETE: "+(System.currentTimeMillis()-last)+" ms from last item to close the stream");
//		
//		System.out.println(counter + " results in "+(System.currentTimeMillis()-start)+" ms total; "+(last-start)+" ms from first to last result");
		
		
		
		List<String> listServiceId = new ArrayList<String>();
		
		int i = 0;
		for (ResultRow rr : list) {
			
			listServiceId.add(rr.getIdToString());
			
			System.out.println(++i +")listserviceId "+listServiceId);
			
			if(i==30)
				break;
		}
		
		
		
		em = factory.createEntityManager();
		String queryStr = "select t from ResultRow t where t."+ResultRow.ID_FIELD+" IN :inclList";
		
		TypedQuery<ResultRow> query2 = em.createQuery(queryStr, ResultRow.class);
		
		query2.setParameter("inclList", listServiceId);
		
//		query.setFirstResult(2);
//		
//		query.setMaxResults(5);
		
		start = System.currentTimeMillis();
		List<ResultRow> results = query2.getResultList();
//		
		i = 0;
		for (ResultRow r: results) {
			System.out.println("ResultRow query "+ ++i +") " + r);
		}

//		ExpressionBuilder expression = new ExpressionBuilder(Taxon.class);
//		expression.get(Taxon.RANK).equalsIgnoreCase("class");
//
//
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		
//		// Query for a List of objects.
////		CriteriaQuery<Object> cq = cb.createQuery();
//		
//		CriteriaQuery<Taxon> cq = cb.createQuery(Taxon.class);
//		
//		Root<Taxon> e = cq.from(Taxon.class);
//	
//		cq.where(cb.equal(e.get(Taxon.RANK), "class"));
//		
//		query = em.createQuery(cq);
//		
//		List<Taxon> result = query.getResultList();
//		
//		for (Taxon taxon : result) {
//			System.out.println("taxon: " + taxon);
//		}
		
	}

	protected static void storeRR(TaxonomyRow row){
		EntityManagerFactory factory = DaoSession.getEntityManagerFactory(session);
		
		EntityManager em = factory.createEntityManager();
		
	    em.getTransaction().begin();	
		
	    em.persist(row);
	   
	    em.getTransaction().commit();

	    em.close();
	}
	
	public static int removeAll(EntityManager em, String tableName) {

		
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM "+tableName).executeUpdate();
			em.getTransaction().commit();
			System.out.println("DELETE FROM "+tableName + " " + removed +" items");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}

		return removed;
	}
	
	public static List<ResultRow> getList(EntityManager em, String tableName) {

		List<ResultRow> listResultRow = new ArrayList<ResultRow>();
		try {
			Query query = em.createQuery("select t from "+tableName+" t");

			listResultRow = query.getResultList();
		} finally {
			em.close();
		}
		return listResultRow;
	}

}
