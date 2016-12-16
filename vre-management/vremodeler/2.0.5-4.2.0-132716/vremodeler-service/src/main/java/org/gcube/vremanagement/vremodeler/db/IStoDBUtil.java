package org.gcube.vremanagement.vremodeler.db;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.vremanagement.vremodeler.impl.ModelerContext;
import org.gcube.vremanagement.vremodeler.impl.ModelerResource;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Collection;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.FunctionalityPersisted;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.GenericResource;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Ghn;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.RunningInstance;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.RuntimeResource;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Service;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VRE;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VreCollectionRelation;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VreFunctionalityRelation;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VreGhnRelation;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;
import org.gcube.vremanagement.vremodeler.utils.reports.Status;
import org.xml.sax.InputSource;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;



/**
 * 
 * @author lucio lelii CNR
 *
 */
public class IStoDBUtil {

	private static GCUBELog logger = new GCUBELog(IStoDBUtil.class);

	private static void createTables(GCUBEScope scope) throws GCUBEFault{

		try{
			ConnectionSource connectionSource = DBInterface.connect();
			TableUtils.createTableIfNotExists(connectionSource, RunningInstance.class);
			TableUtils.createTableIfNotExists(connectionSource, Ghn.class);
			TableUtils.createTableIfNotExists(connectionSource, Collection.class);
			TableUtils.createTableIfNotExists(connectionSource, GenericResource.class);
			TableUtils.createTableIfNotExists(connectionSource, Service.class);
			TableUtils.createTableIfNotExists(connectionSource, RuntimeResource.class);
			TableUtils.createTableIfNotExists(connectionSource, FunctionalityPersisted.class);



		}catch (Exception e){
			logger.error("error creating tables",e); 
			throw new GCUBEFault(e); }
	}

	private static void  createRelationTable(GCUBEScope scope) throws GCUBEFault {
		boolean tablesNotCreated = false;
		ConnectionSource connectionSource = null;
		try{
			connectionSource = DBInterface.connect();
			Dao<VRE, String> vreDao = DaoManager.createDao(connectionSource, VRE.class);
			vreDao.countOf();
		}catch (Exception e) {
			logger.trace("the table doesn't exist");
			tablesNotCreated = true;
		}	

		if (tablesNotCreated){
			try{
				TableUtils.createTableIfNotExists(connectionSource, VRE.class);
				TableUtils.createTableIfNotExists(connectionSource, VreGhnRelation.class);
				TableUtils.createTableIfNotExists(connectionSource, VreCollectionRelation.class);
				TableUtils.createTableIfNotExists(connectionSource, VreFunctionalityRelation.class);

				//in case of ServiceState lost the service recovery the VRE created from the IS
				ISClient client = GHNContext.getImplementation(ISClient.class);
				GCUBEGenericResourceQuery query= client.getQuery(GCUBEGenericResourceQuery.class);
				query.addAtomicConditions(new AtomicCondition("/Profile/SecondaryType","VRE"));
				List<GCUBEGenericResource> vresResources = client.execute(query, scope);
				if (vresResources.size()>0){
					Dao<VRE, String> vreDao =
							DaoManager.createDao(DBInterface.connect(), VRE.class);
					for (GCUBEGenericResource res : vresResources){
						Calendar now = Calendar.getInstance();
						Calendar expires = Calendar.getInstance();
						expires.add(1, Calendar.YEAR);
						
						StringWriter sw = new StringWriter();
						res.store(sw);
						
						String managerXPath = "//Body/Manager/text()";
				        String designerXPath ="//Body/Designer/text()";
						XPath xPath = XPathFactory.newInstance().newXPath();
						String manager =xPath.evaluate(managerXPath, new InputSource(new StringReader(sw.toString())));
						String designer =xPath.evaluate(designerXPath, new InputSource(new StringReader(sw.toString())));
						
						
						try{
							VRE vre = new VRE(res.getID(), res.getName(), res.getDescription(), designer, manager, now , expires, Status.Deployed.name());
							vreDao.create(vre);
							ModelerContext pctx= ModelerContext.getPortTypeContext();
							ModelerResource mr=(ModelerResource)pctx.getWSHome().create(pctx.makeKey(res.getID()), res.getID());
							DeployReport dr = new DeployReport();
							dr.setStatus(Status.Deployed);
							mr.setDeployReport(dr);
							mr.store();
						}catch(Exception e){
							logger.warn("cannot add VRE "+res.getName(),e);
						}
					}
				}
			}catch (Exception e){
				logger.error("error creating relation tables",e); 
				throw new GCUBEFault(e); }
		}
	}

	/**
	 * Initialize all database tables
	 * 
	 * @param GCUBEScope  the scope
	 */
	public static void initDB(GCUBEScope scope) throws GCUBEFault{

		logger.info("Starting initialization!! the database already exists? "+DBInterface.dbAlreadyCreated(scope) );
		if (DBInterface.dbAlreadyCreated(scope))
			cleanDB(scope);

		try{


			createRelationTable(scope);
		}catch (Exception e) {
			logger.trace("table vre already created");
		}

		createTables(scope);
	}

	private static void cleanDB(GCUBEScope scope) throws GCUBEFault
	{

		try{	
			ConnectionSource connectionSource = DBInterface.connect();
			TableUtils.dropTable(connectionSource, Ghn.class, true);
			TableUtils.dropTable(connectionSource, RunningInstance.class, true);
			TableUtils.dropTable(connectionSource, Collection.class, true);
			TableUtils.dropTable(connectionSource, GenericResource.class, true);
			TableUtils.dropTable(connectionSource, RuntimeResource.class, true);
			TableUtils.dropTable(connectionSource, Service.class, true);
			TableUtils.dropTable(connectionSource, FunctionalityPersisted.class, true);
			//TableUtils.dropTable(connectionSource, VRE.class, true);
			//TableUtils.dropTable(connectionSource, VreGhnRelation.class, true);
			//TableUtils.dropTable(connectionSource, VreCollectionRelation.class, true);
			//TableUtils.dropTable(connectionSource, VreFunctionalityRelation.class, true);

		}catch (SQLException e){
			logger.error("error cleaning sqlDB",e); 
			throw new GCUBEFault(e); }
	}


	/*
	private static void insertNeededResources(GCUBEScope scope) throws GCUBEFault{
		List<GCUBEGenericResource> genericResourcesList= null;
		try{
			if (queryClient==null) queryClient= GHNContext.getImplementation(ISClient.class);
			GCUBEGenericResourceQuery query= queryClient.getQuery(GCUBEGenericResourceQuery.class);
			query.addGenericCondition("$result/Profile/SecondaryType/string() eq 'UserProfile' or $result/Profile/SecondaryType/string() eq 'Schemas Searchable Fields' or $result/Profile/SecondaryType/string() eq 'MetadataXSLT' or $result/Profile/SecondaryType/string() eq 'PresentationXSLT'");
			genericResourcesList= queryClient.execute(query, scope);
		}catch(Exception e ){logger.error("Error queryng IS"); throw new GCUBEFault(e); }


		//NEEDEDRESOURCES(ID, TYPE)
		List<String[]> values = new ArrayList<String[]>(genericResourcesList.size());
		List<String> row; 
		for (GCUBEGenericResource gen :genericResourcesList){
			row= new ArrayList<String>(2);
			row.add(gen.getID());
			row.add(gen.getType());
			values.add(row.toArray(new String[2]));
		}

		try{
			DBInterface.connect();
			DBInterface.insertInto("NEEDEDRESOURCES", values.toArray(new String[0][0]));
		}catch (SQLException e){
			logger.error("error inserting Generic resource in the DB ",e); 
			throw new GCUBEFault(e); 
		}

	}
	 */

}
