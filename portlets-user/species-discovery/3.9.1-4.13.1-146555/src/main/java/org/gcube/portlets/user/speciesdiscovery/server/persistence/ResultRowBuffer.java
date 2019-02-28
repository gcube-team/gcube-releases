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

import org.apache.log4j.Logger;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.session.FilterableFetchingBuffer;
import org.gcube.portlets.user.speciesdiscovery.shared.MainTaxonomicRankEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.Taxon;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.FilterCriteria;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 20, 2013
 *
 */
public class ResultRowBuffer extends AbstractSelectableDaoBuffer<ResultRow> implements FilterableFetchingBuffer<ResultRow> {

	protected Logger logger = Logger.getLogger(ResultRowBuffer.class);

	private AbstractPersistence<Taxon> taxonDao;
	private int filteredListSize = 0;

	public ResultRowBuffer(AbstractPersistence<ResultRow> dao, AbstractPersistence<Taxon> taxonDao)
	{
		super(dao, ResultRow.ID_FIELD, ResultRow.SELECTED);
		this.taxonDao = taxonDao;
	}

	/**
	 * {@inheritDoc}
	 * @throws Exception
	 */
	@Override
	public void add(ResultRow row) throws Exception{
		//FOR DEBUG
//		logger.trace("Add item "+ row.getId() + " service id: " +row.getServiceId());

		super.add(row);
	}

	public int getFilteredListSize() throws SQLException
	{
		return filteredListSize;
	}


	//TODO MODIFIED
	@Override
	public List<ResultRow> getFilteredList(FilterCriteria filterCriteria) throws SQLException {

		ResultFilter activeFiltersObject = (ResultFilter) filterCriteria;
		List<ResultRow> list = new ArrayList<ResultRow>();
		Iterator<ResultRow> iterator = null;
		String value;

		if(activeFiltersObject!=null){
			//FILTER BY CLASSIFICATION
			if(activeFiltersObject.isByClassification()){

				int counter = activeFiltersObject.getNumberOfData();
				logger.trace("in classification filter - counter: "+counter);
				logger.trace("in classification filter - rank: "+activeFiltersObject.getRankClassification());
				logger.trace("in classification filter - classification id: "+activeFiltersObject.getClassificationId());

				String columName = null;

				if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.KINGDOM.getLabel())==0)
					columName = ResultRow.KINGDOM_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.FAMILY.getLabel())==0)
					columName = ResultRow.FAMILY_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.GENUS.getLabel())==0)
					columName = ResultRow.GENUS_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.CLASS.getLabel())==0)
					columName = ResultRow.CLASS_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.ORDER.getLabel())==0)
					columName = ResultRow.ORDER_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.PHYLUM.getLabel())==0)
					columName = ResultRow.PHYLUM_ID;
				else if(activeFiltersObject.getRankClassification().compareTo(MainTaxonomicRankEnum.SPECIES.getLabel())==0)
					columName = ResultRow.SPECIES_ID;

//				logger.trace("in classification filter - columName: "+columName);

				try {
					CriteriaBuilder queryBuilder = dao.getCriteriaBuilder();
					Query query = dao.createNewManager().createQuery("select r FROM ResultRow r where r."+columName+ "='"+activeFiltersObject.getClassificationId()+"'");
					query.setMaxResults(counter);
					iterator = query.getResultList().iterator();
//					logger.trace("in classification filter - statement: "+queryBuilder.where().eq(columName, activeFiltersObject.getClassificationId()).getStatement());

				} catch (Exception e) {
					logger.error("Error in activeFiltersObject.isByClassification(): "+e, e);
				}

				//FILTER BY DATA PROVIDER
			}else if(activeFiltersObject.isByDataProvider()){

				try {
					CriteriaBuilder queryBuilder = dao.getCriteriaBuilder();
					value = activeFiltersObject.getDataProviderName();
					CriteriaQuery<Object> cq = queryBuilder.createQuery();
					Predicate pr1 =  queryBuilder.equal(dao.rootFrom(cq).get(ResultRow.DATAPROVIDER_NAME), value);
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
					Predicate pr1 =  queryBuilder.equal(dao.rootFrom(cq).get(ResultRow.DATASOURCE_NAME), NormalizeString.validateUndefined(value));
					cq.where(pr1);

					logger.trace("FILTER BY DATA DATA SOURCE NAME: "+ value );

					iterator = dao.executeCriteriaQuery(cq).iterator();

				} catch (Exception e) {
					logger.error("Error in activeFiltersObject.isByDataSourceName(): "+e, e);
					e.printStackTrace();
				}

				//FILTER BY RANK
			}else if(activeFiltersObject.isByRank()){

				try {

					value = activeFiltersObject.getRankName();
					EntityManager em = dao.createNewManager();

					String queryString = "select *" +
						" FROM "+ResultRow.class.getSimpleName()+" r" +
						" INNER JOIN RESULTROW_TAXON rt on r.ID=rt.RESULTROW_ID" +
						" INNER JOIN "+Taxon.class.getSimpleName()+" t on t.INTERNALID=rt.MATCHINGTAXON_INTERNALID" +
						" where t.RANK = '"+value+"' and t.ID IN" +
						" (select MIN(tax.ID) from TAXON tax)";

					Query query = em.createNativeQuery(queryString, ResultRow.class);
					List<ResultRow> listResultRow = new ArrayList<ResultRow>();
					try {

						listResultRow = query.getResultList();
					} catch (Exception e) {
						logger.error("Error in ResultRow - executeCriteriaQuery: " + e.getMessage(), e);
					}  finally {
						em.close();
					}

					iterator = listResultRow.iterator();

				} catch (Exception e) {
					logger.error("Error in activeFiltersObject.isByRank(): "+e, e);
					e.printStackTrace();
				}
			}

			if(iterator!=null){
				while(iterator.hasNext()){
					ResultRow row = iterator.next();
					list.add(row);
				}
				filteredListSize = list.size();
			}
		}

		logger.trace("RETURNED List size " + list.size());
		return list;
	}

	@Override
	public void updateAllSelection(boolean selection) throws Exception {

		EntityManager em = dao.createNewManager();

		//TODO use filterMap

		try {
			em.getTransaction().begin();
			int updateCount = em.createQuery("UPDATE ResultRow SET " + ResultRow.SELECTED + " = "+ selection).executeUpdate();
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
	public void updateAllSelectionByIds(boolean selection, List<String> listIds)
			throws Exception {

		EntityManager em = dao.createNewManager();

		String queryString = "UPDATE ResultRow t SET "
		+ ResultRow.SELECTED + " = "+ selection +" where  "
		+ ResultRow.ID_FIELD+" IN :inclList";

		try {
			em.getTransaction().begin();

			TypedQuery<ResultRow> query = em.createQuery(queryString, ResultRow.class);

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
