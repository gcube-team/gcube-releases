package org.gcube.informationsystem.resourceregistry.query;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.PermissionMode;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class QueryImpl implements Query {
	
	private static Logger logger = LoggerFactory.getLogger(QueryImpl.class);
	
	@Override
	public String query(String query, Integer limit, String fetchPlan) throws InvalidQueryException {
		if(limit == null) {
			limit = AccessPath.DEFAULT_LIMIT;
		}
		limit = (limit <= 0) ? AccessPath.UNBOUNDED : limit;
		
		ODatabaseDocumentTx oDatabaseDocumentTx = null;
		
		try {
			SecurityContext securityContext = ContextUtility.getCurrentSecurityContext();
			
			oDatabaseDocumentTx = securityContext.getDatabaseDocumentTx(PermissionMode.READER);
			
			OSQLSynchQuery<ODocument> osqlSynchQuery = new OSQLSynchQuery<>(query, limit);
			osqlSynchQuery.setFetchPlan(fetchPlan);
			osqlSynchQuery.setCacheableResult(true);
			
			logger.debug("Going to execute query : \"{}\", fetchPlan : \"{}\", limit : {}", osqlSynchQuery.getText(),
					osqlSynchQuery.getFetchPlan(), osqlSynchQuery.getLimit());
			
			List<Object> records = oDatabaseDocumentTx.query(osqlSynchQuery);
			
			Writer writer = new StringWriter();
			writer.append("{\"result\":[");
			for(int i = 0; i < records.size(); i++) {
				ODocument oDocument = (ODocument) records.get(i);
				writer.append(Utility.toJsonString(oDocument, false));
				if(i < (records.size() - 1)) {
					writer.append(",");
				}
			}
			writer.append("]}");
			
			return writer.toString();
			
		} catch(Exception e) {
			throw new InvalidQueryException(e.getMessage());
		} finally {
			if(oDatabaseDocumentTx != null) {
				oDatabaseDocumentTx.close();
			}
		}
		
	}
	
	@Override
	public String gremlinQuery(String query) throws InvalidQueryException {
		throw new UnsupportedOperationException();
		
		/*
		OGremlinHelper.global().create();
		
		ODatabaseDocumentTx oDatabaseDocumentTx = null;
		try {
			oDatabaseDocumentTx = ContextUtility.getActualSecurityContextDatabaseTx(PermissionMode.READER);
			
			String finalQuery = String.format("select gremlin('%s')", query);
			OCommandSQL OCommandSQL = new OCommandSQL(finalQuery);
			OCommandRequest oCommandRequest = oDatabaseDocumentTx.command(OCommandSQL);
			OBasicResultSet res = oCommandRequest.execute();
			
			Iterator iterator = res.iterator();
			
			while(iterator.hasNext()) {
				ODocument oDocument = (ODocument) iterator.next();
				logger.debug("{}", oDocument);
			}
			
			return res.toString();
			
		} catch(Exception e) {
			throw new InvalidQueryException(e.getMessage());
		} finally {
			if(oDatabaseDocumentTx != null) {
				oDatabaseDocumentTx.close();
			}
		}
		*/
	}
	
}
