package org.gcube.data.analysis.tabulardata.cube.metadata;

import java.util.List;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.gcube.data.analysis.tabulardata.cube.metadata.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.metadata.model.JPATable;
import org.gcube.data.analysis.tabulardata.cube.metadata.model.JPATableFactory;
import org.gcube.data.analysis.tabulardata.cube.metadata.model.TableFactory;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Default
@Singleton
public class JPACubeMetadataWrangler implements CubeMetadataWrangler {

	Logger log = LoggerFactory.getLogger(JPACubeMetadataWrangler.class);

	EntityManagerFactory  emf = null;

	ISEntityManagerProvider emp;

	@Inject
	public JPACubeMetadataWrangler(ISEntityManagerProvider emp) {
		super();
		this.emp = emp;
	}

	// public Table update(Table table){
	// TableConsistencyChecker.checkTableConsistency(table);
	// JPATable jpaTable = JPATableFactory.createJPATable(table);
	// mergeEntity(jpaTable);
	// return TableFactory.createTable(jpaTable);
	// }

	@Override
	public synchronized Table save(Table table, boolean overwrite) {
		TableConsistencyChecker.checkTableConsistency(table);
		JPATable jpaTable = null;
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try{
			if (overwrite)
				try {
					jpaTable = JPATableFactory.updateJPATable(getJPATableById(table.getId().getValue(), em), table);
				} catch (NoSuchTableException e) {
					log.warn("the table with id {} is not persisted, cannot overwrite ",table.getId().getValue());
				}

			if (jpaTable==null)
				jpaTable = JPATableFactory.createJPATable(table);

			persistEntity(jpaTable, em);
			return TableFactory.createTable(jpaTable);
		}finally{
			em.close();
		}
	}

	private void initializeIfNot() {
		if (emf == null)
			emf = emp.get();
	}

	@Override
	public synchronized Table get(TableId id) throws NoSuchTableException {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try{
			return TableFactory.createTable(getJPATableById(id.getValue(), em));
		}finally{
			em.close();
		}
	}

	@Override
	public Table getTableByName(String name) throws NoSuchTableException {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try{
			return TableFactory.createTable(getJPATableByTableName(name, em));
		}finally{
			em.close();
		}
	}

	@Override
	public synchronized List<Table> getAll() {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try{
			return Lists.transform(getAllJPATables(em), new Function<JPATable, Table>() {

				@Override
				public Table apply(JPATable input) {
					return TableFactory.createTable(input);
				}

			});
		}finally{
			em.close();
		}
	}

	@Override
	public synchronized List<Table> getAll(TableType tableType) {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try{
			return Lists.transform(getAllJPATablesByType(tableType, em), new Function<JPATable, Table>() {

				@Override
				public Table apply(JPATable input) {
					return TableFactory.createTable(input);
				}

			});
		}finally{
			em.close();
		}
	}

	@Override
	public synchronized void remove(TableId id) throws NoSuchTableException {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try{
			removeEntity(getJPATableById(id.getValue(), em), em);
		}finally{
			em.close();
		}
	}

	private JPATable getJPATableById(long id, EntityManager em) throws NoSuchTableException {
		JPATable table = em.find(JPATable.class, id);
		if (table==null)throw new NoSuchTableException(id);
		return table;
	}

	private JPATable getJPATableByTableName(String name, EntityManager em) throws NoSuchTableException {
		TypedQuery<JPATable> query = em.createNamedQuery("Table.findByName", JPATable.class);
		query.setParameter("Name", name);
		try{
			return query.getSingleResult();
		}catch(NoResultException e ){
			throw new NoSuchTableException(name);
		}
	}

	private List<JPATable> getAllJPATables(EntityManager em) {
		TypedQuery<JPATable> query = em.createNamedQuery("Table.findAll", JPATable.class);
		return query.getResultList();

	}

	private List<JPATable> getAllJPATablesByType(TableType tableType, EntityManager em ) {
		TypedQuery<JPATable> query = em.createNamedQuery("Table.findAllByType", JPATable.class);
		query.setParameter("TableType", tableType);
		return query.getResultList();

	}

	private void persistEntity(JPATable entity, EntityManager em) {
		try{
			em.getTransaction().begin();					
			if (!em.contains(entity))
				em.persist(entity);
			else em.merge(entity);
			em.flush();
			em.getTransaction().commit();
		}catch(Throwable t){
			log.warn("DB error",t);
			em.getTransaction().rollback();
		}
		//log.debug("Saved entity: " + entity);
	}

	// private void mergeEntity(Object entity){
	// log.debug("Updating entity: " + entity);
	// getEntityManager().getTransaction().begin();
	// getEntityManager().merge(entity);
	// getEntityManager().getTransaction().commit();
	// log.debug("Updated entity: " + entity);
	// }

	private void removeEntity(Object entity, EntityManager em) {
		em.getTransaction().begin();
		em.remove(entity);
		em.flush();
		em.getTransaction().commit();
	}

	private EntityManagerFactory getEntityManagerFactory() {
		initializeIfNot();
		return emf;
	}

}
