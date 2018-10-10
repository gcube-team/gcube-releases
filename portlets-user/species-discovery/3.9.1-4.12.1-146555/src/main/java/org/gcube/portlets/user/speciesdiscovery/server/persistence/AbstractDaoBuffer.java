/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.persistence;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.session.FetchingBuffer;
import org.gcube.portlets.user.speciesdiscovery.shared.FetchingElement;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public abstract class AbstractDaoBuffer<T extends FetchingElement> implements FetchingBuffer<T> {
	
	protected Logger logger = Logger.getLogger(AbstractDaoBuffer.class);
	
	protected AbstractPersistence<T> dao;
	
	public AbstractDaoBuffer(AbstractPersistence<T> dao)
	{
		this.dao = dao;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(T item) throws Exception
	{
		try{
		//DEBUG
//		logger.trace("Add item "+ item.getId() +" to table "+dao.getTableConfig().getTableName());

		dao.insert(item);
		}
		catch (Exception e) {
//			System.out.println("Error in add item: "+e);
			logger.error("Error in add item: "+e,e);
//			throw new SQLException(e);
		}
//		dao.create(item);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() throws Exception
	{
		return (int) dao.countItems();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> getList() throws Exception{
		return dao.getList();
	}
	
	@Override
	public List<T> getList(int startIndex, int offset) throws Exception{
		return dao.getList(startIndex, offset);
	}
	
	@Override
	public List<T> getList(Map<String, String> filterANDMap, int startIndex, int offset) throws Exception{
		return dao.getList(filterANDMap, startIndex, offset);
	}
}
