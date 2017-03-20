package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;
import java.util.ListIterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.ModelerFull;

@Path("/ModelerFull")
public class ModelerFullUtil extends BaseUtil {
	static final Long neutral = new Long(-1);

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(ModelerFull modelerFull) throws Exception {
		return new ModelerUtil().add(new Modeler(modelerFull));
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(ModelerFull modelerFull) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("original %s", modelerFull));
		}

		Modeler copy = new Modeler(modelerFull);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("copy %s", copy));
		}

		return new ModelerUtil().update(copy);
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		return new ModelerUtil().delete(id);
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ModelerFull getModelerFull(@PathParam("id") Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			ModelerFull modelerFull = (ModelerFull) session.get(ModelerFull.class, Long.valueOf(id));

			session.getTransaction().commit();

			return modelerFull;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full modeler for [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/all/{ownerId}/{start}/{end}")
	public List<ModelerFull> getModelerFulls(@PathParam("ownerId") String ownerId, @PathParam("start") Integer start,
			@PathParam("end") Integer end, @QueryParam("status") List<Long> status, @QueryParam("species") Long species)
			throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("reading %s for %s start %s end %s", "ModelerFull", ownerId, start, end));
		}

		Session session = null;

		try {
			logger.trace(String.format("start getModelerFulls"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId)
					.setParameter("neutral", neutral)
					.setParameter("species", species == null ? neutral : species.longValue());
			if (start > 0)
				q.setFirstResult(start);
			if (end > 0) {
				if (end < start)
					end = start + 1;
				q.setMaxResults(end - start);
			}

			List<ModelerFull> list = q.list();

			if (status != null && !status.isEmpty())
				for (ListIterator<ModelerFull> iter = list.listIterator(); iter.hasNext();) {
					Modeler m = (Modeler) iter.next();
					if (!status.contains(m.getStatusId())) {
						iter.remove();
					}
				}

			session.getTransaction().commit();

			logger.trace(String.format("return ModelerFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full modeler for ownerid [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<ModelerFull> getModelerFulls(@PathParam("ownerId") String ownerId, @QueryParam("status") List<Long> status,
			@QueryParam("species") Long species) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getModelerFulls"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId)
					.setParameter("neutral", neutral)
					.setParameter("species", species == null ? neutral : species.longValue());

			List<ModelerFull> list = q.list();

			logger.trace(String.format("status  requested [%s]", status));
			if (status != null && !status.isEmpty())
				for (ListIterator<ModelerFull> iter = list.listIterator(); iter.hasNext();) {
					Modeler m = (Modeler) iter.next();
					logger.trace(String.format("examining [%s]", m));
					if (!status.contains(m.getStatusId())) {
						iter.remove();
					}
				}

			session.getTransaction().commit();

			logger.trace(String.format("return ModelerFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full modeler for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/count/{ownerId}")
	public int getModelerFullCount(@PathParam("ownerId") String ownerId, @QueryParam("species") Long species)
			throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getModelerFulls"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID_COUNT).setParameter("ownerid", ownerId)
					.setParameter("neutral", neutral)
					.setParameter("species", species == null ? neutral : species.longValue());

			Number count = (Number) q.uniqueResult();

			session.getTransaction().commit();

			logger.trace(String.format("return count %s", count));
			return count.intValue();
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve full modeler count for ownerid[%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	// private static final String _GET_ALL_ON_OWNERID = "SELECT
	// {ModelerFullView.*}
	// FROM ModelerFullView WHERE ownerid = :ownerid ORDER BY designation ASC";
	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.ModelerFull s WHERE s.ownerId = :ownerid AND ((:neutral=:species) OR (s.speciesId=:species)) ORDER BY s.designation ASC";
	private static final String _GET_ALL_ON_OWNERID_COUNT = "SELECT count(*) FROM gr.i2s.fishgrowth.model.ModelerFull s WHERE s.ownerId = :ownerid AND ((:neutral=:species) OR (s.speciesId=:species))";
	private static final Logger logger = LoggerFactory.getLogger(ModelerFullUtil.class);
}
