/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.session.SelectableFetchingBuffer;
import org.gcube.portlets.user.speciesdiscovery.shared.SelectableElement;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public abstract class AbstractSelectableDaoBuffer<T extends SelectableElement> extends AbstractDaoBuffer<T> implements SelectableFetchingBuffer<T> {

	protected String idField;
	protected String selectionField;
	
	public AbstractSelectableDaoBuffer(AbstractPersistence<T> dao, String idField, String selectionField) {
		super(dao);
		this.idField = idField;
		this.selectionField = selectionField;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> getSelected() throws SQLException
	{
		
		List<T> selectedItems = new ArrayList<T>();
		try{
		CriteriaBuilder queryBuilder = dao.getCriteriaBuilder();
		CriteriaQuery<Object> cq = queryBuilder.createQuery();
		Predicate pr1 =  queryBuilder.equal(dao.rootFrom(cq).get(selectionField), true);
		cq.where(pr1);
		
		selectedItems = dao.executeCriteriaQuery(cq);

		}catch (Exception e) {
			logger.error("an erro occurred in get selected",e);
		}
		logger.trace("get selected return size: "+ selectedItems.size() );
		return selectedItems;
	}	
	
	@Override
	public void updateSelection(int id, boolean selection) throws Exception {
		T item = dao.getItemByKey(id);
		item.setSelected(selection);
		dao.update(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void updateAllSelection(boolean selection) throws Exception;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int sizeSelected() throws Exception {
		return getSelected().size();
	}
	
}
