/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.persistence;

import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence;
import org.gcube.portlets.user.speciesdiscovery.shared.Occurrence;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class OccurrenceBuffer extends AbstractDaoBuffer<Occurrence> {

	public OccurrenceBuffer(AbstractPersistence<Occurrence> dao) {
		super(dao);
	}
	
	@Override
	public void add(Occurrence occurrence) throws Exception
	{
		logger.trace("Add occurrence "+ occurrence.getServiceId());
//		logger.trace("Add occurrence "+ occurrence);
		super.add(occurrence);
	}

}
