package org.gcube.data.simulfishgrowthdata.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.gcube.data.simulfishgrowthdata.util.ExcelDataReader;
import org.gcube.data.simulfishgrowthdata.util.ExcelReader;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.gcube.data.simulfishgrowthdata.util.UserFriendlyException;
import org.gcube.data.simulfishgrowthdata.util.Utils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.Usage;

@Path("/Modeler")
public class ModelerUtil extends BaseUtil {
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(Modeler modeler) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			session.save(modeler);

			manageUploadFiles(session, modeler);

			session.flush();

			session.getTransaction().commit();
			return Response.status(Response.Status.OK).entity(modeler.getId()).build();
		} catch (Exception e) {
			logger.error(String.format("Could not add modeler [%s]", modeler), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY)
					.entity(Joiner.on(" ~ ").skipNulls().join(UserFriendlyException.getFriendlyTraceFrom(e))).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
		// return Response.status(Response.Status.EXPECTATION_FAILED).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Modeler modeler) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			session.update(modeler);

			manageUploadFiles(session, modeler);

			session.flush();

			session.getTransaction().commit();
			return Response.status(Response.Status.OK).entity(modeler).build();
		} catch (Exception e) {
			logger.error(String.format("Could not update modeler [%s]", modeler), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY)
					.entity(Joiner.on(" ~ ").skipNulls().join(UserFriendlyException.getFriendlyTraceFrom(e))).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			if (doDelete(session, id))
				session.flush();

			session.getTransaction().commit();
			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			logger.error(String.format("Could not delete modeler [%s]", id), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Modeler getModeler(@PathParam("id") Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Modeler modeler = (Modeler) session.get(Modeler.class, Long.valueOf(id));

			session.getTransaction().commit();

			return modeler;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve modeler [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<Modeler> getModelers(@PathParam("ownerId") String ownerId, @QueryParam("statuses") List<Long> statuses)
			throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getModelers"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId);

			List<Modeler> list = q.list();
			if (statuses != null && !statuses.isEmpty())
				for (ListIterator<Modeler> iter = list.listIterator(); iter.hasNext();) {
					Modeler m = (Modeler) iter.next();
					if (!statuses.contains(m.getStatusId())) {
						iter.remove();
					}
				}

			session.getTransaction().commit();

			logger.trace(String.format("return Modelers %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve modelers for [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@DELETE
	@Path("/kpi/{id}")
	public void cleanKPIs(@PathParam("id") Long id) {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("cleaning KPIs for model %s", id));
		}
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();
			doCleanKPIs(session, id);
			session.getTransaction().commit();
		} catch (Exception e) {
			logger.error(String.format("Could not clean KPIs for model [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void doCleanKPIs(Session session, Long id) {
		new SfrUtil().doDeleteAll(session, id);
		new FcrUtil().doDeleteAll(session, id);
		new MortalityUtil().doDeleteAll(session, id);
	}

	/**
	 * 
	 * @param session
	 * @param id
	 * @param uploadFileType
	 * @param uploadFileLocation
	 * @return the fileLocation because it will be used in the array
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private String importRemote(Session session, int kind, long id, String uploadFileType, String uploadFileLocation)
			throws EncryptedDocumentException, InvalidFormatException, MalformedURLException, IOException {
		if (kind == ExcelReader.KIND_SAMPLE)
			if (!Strings.isNullOrEmpty(uploadFileType)) {
				if ("xls".equalsIgnoreCase(uploadFileType) || "xlsx".equalsIgnoreCase(uploadFileType)) {
					ExcelReader.instance(ExcelReader.KIND_SAMPLE).importRemote(session, id, uploadFileLocation);
					return uploadFileLocation;
				}
				logger.error(String.format("uknown type [%s] for id [%s]", uploadFileType, id));
			}
		if (kind == ExcelReader.KIND_LIMITS)
			if (!Strings.isNullOrEmpty(uploadFileType)) {
				if ("xls".equalsIgnoreCase(uploadFileType) || "xlsx".equalsIgnoreCase(uploadFileType)) {
					ExcelReader.instance(ExcelReader.KIND_LIMITS).importRemote(session, id, uploadFileLocation);
					return uploadFileLocation;
				}
				logger.error(String.format("uknown type [%s] for id [%s]", uploadFileType, id));
			}
		return null;
	}

	private void manageUploadFiles(Session session, Modeler modeler) throws UserFriendlyException {
		try {
			manageUploadFile(session, modeler.getId(), ExcelReader.KIND_LIMITS, modeler.getUploadFileTypeWeights(),
					modeler.getUploadFileLocationWeights());
		} catch (Exception e) {
			throw new UserFriendlyException("Could not upload WeightCategories file", e);
		}
	}

	private void manageUploadFile(Session session, long id, int kind, String type, String location)
			throws InvalidFormatException, MalformedURLException, IOException {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("managing upload data file for [%s], of type [%s], located at [%s]", id, type,
					location));
		}
		String uploadData = Strings.nullToEmpty(location);
		// clean existing and unused
		String existingSQL = kind == ExcelReader.KIND_SAMPLE ? _GET_EXISTING_UPLOAD_SOURCE_SAMPLE
				: _GET_EXISTING_UPLOAD_SOURCE_LIMITS;
		Query qUploadSource = session.createQuery(existingSQL).setParameter("simulModelId", id);
		List<String> existingUploadSource = qUploadSource.list();
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("existing uploads for %s are [%s]", id, existingUploadSource));
		}
		if (!existingUploadSource.isEmpty())
			if (uploadData.equals(existingUploadSource.get(0))) {
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("upload data file for %s set to same value; bypassing", id));
				}
				return;
			} else {
				// clean previous
				int recsCleaned;
				if (kind == ExcelReader.KIND_SAMPLE)
					recsCleaned = doCleanSampleData(session, id);
				else
					recsCleaned = doCleanWeightLimits(session, id);
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("upload data file for [%s] changed; erased [%s] mismatching records", id,
							recsCleaned));
				}
			}
		if (!Strings.isNullOrEmpty(uploadData))
			importRemote(session, kind, id, type, uploadData);
	}

	public List<Modeler> getModelersForSite(Session session, Long siteid) {
		try {
			logger.trace(String.format("start getModelersForSites"));

			Query q = session.createQuery(_GET_ALL_ON_SITE).setParameter("siteid", siteid);

			List<Modeler> list = q.list();

			logger.trace(String.format("return Modelers %s", list));
			return list;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not retrieve modelers for site [%s]", siteid), e);
		}
	}

	public int doCleanSampleData(Session session, Long id) {
		final SQLQuery q = session.createSQLQuery(_DELETE_ALL_SAMPLE_DATA);
		q.setParameter("simulModelId", id);
		return q.executeUpdate();
	}

	public int doCleanWeightLimits(Session session, Long id) {
		final SQLQuery q = session.createSQLQuery(_DELETE_ALL_LIMITS_DATA);
		q.setParameter("simulModelId", id);
		return q.executeUpdate();
	}

	public boolean doDelete(Session session, Long id) {
		try {
			return doDelete(session, (Modeler) session.get(Modeler.class, Long.valueOf(id)));
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete Modeler with id [%s]", id), e);
		}
	}

	public boolean doDelete(Session session, Modeler modeler) {
		try {
			if (modeler == null) {
				return false;
			}
			doCleanSampleData(session, modeler.getId());
			doCleanWeightLimits(session, modeler.getId());
			doCleanKPIs(session, modeler.getId());
			session.delete(modeler);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete Modeler  [%s]", modeler), e);
		}
	}

	@GET
	@Path("/usage/{ownerId}")
	public List<Usage> getUsage(@PathParam("ownerId") String ownerId) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getUsage"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createSQLQuery(_GET_USAGE_ON_OWNERID).addEntity(Usage.class).setParameter("ownerid",
					ownerId);

			List<Usage> list = q.list();

			session.getTransaction().commit();

			logger.trace(String.format("return site usage %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve site usage for ownerid [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.Modeler s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final String _GET_EXISTING_UPLOAD_SOURCE_SAMPLE = "SELECT DISTINCT uploadSource FROM gr.i2s.fishgrowth.model.SampleData s WHERE s.simulModelId = :simulModelId";
	private static final String _GET_EXISTING_UPLOAD_SOURCE_LIMITS = "SELECT DISTINCT uploadSource FROM gr.i2s.fishgrowth.model.WeightLimit s WHERE s.simulModelId = :simulModelId";
	private static final String _DELETE_ALL_SAMPLE_DATA = "DELETE FROM SampleData s WHERE s.simulModelId = :simulModelId";
	private static final String _DELETE_ALL_LIMITS_DATA = "DELETE FROM WeightLimit s WHERE s.simulModelId = :simulModelId";
	private static final String _GET_ALL_ON_SITE = "FROM gr.i2s.fishgrowth.model.Modeler s WHERE s.siteId = :siteid ORDER BY s.id ASC";
	private static final String _GET_USAGE_ON_OWNERID = "SELECT us.id as id, us.scenariocount as usage FROM simulusageview us inner join simulmodel e on (us.id=e.id) WHERE e.ownerId = :ownerid ORDER BY us.id ASC";
	private static final Logger logger = LoggerFactory.getLogger(ModelerUtil.class);

}
