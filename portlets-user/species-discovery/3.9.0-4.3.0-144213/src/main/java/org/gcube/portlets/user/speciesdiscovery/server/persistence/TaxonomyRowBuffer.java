/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.server.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.session.FilterableFetchingBuffer;
import org.gcube.portlets.user.speciesdiscovery.shared.MainTaxonomicRankEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.FilterCriteria;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 20, 2013
 *
 */
public class TaxonomyRowBuffer extends AbstractSelectableDaoBuffer<TaxonomyRow> implements FilterableFetchingBuffer<TaxonomyRow> {

	private int filteredListSize = 0;

	public TaxonomyRowBuffer(AbstractPersistence<TaxonomyRow> dao) {
		super(dao, TaxonomyRow.ID_FIELD, TaxonomyRow.SELECTED);
	}

	/**
	 * {@inheritDoc}
	 * @throws Exception
	 */
	@Override
	public void add(TaxonomyRow row) throws Exception
	{
		//DEBUG
//		logger.trace("Add item "+ row.getId() + " service id: " +row.getServiceId());

		super.add(row);
	}

	@Override
	public List<TaxonomyRow> getFilteredList(FilterCriteria filterCriteria) throws SQLException {
		ResultFilter activeFiltersObject = (ResultFilter) filterCriteria;
		List<TaxonomyRow> list = new ArrayList<TaxonomyRow>();
		Iterator<TaxonomyRow> iterator = null;
//		QueryBuilder<TaxonomyRow, Integer> queryBuilder = dao.queryBuilder();
		String value;

		if(activeFiltersObject!=null){
			//FILTER BY CLASSIFICATION
			if(activeFiltersObject.isByClassification()){

				int counter = activeFiltersObject.getNumberOfData();
				String columName = null;

				if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.KINGDOM.getLabel())==0)
					columName = TaxonomyRow.KINGDOM_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.FAMILY.getLabel())==0)
					columName = TaxonomyRow.FAMILY_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.GENUS.getLabel())==0)
					columName = TaxonomyRow.GENUS_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.CLASS.getLabel())==0)
					columName = TaxonomyRow.CLASS_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.ORDER.getLabel())==0)
					columName = TaxonomyRow.ORDER_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.PHYLUM.getLabel())==0)
					columName = TaxonomyRow.PHYLUM_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.SPECIES.getLabel())==0)
					columName = TaxonomyRow.SPECIES_ID;
//
//				logger.trace("in classification filter - columName: "+columName);

				try {

					CriteriaBuilder queryBuilder = dao.getCriteriaBuilder();
					Query query = dao.createNewManager().createQuery("select r FROM TaxonomyRow r where r."+columName+ "='"+activeFiltersObject.getClassificationId()+"'");
					query.setMaxResults(counter);
					iterator = query.getResultList().iterator();
//					logger.trace("in classification filter - statement: "+queryBuilder.where().eq(columName, activeFiltersObject.getClassificationId()).getStatement());

				} catch (Exception e) {
					logger.error("Error in activeFiltersObject.isByClassification(): "+e, e);
					e.printStackTrace();
				}

				//FILTER BY DATA PROVIDER
			}else if(activeFiltersObject.isByDataProvider()){

				try {
					CriteriaBuilder queryBuilder = dao.getCriteriaBuilder();
					value = activeFiltersObject.getDataProviderName();
					CriteriaQuery<Object> cq = queryBuilder.createQuery();
					Predicate pr1 =  queryBuilder.equal(dao.rootFrom(cq).get(TaxonomyRow.DATAPROVIDER_NAME), value);
					cq.where(pr1);

					//TODO FIXME empty value
					logger.trace("FILTER BY DATA PROVIDER: "+ value );
					iterator = dao.executeCriteriaQuery(cq).iterator();

				} catch (Exception e) {
					logger.error("Error in activeFiltersObject.isByDataProvider(): "+e, e);
					e.printStackTrace();
				}

				//FILTER BY DATA SOURCE
			}else if(activeFiltersObject.isByDataSourceName()){

				try {
					CriteriaBuilder queryBuilder = dao.getCriteriaBuilder();
					value = activeFiltersObject.getDataSourceName();
					CriteriaQuery<Object> cq = queryBuilder.createQuery();
					Predicate pr1 =  queryBuilder.equal(dao.rootFrom(cq).get(TaxonomyRow.DATAPROVIDER_NAME), value);
					cq.where(pr1);
					//TODO FIXME empty value
					logger.trace("FILTER BY DATA SOURCE: "+ value );
					iterator = dao.executeCriteriaQuery(cq).iterator();

				} catch (Exception e) {
					logger.error("Error in activeFiltersObject.isByDataProvider(): "+e, e);
					e.printStackTrace();
				}


//				//FILTER BY RANK
			}else if(activeFiltersObject.isByRank()){

				try {
					CriteriaBuilder queryBuilder = dao.getCriteriaBuilder();
					value = activeFiltersObject.getRankName();
//					value = NormalizeString.lowerCaseUpFirstChar(activeFiltersObject.getRankName());
					logger.trace("in rank filter - value: "+value);
					CriteriaQuery<Object> cq = queryBuilder.createQuery();
					Predicate pr1 =  queryBuilder.equal(dao.rootFrom(cq).get(TaxonomyRow.RANK), NormalizeString.validateUndefined(value));
					cq.where(pr1);
					logger.trace("FILTER BY RANK: "+ value );
					iterator = dao.executeCriteriaQuery(cq).iterator();

				} catch (Exception e) {
					logger.error("Error in activeFiltersObject.isByRank(): "+e, e);
				}

			}

			if(iterator!=null){
				while(iterator.hasNext()){
					TaxonomyRow row = iterator.next();
					list.add(row);
				}
				filteredListSize = list.size();
			}
		}

		logger.trace("RETURNED List size " + list.size());
		return list;
	}

	@Override
	public int getFilteredListSize() throws SQLException {
//		return 0;
		return filteredListSize;
	}

	@Override
	public void updateAllSelection(boolean selection) throws Exception {

		EntityManager em = dao.createNewManager();

		//TODO generalize?
		String queryString = "UPDATE TaxonomyRow SET "
		+ ResultRow.SELECTED + " = "+ selection
		+" where "+TaxonomyRow.IS_PARENT +"=false";

		try {
			em.getTransaction().begin();

			int updateCount = em.createQuery(queryString).executeUpdate();

			logger.trace("Updated " + updateCount + " item");

			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.session.SelectableFetchingBuffer#updateAllSelectionByIds(boolean, java.util.List)
	 */
	@Override
	public void updateAllSelectionByIds(boolean selection, List<String> listIds) throws Exception {

		EntityManager em = dao.createNewManager();

		//TODO generalize?
		String queryString = "UPDATE TaxonomyRow SET "
		+ ResultRow.SELECTED + " = "+ selection
		+" where "+TaxonomyRow.IS_PARENT +"=false AND "
		+ResultRow.ID_FIELD+" IN :inclList";

		try {
			em.getTransaction().begin();

			TypedQuery<TaxonomyRow> query = em.createQuery(queryString, TaxonomyRow.class);

			query.setParameter("inclList", listIds);

			int updateCount = query.executeUpdate();

			logger.trace("Updated " + updateCount + " item");

			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

	}


}
