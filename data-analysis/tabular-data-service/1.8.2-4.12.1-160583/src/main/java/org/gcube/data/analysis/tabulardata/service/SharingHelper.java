package org.gcube.data.analysis.tabulardata.service;

import static org.gcube.data.analysis.tabulardata.utils.Util.getOwnerhipAuthorizedObject;

import javax.persistence.EntityManager;

import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity.Type;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.exceptions.NoSuchObjectException;
import org.gcube.data.analysis.tabulardata.metadata.Identifiable;

public class SharingHelper {

	//TODO: remember the u() and g() function for sharing
	public static <T, I extends Identifiable> I share(Class<I> returnClass, T entityId, EntityManager entityManager, SharingEntity... entities)
			throws NoSuchObjectException, InternalSecurityException {
		final I sTr = getOwnerhipAuthorizedObject(entityId, returnClass, entityManager);

		if (entities!=null && entities.length>0){
			for (SharingEntity entity: entities){
				if (entity.getType()==Type.USER){
					String userToken = String.format("u(%s)", entity.getIdentifier());
					if (!sTr.getSharedWith().contains(userToken))
						sTr.getSharedWith().add(userToken);
				}else {
					String groupToken = String.format("g(%s)", entity.getIdentifier());
					if (!sTr.getSharedWith().contains(groupToken))
						sTr.getSharedWith().add(groupToken);
				}
			}
			entityManager.getTransaction().begin();
			entityManager.merge(sTr);
			entityManager.getTransaction().commit();
		}
		return sTr;
	}

	//TODO: remember the u() and g() function for sharing
	public static <T, I extends Identifiable> I unshare(Class<I> returnClass, T entityId, EntityManager entityManager, SharingEntity... entities)
			throws NoSuchObjectException, InternalSecurityException {
		final I sTr = getOwnerhipAuthorizedObject(entityId, returnClass, entityManager);

		if (entities!=null && entities.length>0){
			for (SharingEntity entity: entities)
				if (entity.getType()==Type.USER)
					sTr.getSharedWith().remove(String.format("u(%s)", entity.getIdentifier()));
				else 
					sTr.getSharedWith().remove(String.format("g(%s)", entity.getIdentifier()));
			entityManager.getTransaction().begin();
			entityManager.merge(sTr);
			entityManager.getTransaction().commit();
		}
		return sTr;
	}

}
