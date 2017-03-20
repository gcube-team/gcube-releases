package org.gcube.informationsystem.resourceregistry.rest;

import java.util.Arrays;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.EntityManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.query.Query;
import org.gcube.informationsystem.resourceregistry.query.QueryImpl;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Lucio Lelii (ISTI - CNR)
 */
@Path(AccessPath.ACCESS_PATH_PART)
public class Access {

	private static Logger logger = LoggerFactory.getLogger(Access.class);

	public static final String ID_PATH_PARAM = "id";
	public static final String TYPE_PATH_PARAM = "type";

	/**
	 * It includeSubtypesows to query Entities and Relations in the current
	 * Context.<br />
	 * It accepts idempotent query only.. <br />
	 * <br />
	 * For query syntax please refer to<br />
	 * <a href="https://orientdb.com/docs/last/SQL-Syntax.html" target="_blank">
	 * https://orientdb.com/docs/last/SQL-Syntax.html </a> <br />
	 * <br />
	 * e.g. GET /resource-registry/access?query=SELECT FROM V
	 * 
	 * @param query
	 *            Defines the query to send to the backend.
	 * @param limit
	 *            Defines the number of results you want returned, defaults to
	 *            includeSubtypes results.
	 * @param fetchPlan
	 *            Defines the fetching strategy you want to use. See <a
	 *            href="https://orientdb.com/docs/last/Fetching-Strategies.html"
	 *            target="_blank">
	 *            https://orientdb.com/docs/last/Fetching-Strategies.html </a>
	 * @return The JSON representation of the result
	 * @throws InvalidQueryException
	 *             if the query is invalid or no idempotent
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String query(@QueryParam(AccessPath.QUERY_PARAM) String query,
			@QueryParam(AccessPath.LIMIT_PARAM) int limit,
			@QueryParam(AccessPath.FETCH_PLAN_PARAM) String fetchPlan)
			throws InvalidQueryException {
		logger.info("Requested query (fetch plan {}, limit : {}):\n{}",
				fetchPlan, limit, query);
		Query queryManager = new QueryImpl();
		return queryManager.query(query, limit, fetchPlan);
	}

	/*
	 * e.g. GET
	 * /resource-registry/access/instance/ContactFacet/4d28077b-566d-4132-b073-
	 * f4edaf61dcb9
	 */
	@GET
	@Path(AccessPath.INSTANCE_PATH_PART + "/" + "{" + TYPE_PATH_PARAM + "}"
			+ "/{" + ID_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInstance(@PathParam(TYPE_PATH_PARAM) String type,
			@PathParam(ID_PATH_PARAM) String id)
			throws ERNotFoundException, ResourceRegistryException {
		logger.info("Requested {} with id {}", type, id);

		@SuppressWarnings("rawtypes")
		ERManagement erManagement = ERManagement.getERManagement(type);
		UUID uuid = null;
		try {
			uuid = UUID.fromString(id);
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		}
		erManagement.setUUID(uuid);
		return erManagement.read();
	}

	/*
	 * e.g. GET /resource-registry/access/instances/EService?polymorphic=true
	 * 
	 * &reference=4d28077b-566d-4132-b073-f4edaf61dcb9 &direction=(in|out|both)
	 */
	@SuppressWarnings({ "rawtypes" })
	@GET
	@Path(AccessPath.INSTANCES_PATH_PART + "/" + "{" + TYPE_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInstances(@PathParam(TYPE_PATH_PARAM) String type,
			@QueryParam(AccessPath.POLYMORPHIC_PARAM) Boolean polymorphic,
			@QueryParam(AccessPath.REFERENCE) String reference,
			@QueryParam(AccessPath.DIRECTION) String direction)
			throws ResourceRegistryException {
		logger.info("Requested {} ({}={}) instances", type,
				AccessPath.POLYMORPHIC_PARAM, polymorphic);

		ERManagement erManagement = ERManagement.getERManagement(type);

		if (erManagement instanceof EntityManagement) {
			return erManagement.all(polymorphic);
		}

		if (erManagement instanceof RelationManagement) {
			if (reference != null) {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(reference);
				} catch (Exception e) {
					String errror = String.format(
							"Provided %s (%s) is not a valid %s",
							AccessPath.REFERENCE, reference,
							UUID.class.getSimpleName());
					throw new ResourceRegistryException(errror);
				}

				Direction directionEnum;
				if (direction == null) {
					directionEnum = Direction.BOTH;
				} else {
					try {
						directionEnum = Enum.valueOf(Direction.class, direction
								.trim().toUpperCase());
					} catch (Exception e) {
						String errror = String
								.format("Provided %s (%s) is not valid. Allowed values are %s",
										AccessPath.DIRECTION, direction,
										Arrays.toString(Direction.values()).toLowerCase());
						throw new ResourceRegistryException(errror);
					}
				}

				return ((RelationManagement) erManagement).allFrom(uuid,
						directionEnum, polymorphic);

			} else {
				return erManagement.all(polymorphic);
			}
		}

		throw new ResourceRegistryException("Invalid Request");
	}

	/*
	 * e.g. GET /resource-registry/access/schema/ContactFacet?polymorphic=true
	 */
	@GET
	@Path(AccessPath.SCHEMA_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSchema(@PathParam(TYPE_PATH_PARAM) String type,
			@QueryParam(AccessPath.POLYMORPHIC_PARAM) Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException {
		logger.info("Requested Schema for type {}", type);

		SchemaManagement schemaManagement = new SchemaManagementImpl();
		return schemaManagement.read(type, polymorphic);
	}

}