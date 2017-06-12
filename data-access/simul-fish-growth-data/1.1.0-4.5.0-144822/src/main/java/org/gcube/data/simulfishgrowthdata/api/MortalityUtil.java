package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Mortality;

@Path("/Mortality")
public class MortalityUtil extends BaseUtil {

	@GET
	@Path("/all")
	public List<Mortality> getMortalities(@PathParam("modelId") Long modelId) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();
			Query q = session.createQuery(_GET_ALL_ON_MODELER).setParameter("modelerId", modelId);

			List<Mortality> list = q.list();

			session.getTransaction().commit();

			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve mortality for modelid [%s]", modelId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@DELETE
	@Path("/{id}")
	public Response deleteAll(@PathParam("modelId") Long modelId) {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			int count = doDeleteAll(session, modelId);

			session.getTransaction().commit();
			return Response.status(Response.Status.OK).entity(count).build();
		} catch (Exception e) {
			logger.error("Could not delete", e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public int doDeleteAll(Session session, Long modelId) {
		Query q = session.createQuery(_DEL_ALL_ON_MODELER).setParameter("modelerId", modelId);
		int count = q.executeUpdate();
		return count;
	}

	// private static final String _GET_ALL_ON_OWNERID = "SELECT {SiteView.*}
	// FROM SiteView WHERE ownerid = :ownerid ORDER BY designation ASC";
	private static final String _GET_ALL_ON_MODELER = "FROM gr.i2s.fishgrowth.model.Mortality s WHERE s.modelerId = :modelerId ORDER BY s.temperature ASC, fromWeight DESC";
	private static final String _DEL_ALL_ON_MODELER = "DELETE FROM gr.i2s.fishgrowth.model.Mortality s WHERE s.modelerId = :modelerId";
	private static final Logger logger = LoggerFactory.getLogger(MortalityUtil.class);
}
